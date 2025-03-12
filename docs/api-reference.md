# API Reference

This page provides detailed documentation for the key classes and interfaces in Webb-Swerve-Lib. The library is built with a focus on making swerve drive implementation accessible while offering advanced features for experienced teams.

## Core Classes

### `Drive`

The main swerve drive subsystem that coordinates all drive-related functionality. This class brings together the gyroscope and individual swerve modules to provide holistic control over the drivetrain.

**Class Structure:**
```java
// Run the drive with the specified ChassisSpeed
public void runVelocity(ChassisSpeeds speeds)

// Run the drive with the specified ChassisSpeed and provide module forces for feedforward
public void runVelocity(ChassisSpeeds chassisSpeeds, List<Vector2d> moduleForces)

// Stop all modules
public void stop()

// Set the robot pose (used for odometry resets)
public void setPose(Pose2d pose)

// Get the current estimated pose
public Pose2d getPose()
```

### `Module`

Represents a single swerve module in the drivetrain.

**Key Methods:**
```java
// Run the module with the specified state (speed and angle)
public void runSetpoint(SwerveModuleState state)

// Get the current module state
public SwerveModuleState getState()

// Set whether the module should brake or coast
public void setBrakeMode(boolean brake)
```

### `HolonomicTrajectory`

Represents a generated trajectory for the robot to follow.

**Key Methods:**
```java
// Create a trajectory from a file in the deploy directory
public HolonomicTrajectory(String name)

// Get the duration of this trajectory in seconds
public double getDuration()

// Get the starting pose of the trajectory
public Pose2d getStartPose()

// Get all poses along the trajectory
public Pose2d[] getTrajectoryPoses()

// Get the vehicle state at the specified time
public VehicleState sample(double timeSeconds)

// Get the starting state
public VehicleState getStartState()

// Get the ending state
public VehicleState getEndState()
```

### `DriveTrajectory`

Command that follows a trajectory using closed-loop control.

**Key Methods:**
```java
// Create a trajectory following command
public DriveTrajectory(Drive drive, HolonomicTrajectory trajectory)

// Create a trajectory following command with alliance mirroring option
public DriveTrajectory(Drive drive, HolonomicTrajectory trajectory, boolean mirror)

// Create a trajectory following command with a custom pose supplier
public DriveTrajectory(Drive drive, HolonomicTrajectory trajectory, Supplier<Pose2d> robot, boolean mirror)

// Override the rotation to face a specific direction
public void setOverrideRotation(Optional<Rotation2d> rotation)
```

## Helper and Utility Classes

### `TrajectoryGenerationHelpers`

Contains helper methods for creating trajectories.

**Key Methods:**
```java
// Convert a VehicleState to a Pose2d
public static Pose2d getPose(VehicleState state)

// Convert a Waypoint to a Pose2d
public static Pose2d getPose(Waypoint waypoint)

// Create a velocity constraint from a VehicleState
public static VehicleVelocityConstraint createVelocityConstraint(VehicleState state)

// Get the end velocity constraint from a trajectory
public static VehicleVelocityConstraint endVelocityConstraint(Trajectory trajectory)

// Add a continuation waypoint from a previous trajectory
public static PathSegment.Builder addContinuationWaypoint(PathSegment.Builder builder, Trajectory trajectory)

// Add a pose waypoint to a PathSegment.Builder
public static PathSegment.Builder addPoseWaypoint(PathSegment.Builder builder, Pose2d pose)

// Add a translation waypoint to a PathSegment.Builder
public static PathSegment.Builder addTranslationWaypoint(PathSegment.Builder builder, Translation2d translation)

// Set a pose on a Waypoint.Builder
public static Waypoint.Builder withPose(Waypoint.Builder builder, Pose2d pose)

// Set a linear velocity on a Waypoint.Builder
public static Waypoint.Builder withLinearVelocity(Waypoint.Builder builder, Translation2d linearVelocity)
```

### `DriveTrajectories`

Contains predefined trajectories for the robot to follow.

**Key Fields and Methods:**
```java
// Map of all defined trajectory paths
public static final Map<String, List<PathSegment>> paths

// List of functions that supply dependent paths
public static final List<Function<Set<String>, Map<String, List<PathSegment>>>> suppliedPaths

// Get the last waypoint from a trajectory path
public static Waypoint getLastWaypoint(String trajectoryName)
```

