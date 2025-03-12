# Configuring Your Robot

This guide explains how to configure Webb-Swerve-Lib for your specific robot hardware.

## Annual Configuration Requirements

Each year or when building a new robot, you'll need to update the following configurations:

### Physical Configuration

In your `DriveConstants.java` file, update these essential measurements:

```java
public class DriveConstants {
  // Update these values for your specific robot chassis
  public static final double trackWidthX = Units.inchesToMeters(22.75); // Distance between left and right wheels
  public static final double trackWidthY = Units.inchesToMeters(22.75); // Distance between front and back wheels
  
  // Calculated from the above values - typically don't need to change
  public static final double driveBaseRadius = Math.hypot(trackWidthX / 2, trackWidthY / 2);
  
  // Update based on your actual swerve module gearing and motor specs
  public static final double maxLinearSpeed = 4.69; // meters per second
  public static final double maxAngularSpeed = 4.69 / driveBaseRadius; // radians per second
  
  // Robot width including bumpers - important for auto paths to avoid collisions
  public static final double robotWidth = Units.inchesToMeters(28.0) + 2 * Units.inchesToMeters(2.0);
  
  // Module positions relative to robot center - typically doesn't change unless chassis geometry changes
  public static final Translation2d[] moduleTranslations = {
    new Translation2d(trackWidthX / 2, trackWidthY / 2),     // Front Left
    new Translation2d(trackWidthX / 2, -trackWidthY / 2),    // Front Right
    new Translation2d(-trackWidthX / 2, trackWidthY / 2),    // Back Left
    new Translation2d(-trackWidthX / 2, -trackWidthY / 2)    // Back Right
  };
  
  // Other configuration constants...
}
```

### Motor and Sensor Configuration

Update your motor IDs, encoder channels, and encoder offsets for each module:

```java
public static final ModuleConfig[] moduleConfigs = {
  // Front Left
  ModuleConfig.builder()
      .driveMotorId(16)                                        // Drive motor CAN ID
      .turnMotorId(15)                                         // Turn motor CAN ID
      .encoderChannel(41)                                      // Absolute encoder DIO channel or ID
      .encoderOffset(Rotation2d.fromRadians(2.5356702423749646)) // Encoder offset
      .turnInverted(true)                                      // Whether turn motor is inverted
      .encoderInverted(false)                                  // Whether encoder is inverted
      .build(),
  
  // Front Right (similar configuration)...
  // Back Left (similar configuration)...
  // Back Right (similar configuration)...
};
```

### Gyroscope Configuration

Update the gyroscope ID and type:

```java
public static class PigeonConstants {
  public static final int id = 30; // Update with your gyro's CAN ID
}
```

## Calibrating Absolute Encoder Offsets

For accurate swerve module alignment, you need to calibrate encoder offsets:

1. **Set all encoders to zero initially:**
   ```java
   .encoderOffset(Rotation2d.fromRadians(0.0))
   ```

2. **Create a temporary calibration routine:**
   ```java
   public class EncoderCalibration extends CommandBase {
     private final SwerveSubsystem swerve;
     
     public EncoderCalibration(SwerveSubsystem swerve) {
       this.swerve = swerve;
       addRequirements(swerve);
     }
     
     @Override
     public void initialize() {
       // Set all modules to zero rotation
       swerve.setModuleStates(new SwerveModuleState[] {
         new SwerveModuleState(0.0, Rotation2d.fromDegrees(0)),
         new SwerveModuleState(0.0, Rotation2d.fromDegrees(0)),
         new SwerveModuleState(0.0, Rotation2d.fromDegrees(0)),
         new SwerveModuleState(0.0, Rotation2d.fromDegrees(0))
       });
     }
     
     @Override
     public void execute() {
       // Log the current absolute encoder readings
       for (int i = 0; i < 4; i++) {
         double rawPosition = swerve.getModuleAbsoluteEncoderRad(i);
         Logger.getInstance().recordOutput("Module " + i + " Raw Encoder", rawPosition);
       }
     }
   }
   ```

3. **Run the calibration routine and record values**
   - Physically align all wheels to face forward
   - Run the calibration routine
   - Record the raw encoder values
   
4. **Update offsets in DriveConstants:**
   - Set encoder offsets to the negative of the recorded values

## Updating Robot Characteristics

When robot weight or center of gravity changes:

1. Update the weight parameters in the trajectory generator:
   ```java
   VehicleModel model =
       VehicleModel.newBuilder()
           .setMass(67) // Update with your robot's mass in kg
           .setMoi(5.8) // Update moment of inertia if known
           .setVehicleLength(DriveConstants.trackWidthX)
           .setVehicleWidth(DriveConstants.trackWidthY)
           // Other parameters...
           .build();
   ```

2. Consider tuning the trajectory following PID values based on the new weight characteristics:
   ```java
   linearkP.initDefault(8.0); // May need to increase for heavier robots
   linearkD.initDefault(0.0); // Add some derivative term if oscillation occurs
   thetakP.initDefault(4.0);  // May need to increase for robots with higher moment of inertia
   thetakD.initDefault(0.0);  // Add some derivative term if oscillation occurs
   ```

## Tuning Drive Performance

To optimize drive performance:

1. **Velocity Control Tuning:**
   - Tune PID values for the drive motors in your `ModuleIOComp` implementation
   - Start with conservative P values (0.1) and gradually increase
   - Add FF values based on motor characterization if available

2. **Position Control Tuning for Turn Motors:**
   - Set position PID values for the turn motors
   - Higher P values will make turns more responsive but may cause oscillation

3. **Path Following Tuning:**
   - Adjust the PID values in your trajectory follower
   - Higher values provide tighter path following but can cause oscillation

## CAN Bus Optimization

For optimal CAN bus performance:

1. Group devices by function on separate CAN buses if possible
2. Set appropriate status frame periods:
   ```java
   // Example for drive motors (less frequent updates needed)
   driveMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 20);
   driveMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 20);
   
   // Example for turn motors (more frequent position updates)
   turnMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10);
   turnMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10);
   ```

## Configuration File Structure

For effective organization, we recommend structuring your constants as follows:

```
YourRobotProject/
├── src/main/java/frc/robot/
    ├── Constants.java                 (Robot-wide constants)
    └── subsystems/drive/
        ├── DriveConstants.java        (Drive-specific constants)
        ├── ModuleConstants.java       (Swerve module-specific constants)
        └── AutoConstants.java         (Constants for autonomous/trajectories)
```

## Next Steps

After configuring your robot:
- Learn how to [create trajectories](creating-trajectories.md)
- Set up [autonomous routines](autonomous-programming.md)
- Check out [troubleshooting tips](troubleshooting.md) if you encounter issues