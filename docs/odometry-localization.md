# Odometry and Localization

This guide explains how Webb-Swerve-Lib tracks robot position and orientation using odometry and sensor fusion techniques.

## Understanding Robot Positioning

In FRC competitions, knowing the robot's precise position on the field is crucial for:

- Following autonomous trajectories
- Lining up with game elements
- Navigation relative to field features
- Coordinating with alliance partners

## Odometry Basics

Odometry is the process of estimating position changes by measuring wheel movement and rotation. Webb-Swerve-Lib uses several methods to achieve accurate position tracking:

### Wheel-Based Odometry

The primary odometry system tracks position by measuring individual wheel movements:

1. **Module Position Tracking**: Each swerve module reports its distance traveled and direction
2. **Kinematics**: The SwerveDriveKinematics class converts module movements to robot movement
3. **Integration**: Small movements are added up over time to calculate total displacement

### Gyroscope Integration

A gyroscope provides critical heading information:

1. **Orientation Tracking**: The gyro measures robot rotation around the vertical axis
2. **Drift Compensation**: The gyro's absolute heading reduces cumulative error in rotation estimates
3. **Continuous Updating**: Heading information is updated at a high frequency (typically 200+ Hz)

## The RobotState Class

The `RobotState` class is the central component for position tracking in Webb-Swerve-Lib:

### Key Features

- **Singleton Pattern**: Accessible from anywhere via `RobotState.getInstance()`
- **Pose History**: Maintains a buffer of past poses with timestamps
- **High-Frequency Updates**: Processes odometry updates at up to 1kHz
- **Sensor Fusion**: Combines wheel odometry, gyro, and optional vision data
- **Thread-Safe Design**: Uses locks to prevent race conditions

### Core Methods

```java
// Get the current pose estimation
public Pose2d getEstimatedPose()

// Get robot velocity in various frames
public ChassisSpeeds getRobotRelativeSpeeds()
public ChassisSpeeds getFieldRelativeSpeeds()

// Reset pose estimation (e.g., at the start of a match)
public void resetPose(Pose2d pose)

// Add vision measurements (if vision system available)
public void addVisionMeasurement(Pose2d visionMeasurement, double timestampSeconds)
```

## Odometry Implementation

Webb-Swerve-Lib uses a multi-pronged approach to odometry:

### Phoenix Odometry Thread

For robots using Phoenix motor controllers, a dedicated high-frequency odometry thread is used:

```java
public class PhoenixOdometryThread extends Thread {
    // Singleton instance
    private static PhoenixOdometryThread instance;
    
    // Get the singleton instance
    public static PhoenixOdometryThread getInstance() {
        if (instance == null) {
            instance = new PhoenixOdometryThread();
        }
        return instance;
    }
    
    @Override
    public void run() {
        // Process odometry at up to 1kHz
        // This reduces main loop overhead and improves accuracy
    }
}
```

### Pose Estimation Process

The overall process for estimating the robot's pose includes:

1. **Module Position Collection**: Gather current positions from all modules
2. **Delta Calculation**: Determine change in position since last update
3. **Gyro Integration**: Incorporate gyro data for heading
4. **Pose Update**: Update the estimated pose based on the gathered data
5. **History Storage**: Store the new pose in the pose history buffer

## Enhancing Accuracy with Vision

For higher accuracy, Webb-Swerve-Lib can integrate vision measurements:

### Vision Integration Process

1. **Vision Measurements**: An external vision system (like PhotonVision) provides pose estimates
2. **Timestamp Correlation**: Vision data is tagged with a timestamp for proper integration
3. **Confidence Weighting**: Vision updates are weighted based on confidence or distance
4. **Filter Fusion**: A filter combines vision and odometry data for improved estimates

### Example Vision Integration

```java
// In your vision subsystem or periodic code
public void processVisionData() {
    if (hasValidTarget()) {
        // Get the pose estimation from vision system
        Pose2d visionPose = calculateVisionPose();
        
        // Add the measurement to RobotState with the correct timestamp
        RobotState.getInstance().addVisionMeasurement(visionPose, Timer.getFPGATimestamp());
    }
}
```

## Understanding Odometry Error Sources

Several factors can affect odometry accuracy:

### Common Error Sources

1. **Wheel Slippage**: Wheels sliding or losing traction
2. **Gyro Drift**: Small errors in gyro measurements accumulating over time
3. **Encoder Resolution**: Limited precision in encoder measurements
4. **Mechanical Factors**: Flexing in the drivetrain, uneven floors, etc.
5. **Integration Errors**: Small mathematical errors compounding over time

### Mitigating Errors

