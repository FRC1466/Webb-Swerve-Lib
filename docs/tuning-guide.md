# Swerve Drive Tuning Guide

This guide provides a systematic approach to tuning your swerve drive implementation with Webb-Swerve-Lib for optimal performance.

## Why Proper Tuning Matters

Proper tuning of your swerve drive system is critical for:
- Accurate trajectory following
- Responsive teleop control
- Minimizing oscillations and wheel fighting
- Reducing mechanical wear
- Ensuring consistent behavior in competition

## Tuning Order

For best results, follow this tuning order:

1. **Module Configuration**
2. **Drive Motor Tuning**
3. **Steering Motor Tuning**
4. **Kinematics Verification**
5. **Drive Characterization**
6. **Path Following Tuning**

## 1. Module Configuration

### Verifying Hardware Installation

Before tuning, verify:
- Modules are mounted square to the chassis
- Wheels are properly aligned with the module frames
- All fasteners are tight
- Encoder wires are secure and properly routed

### Configuring Module Constants

Update your `ModuleConstants.java` with accurate measurements:

```java
// Ensure these measurements are accurate for your specific hardware
public static final double wheelDiameter = 0.10033; // meters (verify with actual measurement)
public static final double driveGearRatio = 6.75; // Verify with module documentation
public static final double steerGearRatio = 150.0 / 7.0; // Verify with module documentation
```

### Absolute Encoder Offsets

For each module, you'll need to calibrate the absolute encoder offset:

1. **Visual Alignment**:
   - Place the robot on blocks with wheels free to spin
   - Position all wheels to point in the forward robot direction (use a straight edge)
   - Ensure all wheels are perfectly parallel

2. **Reading Raw Values**:
   - Use Phoenix Tuner or similar tool to read the raw absolute encoder values
   - Record these values for each module

3. **Computing Offsets**:
   - For each module, calculate the offset using the formula:
     ```
     offset = (desired_zero_position - raw_encoder_value)
     ```
   - Convert to radians if needed

4. **Applying Offsets**:
   - Update the module configuration with these offsets:
     ```java
     new ModuleConfig(
         0, // Module index
         new Translation2d(0.3, 0.3), // Position from center
         1, // Drive motor ID
         2, // Steer motor ID
         3, // Encoder ID
         false, // Drive motor inverted
         false, // Steer motor inverted
         Math.toRadians(237.1) // Your calculated offset in radians
     )
     ```

5. **Verification**:
   - After applying offsets, power cycle the robot
   - All wheels should align to their straight-ahead position on startup
   - Jog each module to verify they move as expected

## 2. Drive Motor Tuning

Drive motors control the speed of each wheel.

### Setting Up Drive PID

```java
// Example PID configuration for Falcon 500 motors
driveMotor.config_kP(0, 0.05);
driveMotor.config_kI(0, 0.0);
driveMotor.config_kD(0, 0.0);
driveMotor.config_kF(0, 0.046);
```

### Drive PID Tuning Process

1. **Start with Feed Forward (kF)**:
   - Calculate theoretical kF using the formula:
     ```
     kF = (1023 * duty_cycle) / max_velocity
     ```
   - For Falcon 500s, a starting value around 0.046-0.048 is typical

2. **Add Proportional Gain (kP)**:
   - Start with a low value (0.05 for Falcon 500)
   - Command a constant velocity and observe response
   - Gradually increase until wheel responds quickly without oscillation
   - If oscillation occurs, reduce kP by 20%

3. **Add Derivative Gain (kD) if needed**:
   - If you observe oscillation that can't be fixed by reducing kP
   - Start with kD = kP/10
   - Increase until oscillation is damped

4. **Rarely Add Integral Gain (kI)**:
   - Only if you observe persistent steady-state error
   - Start very small (0.001) and increase gradually
   - Watch for integral windup (oscillation that grows over time)

### Common Drive Motor Values

