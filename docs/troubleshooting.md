# Troubleshooting

This page provides solutions for common issues encountered when using Webb-Swerve-Lib.

## Drive System Issues

### Modules Not Aligning Properly

**Symptoms:**
- Wheels point in incorrect directions
- Robot doesn't drive straight
- Modules oscillate or jitter

**Solutions:**
1. **Check encoder offsets:**
   - Manually align all wheels to face forward
   - Create a temporary program that prints raw encoder values
   - Update the encoder offsets in `DriveConstants.java`

2. **Verify encoder wiring:**
   - Ensure absolute encoders are properly connected
   - Check for loose connections or damaged wires

3. **Check turn motor configuration:**
   - Verify turn motor inversion settings
   - Check PID values for the turn motors

4. **Verify encoder settings:**
   - Check encoder inversion settings
   - Ensure the encoder type is correctly configured

### Robot Doesn't Move as Expected

**Symptoms:**
- Robot moves in wrong direction
- Robot doesn't reach target speed
- Inconsistent movement

**Solutions:**
1. **Check drive motor configuration:**
   - Verify drive motor inversion settings
   - Check PID and feedforward values

2. **Verify odometry:**
   - Check if the pose estimation is accurate
   - Reset odometry if it's drifting

3. **Check motor controllers:**
   - Verify CAN IDs match the constants
   - Ensure motors have proper firmware
   - Check that motors are receiving power

4. **Verify module translations:**
   - Check that the module positions in `DriveConstants.java` match your physical robot

### Robot Drifts or Rotates Unexpectedly

**Symptoms:**
- Robot turns while trying to drive straight
- Inaccurate field-relative movement

**Solutions:**
1. **Check gyro configuration:**
   - Verify gyro is properly mounted
   - Check gyro mounting orientation
   - Ensure gyro is initialized and calibrated

2. **Calibrate modules:**
   - Run through the module offset calibration again
   - Ensure all modules have accurate offsets

3. **Check for mechanical issues:**
   - Inspect wheel wear and replace if necessary
   - Verify all modules have equal friction
   - Check for loose mechanical components

## Trajectory and Autonomous Issues

### Trajectory Generation Fails

**Symptoms:**
- Error messages when running generation tasks
- Missing trajectory files
- Incomplete trajectory files

**Solutions:**
1. **Check trajectory generation service:**
   - Verify the service is running on the expected port
   - Check for any network issues

2. **Validate trajectory definitions:**
   - Check for invalid poses or constraints
   - Verify waypoint sequencing is logical
   - Ensure path segments connect properly

3. **Inspect environment variables:**
   - If using `GENERATE_EMPTY_DRIVE_TRAJECTORIES`, ensure it's set correctly

4. **Check for Java errors:**
   - Look for stack traces in the generation output
   - Check for any illegal arguments in trajectory definitions

### Robot Doesn't Follow Trajectories Correctly

**Symptoms:**
- Robot deviates from expected path
- Robot stops before completing trajectory
- Poor tracking of the desired path

**Solutions:**
1. **Check PID tuning:**
   ```java
   // Adjust these values in your trajectory following command
   linearkP.initDefault(4.0); // Try increasing for tighter path following
   linearkD.initDefault(0.1); // Add some derivative term to reduce oscillation
   thetakP.initDefault(5.0);  // Try increasing for better heading control
   thetakD.initDefault(0.1);  // Add derivative term to reduce oscillation
   ```

2. **Verify starting position:**
   - Ensure robot starts exactly at the trajectory's starting pose
   - Reset the robot's odometry to match the starting position

3. **Check trajectory constraints:**
   - Reduce maximum velocity and acceleration if tracking is poor
   - Check that constraints are within the robot's physical capabilities

4. **Verify trajectory file:**
   - Confirm trajectory was generated correctly
   - Check that the path appears as expected when visualizing

### Autonomous Routine Timing Issues

**Symptoms:**
- Actions occur too early or too late
- Robot finishes trajectory before next action is ready
- Trajectory following takes too long

**Solutions:**
1. **Use Command timing properly:**
   ```java
   // Use sequential commands for precise ordering
   new SequentialCommandGroup(
       new DriveTrajectory(drive, trajectory1),
       new WaitCommand(0.2), // Add small buffer if needed
       new GamePieceCommand()
   )
   ```

