# Getting Started with Webb-Swerve-Lib

This guide will walk you through the initial setup and configuration of the Webb-Swerve-Lib for your FRC robot.

## Prerequisites

- WPILib development environment (2023 or newer)
- Git installed on your development machine
- Basic understanding of swerve drive mechanics
- Robot with swerve modules (compatible with SDS MK4/MK4i or similar)
- Phoenix libraries for TalonFX/Falcon 500 motors
- NavX or Pigeon IMU for gyroscope readings

## Installation

### Option 1: Add as Git Submodule (Recommended)

1. In your robot project's root directory, run:
   ```bash
   git submodule add https://github.com/FRC1466/Webb-Swerve-Lib.git
   ```

2. Update your `settings.gradle` file to include:
   ```gradle
   include ':Webb-Swerve-Lib'
   ```

3. Add the dependency to your `build.gradle` file:
   ```gradle
   dependencies {
       implementation project(':Webb-Swerve-Lib')
       // Your other dependencies
   }
   ```

### Option 2: Add as a Library JAR

1. Download the latest release JAR from the [GitHub releases page](https://github.com/FRC1466/Webb-Swerve-Lib/releases)
2. Add the JAR to your project's lib folder
3. Update your `build.gradle` to include it as a dependency:
   ```gradle
   dependencies {
       implementation files('lib/Webb-Swerve-Lib.jar')
       // Your other dependencies
   }
   ```

## Basic Project Structure

After installing Webb-Swerve-Lib, your robot project should have a structure similar to this:

```
YourRobotProject/
├── src/
│   └── main/
│       └── java/
│           └── frc/
│               └── robot/
│                   ├── Constants.java
│                   ├── RobotContainer.java
│                   ├── Main.java
│                   ├── Robot.java
│                   └── subsystems/
│                       └── drive/
│                           ├── DriveConstants.java
│                           └── Drive.java
└── Webb-Swerve-Lib/ (If using submodule approach)
```

## Initial Setup

1. Create a `DriveConstants.java` file in your project, similar to the one in Webb-Swerve-Lib but customized for your robot's physical parameters.

2. Setup your gyroscope implementation:
   ```java
   // Example using Pigeon2
   public class GyroIOPigeon2 implements GyroIO {
     private final Pigeon2 pigeon;
     
     public GyroIOPigeon2() {
       pigeon = new Pigeon2(DriveConstants.PigeonConstants.id);
       pigeon.getConfigurator().apply(new Pigeon2Configuration());
       pigeon.setYaw(0.0);
     }
     
     @Override
     public void updateInputs(GyroIOInputs inputs) {
       inputs.connected = pigeon.getUpTime().getValue() > 0.0;
       inputs.yawPosition = Rotation2d.fromDegrees(pigeon.getYaw().getValue());
       inputs.yawVelocityRadPerSec = Units.degreesToRadians(pigeon.getAngularVelocityZWorld().getValue());
     }
   }
   ```

3. Define your swerve module configurations in `DriveConstants.java`:
   ```java
   public static final ModuleConfig[] moduleConfigs = {
     // FL
     ModuleConfig.builder()
         .driveMotorId(1)
         .turnMotorId(2)
         .encoderChannel(0)
         .encoderOffset(Rotation2d.fromRadians(0.0))
         .turnInverted(true)
         .encoderInverted(false)
         .build(),
     // FR
     ModuleConfig.builder()
         .driveMotorId(3)
         .turnMotorId(4)
         .encoderChannel(1)
         .encoderOffset(Rotation2d.fromRadians(0.0))
         .turnInverted(true)
         .encoderInverted(false)
         .build(),
     // BL
     ModuleConfig.builder()
         .driveMotorId(5)
         .turnMotorId(6)
         .encoderChannel(2)
         .encoderOffset(Rotation2d.fromRadians(0.0))
         .turnInverted(true)
         .encoderInverted(false)
         .build(),
     // BR
     ModuleConfig.builder()
         .driveMotorId(7)
         .turnMotorId(8)
         .encoderChannel(3)
         .encoderOffset(Rotation2d.fromRadians(0.0))
         .turnInverted(true)
         .encoderInverted(false)
         .build()
   };
   ```

4. Create your `Drive` subsystem in your robot project:
   ```java
   public class Drive extends SubsystemBase {
     private final DrivetrainSubsystem drivetrain;
   
     public Drive() {
       GyroIO gyroIO = new GyroIOPigeon2();
       ModuleIO[] moduleIOs = new ModuleIO[4];
       
       // Initialize your ModuleIO implementations
       for (int i = 0; i < 4; i++) {
         moduleIOs[i] = new ModuleIOComp(DriveConstants.moduleConfigs[i]);
       }
       
       drivetrain = new DrivetrainSubsystem(gyroIO, moduleIOs);
     }
   
     @Override
     public void periodic() {
       drivetrain.periodic();
     }
   
     // Add your drive methods here
   }
   ```

## Next Steps

After completing the basic setup:

1. Learn how to [configure your robot's specific parameters](configuring-robot.md)
2. Start [creating trajectories](creating-trajectories.md) for autonomous operation
3. Explore the [API Reference](api-reference.md) for advanced usage

## Troubleshooting Initial Setup

If you encounter issues during setup:

- Ensure all motor IDs are correctly configured
- Verify encoder channels and offsets
- Check that the gyroscope is properly connected
- Confirm the Maven dependencies are correctly included

For more detailed troubleshooting, see the [Troubleshooting](troubleshooting.md) guide.