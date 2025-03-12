// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.vision;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.RobotBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.littletonrobotics.junction.Logger;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import org.webbrobotics.frc2025.RobotState;

/** Vision subsystem for vision. */
public class Vision {
  // IO instances
  private final VisionIO[] cameras;

  // Standard deviation tracking for pose estimation
  private Matrix<N3, N1> currentStdDevs = VisionConstants.SINGLE_TAG_STD_DEVS;

  // Tag tracking
  private final Set<Integer> allSeenTagIds = new HashSet<>();
  private final Set<Integer> currentlyVisibleTagIds = new HashSet<>();
  private long lastLogTime = 0;

  /** Creates the Vision subsystem. */
  public Vision() {
    // Initialize VisionIO array with cameras from constants
    cameras = new VisionIO[VisionConstants.CAMERA_NAMES.length];

    for (int i = 0; i < VisionConstants.CAMERA_NAMES.length; i++) {
      if (RobotBase.isSimulation()) {
        cameras[i] =
            new VisionIOSim(
                VisionConstants.CAMERA_NAMES[i],
                VisionConstants.ROBOT_TO_CAMERAS[i],
                VisionConstants.TAG_LAYOUT);
      } else {
        cameras[i] =
            new PhotonVisionIO(
                VisionConstants.CAMERA_NAMES[i],
                VisionConstants.ROBOT_TO_CAMERAS[i],
                VisionConstants.TAG_LAYOUT);
      }
    }
  }

  /** Called periodically to update vision processing. */
  public void periodic() {
    // Process each camera
    for (VisionIO camera : cameras) {
      PhotonPipelineResult result = camera.getLatestResult();
      String cameraName = camera.getCameraName();

      // Log basic results
      if (result.hasTargets()) {
        PhotonTrackedTarget bestTarget = result.getBestTarget();
        Logger.recordOutput("Vision/" + cameraName + "/BestTargetYaw", bestTarget.getYaw());
        Logger.recordOutput("Vision/" + cameraName + "/BestTargetPitch", bestTarget.getPitch());
        Logger.recordOutput("Vision/" + cameraName + "/BestTargetArea", bestTarget.getArea());
        Logger.recordOutput("Vision/" + cameraName + "/BestTargetID", bestTarget.getFiducialId());

        // Log all detected targets
        List<PhotonTrackedTarget> targets = result.getTargets();
        logTargetData(cameraName, targets);
      } else {
        Logger.recordOutput("Vision/" + cameraName + "/NoTargets", true);
      }

      Logger.recordOutput(
          "Vision/" + cameraName + "/CameraTimeSeconds", result.getTimestampSeconds());
    }

    // Log tracking data
    if (RobotBase.isSimulation()) {
      Pose2d simPose = RobotState.getInstance().getEstimatedPose();
      Logger.recordOutput("Vision/SimulationPoseSource", simPose);
      simulationPeriodic(simPose);
    } else {
      logSeenAprilTags();
    }

    Optional<EstimatedRobotPose> visionEst = getEstimatedGlobalPose();
    if (visionEst.isPresent()) {
      EstimatedRobotPose robotPose = visionEst.get();
      Pose2d visionPose = robotPose.estimatedPose.toPose2d();
      double timestamp = robotPose.timestampSeconds;

      // Add the vision measurement to our state estimator
      RobotState.getInstance().addVisionMeasurement(visionPose, timestamp);

      // Log vision data
      Logger.recordOutput("Vision/ProcessedPose", visionPose);
      Logger.recordOutput("Vision/ProcessedTimestamp", timestamp);
    }
  }

