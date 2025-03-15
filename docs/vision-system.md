# Vision System

## Overview
The vision system in Webb-Swerve-Lib uses [PhotonVision](https://docs.photonvision.org/en/v2025.2.1/) to detect AprilTags on the field and estimate the robot's pose. This system is crucial for accurate autonomous navigation and alignment tasks.

## Key Components
- **PhotonCamera**: Captures images and detects AprilTags.
- **PhotonPoseEstimator**: Estimates the robot's pose based on detected tags.
- **VisionSystemSim**: Simulates the vision system for testing and development.
- **Field2d**: Visualizes the robot and detected tags on a field diagram.

## Vision Code Details
The vision code is implemented in the `Vision` class, which handles camera initialization, pose estimation, and logging. Key methods include:
- `getEstimatedGlobalPose()`: Returns the latest estimated robot pose based on vision data.
- `updateEstimationStdDevs()`: Calculates dynamic standard deviations for pose estimation based on the number of detected tags and their distances.
- `logCameraData()`: Logs data from individual cameras, including detected tag IDs and poses.
- `logSeenAprilTags()`: Logs all AprilTags that have been seen at least once during the match.

## Example Usage
To use the vision system, create an instance of the `Vision` class and call its methods in your robot code. For example:
```java
Vision vision = new Vision();
Optional<EstimatedRobotPose> pose = vision.getEstimatedGlobalPose();
pose.ifPresent(p -> {
    // Use the estimated pose for navigation or alignment
    System.out.println("Estimated Pose: " + p.estimatedPose);
});
```

## Simulation
The vision system can be simulated using the `VisionSystemSim` class. This allows you to test your vision code without a physical robot. To enable simulation, call the `simulationPeriodic()` method with the simulated robot pose:
```java
vision.simulationPeriodic(simulatedPose);
```
