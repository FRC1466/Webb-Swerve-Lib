# Common Issues and Solutions

This guide documents frequently encountered issues when working with Webb-Swerve-Lib and their solutions. Use this as a reference when troubleshooting problems.

## Initial Setup Issues

### Modules Not Moving or Moving Incorrectly

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Motor ID Misconfiguration** | One or more modules not responding | Incorrect CAN IDs in configuration | Verify motor IDs using Phoenix Tuner |
| **Reversed Motors** | Module moves in wrong direction | Drive/steer motor wired backwards or software config incorrect | Invert motor direction in code or swap connector polarity |
| **Faulty Motor Controllers** | Module unresponsive or intermittent | Controller damaged or losing connection | Replace controller or check connections |
| **Control Mode Mismatch** | Erratic movement | Using wrong control mode (position vs. velocity) | Ensure proper control mode selection |

### Gyro Problems

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Gyro Not Connected** | Robot Rotation issues, "Disconnected gyro" alert | Bad wiring or initialization | Check wiring, reboot gyro, verify firmware |
| **Inverted Gyro** | Robot turns opposite to expected | Gyro direction inverted | Update inversion setting in gyro configuration |
| **Gyro Drift** | Gradually increasing heading error | Gyro calibration issues | Allow longer gyro calibration time on startup |

## Odometry Issues

### Position Tracking Errors

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Odometry Drift** | Autonomous paths veer off course | Wheel slippage, incorrect wheel diameter | Tune wheel diameter in configuration, add vision for corrections |
| **Initial Pose Issues** | Robot starts autonomous at wrong position | Incorrect reset pose | Verify initial pose setup, check field coordinates |
| **Accelerating Drift** | Drift gets worse over time | Accumulating integration errors | Reset pose periodically with vision if available |

### Calibration Problems

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Module Alignment Offset** | Wheels not aligned when robot powered on | Incorrect absolute encoder offset | Recalibrate module offsets following the procedure below |
| **Inconsistent Module Response** | Some modules behave differently | Inconsistent PID tuning | Ensure all module PID values match |

## Trajectory Following Issues

### Path Execution Problems

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Path Not Found** | "Trajectory not found" error | Missing or misspelled trajectory name | Check trajectory name in the `DriveTrajectories` class |
| **Path Too Aggressive** | Robot tips or slips | Velocity/acceleration too high | Reduce max velocity and acceleration constraints |
| **Robot Doesn't Follow Path** | Path deviates significantly | Poor PID tuning | Tune PID values in `DriveTrajectory` class |
| **Heading Issues** | Robot moves correctly but faces wrong way | Missing heading waypoints | Add explicit heading waypoints |

### Performance Tuning

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Slow Response** | Robot lags behind trajectory | PID values too conservative | Increase proportional gain carefully |
| **Oscillation** | Robot wobbles while following path | PID values too aggressive | Reduce proportional gain, increase derivative gain |
| **Steady-State Error** | Robot consistently off from target | Insufficient integral term | Add small integral gain to eliminate steady-state error |

## Module Hardware Issues

### Mechanical Problems

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Module Jamming** | Module suddenly stops turning | Physical obstruction or mechanical failure | Inspect module for debris or damage |
| **Wheel Slippage** | Poor traction, spinning in place | Low friction, excessive acceleration | Reduce acceleration, clean wheels, check surface |
| **Gear Train Issues** | Noise or inconsistent movement | Damaged gears or misalignment | Inspect gearbox, replace damaged components |

### Electrical Problems

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Voltage Brownout** | Robot loses power momentarily | Drawing too much current | Monitor battery voltage, reduce acceleration demands |
| **Encoder Failures** | Erratic position reporting | Loose encoder connection | Check encoder cables and connections |
| **CAN Bus Issues** | Intermittent communication | Loose CAN connections, excessive EMI | Secure CAN connections, separate signal wires from power |

## Software Configuration Issues

### Control Loop Timing

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Loop Overruns** | "Loop time overrun" warnings | Too much processing in periodic loop | Optimize code, move heavy processing to separate threads |
| **Inconsistent Updates** | Jerky module movement | Delayed control signals | Ensure consistent loop timing |

### Software Architecture Issues

| Issue | Symptoms | Possible Causes | Solutions |
|-------|----------|----------------|-----------|
| **Race Conditions** | Intermittent strange behavior | Multithreading issues | Check thread safety, especially in `RobotState` |
| **Memory Leaks** | Gradually slowing performance | Accumulated objects | Monitor memory usage, fix resource leaks |

## Procedures

### Module Offset Calibration Procedure

1. **Manual Alignment**:
   - Place the robot on a flat surface with room around it
   - Power on the robot without enabling
   - Use Phoenix Tuner to manually align all wheels pointing forward
   - Record absolute encoder positions for each module
   - Update the offsets in your `ModuleConstants` class

2. **Automated Calibration**:
   ```java
   // Add this method to your DriveSubsystem
   public void calibrateModules() {
     // Implementation details for calibrating module offsets
   }
   ```

3. **Verification**:
   - After updating offsets, power cycle the robot
   - All wheels should align forward on startup
   - Test with small movement commands to ensure correct directions

### PID Tuning Process

Follow this sequence to tune trajectory following PID:

1. **Start with conservative values**:
   - Set all gains to zero
   - Add small proportional gain first (e.g., 0.1)
   
2. **Gradually increase proportional gain until oscillation**:
   - Test with a simple straight-line path
   - Increase P until you see slight oscillation
   - Back off P by 20%
   
3. **Add derivative gain to dampen oscillation**:
   - Start with a small D value (e.g., P/10)
   - Increase until oscillation is damped
   
4. **Add small integral gain if needed for steady-state error**:
   - Start very small (e.g., P/100)
   - Be cautious of integral windup

## Debugging Tools

### Using AdvantageScope

AdvantageScope is a powerful tool for analyzing robot behavior:

1. **Path Visualization**:
   - View both commanded and actual robot paths
   - Compare expected vs. actual module states
   
2. **Timing Analysis**:
   - Check loop execution timing
   - Identify processing bottlenecks
   
3. **Data Correlation**:
   - Link issues to specific events or commands
   - Track multiple variables simultaneously

### Logging Best Practices

1. **Essential Data to Log**:
   - Robot pose (estimated and target)
   - Module states (commanded and measured)
   - Control loop timing
   - Battery voltage
   
2. **Setting Up Effective Logging**:
   ```java
   // In your Drive subsystem
   Logger.recordOutput("Drive/ModuleStates/Measured", getModuleStates());
   Logger.recordOutput("Drive/ModuleStates/Setpoints", setpointStates);
   Logger.recordOutput("Drive/Pose", getPose());
   ```

## Next Steps

- Check out the [tuning guide](tuning-guide.md) for detailed performance tuning
- Learn about [odometry and localization](odometry-localization.md)
- Review the [API reference](api-reference.md) for detailed method documentation