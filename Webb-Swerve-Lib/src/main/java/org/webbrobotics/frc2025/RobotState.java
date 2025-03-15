// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.interpolation.TimeInterpolatableBuffer;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.littletonrobotics.junction.AutoLogOutput;
import org.littletonrobotics.junction.Logger;
import org.webbrobotics.frc2025.subsystems.drive.DriveConstants;
import org.webbrobotics.frc2025.util.GeomUtil;

@ExtensionMethod({GeomUtil.class})
public class RobotState {
  private static final double poseBufferSizeSec = 2.0;
  private static final Matrix<N3, N1> odometryStateStdDevs =
      new Matrix<>(VecBuilder.fill(0.003, 0.003, 0.002));

  private static RobotState instance;

  public static RobotState getInstance() {
    if (instance == null) instance = new RobotState();
    return instance;
  }

  // Pose Estimation Members
  @Getter @AutoLogOutput private Pose2d odometryPose = new Pose2d();
  @Getter @AutoLogOutput private Pose2d estimatedPose = new Pose2d();

  private final TimeInterpolatableBuffer<Pose2d> poseBuffer =
      TimeInterpolatableBuffer.createBuffer(poseBufferSizeSec);
  private final Matrix<N3, N1> qStdDevs = new Matrix<>(Nat.N3(), Nat.N1());

  // Odometry
  private final SwerveDriveKinematics kinematics;
  private SwerveModulePosition[] lastWheelPositions =
      new SwerveModulePosition[] {
        new SwerveModulePosition(),
        new SwerveModulePosition(),
        new SwerveModulePosition(),
        new SwerveModulePosition()
      };
  // Assume gyro starts at zero
  private Rotation2d gyroOffset = Rotation2d.kZero;

  @Getter
  @AutoLogOutput(key = "RobotState/RobotVelocity")
  private ChassisSpeeds robotVelocity = new ChassisSpeeds();

  @Getter @Setter private OptionalDouble distanceToBranch = OptionalDouble.empty();

  public RobotState() {
    for (int i = 0; i < 3; ++i) {
      qStdDevs.set(i, 0, Math.pow(odometryStateStdDevs.get(i, 0), 2));
    }
    kinematics = new SwerveDriveKinematics(DriveConstants.moduleTranslations);
  }

  public void resetPose(Pose2d pose) {
    // Gyro offset is the rotation that maps the old gyro rotation (estimated - offset) to the new
    // frame of rotation
    gyroOffset = pose.getRotation().minus(odometryPose.getRotation().minus(gyroOffset));
    estimatedPose = pose;
    odometryPose = pose;
    poseBuffer.clear();
  }

  public void addOdometryObservation(OdometryObservation observation) {
    Twist2d twist = kinematics.toTwist2d(lastWheelPositions, observation.wheelPositions());
    lastWheelPositions = observation.wheelPositions();
    Pose2d lastOdometryPose = odometryPose;
    odometryPose = odometryPose.exp(twist);
    // Use gyro if connected
    observation.gyroAngle.ifPresent(
        gyroAngle -> {
          // Add offset to measured angle
          Rotation2d angle = gyroAngle.plus(gyroOffset);
          odometryPose = new Pose2d(odometryPose.getTranslation(), angle);
        });
    // Add pose to buffer at timestamp
    poseBuffer.addSample(observation.timestamp(), odometryPose);
    // Calculate diff from last odometry pose and add onto pose estimate
    Twist2d finalTwist = lastOdometryPose.log(odometryPose);
    estimatedPose = estimatedPose.exp(finalTwist);
  }

  public void addDriveSpeeds(ChassisSpeeds speeds) {
    robotVelocity = speeds;
  }

  @AutoLogOutput(key = "RobotState/FieldVelocity")
  public ChassisSpeeds getFieldVelocity() {
    return ChassisSpeeds.fromRobotRelativeSpeeds(robotVelocity, getRotation());
  }

  public Rotation2d getRotation() {
    return estimatedPose.getRotation();
  }

  public void periodicLog() {
    // Any logging that might be needed can go here
  }

  /**
   * Add a vision measurement to the pose estimator with default standard deviations.
   *
   * @param visionPose The pose measured by vision
   * @param timestamp The timestamp when the measurement was taken
   */
  public void addVisionMeasurement(Pose2d visionPose, double timestamp) {
    // Use default standard deviations - this will apply less weight to vision measurements
    Matrix<N3, N1> defaultStdDevs = new Matrix<>(VecBuilder.fill(4, 4, 8));
    addVisionMeasurement(visionPose, timestamp, defaultStdDevs);
  }

  /**
   * Add a vision measurement to the pose estimator with specified standard deviations.
   *
   * @param visionPose The pose measured by vision
   * @param timestamp The timestamp when the measurement was taken
   * @param stdDevs Standard deviations of the vision measurement (x, y, theta)
   */
  public void addVisionMeasurement(Pose2d visionPose, double timestamp, Matrix<N3, N1> stdDevs) {
    // Get odometry based pose at timestamp
    var sample = poseBuffer.getSample(timestamp);
    if (sample.isEmpty()) {
      // exit if not there
      return;
    }

    // sample --> odometryPose transform and backwards of that
    var sampleToOdometryTransform = new Transform2d(sample.get(), odometryPose);
    var odometryToSampleTransform = new Transform2d(odometryPose, sample.get());
    // get old estimate by applying odometryToSample Transform
    Pose2d estimateAtTime = estimatedPose.plus(odometryToSampleTransform);

    // difference between estimate and vision pose
    Transform2d transform = new Transform2d(estimateAtTime, visionPose);

    // Calculate a single confidence factor between 0 and 1
    // Average the standard deviations and map to a confidence factor
    double avgStdDev = (stdDevs.get(0, 0) + stdDevs.get(1, 0) + stdDevs.get(2, 0)) / 3.0;
    double confidenceFactor = 1.0 / (1.0 + avgStdDev);

    // Apply a partial correction based on confidence
    Transform2d correctionTransform =
        new Transform2d(
            transform.getTranslation().getX() * confidenceFactor,
            transform.getTranslation().getY() * confidenceFactor,
            new Rotation2d(transform.getRotation().getRadians() * confidenceFactor));

    // Recalculate current estimate by applying transform to old estimate
    // then replaying odometry data
    estimatedPose = estimateAtTime.plus(correctionTransform).plus(sampleToOdometryTransform);

    // Log the standard deviations used
    Logger.recordOutput("Vision/MeasurementStdDevX", stdDevs.get(0, 0));
    Logger.recordOutput("Vision/MeasurementStdDevY", stdDevs.get(1, 0));
    Logger.recordOutput("Vision/MeasurementStdDevTheta", stdDevs.get(2, 0));
  }

  public record OdometryObservation(
      SwerveModulePosition[] wheelPositions, Optional<Rotation2d> gyroAngle, double timestamp) {}
}