2. **Adjust trajectory speed:**
   - Modify trajectory constraints for better timing
   - Use slower speeds when precision is critical

3. **Use event markers:**
   - Create logical markers in autonomous sequences
   - Log timestamped events for debugging

## Hardware Connection Issues

### CAN Bus Communication Problems

**Symptoms:**
- Frequent disconnects
- Delayed responses
- Missing motor controllers

**Solutions:**
1. **Optimize CAN bus updates:**
   ```java
   // Reduce CAN message frequency for non-critical updates
   motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 20);
   motor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 40);
   ```

2. **Check physical connections:**
   - Inspect for damaged CAN wires
   - Verify termination resistors are installed
   - Look for loose connections

3. **Split CAN bus:**
   - Consider using multiple CAN buses for different subsystems
   - Prioritize drivetrain on a dedicated CAN bus

### Robot Code Crashes or Freezes

**Symptoms:**
- Robot code unexpectedly stops
- Code crashes during specific maneuvers
- Drive system becomes unresponsive

**Solutions:**
1. **Check for exceptions:**
   - Review robot logs for exceptions
   - Add try/catch blocks around critical code

2. **Monitor CPU usage:**
   - Check for computationally intensive operations
   - Optimize loops and frequent calculations

3. **Verify dependencies:**
   - Check that all required libraries are installed
   - Verify AdvantageKit and other dependencies are compatible

## Simulation Issues

### Simulation Behavior Differs from Real Robot

**Symptoms:**
- Autonomous works in simulation but not on real robot
- Trajectories appear different in simulation

**Solutions:**
1. **Check simulation constants:**
   - Ensure simulation models match real robot
   - Adjust simulation friction and constraints

2. **Verify hardware differences:**
   - Account for physical limitations not modeled in simulation
   - Add realistic delays and acceleration limits

### Simulation Crashes or Has Visual Glitches

**Symptoms:**
- Simulation crashes
- Robot visualization behaves erratically

**Solutions:**
1. **Update WPILib:**
   - Ensure you're using the latest WPILib release
   - Check for known simulation issues

2. **Check available resources:**
   - Monitor CPU and memory usage
   - Close other resource-intensive applications

## Performance Tuning

### Sluggish Response

**Symptoms:**
- Robot responds slowly to commands
- Delayed movement or rotation

**Solutions:**
1. **Tune PID values:**
   - Increase P term for more responsive control
   - Add D term to reduce settling time
   - Consider adding feedforward terms

2. **Optimize code execution:**
   - Remove unnecessary computations from critical loops
   - Check for blocking operations

### Oscillation or Overshoot

**Symptoms:**
- Robot oscillates around target points
- Overshoots desired position or angle

**Solutions:**
1. **Adjust PID tuning:**
   - Decrease P term to reduce overshoot
   - Increase D term to dampen oscillations
   - Consider adjusting maximum acceleration

2. **Check mechanical system:**
   - Look for backlash in the drivetrain
   - Check for loose components

## Common Error Messages

### "Could not load trajectory"

**Problem:** The specified trajectory file could not be found or loaded.

**Solutions:**
1. Verify trajectory name matches exactly what was generated
2. Check that trajectory files are deployed in the correct location
3. Run trajectory generation to ensure files are created

### "Failed to connect to trajectory service"

**Problem:** The trajectory generation service is not accessible.

**Solutions:**
1. Verify the service is running
2. Check network connection to the service
3. Verify port configurations

### "Invalid path segment" or "Invalid trajectory"

**Problem:** There's an issue with the trajectory definition.

**Solutions:**
1. Check waypoint coordinates
2. Ensure segments connect properly
3. Verify constraints are reasonable

## Getting Further Help

If the troubleshooting steps above don't resolve your issue, consider:

1. **Logging detailed data:**
   - Enable comprehensive logging
   - Use AdvantageScope to visualize robot behavior

2. **Check GitHub issues:**
   - Search for similar issues in the Webb-Swerve-Lib repository
   - Consider creating a new issue with detailed information

3. **Community resources:**
   - Post in FIRST forums with specific error details
   - Share videos or logs of the issue occurring