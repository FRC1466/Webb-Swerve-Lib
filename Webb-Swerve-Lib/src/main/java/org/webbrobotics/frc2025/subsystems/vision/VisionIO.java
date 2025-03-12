// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Transform3d;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;

public interface VisionIO {
  /**
   * Returns the latest PhotonVision pipeline result.
   *
   * @return The PhotonPipelineResult
   */
  PhotonPipelineResult getLatestResult();

  /**
   * Gets a pose estimation using AprilTags if available.
   *
   * @return Optional containing the estimated robot pose, or empty if not available
   */
  Optional<EstimatedRobotPose> getEstimatedGlobalPose();

  /**
   * Gets the camera name.
   *
   * @return The camera name
   */
  String getCameraName();

  /** Used in simulation only - updates the simulated camera with the current robot pose. */
  default void simulationPeriodic(edu.wpi.first.math.geometry.Pose2d robotSimPose) {}

  /** Used in simulation only - resets the pose history for the simulated camera. */
  default void resetSimPose(edu.wpi.first.math.geometry.Pose2d pose) {}
}

/** Implementation for PhotonVision camera system */
class PhotonVisionIO implements VisionIO {
  private final PhotonCamera camera;
  private final PhotonPoseEstimator poseEstimator;
  private final String cameraName;

  /**
   * Creates a PhotonVisionIO with the given camera name and robot-to-camera transform.
   *
   * @param cameraName The name of the PhotonVision camera
   * @param robotToCamera The transform from the robot to the camera
   * @param tagLayout The AprilTag field layout
   */
  public PhotonVisionIO(
      String cameraName, Transform3d robotToCamera, AprilTagFieldLayout tagLayout) {
    this.cameraName = cameraName;
    this.camera = new PhotonCamera(cameraName);
    this.poseEstimator =
        new PhotonPoseEstimator(
            tagLayout,
            PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
            robotToCamera);
    this.poseEstimator.setMultiTagFallbackStrategy(
        PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY);
  }

  @Override
  public PhotonPipelineResult getLatestResult() {
    return camera.getLatestResult();
  }

  @Override
  public Optional<EstimatedRobotPose> getEstimatedGlobalPose() {
    return poseEstimator.update(camera.getLatestResult());
  }

  @Override
  public String getCameraName() {
    return cameraName;
  }
}