| Motor Type | Typical kP | Typical kF | Notes |
|------------|------------|------------|-------|
| Falcon 500 | 0.05-0.15  | 0.045-0.055 | Values for velocity control |
| NEO        | 0.0001-0.0005 | 0.00017-0.00022 | SparkMAX uses different units |

## 3. Steering Motor Tuning

Steering motors control the angle of each module.

### Setting Up Steering PID

```java
// Example PID configuration for steering with Falcon 500
steerMotor.config_kP(0, 0.6);
steerMotor.config_kI(0, 0.0);
steerMotor.config_kD(0, 12.0);
steerMotor.configMotionCruiseVelocity(10000);
steerMotor.configMotionAcceleration(10000);
```

### Steering PID Tuning Process

1. **Start with Proportional Gain (kP)**:
   - Begin with a conservative value (0.2 for Falcon 500)
   - Command 90-degree turns and observe response
   - Increase until module moves quickly to position without significant overshoot
   - If overshoot exceeds 5 degrees, proceed to add kD

2. **Add Derivative Gain (kD)**:
   - Start with kD = kP × 10 (higher than drive motors)
   - Increase until overshoot is minimized
   - Steering typically needs higher kD values for stability

3. **Motion Magic Parameters (if applicable)**:
   - Set cruise velocity to ~80% of free speed
   - Set acceleration to cruise velocity divided by desired time-to-speed

4. **Fine-Tuning**:
   - Test rapid direction changes (e.g., 0° → 180° → 0°)
   - Look for smooth transitions without fighting or oscillation
   - Adjust kP and kD as needed for quick, stable responses

### Common Steering Motor Values

| Motor Type | Typical kP | Typical kD | Notes |
|------------|------------|------------|-------|
| Falcon 500 | 0.2-0.8    | 5.0-15.0   | For motion magic control |
| NEO        | 0.0005-0.002 | 0.001-0.01 | For position control |

## 4. Kinematics Verification

Verify that the robot moves as expected with simple commands:

### Translation Test

1. Command the robot to drive forward at a modest speed (1.0 m/s)
2. Verify:
   - Robot moves straight
   - All wheels point forward
   - No individual wheel fighting or oscillation
   - Distance traveled matches expected distance (measure with tape measure)

### Rotation Test

1. Command the robot to rotate in place at a modest angular velocity (0.5 rad/s)
2. Verify:
   - Robot rotates smoothly around its center
   - All wheels point tangent to the circle of rotation
   - No individual wheel fighting or oscillation

### Troubleshooting Common Issues

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Robot drifts when driving straight | Module angles not calibrated | Re-zero module offsets |
| Individual wheel fights | Incorrect module position in code | Verify module positions in `DriveConstants` |
| One wheel behaves differently | Inconsistent PID values | Ensure all modules use same PID values |
| Robot moves in wrong direction | Coordinate system confusion | Check signs of translations and rotations |

## 5. Drive Characterization

For optimal control, characterize your drivetrain:

### Using SysId Tool

1. **Set Up SysId**:
   - Add the relevant SysId code to your robot project
   - Disable any safety features temporarily

2. **Run Quasistatic Test**:
   - Measures slowly increasing voltage
   - Determines kS (static friction) and kV (velocity coefficient)

3. **Run Dynamic Test**:
   - Measures step voltage response
   - Determines kA (acceleration coefficient)

4. **Apply Values to Feedforward**:
   ```java
   // In your DriveConstants class
   public static final double kS = 0.32; // From SysId
   public static final double kV = 1.51; // From SysId
   public static final double kA = 0.27; // From SysId
   
   // In your Drive implementation
   SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(kS, kV, kA);
   ```

## 6. Path Following Tuning

Tune the PID controllers for autonomous trajectory following:

### Setting Up Controllers

```java
// In your DriveConstants class
public static final double kPTranslation = 3.0;
public static final double kITranslation = 0.0;
public static final double kDTranslation = 0.0;

public static final double kPRotation = 3.0;
public static final double kIRotation = 0.0;
public static final double kDRotation = 0.0;
```

