# Creating Trajectories

This guide explains how to create, generate, and use trajectories with Webb-Swerve-Lib for autonomous robot movement.

## Trajectory Basics

A trajectory in Webb-Swerve-Lib is composed of:
- A series of waypoints (positions and headings)
- Constraints (velocities, accelerations, etc.)
- Motion profiling for smooth movement

The library uses these elements to generate optimized paths that your robot can follow with precision.

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
        // First segment
        PathSegment.newBuilder()
            .addPoseWaypoint(new Pose2d(0, 0, new Rotation2d(0)))
            .addTranslationWaypoint(new Translation2d(1, 0.5)) // Intermediate point (position only)
            .addPoseWaypoint(new Pose2d(2, 1, Rotation2d.fromDegrees(45)))
            .setMaxVelocity(2.0) // Limit max velocity to 2.0 m/s for this segment
            .build(),
        
        // Second segment (connected to the first)
        PathSegment.newBuilder()
            .addPoseWaypoint(new Pose2d(2, 1, Rotation2d.fromDegrees(45))) // Starting where first segment ends
            .addPoseWaypoint(new Pose2d(3, 3, Rotation2d.fromDegrees(90)))
            .setMaxVelocity(1.5) // Different constraints for this segment
            .setMaxOmega(Math.PI) // Limit rotation rate
            .build()
    )
);
```

### Helper Methods

The `TrajectoryGenerationHelpers` class provides utility methods to simplify path creation:

```java
// Adding a pose waypoint
PathSegment.Builder segmentBuilder = PathSegment.newBuilder();
segmentBuilder.addPoseWaypoint(new Pose2d(1, 2, new Rotation2d(Math.PI/2)));

// Or using the extension method
segmentBuilder = segmentBuilder.addPoseWaypoint(new Pose2d(1, 2, new Rotation2d(Math.PI/2)));

// Adding a waypoint with just position (no heading)
segmentBuilder = segmentBuilder.addTranslationWaypoint(new Translation2d(2, 3));

// Continuing from a previous trajectory
segmentBuilder = segmentBuilder.addContinuationWaypoint(previousTrajectory);
```

## Connecting Trajectories

You can chain trajectories for more complex autonomous routines:

```java
// Define a dependent trajectory
suppliedPaths.add(
    (completedPaths) -> {
        // Only generate this path if "firstPath" has been processed
        if (!completedPaths.contains("firstPath")) {
            return null;
        }
        
        Map<String, List<PathSegment>> result = new HashMap<>();
        result.put(
            "secondPath",
            List.of(
                PathSegment.newBuilder()
                    // Start from the end of the first path
                    .addWaypoints(getLastWaypoint("firstPath"))
                    .addPoseWaypoint(new Pose2d(4, 2, Rotation2d.fromDegrees(180)))
                    .build()
            )
        );
        return result;
    }
);
```

## Velocity Constraints

Control the speed of your robot along the path:

```java
PathSegment.newBuilder()
    .addPoseWaypoint(startPose)
    .addPoseWaypoint(endPose)
    .setMaxVelocity(2.0) // Maximum linear velocity in m/s
    .setMaxOmega(Math.PI) // Maximum angular velocity in rad/s
    .build()
```

For specific velocities at waypoints:

```java
Waypoint.Builder waypointBuilder = Waypoint.newBuilder()
    .withPose(pose)
    .withLinearVelocity(new Translation2d(1.0, 0.0)); // X velocity of 1.0 m/s, Y velocity of 0.0 m/s

// Or set to zero velocity at endpoint
waypointBuilder.setZeroVelocity();
```

## Generating Trajectory Files

After defining your trajectories in `DriveTrajectories.java`, you need to generate the trajectory files:

1. **Run the generator:**

   ```bash
   ./gradlew runTrajectoryGeneration
   ```

   This command runs the `GenerateTrajectories` class which:
   - Creates a vehicle model based on your robot's physical characteristics
   - Connects to the trajectory generation service
   - Processes all trajectory definitions
   - Generates optimized paths
   - Saves the paths as `.pathblob` files in `src/main/deploy/trajectories/`

2. **Deploy the trajectories:**

   ```bash
   ./gradlew deploy
   ```

   This command deploys the trajectory files to your robot.

## Using Trajectories in Autonomous Routines

Once generated, you can use the trajectories in your autonomous routines:

```java
public Command getAutonomousCommand() {
    // Load the trajectory
    HolonomicTrajectory trajectory = new HolonomicTrajectory("yourTrajectoryName");
    
    // Create a command to follow the trajectory
    return new DriveTrajectory(driveSubsystem, trajectory);
}
```

## Field Coordinates and Waypoint Positioning

It's important to understand the field coordinate system:

- Origin (0,0) is at the bottom-left corner of the field
- X-axis points away from your alliance wall
- Y-axis points to the left when standing at your alliance wall
- Rotations are measured counter-clockwise from the X-axis

Positioning waypoints effectively:

1. Use field landmarks as reference points
2. Account for the robot's dimensions:
   ```java
   // Position robot center 1 meter from a wall
   new Pose2d(1.0 + robotLength/2, y, rotation)
   ```

3. Use the `FieldConstants` class for predefined field positions

## Tips for Effective Trajectory Creation

1. **Start Simple**
   - Create basic trajectories first
   - Test and validate before adding complexity

2. **Smooth Paths**
   - Add intermediate waypoints to avoid sharp turns
   - Use appropriate velocity constraints for different segments

3. **Testing and Iteration**
   - Visualize trajectories in simulation
   - Start with slower velocities and gradually increase
   - Test on open parts of the field before tight spaces

4. **Common Patterns**
   - Create reusable patterns for common movements
   - Build a library of trajectory segments

## Debugging Trajectories

If you encounter issues with trajectories:

1. **Visualization**
   - Use the `Trajectory/TrajectoryPoses` logged output to visualize the path in AdvantageScope

2. **Common Problems**
   - Check waypoint coordinates and orientations
   - Ensure constraints are reasonable
   - Verify dependency chain for supplied paths

3. **Advanced Debugging**
   - Look at individual segments and state transitions
   - Examine velocity and acceleration profiles

## Next Steps

Now that you understand how to create trajectories:
- Learn about [autonomous programming](autonomous-programming.md)
- See the [API reference](api-reference.md) for more details
- Check out [troubleshooting](troubleshooting.md) if you encounter issues