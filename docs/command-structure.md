# Command Structure

This guide explains the command-based architecture used in Webb-Swerve-Lib and how to effectively structure your code for teleop and autonomous operation.

## Command-Based Programming Overview

Webb-Swerve-Lib uses WPILib's command-based programming framework, which offers several advantages:

- **Modular Design**: Break down complex behaviors into reusable components
- **Declarative Control Flow**: Describe what should happen rather than how to execute it
- **Scheduling**: Commands run automatically at appropriate times
- **Cancellation**: Automatically end commands when appropriate
- **Resource Management**: Prevent multiple commands from controlling the same subsystem

## Core Command Concepts

### Subsystems

Subsystems represent the physical components of your robot:

```java
public class Drive extends SubsystemBase {
    // Hardware interfaces
    private final GyroIO gyroIO;
    private final Module[] modules = new Module[4]; // FL, FR, BL, BR
    
    // Methods for controlling the subsystem
    public void runVelocity(ChassisSpeeds speeds) {
        // Implementation
    }
    
    public void stop() {
        // Implementation
    }
}
```

### Commands

Commands define actions that your robot can perform:

```java
public class DriveTrajectory extends CommandBase {
    private final Drive driveSubsystem;
    private final HolonomicTrajectory trajectory;
    
    public DriveTrajectory(Drive driveSubsystem, HolonomicTrajectory trajectory) {
        this.driveSubsystem = driveSubsystem;
        this.trajectory = trajectory;
        addRequirements(driveSubsystem);
    }
    
    @Override
    public void initialize() {
        // Setup before command runs
    }
    
    @Override
    public void execute() {
        // Code that runs periodically during the command
    }
    
    @Override
    public void end(boolean interrupted) {
        // Cleanup when command ends
    }
    
    @Override
    public boolean isFinished() {
        // Determine if command is complete
        return false;
    }
}
```

## Command Structure in Webb-Swerve-Lib

Webb-Swerve-Lib organizes commands in a hierarchical structure:

### Subsystem-Level Commands

These commands directly control a single subsystem:

- `DefaultDriveCommand`: Default command for teleoperated control
- `DriveTrajectory`: Command for following a pre-defined trajectory
- `XStanceCommand`: Command for setting the wheels in an X pattern to resist movement

### Robot-Level Commands

These commands combine multiple subsystems for higher-level robot behaviors:

- Autonomous routines that coordinate drive and other subsystems
- Game-piece manipulation sequences that involve multiple subsystems

### Sequential and Parallel Command Groups

Command groups combine multiple commands:

```java
// Sequential execution - commands run one after another
new SequentialCommandGroup(
    new DriveTrajectory(drive, trajectoryToGoal),
    new ScoreCommand(arm, intake),
    new DriveTrajectory(drive, trajectoryToNextPosition)
);

// Parallel execution - commands run simultaneously
new ParallelCommandGroup(
    new DriveTrajectory(drive, approachTrajectory),
    new PrepareToScoreCommand(arm)
);
```

## Default Commands

The `DefaultDriveCommand` runs whenever no other command is using the Drive subsystem:

```java
public class RobotContainer {
    private final Drive drive = new Drive(...);
    
    public RobotContainer() {
        // Configure default command for teleoperated driving
        drive.setDefaultCommand(new DefaultDriveCommand(
            drive,
            () -> -driverController.getLeftY(), // Forward/backward
            () -> -driverController.getLeftX(), // Left/right
            () -> -driverController.getRightX() // Rotation
        ));
    }
}
```

## Common Command Types

### Instant Commands

For immediate, non-blocking actions:

```java
// Reset robot pose at start of autonomous
new InstantCommand(() -> drive.setPose(new Pose2d(1.5, 5.5, Rotation2d.fromDegrees(180))));
```

### Run Commands

For continuous actions that don't automatically end:

```java
// Run intake until canceled externally
new RunCommand(() -> intake.setSpeed(1.0), intake);
```

### Trigger Bindings

Map controller buttons to commands:

```java
// When A button is pressed, activate X-stance
driverController.a().onTrue(new XStanceCommand(drive));

// While right bumper is held, run intake
driverController.rightBumper().whileTrue(new RunCommand(() -> intake.setSpeed(1.0), intake));

// When left bumper is pressed, toggle scoring mode
driverController.leftBumper().toggleOnTrue(new SetScoringModeCommand(arm));
```

## Creating Custom Commands

Create custom commands for specific robot actions:

