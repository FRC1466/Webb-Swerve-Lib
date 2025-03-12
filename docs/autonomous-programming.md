# Autonomous Programming

This guide explains how to create effective autonomous routines with Webb-Swerve-Lib using generated trajectories and WPILib's command-based programming.

## Autonomous Architecture

Webb-Swerve-Lib uses a command-based approach to autonomous programming, allowing you to:

1. Sequence multiple trajectories and actions
2. Create reusable components for various game strategies
3. Handle timing and parallel execution of subsystems
4. Respond to sensor inputs during autonomous execution

## Components of Autonomous Routines

### DriveTrajectory Command

The fundamental command for trajectory following is `DriveTrajectory`, which drives the robot along a specified path:

```java
public class DriveTrajectory extends Command {
    private final Drive driveSubsystem;
    private final HolonomicTrajectory trajectory;
    private final PIDController xController;
    private final PIDController yController;
    private final PIDController rotationController;
    
    // Constructor and implementation details
}
```

### Creating Autonomous Routines

Autonomous routines are created by combining trajectory following with other game-specific actions:

```java
public class ExampleAuto extends SequentialCommandGroup {
    public ExampleAuto(Drive driveSubsystem, IntakeSubsystem intakeSubsystem) {
        // Load trajectories
        HolonomicTrajectory driveToNoteTrajectory = new HolonomicTrajectory("driveToNote");
        HolonomicTrajectory driveToSpeakerTrajectory = new HolonomicTrajectory("driveToSpeaker");
        
        // Add sequential commands
        addCommands(
            // Drive to game piece
            new DriveTrajectory(driveSubsystem, driveToNoteTrajectory),
            
            // Intake game piece
            new IntakeCommand(intakeSubsystem).withTimeout(1.0),
            
            // Drive to scoring position
            new DriveTrajectory(driveSubsystem, driveToSpeakerTrajectory),
            
            // Score game piece
            new ScoreCommand(intakeSubsystem)
        );
    }
}
```

## Loading and Using Trajectories

Once you've [created and generated trajectories](creating-trajectories.md), you can use them in your autonomous routines:

```java
// Loading a trajectory
HolonomicTrajectory trajectory = new HolonomicTrajectory("trajectoryName");

// Using the trajectory with a DriveTrajectory command
DriveTrajectory driveCommand = new DriveTrajectory(driveSubsystem, trajectory);
```

## Autonomous Routine Structure

### Simple Linear Sequence

For a basic autonomous routine that executes steps in order:

```java
public class SimpleAutoRoutine extends SequentialCommandGroup {
    public SimpleAutoRoutine(Drive driveSubsystem, OtherSubsystem otherSubsystem) {
        addCommands(
            // Reset robot pose to trajectory starting point
            new InstantCommand(() -> driveSubsystem.setPose(
                new Pose2d(1.5, 5.5, Rotation2d.fromDegrees(180))
            )),
            
            // Follow first trajectory
            new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("path1")),
            
            // Execute an action
            new ActionCommand(otherSubsystem),
            
            // Follow second trajectory
            new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("path2"))
        );
    }
}
```

### Parallel Actions

To perform actions while driving:

```java
public class ParallelAutoRoutine extends SequentialCommandGroup {
    public ParallelAutoRoutine(Drive driveSubsystem, ArmSubsystem armSubsystem) {
        addCommands(
            // Reset pose
            new InstantCommand(() -> driveSubsystem.setPose(
                new Pose2d(1.5, 5.5, Rotation2d.fromDegrees(180))
            )),
            
            // Drive while positioning arm
            new ParallelCommandGroup(
                new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("approachTarget")),
                new PositionArmCommand(armSubsystem, ArmPosition.SCORING)
            ),
            
            // Score game piece
            new ScoreCommand(armSubsystem)
        );
    }
}
```

### Conditional Execution

For decision-making during autonomous:

