// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import java.util.Comparator;
import java.util.function.DoubleSupplier;
import org.littletonrobotics.junction.Logger;
import org.webbrobotics.frc2025.FieldConstants;
import org.webbrobotics.frc2025.RobotState;
import org.webbrobotics.frc2025.subsystems.drive.Drive;
import org.webbrobotics.frc2025.subsystems.drive.DriveConstants;
import org.webbrobotics.frc2025.util.AllianceFlipUtil;
import org.webbrobotics.frc2025.util.LoggedTunableNumber;

public class DriveToAlgae extends DriveToPose {
  private static final LoggedTunableNumber lookAheadSecs =
      new LoggedTunableNumber("DriveToAlgae/LookAheadSecs", 0.3);
  private static final LoggedTunableNumber angleDifferenceWeight =
      new LoggedTunableNumber("DriveToAlgae/AngleDifferenceWeight", 0.3);
  private static final LoggedTunableNumber algaeMaxDistance =
      new LoggedTunableNumber("DriveToAlgae/AlgaeMaxDistance", 1.4);
  private static final LoggedTunableNumber algaeMaxAngleDeg =
      new LoggedTunableNumber("DriveToAlgae/AlgaeMaxAngleDegrees", 70.0);

  public DriveToAlgae(
      Drive drive, DoubleSupplier driverX, DoubleSupplier driverY, DoubleSupplier driverOmega) {
    super(
        drive,
        () -> {
          RobotState instance = RobotState.getInstance();
          Pose2d robot = instance.getEstimatedPose();

          ChassisSpeeds robotVelocity = instance.getRobotVelocity();

          Pose2d predictedRobot =
              instance.getEstimatedPose().exp(robotVelocity.toTwist2d(lookAheadSecs.get()));
          Logger.recordOutput("DriveToAlgae/LookAheadPose", predictedRobot);

          return instance.getAlgaeTranslations().stream()
              .min(
                  Comparator.comparingDouble(
                      algae ->
                          algae.getDistance(predictedRobot.getTranslation())
                              + Math.abs(
                                  algae
                                          .minus(robot.getTranslation())
                                          .getAngle()
                                          .minus(robot.getRotation())
                                          .getRadians()
                                      * angleDifferenceWeight.get())))
              .filter(
                  algae ->
                      algae.getDistance(predictedRobot.getTranslation()) <= algaeMaxDistance.get()
                          && Math.abs(
                                  algae
                                      .minus(predictedRobot.getTranslation())
                                      .getAngle()
                                      .getDegrees())
                              <= algaeMaxAngleDeg.get())
              .map(
                  algae -> {
                    Logger.recordOutput("DriveToAlgae/TargetedAlgae", new Translation2d[] {algae});
                    return new Pose2d(algae, robot.getTranslation().minus(algae).getAngle())
                        .transformBy(
                            new Transform2d(
                                FieldConstants.algaeDiameter / 2.0
                                    + DriveConstants.robotWidth / 2.0,
                                0.0,
                                Rotation2d.kPi));
                  })
              .orElseGet(
                  () -> {
                    Logger.recordOutput("DriveToAlgae/TargetedAlgae", new Translation2d[] {});
                    return RobotState.getInstance().getEstimatedPose();
                  });
        },
        RobotState.getInstance()::getEstimatedPose,
        () ->
            DriveCommands.getLinearVelocityFromJoysticks(
                    driverX.getAsDouble(), driverY.getAsDouble())
                .times(AllianceFlipUtil.shouldFlip() ? -1.0 : 1.0),
        () -> DriveCommands.getOmegaFromJoysticks(driverOmega.getAsDouble()));
  }
}