```java
public class ScoreGamePiece extends CommandBase {
    private final ArmSubsystem arm;
    private final IntakeSubsystem intake;
    private boolean finished = false;
    
    public ScoreGamePiece(ArmSubsystem arm, IntakeSubsystem intake) {
        this.arm = arm;
        this.intake = intake;
        addRequirements(arm, intake);
    }
    
    @Override
    public void initialize() {
        arm.setPosition(ArmPosition.SCORING);
    }
    
    @Override
    public void execute() {
        // Wait until arm is in position before ejecting
        if (arm.atTargetPosition() && !finished) {
            intake.eject();
            finished = true;
        }
    }
    
    @Override
    public boolean isFinished() {
        return finished;
    }
    
    @Override
    public void end(boolean interrupted) {
        intake.stop();
        arm.setPosition(ArmPosition.STOWED);
    }
}
```

## Drive-Specific Commands

Webb-Swerve-Lib provides several specialized commands for the drive subsystem:

### DefaultDriveCommand

Handles standard teleoperated driving:

```java
public class DefaultDriveCommand extends CommandBase {
    private final Drive drive;
    private final DoubleSupplier translationXSupplier;
    private final DoubleSupplier translationYSupplier;
    private final DoubleSupplier rotationSupplier;
    
    // Constructor and standard command methods
    
    @Override
    public void execute() {
        // Apply deadband and modifiers to driver inputs
        double translationX = MathUtil.applyDeadband(translationXSupplier.getAsDouble(), 0.1);
        double translationY = MathUtil.applyDeadband(translationYSupplier.getAsDouble(), 0.1);
        double rotation = MathUtil.applyDeadband(rotationSupplier.getAsDouble(), 0.1);
        
        // Calculate field-relative chassis speeds
        ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            translationX * DriveConstants.kMaxSpeedMetersPerSecond,
            translationY * DriveConstants.kMaxSpeedMetersPerSecond,
            rotation * DriveConstants.kMaxAngularSpeedRadiansPerSecond,
            RobotState.getInstance().getHeading());
            
        // Command the drive subsystem
        drive.runVelocity(chassisSpeeds);
    }
}
```

### DriveTrajectory

Follows pre-generated trajectories for autonomous:

```java
public class DriveTrajectory extends CommandBase {
    private final Drive drive;
    private final HolonomicTrajectory trajectory;
    private final PIDController xController;
    private final PIDController yController;
    private final PIDController rotationController;
    private final Timer timer = new Timer();
    
    // Constructor and setup
    
    @Override
    public void initialize() {
        timer.reset();
        timer.start();
    }
    
    @Override
    public void execute() {
        double currentTime = timer.get();
        
        // Get desired state from trajectory
        HolonomicTrajectoryState desiredState = trajectory.sample(currentTime);
        
        // Calculate feedback adjustments
        double xFeedback = xController.calculate(
            RobotState.getInstance().getEstimatedPose().getX(),
            desiredState.poseMeters.getX());
            
        double yFeedback = yController.calculate(
            RobotState.getInstance().getEstimatedPose().getY(),
            desiredState.poseMeters.getY());
            
        double rotationFeedback = rotationController.calculate(
            RobotState.getInstance().getHeading().getRadians(),
            desiredState.poseMeters.getRotation().getRadians());
        
        // Combine feedforward and feedback
        ChassisSpeeds targetChassisSpeeds = new ChassisSpeeds(
            desiredState.velocityMetersPerSecond.x + xFeedback,
            desiredState.velocityMetersPerSecond.y + yFeedback,
            desiredState.angularVelocityRadiansPerSecond + rotationFeedback);
            
        // Command the drive
        drive.runVelocity(targetChassisSpeeds);
    }
    
    @Override
    public boolean isFinished() {
        return timer.get() >= trajectory.getTotalTimeSeconds();
    }
}
```

## Command Composition Patterns

### Build Complex Behaviors from Simple Commands

```java
// Multi-step autonomous routine
new SequentialCommandGroup(
    // Move to pickup location
    new DriveTrajectory(drive, pickupTrajectory),
    
    // Lower arm and run intake
    new ParallelCommandGroup(
        new SetArmPositionCommand(arm, ArmPosition.GROUND_PICKUP),
        new RunCommand(() -> intake.setSpeed(1.0), intake).withTimeout(1.0)
    ),
    
    // Stow arm
    new SetArmPositionCommand(arm, ArmPosition.STOWED),
    
    // Drive to scoring position
    new DriveTrajectory(drive, scoreTrajectory),
    
    // Score game piece
    new ScoreGamePiece(arm, intake)
);
```

