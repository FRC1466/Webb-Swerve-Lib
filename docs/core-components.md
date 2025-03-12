# Core Components

This page documents the core components that make up Webb-Swerve-Lib's architecture. Understanding these components is essential for effective use, maintenance, and troubleshooting.

## System Architecture Overview

Webb-Swerve-Lib uses a layered architecture based on the Command-Based programming paradigm and AdvantageKit principles:

![Architecture Diagram](https://via.placeholder.com/750x400?text=Webb-Swerve-Lib+Architecture)

1. **Hardware Abstraction Layer**: Isolates hardware-specific code
2. **Core Components Layer**: Provides the fundamental functionality
3. **Control Layer**: Handles robot control algorithms
4. **Command Layer**: Implements high-level robot behaviors

### Dependency Flow

```
Commands → Subsystems → Hardware Interfaces → Physical Hardware
```

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
Rotation2d rotation = RobotState.getInstance().getHeading();

// Get the robot's velocity in field-relative coordinates
ChassisSpeeds fieldRelativeSpeeds = RobotState.getInstance().getFieldRelativeSpeeds();

// Reset the robot's position (e.g., at the start of a match)
RobotState.getInstance().resetPose(new Pose2d(1.5, 3.0, Rotation2d.fromDegrees(180)));
```

### Implementation Details

`RobotState` uses a thread-safe design with locks to prevent race conditions when updating pose information. It incorporates:

- **Gyroscope Integration**: Uses gyro data for heading information
- **Wheel Odometry**: Uses module positions for translational movement
- **Estimation Filter**: Combines multiple sources for improved accuracy
- **Timestamps**: All measurements are time-stamped for proper synchronization

## Drive Subsystem

The `Drive` subsystem is the main interface for controlling the swerve drivetrain.

### Responsibilities

- Managing swerve modules
- Processing driver inputs
- Executing autonomous trajectories
- Handling odometry updates
- Managing motor control modes (brake/coast)

### Key Components

1. **Modules**: Array of swerve modules (typically 4)
2. **Kinematics**: SwerveDriveKinematics for coordinate transformations
3. **Setpoint Generator**: Creates optimized module states
4. **Gyroscope**: Provides heading information

### Important Methods

```java
// Control methods
public void runVelocity(ChassisSpeeds speeds)
public void stop()

// State methods
public Pose2d getPose()
public void setPose(Pose2d pose)
public SwerveModuleState[] getModuleStates()

// Configuration
public void setBrakeMode(boolean enabled)
```

### Under the Hood

The Drive subsystem manages several complex behaviors:

1. **Module Optimization**: Prevents modules from rotating more than necessary
2. **Cosine Compensation**: Adjusts wheel speeds during transitions
3. **Field-Relative Transformations**: Converts driver inputs to field coordinates
4. **Acceleration Management**: Smooths acceleration to prevent tipping

## Module

The `Module` class represents a single swerve module and handles the control of both the drive and steering motors.

### Responsibilities

- Controlling drive motor velocity
- Positioning steering motor
- Reading encoder values
- Providing module state information

### Structure

Each module has:

1. `ModuleIO`: Hardware abstraction interface
2. `ModuleIOInputsAutoLogged`: Input values from sensors
3. Control algorithms for both motors
4. State tracking and reporting

### Key Methods

```java
// Set the desired module state (speed and angle)
public void runSetpoint(SwerveModuleState state)

// Get the current module position
public SwerveModulePosition getPosition()

// Get the current module state
public SwerveModuleState getState()
```

## Phoenix Odometry Thread

A specialized thread for processing high-frequency odometry updates from Phoenix motor controllers.

### Purpose

- Process encoder readings at up to 1kHz
- Reduce main loop overhead
- Improve odometry accuracy
- Synchronize timestamps across the system

### Design Considerations

- Uses a separate thread to minimize impact on the main robot loop
- Incorporates safety mechanisms to handle disconnected/failed hardware
- Carefully manages synchronization with the main thread

## Trajectory Generation Components

The trajectory system comprises several parts working together:

1. **PathSegment**: Defines waypoints and constraints for a path segment
2. **HolonomicTrajectory**: Complete trajectory object with timing information
3. **DriveTrajectory**: Command for following a trajectory
4. **TrajectoryManager**: Loads and manages available trajectories

## Common Patterns Used

Webb-Swerve-Lib uses several design patterns to maintain code quality:

1. **Singleton Pattern**: Used for global access to shared resources (RobotState)
2. **Command Pattern**: For encapsulating robot behaviors
3. **Strategy Pattern**: For different control algorithms
4. **Observer Pattern**: For event handling and state updates
5. **Factory Pattern**: For creating complex objects

## Thread Safety Considerations

The library is designed to be thread-safe in critical areas:

- **Locks**: Used for protected access to shared state
- **Atomic Operations**: Used for thread-safe counter increments
- **Synchronized Methods**: Used for methods that must execute exclusively
- **Thread Management**: Careful control of background threads

## Next Steps

- Learn about the [module configuration](module-configuration.md)
- Understand how to [create and use trajectories](creating-trajectories.md)
- Explore [autonomous programming](autonomous-programming.md)
