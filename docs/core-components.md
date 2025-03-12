# Core Components

This page documents the core components that make up Webb-Swerve-Lib's architecture.

## RobotState

`RobotState` is a central singleton class that maintains the robot's pose estimation and provides access to this information throughout the codebase.

### Purpose

`RobotState` serves several critical functions:

1. **Pose Tracking**: Maintains the robot's position and orientation on the field
2. **Odometry Processing**: Processes wheel encoder and gyro data to update the robot's odometry
3. **History Storage**: Maintains a time-indexed buffer of past poses for latency compensation
4. **Velocity Tracking**: Tracks and exposes the robot's velocity in various reference frames

### Key Features

- **Singleton Pattern**: Access the shared instance via `RobotState.getInstance()`
- **Multiple Estimation Sources**: Fuses wheel odometry with gyro data
- **Pose History**: Maintains a buffer of past poses for latency compensation
- **Coordinate Transformations**: Converts between robot-relative and field-relative coordinates

### Usage

```java
// Get the current estimated pose of the robot
Pose2d currentPose = RobotState.getInstance().getEstimatedPose();

// Get the robot's rotation
Rotation2d heading = RobotState.getInstance().getRotation();

// Get robot velocity (robot-relative)
ChassisSpeeds robotVelocity = RobotState.getInstance().getRobotVelocity();

// Get field-relative velocity
ChassisSpeeds fieldVelocity = RobotState.getInstance().getFieldVelocity();

// Reset the robot's position (e.g., at the start of a match)
RobotState.getInstance().resetPose(new Pose2d(1.0, 2.0, Rotation2d.fromDegrees(90.0)));
```

### Key Methods

- **`resetPose(Pose2d pose)`**: Resets the robot's estimated position
- **`addOdometryObservation(OdometryObservation observation)`**: Adds a new odometry measurement
- **`addDriveSpeeds(ChassisSpeeds speeds)`**: Updates the robot's velocity information
- **`getRotation()`**: Gets the current robot heading
- **`periodicLog()`**: Called periodically to log state information

### Customization

When implementing a robot-specific version of Webb-Swerve-Lib, you might extend RobotState to:

1. Add vision-based pose estimation
2. Implement custom filtering algorithms
3. Add game-specific state information
4. Track additional robot metrics

## Other Core Components

### Drive Subsystem

The primary subsystem responsible for controlling the swerve drive modules and implementing the drive commands.

### Module Classes

Classes that represent individual swerve modules and provide interfaces to hardware.

### Trajectory Generation

Components that create, store, and execute motion paths for autonomous routines.

### Kinematics

Classes that handle the mathematical transformations between robot motion and individual module states.
