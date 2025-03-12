// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;
import org.webbrobotics.frc2025.RobotState;

public class VisionIOSim implements VisionIO {
  private final PhotonCamera camera;
  private final PhotonPoseEstimator poseEstimator;
  private final String cameraName;

  public VisionIOSim(String cameraName, Transform3d robotToCamera, AprilTagFieldLayout tagLayout) {
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

  @Override
  public void simulationPeriodic(Pose2d robotSimPose) {
    // Simulate camera updates based on robot pose
    poseEstimator.setReferencePose(robotSimPose);
  }

  @Override
  public void resetSimPose(Pose2d pose) {
    RobotState robotState = new RobotState();
    // Reset the pose history for the simulated camera
    robotState.resetPose(pose);
  }
}