```java
public class ConditionalAutoRoutine extends SequentialCommandGroup {
    public ConditionalAutoRoutine(Drive driveSubsystem, VisionSubsystem visionSubsystem, 
                                IntakeSubsystem intakeSubsystem) {
        addCommands(
            // Drive to a position where we can see targets
            new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("scanPosition")),
            
            // Decide what to do based on vision
            new ConditionalCommand(
                // If target detected
                new SequentialCommandGroup(
                    new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("approachTarget")),
                    new ScoreCommand(intakeSubsystem)
                ),
                // If no target
                new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("fallbackPath")),
                // Condition to evaluate
                visionSubsystem::hasTarget
            )
        );
    }
}
```

## Alliance-Based Path Selection

Different alliances may need mirrored autonomous paths:

```java
public class AllianceAwareAutoRoutine extends SequentialCommandGroup {
    public AllianceAwareAutoRoutine(Drive driveSubsystem, AllianceColor alliance) {
        // Select the appropriate path based on alliance
        String pathName = (alliance == AllianceColor.RED) ? 
            "redSidePathToScore" : "blueSidePathToScore";
        
        addCommands(
            // Reset pose with appropriate alliance starting position
            new InstantCommand(() -> driveSubsystem.setPose(
                getAllianceStartingPose(alliance)
            )),
            
            // Execute the alliance-specific trajectory
            new DriveTrajectory(driveSubsystem, new HolonomicTrajectory(pathName))
        );
    }
    
    private Pose2d getAllianceStartingPose(AllianceColor alliance) {
        if (alliance == AllianceColor.RED) {
            return new Pose2d(15.0, 5.5, Rotation2d.fromDegrees(0));
        } else {
            return new Pose2d(1.5, 5.5, Rotation2d.fromDegrees(180));
        }
    }
}
```

## Autonomous Mode Selection

Using the WPILib SendableChooser to select autonomous routines:

```java
public class RobotContainer {
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();
    
    public RobotContainer() {
        // Configure subsystems, bindings, etc.
        
        // Configure autonomous options
        configureAutonomousChooser();
    }
    
    private void configureAutonomousChooser() {
        // Add options to the chooser
        autoChooser.setDefaultOption("2 Ball Auto", new TwoBallAuto(drive, intake, shooter));
        autoChooser.addOption("3 Ball Auto", new ThreeBallAuto(drive, intake, shooter));
        autoChooser.addOption("Defense Auto", new DefenseAuto(drive));
        autoChooser.addOption("Do Nothing", new InstantCommand());
        
        // Put the chooser on the dashboard
        SmartDashboard.putData("Auto Mode", autoChooser);
    }
    
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
```

## PID Tuning for Trajectory Following

The `DriveTrajectory` command uses PID controllers for tracking. These need proper tuning:

```java
// Example PID values for trajectory following
public static final double kPTranslation = 5.0;
public static final double kITranslation = 0.0;
public static final double kDTranslation = 0.0;

public static final double kPRotation = 5.0;
public static final double kIRotation = 0.0;
public static final double kDRotation = 0.1;
```

### Tuning Process

1. **Start with Translation**: Tune translational controllers first
   - Increase kP until the robot follows the path with minimal lag
   - Add small kD if oscillation occurs
   - Add minimal kI only if steady-state error persists

2. **Then Tune Rotation**: Tune the rotational controller
   - Increase kP until rotation tracking is responsive
   - Add kD to reduce oscillation
   - Rotation typically needs higher damping (kD)

## Teleop to Autonomous Transitions

For smooth competition play, handle transitions between teleop and autonomous:

```java
public class Robot extends TimedRobot {
    private Command autonomousCommand;
    private RobotContainer robotContainer;
    
    @Override
    public void autonomousInit() {
        // Get the selected autonomous command
        autonomousCommand = robotContainer.getAutonomousCommand();
        
        // Schedule the autonomous command
        if (autonomousCommand != null) {
            autonomousCommand.schedule();
        }
    }
    
    @Override
    public void teleopInit() {
        // End autonomous command when teleop starts
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        
        // Set brake mode for teleop
        robotContainer.getDrive().setBrakeMode(true);
    }
}
```

## Event Markers

Webb-Swerve-Lib supports event markers for triggering actions at specific points along trajectories:

