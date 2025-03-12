# Utility Classes

Webb-Swerve-Lib includes several utility classes that provide common functionality used throughout the codebase. This document details each utility and how to effectively use it in your robot code.

## DoublePressTracker

`DoublePressTracker` is a utility class that detects double-press events from buttons or triggers.

### Purpose
This class solves the common problem of needing to detect when an operator quickly presses a button twice in succession, which is often used for mode toggles or special functions.

### Features
- Configurable timeout between presses
- Clean state machine implementation
- Integration with WPILib Trigger system

### Usage
```java
// Create a double-press trigger from an existing trigger
Trigger regularButton = new JoystickButton(controller, 1);
Trigger doublePressButton = DoublePressTracker.doublePress(regularButton);

// Use the double-press trigger in commands
doublePressButton.onTrue(new InstantCommand(() -> System.out.println("Double pressed!")));
```

### Implementation Details
The class uses a state machine with four states:
1. `IDLE` - Waiting for first press
2. `FIRST_PRESS` - First button press detected
3. `FIRST_RELEASE` - Button released after first press
4. `SECOND_PRESS` - Second press detected within time window

## OverrideSwitches

`OverrideSwitches` provides an interface for physical override switches found on operator consoles.

### Purpose
This utility allows robot code to easily interface with manual override switches, which are commonly used in competition robots for mode selection, failsafe operations, or manual control overrides.

### Features
- Support for driver and operator side switches
- Multi-directional switch support
- Connectivity detection
- WPILib Trigger integration

### Usage
```java
// Create an instance with the joystick port
OverrideSwitches overrides = new OverrideSwitches(3);

// Check switch states
if (overrides.getDriverSwitch(0)) {
    // Driver switch 0 is on
}

// Use as triggers
overrides.operatorSwitch(2).onTrue(new InstantCommand(() -> enableBackupMode()));
overrides.multiDirectionSwitchLeft().whileTrue(new ManualControlCommand());
```

### Available Switch Types
- Driver switches (0-2 left to right)
- Operator switches (0-4 left to right)
- Multi-directional switch (LEFT, NEUTRAL, RIGHT positions)

## SwerveSetpointGenerator

`SwerveSetpointGenerator` is a utility that generates kinematically feasible swerve drive commands.

### Purpose
This class ensures smooth transitions between different drive states by generating intermediate setpoints that respect the kinematic constraints of the swerve modules, preventing wheel slip and improving drive performance.

### Features
- Respects maximum velocity, acceleration, and steering rate constraints
- Handles module rotation optimization
- Prevents "fighting" between modules during direction changes
- Smoothly converges to desired states

### Usage
```java
// Create the generator with kinematics and module locations
SwerveSetpointGenerator generator = SwerveSetpointGenerator.builder()
    .kinematics(kinematics)
    .moduleLocations(moduleLocations)
    .build();

// Generate a new setpoint
SwerveSetpoint nextSetpoint = generator.generateSetpoint(
    moduleLimits,             // Physical limits of the modules
    previousSetpoint,         // Last setpoint commanded
    desiredChassisSpeeds,     // Desired robot movement
    0.02                      // Loop period in seconds
);
```

### Key Methods
- `generateSetpoint()`: Computes a new setpoint that converges to the desired state while respecting kinematic constraints

### Mathematical Approach
The generator uses numerical optimization techniques to:
1. Find the maximum interpolation factor between current and desired states
2. Ensure wheel velocity and acceleration limits are respected
3. Ensure steering velocity limits are respected
4. Handle cases where flipping modules is more efficient

## EqualsUtil

`EqualsUtil` provides utility methods for comparing floating-point values and geometric objects with appropriate epsilon values.

### Purpose
Floating-point comparisons require epsilon values to handle numerical precision issues. This utility standardizes these comparisons across the codebase.

### Features
- Epsilon-based floating-point equality checks
- Extension methods for WPILib geometric classes
- Standardized epsilon values

### Usage
```java
// Static import for convenience
import static org.webbrobotics.frc2025.util.EqualsUtil.*;

// Check if two double values are approximately equal
if (epsilonEquals(value1, value2)) {
    // Values are approximately equal
}

// With custom epsilon
if (epsilonEquals(value1, value2, 0.001)) {
    // Values are equal within 0.001
}

// Using extension methods for geometric types
Pose2d pose1 = new Pose2d();
Pose2d pose2 = new Pose2d(0.0001, 0, Rotation2d.fromDegrees(0.01));

if (pose1.epsilonEquals(pose2)) {
    // Poses are approximately equal
}
```

## GeomUtil

`GeomUtil` provides extensions and utilities for working with WPILib's geometry classes.

### Purpose
This utility simplifies common geometric operations and adds functionality missing from the WPILib geometry classes.

### Features
- Extension methods for Pose2d, Translation2d, and Rotation2d
- Interpolation and transformation helpers
- Common geometric operations

### Usage
```java
// To use extension methods
@ExtensionMethod({GeomUtil.class})
public class MyClass {
    public void example() {
        // Using extension methods
        Pose2d pose = new Pose2d(2, 3, new Rotation2d(1.0));
        Twist2d twist = new Twist2d(0.1, 0.2, 0.3);
        
        // Apply twist to pose using extension method
        Pose2d newPose = pose.exp(twist);
        
        // Compute twist between poses
        Twist2d computedTwist = pose.log(newPose);
    }
}
```

### Key Methods
- `exp(Twist2d)`: Transform a pose by a twist
- `log(Pose2d)`: Find the twist between two poses
- `interpolate(Pose2d, double)`: Interpolate between poses
- Various transformation and coordinate conversion helpers

## PhoenixUtil

`PhoenixUtil` provides helper methods for working with CTRE Phoenix devices.

### Purpose
This utility simplifies common operations when working with CTRE Phoenix motor controllers and sensors, particularly handling error cases and configuration.

### Features
- Retry mechanisms for Phoenix API calls
- Signal registration and management
- Standardized error handling

### Usage
```java
// Try a Phoenix operation until it succeeds (up to maxTries)
PhoenixUtil.tryUntilOk(5, () -> talon.getConfigurator().apply(config, 0.25));

// Register signals for periodic refresh
PhoenixUtil.registerSignals(
    true,
    drivePosition,
    driveVelocity,
    driveAppliedVolts
);
```

## Other Utilities

### BuildConstants

Auto-generated class containing build information like Git commit, build date, etc.

### Constants

Global constants class containing robot configuration options:
- Robot type selection (competition, development, simulation)
- Operation mode detection
- Tuning mode flags
- Deployment validation

### Summary

The utility classes in Webb-Swerve-Lib provide consistent, reusable functionality that helps simplify robot code and improve reliability. By understanding and leveraging these utilities, teams can write more maintainable and robust code while focusing on robot-specific functionality.
