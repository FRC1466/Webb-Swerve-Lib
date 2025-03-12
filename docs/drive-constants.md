# Drive Constants

This guide explains the essential drive configuration parameters in Webb-Swerve-Lib that affect the overall behavior of your swerve drive system.

## Overview

Drive constants define the fundamental behavior of your swerve drive, including:

- Physical dimensions and layout
- Performance limits
- Control parameters
- Field-relative behavior
- Special operation modes

These constants are typically defined in a `DriveConstants` class within your robot project.

## Basic Configuration

### Physical Layout Constants

```java
public final class DriveConstants {
    // Robot physical dimensions
    public static final double trackWidthX = 0.62; // Width between left and right wheels (meters)
    public static final double trackWidthY = 0.62; // Length between front and back wheels (meters)
    
    // Module positions (from robot center)
    public static final Translation2d[] moduleTranslations = new Translation2d[] {
        new Translation2d(trackWidthX / 2.0, trackWidthY / 2.0), // Front Left
        new Translation2d(trackWidthX / 2.0, -trackWidthY / 2.0), // Front Right
        new Translation2d(-trackWidthX / 2.0, trackWidthY / 2.0), // Back Left
        new Translation2d(-trackWidthX / 2.0, -trackWidthY / 2.0) // Back Right
    };
}
```

### Robot Performance Limits

Define the maximum capabilities and limits of your drive system:

```java
// Maximum speed and acceleration profiles
public static final ModuleConstraints moduleLimitsFree = new ModuleConstraints(
    4.5, // Max linear velocity (m/s)
    3.0, // Max linear acceleration (m/s²)
    Math.PI * 8.0, // Max angular velocity (rad/s) - full rotation in 0.25 seconds
    Math.PI * 8.0 // Max angular acceleration (rad/s²)
);

// Precision/slow mode for careful operations
public static final ModuleConstraints moduleLimitsPrecision = new ModuleConstraints(
    2.0, // Reduced velocity for precision (m/s)
    1.5, // Reduced acceleration (m/s²)
    Math.PI * 4.0, // Reduced rotation rate (rad/s)
    Math.PI * 4.0  // Reduced angular acceleration (rad/s²)
);

// Auto mode settings - typically more conservative
public static final ModuleConstraints moduleLimitsAuto = new ModuleConstraints(
    3.0, // Auto velocity (m/s)
    2.0, // Auto acceleration (m/s²)
    Math.PI * 6.0, // Auto angular velocity (rad/s)
    Math.PI * 6.0  // Auto angular acceleration (rad/s²)
);
```

## Control Parameters

These constants determine how the robot responds to driver inputs:

```java
// Joystick input scaling
public static final double kMaxSpeedMetersPerSecond = 4.5; // Maximum speed from full joystick
public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI * 2; // Maximum rotation speed

// Rate limiting to prevent tipping
public static final double kDirectionSlewRate = 1.2; // Units/s
public static final double kMagnitudeSlewRate = 1.8; // Units/s
public static final double kRotationalSlewRate = 2.0; // Units/s

// Deadband for ignoring small joystick inputs
public static final double kJoystickDeadband = 0.1; // From 0.0 to 1.0

// PID gains for trajectory following
public static final double kPTranslation = 5.0;
public static final double kITranslation = 0.0;
public static final double kDTranslation = 0.0;

public static final double kPRotation = 5.0;
public static final double kIRotation = 0.0;
public static final double kDRotation = 0.1;
```

## Field-Relative Configuration

These constants define how the robot aligns with and navigates the field:

```java
// Field dimensions for the current game
// Note: These are just placeholders - update with actual field dimensions
public static final double fieldLength = 16.54; // meters
public static final double fieldWidth = 8.02; // meters

// Default starting position for autonomous
public static final Pose2d defaultStartingPose = new Pose2d(1.5, 5.0, Rotation2d.fromDegrees(180.0));

// Alliance-specific settings
public enum Alliance {
    BLUE,
    RED
}

// Flip coordinates based on alliance color
public static Pose2d allianceFlip(Pose2d pose, Alliance alliance) {
    if (alliance == Alliance.BLUE) {
        return pose;
    } else {
        // For red alliance, mirror across field center X
        return new Pose2d(
            fieldLength - pose.getX(),
            pose.getY(),
            Rotation2d.fromRadians(Math.PI - pose.getRotation().getRadians())
        );
    }
}
```

## Special Operation Modes

Configure special operation modes for different driving scenarios:

```java
// X-stance (wheels form an X to resist movement)
public static final SwerveModuleState[] xStanceStates = new SwerveModuleState[] {
    new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
    new SwerveModuleState(0, Rotation2d.fromDegrees(135)),
    new SwerveModuleState(0, Rotation2d.fromDegrees(135)),
    new SwerveModuleState(0, Rotation2d.fromDegrees(45))
};

// Auto-balance parameters (for balancing on charging station)
public static final double balancePitchThresholdDegrees = 2.5; // When to consider balanced
public static final double balanceSpeedFactor = 0.6; // Max speed during balance (fraction)
public static final double balanceGain = 0.03; // Proportional gain for balancing
```