### `DriveConstants`

Contains constants related to the drive subsystem.

**Key Fields:**
```java
// Physical dimensions
public static final double trackWidthX
public static final double trackWidthY
public static final double driveBaseRadius

// Performance limits
public static final double maxLinearSpeed
public static final double maxAngularSpeed

// Module positions
public static final Translation2d[] moduleTranslations

// Module configurations
public static final ModuleConfig[] moduleConfigs
```

### `ModuleConfig`

Configuration for a single swerve module.

**Fields:**
```java
int driveMotorId         // CAN ID for the drive motor
int turnMotorId          // CAN ID for the turn motor
int encoderChannel       // DIO channel or CAN ID for the absolute encoder
Rotation2d encoderOffset // Offset to apply to the encoder reading
boolean turnInverted     // Whether the turn motor is inverted
boolean encoderInverted  // Whether the encoder is inverted
```

### `GyroIO` Interface

Interface for the gyroscope input/output.

**Key Methods:**
```java
// Update the gyro inputs (to be implemented by specific hardware)
public void updateInputs(GyroIOInputs inputs)

// Zero the gyro (set current heading to zero)
public default void zeroGyro() {}
```

### `ModuleIO` Interface

Interface for swerve module input/output.

**Key Methods:**
```java
// Update the module inputs (to be implemented by specific hardware)
public void updateInputs(ModuleIOInputs inputs)

// Set the target state for the module
public default void setTargetState(SwerveModuleState targetState, boolean isOpenLoop) {}

// Set whether the module should brake or coast
public default void setBrakeMode(boolean brake) {}
```

## Protocol Buffer Classes

### `VehicleTrajectoryServiceOuterClass.VehicleState`

Represents the state of the robot at a point in time.

**Key Fields:**
```java
double x      // X position
double y      // Y position
double theta  // Rotation in radians
double vx     // X velocity
double vy     // Y velocity
double omega  // Angular velocity
List<ModuleForce> moduleForces  // Forces at each module
```

### `VehicleTrajectoryServiceOuterClass.Trajectory`

Contains a series of timestamped vehicle states.

**Key Fields and Methods:**
```java
List<TimestampedVehicleState> states  // States along the trajectory
String hashCode                        // Hash code for the trajectory

// Get the number of states
int getStatesCount()

// Get a specific state
TimestampedVehicleState getStates(int index)
```

### `VehicleTrajectoryServiceOuterClass.PathSegment`

Represents a segment of a path.

**Key Fields and Methods:**
```java
List<Waypoint> waypoints  // Waypoints defining this segment
double maxVelocity        // Maximum velocity for this segment
double maxOmega           // Maximum angular velocity for this segment
boolean straightLine      // Whether this segment is a straight line

// Builder for creating path segments
static Builder newBuilder()
```

### `VehicleTrajectoryServiceOuterClass.Waypoint`

Represents a point along a trajectory.

**Key Fields and Methods:**
```java
double x                     // X position
double y                     // Y position
double headingConstraint     // Target heading in radians
VehicleVelocityConstraint vehicleVelocity  // Velocity constraint

// Builder for creating waypoints
static Builder newBuilder()
```

## I/O Implementation Classes

### `GyroIOPigeon2`

Pigeon2 IMU implementation of GyroIO.

**Key Methods:**
```java
// Constructor with Pigeon2 CAN ID
public GyroIOPigeon2()

// Update gyro inputs from the Pigeon2
@Override
public void updateInputs(GyroIOInputs inputs)

// Zero the gyro
@Override
public void zeroGyro()
```

### `ModuleIOComp`

Competition robot implementation of ModuleIO.

**Key Methods:**
```java
// Constructor with module configuration
public ModuleIOComp(DriveConstants.ModuleConfig config)

// Update module inputs from hardware
@Override
public void updateInputs(ModuleIOInputs inputs)

// Set the target state for the module
@Override
public void setTargetState(SwerveModuleState targetState, boolean isOpenLoop)

// Set whether the module should brake or coast
@Override
public void setBrakeMode(boolean brake)
```

## Next Steps

For more detailed information about specific methods and their implementation, refer to the source code documentation. If you encounter issues while using these classes, check the [troubleshooting guide](troubleshooting.md).