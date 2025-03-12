# Module Configuration

This guide explains how to configure swerve drive modules in Webb-Swerve-Lib, including motor setup, encoder configuration, and PID tuning.

## Module Structure

Each swerve module in Webb-Swerve-Lib consists of:

1. **Drive Motor**: Controls wheel speed
2. **Steering Motor**: Controls module rotation
3. **Absolute Encoder**: Provides absolute angle feedback for the module
4. **Drive Encoder**: Typically integrated into the drive motor for velocity/position feedback

## Hardware Setup

### Supported Hardware

Webb-Swerve-Lib supports various hardware configurations:

**Drive Motors**:
- Falcon 500 / TalonFX
- NEO / SparkMAX
- Others via custom IO implementation

**Steering Motors**:
- Falcon 500 / TalonFX
- NEO / SparkMAX
- Others via custom IO implementation

**Absolute Encoders**:
- CANCoder
- Analog Absolute Encoders
- SparkMAX with Attached Absolute Encoders
- Others via custom IO implementation

### Physical Installation Guidelines

For optimal swerve module performance:

1. **Module Alignment**: Mount modules square to the frame with wheels positioned in the correct orientation
2. **Cable Management**: Route cables to prevent binding during module rotation
3. **Encoder Mounting**: Ensure absolute encoders are securely attached to the steering axis
4. **Gear Mesh**: Check for proper gear mesh and alignment in the drive and steering gearing

## Module Constants

The `ModuleConstants` class defines the physical characteristics and configuration of each module.

### Essential Parameters

```java
// Example ModuleConstants class structure
public final class ModuleConstants {
    // Common module constants
    public static final double wheelDiameter = 0.10033; // meters (SDS MK4i)
    public static final double driveGearRatio = 6.75; // SDS MK4i L2
    public static final double steerGearRatio = 150.0 / 7.0; // SDS MK4i
    
    // Module-specific constants (FL = Front Left, etc.)
    public static final ModuleConfig[] moduleConfigs = new ModuleConfig[] {
        // FL Module
        new ModuleConfig(
            0, // Module index
            new Translation2d(0.3, 0.3), // Module position from robot center (meters)
            1, // Drive motor CAN ID
            2, // Steer motor CAN ID
            3, // Absolute encoder CAN ID
            false, // Drive motor inverted
            false, // Steer motor inverted
            Math.toRadians(237.1) // Absolute encoder offset (radians)
        ),
        // FR Module
        // ... similar for other modules
    };
}
```

### Module Position Configuration

The module positions define the kinematics of your swerve drive:

```java
// Example module positions (based on a square chassis with modules at the corners)
// Positions are relative to robot center (origin at center of robot)
new Translation2d(0.3, 0.3), // Front Left
new Translation2d(0.3, -0.3), // Front Right
new Translation2d(-0.3, 0.3), // Back Left
new Translation2d(-0.3, -0.3) // Back Right
```

> **Important**: Use consistent measurement units (meters) and coordinate system (X forward, Y left)

## Encoder Configuration

### Absolute Encoder Setup

Absolute encoders are critical for swerve drive to know the steering angle at startup:

1. **Offsets**: Calibrate offsets to ensure modules point in the expected direction:

```java
// Example offset configuration
Math.toRadians(237.1) // FL Module offset in radians
```

2. **Direction**: Configure encoder direction to match your module design:

```java
// If using CANCoders, set direction in Phoenix Tuner or in code:
canCoder.configSensorDirection(true); // true = counter-clockwise positive
```

### Determining Module Offsets

Follow this procedure to determine your module offsets:

1. **Manual Wheel Alignment**:
   - Position all wheels pointing in the forward direction
   - Use a straight edge to ensure precise alignment

2. **Reading Raw Encoder Values**:
   - Use Phoenix Tuner or other tools to read the raw encoder values
   - Record these values for each module

3. **Calculating Offsets**:
   - For each module, calculate the offset required to make the module read 0 degrees when pointing forward
   - Convert to the correct angle units (typically radians for internal use)

4. **Verification**:
   - After applying offsets, power cycle the robot
   - All modules should align to their "zeroed" position on startup

## Motor Controller Configuration

### Drive Motor Configuration

```java
// Example drive motor configuration (Falcon 500 / TalonFX)
driveMotor.configFactoryDefault();
driveMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor);
driveMotor.setNeutralMode(NeutralMode.Brake);
driveMotor.configVoltageCompSaturation(12.0);
driveMotor.enableVoltageCompensation(true);
driveMotor.setInverted(moduleConfig.driveMotorInverted);
driveMotor.config_kP(0, 0.1);
driveMotor.config_kI(0, 0.0);
driveMotor.config_kD(0, 0.0);
driveMotor.configClosedloopRamp(0.25);
```

