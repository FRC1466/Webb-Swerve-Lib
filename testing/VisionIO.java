// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
//p ackage org.webbrobotics.frc2025.subsystems.vision;

// import edu.wpi.first.apriltag.AprilTagFieldLayout;
// import edu.wpi.first.math.Matrix;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Transform3d;
// import edu.wpi.first.math.numbers.N1;
// import edu.wpi.first.math.numbers.N3;
// import edu.wpi.first.wpilibj.smartdashboard.Field2d;
// import java.util.List;
// import java.util.Optional;
// import org.photonvision.EstimatedRobotPose;
// import org.photonvision.PhotonCamera;
// import org.photonvision.PhotonPoseEstimator;
// import org.photonvision.targeting.PhotonPipelineResult;
// import org.photonvision.targeting.PhotonTrackedTarget;

// public interface VisionIO {
//   /**
//    * Returns the latest PhotonVision pipeline result.
//    *
//    * @return The PhotonPipelineResult
//    */
//   PhotonPipelineResult getLatestResult();

//   /**
//    * Gets a pose estimation using AprilTags if available.
//    *
//    * @return Optional containing the estimated robot pose, or empty if not available
//    */
//   Optional<EstimatedRobotPose> getEstimatedGlobalPose();

//   /**
//    * Gets the camera name.
//    *
//    * @return The camera name
//    */
//   String getCameraName();

//   /**
//    * Returns the standard deviations for the estimated pose.
//    *
//    * @return Matrix of standard deviations for the pose estimator
//    */
//   default Matrix<N3, N1> getEstimationStdDevs() {
//     return null;
//   }

//   /**
//    * Gets the list of tracked targets from the latest result.
//    *
//    * @return List of tracked targets
//    */
//   default List<PhotonTrackedTarget> getLatestTargets() {
//     return getLatestResult().getTargets();
//   }

//   /** Used in simulation only - updates the simulated camera with the current robot pose. */
//   default void simulationPeriodic(Pose2d robotSimPose) {}

//   /** Used in simulation only - resets the pose history for the simulated camera. */
//   default void resetSimPose(Pose2d pose) {}

//   /** Used in simulation only - gets the debug field for visualization. */
//   default Field2d getSimDebugField() {
//     return null;
//   }
// }

// /** Implementation for PhotonVision camera system */
// class PhotonVisionIO implements VisionIO {
//   private final PhotonCamera camera;
//   private final PhotonPoseEstimator poseEstimator;
//   private final String cameraName;

//   /**
//    * Creates a PhotonVisionIO with the given camera name and robot-to-camera transform.
//    *
//    * @param cameraName The name of the PhotonVision camera
//    * @param robotToCamera The transform from the robot to the camera
//    * @param tagLayout The AprilTag field layout
//    */
//   public PhotonVisionIO(
//       String cameraName, Transform3d robotToCamera, AprilTagFieldLayout tagLayout) {
//     this.cameraName = cameraName;
//     this.camera = new PhotonCamera(cameraName);
//     this.poseEstimator =
//         new PhotonPoseEstimator(
//             tagLayout,
//             PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
//             robotToCamera);
//     this.poseEstimator.setMultiTagFallbackStrategy(
//         PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY);
//   }

//   @Override
//   public PhotonPipelineResult getLatestResult() {
//     return camera.getLatestResult();
//   }

//   @Override
//   public Optional<EstimatedRobotPose> getEstimatedGlobalPose() {
//     return poseEstimator.update(camera.getLatestResult());
//   }

//   @Override
//   public String getCameraName() {
//     return cameraName;
//   }
// }