### Tuning Translation Controllers

1. **Start Conservative**:
   - Begin with kP = 1.0 for both X and Y controllers
   - Run a simple straight-line path

2. **Increase kP**:
   - Gradually increase kP until the robot follows the path with minimal lag
   - If oscillation occurs, reduce kP by 20%

3. **Add kD if needed**:
   - If oscillation persists, add kD starting at kP/10
   - Increase until oscillation is damped

### Tuning Rotation Controller

1. **Start Conservative**:
   - Begin with kP = 1.0 for rotation controller
   - Run a path with heading changes

2. **Increase kP**:
   - Gradually increase kP until the robot follows heading changes responsively
   - Rotation typically needs higher kP than translation

3. **Add kD**:
   - Rotation almost always benefits from some kD
   - Start with kD = kP/10 and adjust as needed

### Fine-Tuning with Complex Paths

1. Test with more complex paths that include:
   - Tight turns
   - Varying speeds
   - Combined translation and rotation

2. Adjust parameters as needed based on:
   - Path following accuracy
   - Smoothness of motion
   - Absence of oscillation

## Performance Tuning

After basic functionality is achieved, optimize for performance:

### Module Constraints

Tune the module constraints based on robot weight and power:

```java
// Aggressive for lighter robots
public static final ModuleConstraints moduleConstraints = new ModuleConstraints(
    4.5, // Max velocity (m/s)
    3.0, // Max acceleration (m/s²)
    Math.PI * 8.0, // Max angular velocity (rad/s)
    Math.PI * 8.0  // Max angular acceleration (rad/s²)
);

// Conservative for heavier robots
public static final ModuleConstraints moduleConstraints = new ModuleConstraints(
    3.0, // Max velocity (m/s)
    1.5, // Max acceleration (m/s²)
    Math.PI * 6.0, // Max angular velocity (rad/s)
    Math.PI * 6.0  // Max angular acceleration (rad/s²)
);
```

### Slew Rate Limiting

Adjust slew rates to prevent tipping while maintaining responsiveness:

```java
public static final double kDirectionSlewRate = 1.2; // Units/s
public static final double kMagnitudeSlewRate = 1.8; // Units/s
public static final double kRotationalSlewRate = 2.0; // Units/s
```

## Tuning Checklist

Use this checklist to ensure you've completed all tuning steps:

- [ ] Measured and configured correct module positions
- [ ] Calibrated absolute encoder offsets
- [ ] Tuned drive motor PID and feed forward
- [ ] Tuned steering motor PID
- [ ] Verified kinematics with basic movement tests
- [ ] Characterized drive with SysId (optional)
- [ ] Tuned path following PID controllers
- [ ] Set appropriate motion constraints
- [ ] Configured slew rate limits for teleop
- [ ] Tested and validated with competition-like scenarios

## Common Values Table

| Parameter | Light Robot | Medium Robot | Heavy Robot |
|-----------|------------|-------------|------------|
| Max Velocity | 4.5-5.0 m/s | 3.5-4.5 m/s | 2.5-3.5 m/s |
| Max Acceleration | 3.0-4.0 m/s² | 2.0-3.0 m/s² | 1.5-2.0 m/s² |
| Translation kP | 4.0-6.0 | 3.0-5.0 | 2.0-4.0 |
| Rotation kP | 5.0-7.0 | 4.0-6.0 | 3.0-5.0 |
| Slew Rate | 1.8-2.5 | 1.2-1.8 | 0.8-1.5 |

## Resources for Further Tuning

- [WPILib Documentation on PID Tuning](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-pid-controller.html)
- [SDS Swerve Module Documentation](https://www.swervedrivespecialties.com/products/mk4-swerve-module)
- [FIRST Robotics Q&A](https://frc-qa.firstinspires.org/)
- [Chief Delphi Forums](https://www.chiefdelphi.com/)