### Steering Motor Configuration

```java
// Example steering motor configuration (Falcon 500 / TalonFX)
steerMotor.configFactoryDefault();
steerMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor);
steerMotor.setNeutralMode(NeutralMode.Brake);
steerMotor.configVoltageCompSaturation(12.0);
steerMotor.enableVoltageCompensation(true);
steerMotor.setInverted(moduleConfig.steerMotorInverted);
steerMotor.config_kP(0, 0.6);
steerMotor.config_kI(0, 0.0);
steerMotor.config_kD(0, 12.0);
steerMotor.configMotionCruiseVelocity(10000);
steerMotor.configMotionAcceleration(10000);
```

## Common Module Configurations

### SDS MK4/MK4i Configurations

Standard SDS MK4/MK4i gear ratios:

| Configuration | Drive Gear Ratio | Max Speed |
|---------------|-----------------|-----------|
| L1            | 8.14:1          | 14.5 ft/s |
| L2            | 6.75:1          | 17.5 ft/s |
| L3            | 6.12:1          | 19.3 ft/s |
| L4            | 5.14:1          | 22.9 ft/s |

Steering gear ratio for all variants: 150.0 / 7.0 (approximately 21.43:1)

### REV MAXSwerve Configurations

Standard REV MAXSwerve gear ratios:

| Configuration | Drive Gear Ratio | Max Speed |
|---------------|-----------------|-----------|
| Standard      | 8.14:1          | 14.5 ft/s |
| Fast          | 6.75:1          | 17.5 ft/s |

## PID Tuning

Proper PID tuning is crucial for smooth, accurate swerve module control.

### Drive Motor PID

The drive motor PID controls velocity of the wheel:

1. **Start with Conservative Values**:
   ```java
   driveMotor.config_kP(0, 0.05);
   driveMotor.config_kI(0, 0.0);
   driveMotor.config_kD(0, 0.0);
   ```

2. **Tuning Process**:
   - Increase kP until the wheel velocity response is quick without overshoot
   - Add small kD if there is oscillation
   - Add minimal kI only if there is persistent steady-state error

3. **Typical Values**:
   - Falcon 500: kP = 0.05-0.15, kD = 0.0-1.0
   - NEO: kP = 0.0001-0.0005, kD = 0.0-0.001

### Steering Motor PID

The steering motor PID controls module angle:

1. **Start with Conservative Values**:
   ```java
   steerMotor.config_kP(0, 0.2);
   steerMotor.config_kI(0, 0.0);
   steerMotor.config_kD(0, 2.0);
   ```

2. **Tuning Process**:
   - Increase kP until the module turns quickly to the target position
   - Add kD to reduce overshoot (typically 10-20× the kP value)
   - Avoid kI unless absolutely necessary due to potential oscillation

3. **Typical Values**:
   - Falcon 500: kP = 0.2-0.8, kD = 5.0-15.0
   - NEO: kP = 0.0005-0.002, kD = 0.001-0.01

## Advanced Configuration

### Velocity Limiting

Configure velocity limitations to prevent tipping and excessive wheel slip:

```java
// Example from DriveConstants.java
public static final ModuleConstraints moduleLimitsFast = new ModuleConstraints(
    4.5, // Max linear velocity (m/s)
    3.0, // Max linear acceleration (m/s²)
    Math.PI * 8.0, // Max angular velocity (rad/s)
    Math.PI * 8.0 // Max angular acceleration (rad/s²)
);

public static final ModuleConstraints moduleLimitsPrecision = new ModuleConstraints(
    2.0, // Reduced velocity for precision control
    2.0,
    Math.PI * 4.0,
    Math.PI * 4.0
);
```

### Cosine Compensation

Webb-Swerve-Lib uses cosine compensation to improve module transitions during rotation:

```java
// This happens automatically in the library
setpointState.cosineScale(wheelAngle);
```

This scales wheel velocity based on the angular difference between current and target orientations to improve smoothness.

## Troubleshooting

| Problem | Possible Causes | Solutions |
|---------|----------------|-----------|
| Modules not aligning at startup | Incorrect offsets | Recalibrate module offsets |
| Modules oscillate | PID tuning too aggressive | Reduce kP or increase kD |
| Slow module response | PID tuning too conservative | Increase kP carefully |
| Wheels fight each other | Kinematics mismatch | Verify module positions match physical robot |

## Next Steps

- Learn about [drive constants](drive-constants.md) for overall drivetrain configuration
- Explore [creating trajectories](creating-trajectories.md) for autonomous movement
- Understand [odometry and localization](odometry-localization.md) for position tracking