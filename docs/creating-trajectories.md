# Creating Trajectories

This guide explains how to create, generate, and use trajectories with Webb-Swerve-Lib for autonomous robot movement, with detailed examples and common troubleshooting steps.

## Trajectory Basics

A trajectory in Webb-Swerve-Lib is composed of:

- A series of waypoints (positions and headings)
- Constraints (velocities, accelerations, etc.)
- Motion profiling for smooth movement

The library uses these elements to generate optimized paths that your robot can follow with precision.

## Trajectory System Components

The Webb-Swerve-Lib trajectory system consists of several components:

1. **PathSegment**: The building block of trajectories that specifies waypoints and constraints
2. **HolonomicTrajectory**: A complete trajectory object that includes timing information
3. **DriveTrajectory**: A command that uses a PID controller to follow a trajectory
4. **DriveTrajectories**: A class that defines and manages available trajectories

## Defining Trajectories

Trajectories are defined in the `DriveTrajectories` class. Each trajectory is composed of one or more `PathSegment` objects.

### Basic Structure

```java
// Define a new trajectory in DriveTrajectories.java
paths.put(
    "yourTrajectoryName",
    List.of(
        PathSegment.newBuilder()
            .addPoseWaypoint(new Pose2d(0, 0, new Rotation2d(0))) // Starting position
            .addPoseWaypoint(new Pose2d(2, 1, Rotation2d.fromDegrees(45))) // Ending position
            .build()
    )
);
```

### Defining Complex Trajectories

For more complex paths with multiple waypoints and constraints:

```java
paths.put(
    "complexTrajectory",
    List.of(
        // First segment - leave starting area
        PathSegment.newBuilder()
            .addPoseWaypoint(new Pose2d(1.0, 1.0, Rotation2d.fromDegrees(0))) // Starting pose
            .addPoseWaypoint(new Pose2d(3.0, 1.0, Rotation2d.fromDegrees(0))) // Intermediate pose
            .setMaxVelocity(2.0) // m/s
            .setMaxAcceleration(1.5) // m/s^2
            .setReversed(false) // Drive forward
            .build(),
            
        // Second segment - approach scoring position
        PathSegment.newBuilder()
            .addPoseWaypoint(new Pose2d(3.0, 1.0, Rotation2d.fromDegrees(0))) // Continue from last pose
            .addHeadingWaypoint(0.5, Rotation2d.fromDegrees(45)) // Turn during movement
            .addPoseWaypoint(new Pose2d(4.0, 2.0, Rotation2d.fromDegrees(90))) // Final pose
            .setMaxVelocity(1.0) // Slower for precision
            .setMaxAcceleration(0.8)
            .build()
    )
);
```

### Waypoint Types

The library supports several types of waypoints:

1. **Pose Waypoints**: Specify position (x, y) and heading
   ```java
   .addPoseWaypoint(new Pose2d(x, y, rotation))
   ```

2. **Translation Waypoints**: Specify position only, no heading constraint
   ```java
   .addTranslationWaypoint(new Translation2d(x, y))
   ```

3. **Heading Waypoints**: Specify a heading at a particular point along the path
   ```java
   .addHeadingWaypoint(t, rotation) // t is a value from 0.0 to 1.0
   ```

## Trajectory Constraints

Control the behavior of your trajectory with constraints:

```java
.setMaxVelocity(2.0) // Maximum velocity in m/s
.setMaxAcceleration(1.5) // Maximum acceleration in m/s²
.setStartVelocity(0.0) // Starting velocity
.setEndVelocity(0.0) // Ending velocity
.setReversed(false) // Drive forward (true for reverse)
```

### Common Constraint Values

| Robot Type | Max Velocity (m/s) | Max Acceleration (m/s²) |
|------------|-------------------|------------------------|
| Light      | 7 - 10            | 2.5 - 3.5              |
| Medium     | 5 - 7             | 1.5 - 2.5              |
| Heavy      | 3.5 - 5           | 0.8 - 1.5              |

> **Note**: These are starting points. Always test and tune for your specific robot.

## Generating Trajectories

Once you've defined your trajectories, you need to generate them:

```java
// In your DriveTrajectories.java class
public DriveTrajectories() {
    // Define trajectories in the paths map
    defineTrajectories();
    
    // Generate all trajectories
    paths.forEach((name, segments) -> {
        trajectories.put(name, generateTrajectory(segments));
    });
}

private HolonomicTrajectory generateTrajectory(List<PathSegment> segments) {
    // Implementation depends on the trajectory generation method
    // This is handled internally by the library
}
```

## Using Trajectories in Autonomous Commands

Once trajectories are generated, you can use them in autonomous commands:

```java
public class SampleAutoRoutine extends SequentialCommandGroup {
    public SampleAutoRoutine(Drive driveSubsystem, OtherSubsystem otherSubsystem) {
        // Load the trajectory
        HolonomicTrajectory driveToGoalTrajectory = new HolonomicTrajectory("driveToGoal");
        
        addCommands(
            // Drive to goal position
            new DriveTrajectory(driveSubsystem, driveToGoalTrajectory),
            
            // Perform an action (e.g., score)
            new ScoreCommand(otherSubsystem),
            
            // Drive to another position
            new DriveTrajectory(driveSubsystem, new HolonomicTrajectory("driveToNextPosition"))
        );
    }
}
```

## Advanced Techniques

### Adding Intermediate Waypoints

For smoother paths, add intermediate waypoints:

```java
PathSegment.newBuilder()
    .addPoseWaypoint(new Pose2d(1.0, 1.0, new Rotation2d(0)))
    .addTranslationWaypoint(new Translation2d(1.5, 1.2)) // Intermediate point
    .addTranslationWaypoint(new Translation2d(2.0, 1.5)) // Another intermediate point
    .addPoseWaypoint(new Pose2d(3.0, 2.0, Rotation2d.fromDegrees(45)))
    .build()
```

### Creating Composite Paths

For complex maneuvers, combine multiple path segments:

```java
List<PathSegment> complexPath = new ArrayList<>();

// Add first segment (drive forward)
complexPath.add(PathSegment.newBuilder()
    .addPoseWaypoint(start)
    .addPoseWaypoint(intermediate)
    .setMaxVelocity(2.0)
    .build());

// Add second segment (turn and approach)
complexPath.add(PathSegment.newBuilder()
    .addPoseWaypoint(intermediate)
    .addHeadingWaypoint(0.5, Rotation2d.fromDegrees(45))
    .addPoseWaypoint(target)
    .setMaxVelocity(1.0)
    .build());

paths.put("complexManeuver", complexPath);
```

## Visualizing and Testing Trajectories

The library provides tools to visualize your trajectories:

1. **AdvantageScope**: View generated paths in AdvantageScope for verification
2. **Field2d**: Use the Field2d widget in Shuffleboard to see paths during testing
3. **Logging**: Enable trajectory logging to analyze execution

```java
// Log the current trajectory for visualization
Logger.recordOutput("Autonomous/CurrentTrajectory", currentTrajectory);
```

## Common Issues and Solutions

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Robot doesn't follow path | Incorrect PID values | Tune PID constants for your robot |
| Path is too aggressive | Constraints too loose | Reduce max velocity/acceleration |
| Robot overshoots targets | Momentum not accounted for | Add end velocity constraints |
| Jerky movement | Not enough waypoints | Add intermediate waypoints for smoother curves |
| Robot doesn't face correct direction | Missing heading waypoints | Add explicit heading waypoints |

### Troubleshooting Trajectory Following

If your robot isn't following trajectories correctly:

1. **Check Odometry**: Ensure your robot's position tracking is accurate
2. **Visualize Path**: Use AdvantageScope to compare intended vs. actual path
3. **Tune Controllers**: Adjust the PID controllers that follow the trajectory
4. **Check Constraints**: Make sure velocity and acceleration limits are appropriate
5. **Test Incrementally**: Start with simple paths before trying complex ones

## Best Practices

1. **Start Simple**: Begin with straight-line paths before adding complexity
2. **Use Heading Waypoints**: Explicitly define robot orientation at key points
3. **Test in Sim**: Test trajectories in simulation before deploying to the robot
4. **Iterate**: Refine trajectories based on real-world testing
5. **Document**: Keep notes on what works and what doesn't for your specific robot

## Next Steps

Now that you understand how to create trajectories:
- Learn about [autonomous programming](autonomous-programming.md)
- See the [API reference](api-reference.md) for more details
- Check out [troubleshooting](common-issues.md) if you encounter issues