## Module-Specific Constants

While individual module constants are defined in `ModuleConstants`, you can include references here:

```java
// CANCoder offsets (if using Phoenix hardware)
public static final double[] moduleOffsetDegrees = {
    237.1, // Front Left
    126.9, // Front Right
    183.9, // Back Left
    271.1  // Back Right
};

// Module positions for kinematics
public static final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
    moduleTranslations
);
```

## Odometry Configuration

Constants for robot pose estimation:

```java
// Vision parameters
public static final Matrix<N3, N1> stateStdDevs = VecBuilder.fill(0.1, 0.1, 0.1);
public static final Matrix<N3, N1> visionMeasurementStdDevs = VecBuilder.fill(0.9, 0.9, 0.9);

// Odometry update rate
public static final double odometryUpdateFrequency = 250.0; // Hz
```

## Choosing Appropriate Values

### Velocity and Acceleration

When setting velocity and acceleration limits, consider:

1. **Robot Weight**: Heavier robots need lower acceleration limits
2. **Drive Power**: Motor power affects maximum achievable speed
3. **Wheel Traction**: Higher acceleration requires more traction
4. **Competition Rules**: Speed limits may be dictated by game rules

### General Guidelines

| Robot Weight | Recommended Max Velocity | Recommended Max Acceleration |
|--------------|-------------------------|----------------------------|
| Light (<50 lbs) | 4.5-5.0 m/s | 3.0-4.0 m/s² |
| Medium (50-100 lbs) | 3.5-4.5 m/s | 2.0-3.0 m/s² |
| Heavy (100+ lbs) | 2.5-3.5 m/s | 1.5-2.0 m/s² |

### PID Tuning Baselines

Start with these values and tune based on your robot's performance:

| Parameter | Conservative Start | Typical Range |
|-----------|-------------------|---------------|
| Translation P | 1.0 | 1.0-7.0 |
| Translation D | 0.0 | 0.0-0.5 |
| Rotation P | 2.0 | 2.0-7.0 |
| Rotation D | 0.1 | 0.0-1.0 |

## Example Drive Constants Class

Here's a complete example of a `DriveConstants` class:

```java
package org.webbrobotics.frc2025.subsystems.drive;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public final class DriveConstants {
    // Physical layout
    public static final double trackWidthX = 0.62;
    public static final double trackWidthY = 0.62;
    
    public static final Translation2d[] moduleTranslations = new Translation2d[] {
        new Translation2d(trackWidthX / 2.0, trackWidthY / 2.0),
        new Translation2d(trackWidthX / 2.0, -trackWidthY / 2.0),
        new Translation2d(-trackWidthX / 2.0, trackWidthY / 2.0),
        new Translation2d(-trackWidthX / 2.0, -trackWidthY / 2.0)
    };
    
    // Performance limits
    public static final ModuleConstraints moduleLimitsFree = new ModuleConstraints(
        4.5, 3.0, Math.PI * 8.0, Math.PI * 8.0
    );
    
    public static final ModuleConstraints moduleLimitsPrecision = new ModuleConstraints(
        2.0, 1.5, Math.PI * 4.0, Math.PI * 4.0
    );
    
    // Control parameters
    public static final double kMaxSpeedMetersPerSecond = 4.5;
    public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI * 2;
    
    public static final double kDirectionSlewRate = 1.2;
    public static final double kMagnitudeSlewRate = 1.8;
    public static final double kRotationalSlewRate = 2.0;
    
    public static final double kJoystickDeadband = 0.1;
    
    // PID values
    public static final double kPTranslation = 5.0;
    public static final double kITranslation = 0.0;
    public static final double kDTranslation = 0.0;
    
    public static final double kPRotation = 5.0;
    public static final double kIRotation = 0.0;
    public static final double kDRotation = 0.1;
    
    // Odometry
    public static final Matrix<N3, N1> stateStdDevs = VecBuilder.fill(0.1, 0.1, 0.1);
    public static final Matrix<N3, N1> visionMeasurementStdDevs = VecBuilder.fill(0.9, 0.9, 0.9);
    
    // Special states
    public static final SwerveModuleState[] xStanceStates = new SwerveModuleState[] {
        new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
        new SwerveModuleState(0, Rotation2d.fromDegrees(135)),
        new SwerveModuleState(0, Rotation2d.fromDegrees(135)),
        new SwerveModuleState(0, Rotation2d.fromDegrees(45))
    };
}
```

## Updating Constants for New Robots

When adapting the library for a new robot:

1. **Measure Your Robot**: Get accurate trackwidth and wheelbase measurements
2. **Start Conservative**: Begin with lower speed and acceleration limits
3. **Tune Iteratively**: Gradually increase limits based on testing
4. **Document Changes**: Record which values worked best for future reference

## Next Steps

- Learn about [module configuration](module-configuration.md) for individual swerve modules
- Explore [creating trajectories](creating-trajectories.md) for autonomous movement
- Check out [odometry and localization](odometry-localization.md) for position tracking