// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
// package org.webbrobotics.frc2025.subsystems.vision;

// import edu.wpi.first.apriltag.AprilTagFieldLayout;
// import edu.wpi.first.math.Matrix;
// import edu.wpi.first.math.VecBuilder;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Transform3d;
// import edu.wpi.first.math.numbers.N1;
// import edu.wpi.first.math.numbers.N3;
// import edu.wpi.first.wpilibj.smartdashboard.Field2d;
// import java.util.Optional;
// import org.littletonrobotics.junction.Logger;
// import org.photonvision.EstimatedRobotPose;
// import org.photonvision.PhotonCamera;
// import org.photonvision.PhotonPoseEstimator;
// import org.photonvision.simulation.PhotonCameraSim;
// import org.photonvision.simulation.SimCameraProperties;
// import org.photonvision.simulation.VisionSystemSim;
// import org.photonvision.targeting.PhotonPipelineResult;

// public class VisionIOSim implements VisionIO {
//   private final PhotonCamera camera;
//   private final PhotonPoseEstimator poseEstimator;
//   private final String cameraName;

//   // Simulation components
//   private final PhotonCameraSim cameraSim;
//   private final VisionSystemSim visionSim;

//   // Standard deviations for pose estimation
//   private Matrix<N3, N1> currentStdDevs = VecBuilder.fill(4, 4, 8); // Default to single-tag
// values

//   public VisionIOSim(String cameraName, Transform3d robotToCamera, AprilTagFieldLayout tagLayout)
// {
//     this.cameraName = cameraName;
//     this.camera = new PhotonCamera(cameraName);
//     this.poseEstimator =
//         new PhotonPoseEstimator(
//             tagLayout,
//             PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
//             robotToCamera);
//     this.poseEstimator.setMultiTagFallbackStrategy(
//         PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY);

//     // Initialize simulation components
//     visionSim = new VisionSystemSim(cameraName);
//     visionSim.addAprilTags(tagLayout);

//     // Create camera properties for simulation
//     var cameraProp = new SimCameraProperties();
//     cameraProp.setCalibration(960, 720, Rotation2d.fromDegrees(90));
//     cameraProp.setCalibError(0.35, 0.10);
//     cameraProp.setFPS(15);
//     cameraProp.setAvgLatencyMs(50);
//     cameraProp.setLatencyStdDevMs(15);

//     // Create camera simulation
//     cameraSim = new PhotonCameraSim(camera, cameraProp);
//     visionSim.addCamera(cameraSim, robotToCamera);

//     // Enable debug wireframe to see the camera field of view
//     cameraSim.enableDrawWireframe(true);

//     // Log initialization
//     Logger.recordOutput("Vision/Sim/" + cameraName + "/Initialized", true);
//   }

//   @Override
//   public PhotonPipelineResult getLatestResult() {
//     return camera.getLatestResult();
//   }

//   @Override
//   public Optional<EstimatedRobotPose> getEstimatedGlobalPose() {
//     var result = camera.getLatestResult();
//     var estimation = poseEstimator.update(result);

//     // Update standard deviations based on number of tags
//     if (estimation.isPresent()) {
//       if (result.getTargets().size() > 1) {
//         // Multi-tag detection gives better accuracy
//         currentStdDevs = VecBuilder.fill(2, 2, 4); // Multi-tag std devs
//       } else {
//         // Single tag detection
//         currentStdDevs = VecBuilder.fill(4, 4, 8); // Single-tag std devs
//       }
//     }

//     return estimation;
//   }

//   @Override
//   public Matrix<N3, N1> getEstimationStdDevs() {
//     return currentStdDevs;
//   }

//   @Override
//   public String getCameraName() {
//     return cameraName;
//   }

//   @Override
//   public void simulationPeriodic(Pose2d robotSimPose) {
//     if (visionSim != null) {
//       // Update the vision simulation with the current robot pose
//       visionSim.update(robotSimPose);

//       // Log that the simulation was updated and with what pose
//       Logger.recordOutput("Vision/Sim/" + cameraName + "/UpdatedWithPose", robotSimPose);

//       // Log whether the camera can see any targets
//       Logger.recordOutput(
//           "Vision/Sim/" + cameraName + "/SeesTargets", camera.getLatestResult().hasTargets());

//       // If there are targets, log how many
//       if (camera.getLatestResult().hasTargets()) {
//         Logger.recordOutput(
//             "Vision/Sim/" + cameraName + "/TargetCount",
//             camera.getLatestResult().getTargets().size());
//       }
//     }
//   }

//   @Override
//   public void resetSimPose(Pose2d pose) {
//     if (visionSim != null) {
//       visionSim.resetRobotPose(pose);
//     }
//   }

//   @Override
//   public Field2d getSimDebugField() {
//     return visionSim.getDebugField();
//   }
// }