### Conditional Command Execution

```java
// Choose trajectory based on detected game piece
new ConditionalCommand(
    new DriveTrajectory(drive, leftObjectTrajectory),
    new DriveTrajectory(drive, rightObjectTrajectory),
    vision::isObjectOnLeft
);
```

### Using Event Markers for Timing

```java
// Create trajectory with events
PathSegment segment = PathSegment.newBuilder()
    .addPoseWaypoint(startPose)
    .addPoseWaypoint(endPose)
    .addEvent("deployIntake", 0.5) // 50% through the path
    .build();

// Handle events in command
DriveTrajectory trajectoryCommand = new DriveTrajectory(drive, 
    new HolonomicTrajectory(segment));
    
trajectoryCommand.addEventHandler("deployIntake", 
    new InstantCommand(() -> intake.deploy()));
```

## Advanced Command Patterns

### Command Interruptibility

Control whether commands can be interrupted:

```java
// Cannot be interrupted once started
new ScoreGamePiece(arm, intake).withInterruptible(false);

// Can be interrupted
new DefaultDriveCommand(drive, translationX, translationY, rotation);
```

### Command Timeouts

Add timeouts to commands:

```java
// Command will end after 5 seconds if not already finished
new ArmExtendCommand(arm).withTimeout(5.0);

// Timeout a sequential group
new SequentialCommandGroup(
    new DriveForward(drive),
    new IntakeGamePiece(intake)
).withTimeout(10.0);
```

### Command Scheduling

You can manually schedule commands in your code:

```java
// Schedule a command programmatically
CommandScheduler.getInstance().schedule(new EmergencyStopCommand(drive));

// Cancel all currently running commands
CommandScheduler.getInstance().cancelAll();
```

## Best Practices

### Command Design

1. **Single Responsibility**: Each command should do one thing well
2. **Clear End Conditions**: Define precisely when commands should end
3. **Appropriate Requirements**: Add all subsystems the command will use
4. **Clean Up**: Always clean up in `end()` method
5. **Error Handling**: Consider what happens if the command is interrupted

### Subsystem Integration

1. **Method Granularity**: Provide appropriate methods on subsystems
2. **State Management**: Use subsystems to track internal state
3. **Default Commands**: Set appropriate default commands for all subsystems

## Common Command Issues

| Issue | Symptoms | Solution |
|-------|----------|----------|
| Command never ends | Robot "hangs" in autonomous | Check `isFinished()` implementation |
| Resource conflicts | Erratic subsystem behavior | Ensure all commands add proper requirements |
| Command ends early | Actions not completed | Check `isFinished()` conditions or add timeout |
| State not reset | Subsystem "remembers" previous actions | Clean up in `end()` method |

## Command Testing

Test your commands with the command simulator:

```java
// Set up a simulated robot for testing
@Test
public void testDriveTrajectory() {
    // Set up a simulated robot
    Drive simulatedDrive = new Drive(new GyroIOSim(), ...);
    
    // Create a test trajectory
    HolonomicTrajectory testTrajectory = new HolonomicTrajectory("testPath");
    
    // Create the command to test
    DriveTrajectory command = new DriveTrajectory(simulatedDrive, testTrajectory);
    
    // Schedule the command
    command.schedule();
    
    // Run the command scheduler for the duration of the trajectory
    for (int i = 0; i < testTrajectory.getTotalTimeSeconds() * 50; i++) {
        CommandScheduler.getInstance().run();
        simulatedDrive.periodic();
        // Advance simulation time
    }
    
    // Verify the command completed successfully
    assertTrue(command.isFinished());
    
    // Verify the robot ended at the expected position
    Pose2d finalPose = simulatedDrive.getPose();
    assertEquals(testTrajectory.getEndPose().getX(), finalPose.getX(), 0.1);
    assertEquals(testTrajectory.getEndPose().getY(), finalPose.getY(), 0.1);
    assertEquals(testTrajectory.getEndPose().getRotation().getDegrees(), 
                 finalPose.getRotation().getDegrees(), 5.0);
}
```

## Next Steps

- Review the [API Reference](api-reference.md) for detailed method documentation
- Learn about [autonomous programming](autonomous-programming.md)
- Study [PID tuning](tuning-guide.md) for trajectory following