# Autonomous Programming

This guide explains how to create autonomous routines with Webb-Swerve-Lib using generated trajectories.

## Autonomous Basics

Autonomous routines in Webb-Swerve-Lib use the WPILib command framework to sequence actions. A typical autonomous routine will:

1. Follow one or more trajectories
2. Execute game-specific actions (shooting, intaking, etc.)
3. Respond to sensor inputs when needed
4. Handle timing and coordination of subsystems

## Loading and Using Trajectories

Once you've [created and generated trajectories](creating-trajectories.md), you can use them in your autonomous routines:

```java
public class ExampleAuto extends SequentialCommandGroup {
    public ExampleAuto(Drive driveSubsystem, OtherSubsystem otherSubsystem) {
        // Load the trajectory
        HolonomicTrajectory driveToGoalTrajectory = new HolonomicTrajectory("driveToGoal");
        
        addCommands(
            // Drive to goal
            new DriveTrajectory(driveSubsystem, driveToGoalTrajectory),
            
            // Perform an action (e.g., score)
            new ScoreCommand(otherSubsystem),
            
            // Drive to another position
            new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("driveToNextPosition"))
        );
    }
}
```

## Trajectory Following Using DriveTrajectory

The `DriveTrajectory` command is the core component for following trajectories in autonomous:

```java
// Basic usage
new DriveTrajectory(driveSubsystem, trajectory);

// With mirroring (for opposite alliance)
new DriveTrajectory(driveSubsystem, trajectory, true);

// With custom pose supplier
new DriveTrajectory(driveSubsystem, trajectory, customPoseSupplier, false);
```

### How DriveTrajectory Works

The `DriveTrajectory` command:

1. Resets controllers at initialization
2. Samples the trajectory based on elapsed time
3. Applies feedback corrections based on current robot pose
4. Computes and sends velocity commands to the drive subsystem
5. Continues until the trajectory duration is reached

## Creating a Complete Autonomous Routine

Here's a complete example of an autonomous routine that follows trajectories and interacts with other subsystems:

```java
public class ThreeScoreAuto extends SequentialCommandGroup {
    public ThreeScoreAuto(Drive drive, Shooter shooter, Intake intake) {
        addCommands(
            // Start with an initial delay if needed
            new WaitCommand(0.1),
            
            // Score preloaded game piece
            new ScoreCommand(shooter),
            
            // Drive to pickup position
            new DriveTrajectory(drive, new HolonomicTrajectory("driveToPickup")),
            
            // Run intake while continuing to drive
            new ParallelCommandGroup(
                new IntakeCommand(intake),
                new DriveTrajectory(drive, new HolonomicTrajectory("driveWhileIntaking"))
            ),
            
            // Return to scoring position
            new DriveTrajectory(drive, new HolonomicTrajectory("returnToScore")),
            
            // Score second game piece
            new ScoreCommand(shooter),
            
            // Repeat for third game piece...
            new DriveTrajectory(drive, new HolonomicTrajectory("driveToSecondPickup")),
            new IntakeCommand(intake),
            new DriveTrajectory(drive, new HolonomicTrajectory("returnToScoreAgain")),
            new ScoreCommand(shooter)
        );
    }
}
```

## Conditional Autonomous Logic

For more complex autonomous routines that need to make decisions based on sensors or game state:

```java
public class ConditionalAuto extends SequentialCommandGroup {
    public ConditionalAuto(Drive drive, Vision vision, Shooter shooter) {
        addCommands(
            // Initial trajectory
            new DriveTrajectory(drive, new HolonomicTrajectory("initialPath")),
            
            // Conditional command based on vision
            new ConditionalCommand(
                // If target detected
                new SequentialCommandGroup(
                    new AimAtTargetCommand(drive, vision),
                    new ShootCommand(shooter)
                ),
                // If no target detected
                new SequentialCommandGroup(
                    new DriveTrajectory(drive, new HolonomicTrajectory("fallbackPath")),
                    new ShootCommand(shooter)
                ),
                // Condition
                vision::hasTarget
            )
        );
    }
}
```

## Alliance Color Handling

Autonomous routines often need to adapt based on alliance color. Webb-Swerve-Lib provides mirroring capabilities:

```java
public Command getAutonomousCommand() {
    HolonomicTrajectory trajectory = new HolonomicTrajectory("trajectoryName");
    
    // Mirror the trajectory if on the blue alliance
    boolean mirror = DriverStation.getAlliance().isPresent() && 
                    DriverStation.getAlliance().get() == Alliance.Blue;
    
    return new DriveTrajectory(drive, trajectory, mirror);
}
```

## Integrating with Field Elements

To interact with field elements during autonomous:

1. **Define field element positions in FieldConstants:**

   ```java
   public class FieldConstants {
       public static final double fieldLength = 16.54;
       public static final double fieldWidth = 8.21;
       
       public static class ScoringLocations {
           public static final Pose2d highGoal = new Pose2d(15.2, 4.1, Rotation2d.fromDegrees(0));
           // Other field elements...
       }
   }
   ```

2. **Use these constants in trajectory creation:**

   ```java
   // In DriveTrajectories.java
   paths.put(
       "driveToScore",
       List.of(
           PathSegment.newBuilder()
               .addPoseWaypoint(startPose)
               .addPoseWaypoint(
                   // Position slightly in front of the goal
                   new Pose2d(
                       FieldConstants.ScoringLocations.highGoal.getX() - 0.5,
                       FieldConstants.ScoringLocations.highGoal.getY(),
                       FieldConstants.ScoringLocations.highGoal.getRotation()
                   )
               )
               .build()
       )
   );
   ```

## Performance Tuning

To optimize autonomous performance:

1. **PID Tuning:**
   ```java
   // Tune these values for your specific robot
   private static final LoggedTunableNumber linearkP = new LoggedTunableNumber("DriveTrajectory/LinearkP", 8.0);
   private static final LoggedTunableNumber linearkD = new LoggedTunableNumber("DriveTrajectory/LinearkD", 0.0);
   private static final LoggedTunableNumber thetakP = new LoggedTunableNumber("DriveTrajectory/ThetakP", 4.0);
   private static final LoggedTunableNumber thetakD = new LoggedTunableNumber("DriveTrajectory/ThetakD", 0.0);
   ```

2. **Start with lower velocities** and gradually increase as you validate your trajectories.

3. **Log and visualize performance** using AdvantageScope to identify issues.

## Custom Heading Control

For advanced autonomous routines where you need to control the heading independently of the path:

```java
public Command createAutoCommand() {
    DriveTrajectory trajectoryCommand = new DriveTrajectory(drive, trajectory);
    
    // Override heading to face a specific direction (e.g., toward a target)
    trajectoryCommand.setOverrideRotation(Optional.of(Rotation2d.fromDegrees(90)));
    
    return trajectoryCommand;
}
```

## Multi-Path Autonomous Selection

For competition, it's useful to have multiple autonomous routines selectable from the dashboard:

```java
public class RobotContainer {
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();
    
    public RobotContainer() {
        // Configure autonomous options
        autoChooser.setDefaultOption("Two Score", new TwoScoreAuto(drive, shooter, intake));
        autoChooser.addOption("Three Score", new ThreeScoreAuto(drive, shooter, intake));
        autoChooser.addOption("Defense", new DefenseAuto(drive));
        
        // Put the chooser on the dashboard
        SmartDashboard.putData("Auto Chooser", autoChooser);
    }
    
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
```

## Testing and Debugging

For effective testing of autonomous routines:

1. **Simulation First:**
   - Test trajectories in simulation before running on the robot
   - Visualize paths and robot position using AdvantageScope

2. **Incremental Testing:**
   - Test one trajectory segment at a time
   - Validate each action before combining them

3. **Debugging Tools:**
   - Use `Logger.recordOutput()` to log key data points
   - Add "checkpoints" in autonomous code to identify where issues occur

4. **Common Issues:**
   - Initial pose mismatch (ensure robot starts at the expected position)
   - Trajectory following errors (adjust PID values or constraints)
   - Subsystem timing issues (add small delays or use proper command chaining)

## Next Steps

Now that you understand how to create autonomous routines:
- Explore the [API reference](api-reference.md) for advanced features
- See [troubleshooting tips](troubleshooting.md) if you encounter issues
- Learn about [advanced features and optimization](advanced-features.md)