  /**
   * The latest estimated robot pose on the field from vision data.
   *
   * @return An EstimatedRobotPose with the estimate, or empty if not available
   */
  public Optional<EstimatedRobotPose> getEstimatedGlobalPose() {
    Optional<EstimatedRobotPose> visionEst = Optional.empty();
    List<PhotonTrackedTarget> allDetectedTags = new ArrayList<>();

    // Try each camera until we get a valid result
    for (VisionIO camera : cameras) {
      // Get the raw result to collect all tags
      PhotonPipelineResult result = camera.getLatestResult();
      if (result.hasTargets()) {
        allDetectedTags.addAll(result.getTargets());
      }

      // Try to get a pose estimation
      var est = camera.getEstimatedGlobalPose();
      if (est.isPresent() && !visionEst.isPresent()) {
        visionEst = est;
        updateEstimationStdDevs(est, result.getTargets());
      }
    }

    // Log the vision pose estimation
    visionEst.ifPresent(
        est -> {
          Logger.recordOutput("Vision/EstimatedPose", est.estimatedPose.toPose2d());
          Logger.recordOutput("Vision/EstimationTimestamp", est.timestampSeconds);

          // Create array of field poses for all detected tags
          Pose2d[] tagPoses = new Pose2d[est.targetsUsed.size()];
          int[] tagIDs = new int[est.targetsUsed.size()];

          for (int i = 0; i < est.targetsUsed.size(); i++) {
            PhotonTrackedTarget target = est.targetsUsed.get(i);
            tagIDs[i] = target.getFiducialId();

            var optTagPose = VisionConstants.TAG_LAYOUT.getTagPose(tagIDs[i]);
            tagPoses[i] = optTagPose.map(Pose3d::toPose2d).orElse(new Pose2d());
          }

          Logger.recordOutput("Vision/EstimatedPoseTagIDs", tagIDs);
          Logger.recordOutput("Vision/EstimatedPoseTagPoses", tagPoses);
          Logger.recordOutput("Vision/EstimatedPoseTagCount", est.targetsUsed.size());
        });

    // If no pose estimation but tags detected, log them
    if (visionEst.isEmpty() && !allDetectedTags.isEmpty()) {
      int[] tagIDs = new int[allDetectedTags.size()];
      Pose2d[] tagPoses = new Pose2d[allDetectedTags.size()];

      for (int i = 0; i < allDetectedTags.size(); i++) {
        PhotonTrackedTarget target = allDetectedTags.get(i);
        tagIDs[i] = target.getFiducialId();

        var tagPose = VisionConstants.TAG_LAYOUT.getTagPose(tagIDs[i]);
        tagPoses[i] = tagPose.map(Pose3d::toPose2d).orElse(new Pose2d());
      }

      Logger.recordOutput("Vision/DetectedTagIDs", tagIDs);
      Logger.recordOutput("Vision/DetectedTagPoses", tagPoses);
      Logger.recordOutput("Vision/DetectedTagCount", allDetectedTags.size());
    }

    return visionEst;
  }

  /**
   * Updates the estimation standard deviations based on target information.
   *
   * @param estimatedPose The estimated robot pose
   * @param targets The targets used for estimation
   */
  private void updateEstimationStdDevs(
      Optional<EstimatedRobotPose> estimatedPose, List<PhotonTrackedTarget> targets) {
    if (estimatedPose.isEmpty()) {
      // No pose input, default to single-tag std devs
      currentStdDevs = VisionConstants.SINGLE_TAG_STD_DEVS;
      return;
    }

    // Count visible tags and calculate average distance
    int numTags = 0;
    double avgDist = 0;

    for (PhotonTrackedTarget tgt : targets) {
      var tagPose = VisionConstants.TAG_LAYOUT.getTagPose(tgt.getFiducialId());
      if (tagPose.isEmpty()) continue;

      numTags++;
      avgDist +=
          tagPose
              .get()
              .toPose2d()
              .getTranslation()
              .getDistance(estimatedPose.get().estimatedPose.toPose2d().getTranslation());
    }

    if (numTags == 0) {
      // No valid tags, use single tag defaults
      currentStdDevs = VisionConstants.SINGLE_TAG_STD_DEVS;
    } else {
      // Adjust std devs based on number of tags and distance
      avgDist /= numTags;

      Matrix<N3, N1> baseStdDevs =
          (numTags > 1) ? VisionConstants.MULTI_TAG_STD_DEVS : VisionConstants.SINGLE_TAG_STD_DEVS;

      // Far away single tag is highly unreliable
      if (numTags == 1 && avgDist > 4) {
        currentStdDevs = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
      } else {
        // Scale std devs based on distance
        currentStdDevs = baseStdDevs.times(1 + (avgDist * avgDist / 30));
      }
    }
  }