Webb-Swerve-Lib employs several strategies to reduce odometry errors:

1. **High Update Rate**: More frequent updates reduce integration error
2. **Multi-Sensor Fusion**: Combining different sensor inputs improves accuracy
3. **Vision Corrections**: Periodic absolute position updates from vision
4. **Kalman Filtering**: Statistical techniques to reduce noise and uncertainty

## Odometry Configuration and Tuning

Proper configuration is essential for accurate odometry:

### Key Configuration Parameters

```java
// In DriveConstants.java
// Standard deviations for odometry state (position and heading)
public static final Matrix<N3, N1> stateStdDevs = VecBuilder.fill(0.1, 0.1, 0.1);

// Standard deviations for vision measurements
public static final Matrix<N3, N1> visionMeasurementStdDevs = VecBuilder.fill(0.9, 0.9, 0.9);

// Update frequency settings
public static final double odometryUpdateFrequency = 250.0; // Hz
```

### Tuning Process

1. **Start Conservative**: Begin with higher standard deviations for vision
2. **Test Vision Reliability**: Evaluate the consistency of vision measurements
3. **Adjust Trust Levels**: Lower vision standard deviations if vision is reliable
4. **Verify with Known Positions**: Test odometry against known field positions

## Visualizing and Debugging Odometry

Webb-Swerve-Lib provides tools to visualize odometry performance:

### Using AdvantageScope

AdvantageScope offers powerful visualization options:

1. **Field View**: Display robot position on a field diagram
2. **Pose History**: Track the robot's path over time
3. **Comparison Plot**: Compare estimated vs. actual positions

### Logging for Analysis

```java
// In your Drive subsystem
public void periodic() {
    // Log estimated pose for visualization
    Logger.recordOutput("Odometry/RobotPose", RobotState.getInstance().getEstimatedPose());
    
    // Log raw odometry inputs for debugging
    Logger.recordOutput("Odometry/ModulePositions", getModulePositions());
    Logger.recordOutput("Odometry/GyroYaw", getGyroYaw());
    
    // If using vision, log vision measurements too
    Logger.recordOutput("Odometry/VisionPose", getLatestVisionPose());
}
```

## Common Odometry Issues and Solutions

| Issue | Symptoms | Solutions |
|-------|----------|----------|
| **Drift Over Time** | Position estimate gradually becomes inaccurate | Add vision integration, check for wheel slippage, verify encoder configuration |
| **Heading Issues** | Robot orientation incorrect while position is fine | Check gyro calibration, ensure proper mounting orientation |
| **Jumps in Position** | Sudden changes in estimated position | Filter vision data more aggressively, check for sensor noise |
| **Inconsistent Tracking** | Works well sometimes but fails other times | Look for environmental factors like lighting or floor surface changes |

## Best Practices

1. **Reset Odometry at Start**: Always reset odometry at the beginning of a match to a known position
2. **Calibrate Before Competition**: Ensure all sensors are calibrated before each competition
3. **Use Multiple Reference Points**: If using vision, track multiple targets when possible
4. **Log Everything**: Record odometry data for post-match analysis
5. **Regular Validation**: Periodically check odometry against known positions

## Advanced Techniques

### Fusing Multiple Pose Sources

For highest accuracy, fuse multiple pose estimation sources:

```java
// Multiple vision sources example (AprilTags + retroreflective tape)
if (hasAprilTagTarget()) {
    Pose2d aprilTagPose = calculateAprilTagPose();
    // Higher confidence in AprilTag measurements
    RobotState.getInstance().addVisionMeasurement(
        aprilTagPose, 
        Timer.getFPGATimestamp(),
        VecBuilder.fill(0.5, 0.5, 0.8) // Lower standard deviations = higher confidence
    );
} else if (hasRetroreflectiveTarget()) {
    Pose2d retroPose = calculateRetroPose();
    // Lower confidence in retroreflective measurements
    RobotState.getInstance().addVisionMeasurement(
        retroPose, 
        Timer.getFPGATimestamp(),
        VecBuilder.fill(1.2, 1.2, 1.5) // Higher standard deviations = lower confidence
    );
}
```

### Latency Compensation

Compensate for sensor and processing delays:

```java
// Vision system with known latency
double latencySeconds = 0.05; // 50ms latency
Pose2d visionPose = calculateVisionPose();
double timestamp = Timer.getFPGATimestamp() - latencySeconds;
RobotState.getInstance().addVisionMeasurement(visionPose, timestamp);
```

## Next Steps

- Learn about [autonomous programming](autonomous-programming.md)
- Understand [trajectory following](creating-trajectories.md)
- Check [common issues](common-issues.md) for troubleshooting odometry problems