```java
// Create a trajectory with event markers
PathSegment.newBuilder()
    .addPoseWaypoint(new Pose2d(0, 0, new Rotation2d(0)))
    .addTranslationWaypoint(new Translation2d(1, 1))
    .addEvent("startIntake", 0.25) // Event at 25% along the path
    .addEvent("prepareToScore", 0.75) // Event at 75% along the path
    .addPoseWaypoint(new Pose2d(2, 2, Rotation2d.fromDegrees(90)))
    .build();

// Using event markers in an autonomous routine
public class EventMarkerAuto extends SequentialCommandGroup {
    public EventMarkerAuto(Drive driveSubsystem, IntakeSubsystem intakeSubsystem) {
        HolonomicTrajectory trajectory = new HolonomicTrajectory("pathWithEvents");
        
        // Create the drive command
        DriveTrajectory driveCommand = new DriveTrajectory(driveSubsystem, trajectory);
        
        // Configure event handlers
        driveCommand.addEventHandler("startIntake", 
            new InstantCommand(() -> intakeSubsystem.setIntakePower(1.0)));
            
        driveCommand.addEventHandler("prepareToScore", 
            new InstantCommand(() -> intakeSubsystem.prepareToScore()));
            
        addCommands(driveCommand);
    }
}
```

## Testing and Debugging Autonomous

### Visualization Tools

Webb-Swerve-Lib works with AdvantageScope for visualization:

```java
// In periodic() method of your Drive subsystem or command
Logger.recordOutput("Trajectory/DesiredPose", currentTrajectoryPose);
Logger.recordOutput("Trajectory/ActualPose", robotState.getEstimatedPose());
Logger.recordOutput("Trajectory/Error", calculatePoseError());
```

### Iterative Testing Process

1. **Start Simple**: Test basic trajectories first
2. **Test in Simulation**: Verify trajectories work in simulation before deploying
3. **Real-World Testing**: Test on the actual field, making adjustments as needed
4. **Measure Accuracy**: Record and analyze position error
5. **Tune Parameters**: Adjust PID values based on real-world performance

## Common Autonomous Issues

| Issue | Symptom | Cause | Solution |
|-------|---------|-------|----------|
| Path Not Following | Robot deviates from intended path | Insufficient PID values | Increase kP for translation and rotation |
| Oscillation | Robot weaves along path | PID values too aggressive | Reduce kP and/or increase kD |
| Pose Reset Issues | Robot starts in wrong position | Incorrect initial pose reset | Verify setPose() call at start of auto |
| Timing Problems | Actions happen too early/late | Event timing issues | Use event markers instead of time-based sequencing |
| Alliance Mismatch | Path designed for wrong alliance | Missing alliance-specific logic | Implement alliance-aware path selection |

## Field-Centric Autonomous

For strategies that need to be robust across starting positions:

```java
public class FieldCentricAuto extends SequentialCommandGroup {
    public FieldCentricAuto(Drive driveSubsystem, FieldElement targetElement) {
        // Get the current robot position
        Pose2d startingPose = RobotState.getInstance().getEstimatedPose();
        
        // Calculate path to target element
        HolonomicTrajectory pathToTarget = 
            TrajectoryGenerator.generatePathToTarget(startingPose, targetElement.getPose());
        
        addCommands(
            // Follow the dynamically generated path
            new DriveTrajectory(driveSubsystem, pathToTarget)
        );
    }
}
```

## Autonomous Strategy Planning

When designing autonomous routines, consider:

1. **Scoring Priority**: Decide which game elements to prioritize
2. **Time Management**: Balance scoring vs. mobility points
3. **Alliance Coordination**: Design routines that complement alliance partners
4. **Robustness**: Plan for potential failures or unexpected obstacles
5. **Endgame Position**: Finish autonomous in a favorable position for teleop

## Best Practices

1. **Test Thoroughly**: Validate autonomous routines in various starting conditions
2. **Build Modular Components**: Create reusable command sequences
3. **Document Strategies**: Keep notes on which routines work best for different alliances/partners
4. **Practice Driver Takeover**: Ensure drivers can smoothly take over from any autonomous ending
5. **Time Your Routines**: Know exactly how long each routine takes to execute

## Next Steps

- Learn about [odometry and localization](odometry-localization.md)
- Understand [common issues](common-issues.md) in swerve drive systems
- Study the [API reference](api-reference.md) for detailed class documentation