  /** Gets the current standard deviations for the vision pose estimate. */
  public Matrix<N3, N1> getEstimationStdDevs() {
    return currentStdDevs;
  }

  /** Logs target data from a specific camera. */
  private void logTargetData(String cameraName, List<PhotonTrackedTarget> targets) {
    if (targets.isEmpty()) return;

    int[] tagIDs = new int[targets.size()];
    Pose3d[] tagPoses = new Pose3d[targets.size()];

    for (int i = 0; i < targets.size(); i++) {
      PhotonTrackedTarget target = targets.get(i);
      tagIDs[i] = target.getFiducialId();

      var tagPose = VisionConstants.TAG_LAYOUT.getTagPose(target.getFiducialId());
      tagPoses[i] = tagPose.orElse(new Pose3d());

      // Track tags that are seen
      if (tagPose.isPresent()) {
        currentlyVisibleTagIds.add(target.getFiducialId());
        allSeenTagIds.add(target.getFiducialId());
      }
    }

    Logger.recordOutput("Vision/" + cameraName + "/DetectedTagIDs", tagIDs);
    Logger.recordOutput("Vision/" + cameraName + "/DetectedTagPoses", tagPoses);
    Logger.recordOutput("Vision/" + cameraName + "/NumberOfDetectedTags", targets.size());
  }

  /** Logs all AprilTags that have been seen during the match. */
  private void logSeenAprilTags() {
    // Convert collections to arrays for logging
    int[] allSeenIds = allSeenTagIds.stream().mapToInt(Integer::intValue).sorted().toArray();
    int[] currentlyVisibleIds =
        currentlyVisibleTagIds.stream().mapToInt(Integer::intValue).sorted().toArray();

    // Create pose arrays for logging
    Map<Integer, Pose3d> tagPoses = new HashMap<>();
    for (Integer id : allSeenTagIds) {
      VisionConstants.TAG_LAYOUT.getTagPose(id).ifPresent(pose -> tagPoses.put(id, pose));
    }

    // Create arrays for visible tags
    Pose3d[] visibleTagPoses = new Pose3d[currentlyVisibleTagIds.size()];
    int index = 0;
    for (Integer id : currentlyVisibleIds) {
      visibleTagPoses[index++] = tagPoses.getOrDefault(id, new Pose3d());
    }

    // Create arrays for all tags ever seen
    Pose3d[] allTagPoses = new Pose3d[allSeenTagIds.size()];
    index = 0;
    for (Integer id : allSeenIds) {
      allTagPoses[index++] = tagPoses.getOrDefault(id, new Pose3d());
    }

    // Log currently visible tags
    Logger.recordOutput("Vision/CurrentlyVisibleTagIDs", currentlyVisibleIds);
    Logger.recordOutput("Vision/CurrentlyVisibleTagPoses", visibleTagPoses);
    Logger.recordOutput("Vision/CurrentlyVisibleTagCount", currentlyVisibleIds.length);

    // Log historical tags (less frequently)
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastLogTime > 500) { // Update every 500ms
      Logger.recordOutput("Vision/AllSeenAprilTagIDs", allSeenIds);
      Logger.recordOutput("Vision/AllSeenAprilTagPoses", allTagPoses);
      Logger.recordOutput("Vision/TotalUniqueTagsSeen", allSeenIds.length);
      lastLogTime = currentTime;
    }

    // Clear currently visible tags for next cycle
    currentlyVisibleTagIds.clear();
  }

  /** Updates all simulated cameras with the current robot pose. */
  public void simulationPeriodic(Pose2d robotSimPose) {
    // Log the simulation pose to help diagnose the issue
    Logger.recordOutput("Vision/SimulatedPose", robotSimPose);
    for (VisionIO camera : cameras) {
      camera.simulationPeriodic(robotSimPose);
    }
  }

  /** Resets the pose history for all simulated cameras. */
  public void resetSimPose(Pose2d pose) {
    for (VisionIO camera : cameras) {
      camera.resetSimPose(pose);
    }
  }
}
