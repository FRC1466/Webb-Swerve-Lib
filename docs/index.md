# Webb-Swerve-Lib Documentation

Welcome to the Webb-Swerve-Lib documentation! This comprehensive guide will help you understand how to use, configure, and troubleshoot the Webb-Swerve-Lib for your FRC robot.

## What is Webb-Swerve-Lib?

Webb-Swerve-Lib is a robust swerve drive implementation for FRC robots developed by Team 1466 (Webb School Robotics). Built on the AdvantageKit framework, it provides:

- High-performance swerve drive control
- Trajectory generation and following
- Autonomous path planning and execution
- Simulation support for testing without hardware
- Extensive telemetry and logging
- Built-in odometry and pose estimation

## Documentation Structure

### Core Concepts
- [Getting Started](getting-started.md) - First steps and installation
- [Configuring Your Robot](configuring-robot.md) - Set up the library for your specific robot
- [Understanding Swerve Drive](understanding-swerve.md) - Key concepts behind swerve drive technology

### Implementation
- [Module Configuration](module-configuration.md) - How to configure individual swerve modules
- [Drive Constants](drive-constants.md) - Configuring drive parameters
- [Creating Trajectories](creating-trajectories.md) - Learn to create and use trajectories
- [Autonomous Programming](autonomous-programming.md) - Build autonomous routines

### Technical Details
- [Core Components](core-components.md) - Learn about the RobotState and other core systems
- [Command Structure](command-structure.md) - Understanding the command hierarchy
- [Odometry and Localization](odometry-localization.md) - How the robot tracks its position
- [Utility Classes](utility-classes.md) - Documentation for helper utilities
- [Vision System](vision-system.md) - Details on the vision system and its usage
- [Hardware Abstraction Layer (HAL)](hal.md) - Information on HAL and when to disable it

### Reference
- [API Reference](api-reference.md) - Detailed class and method documentation
- [Common Issues](common-issues.md) - Troubleshooting frequent problems
- [Tuning Guide](tuning-guide.md) - How to fine-tune your swerve drive performance

## Quick Start

1. Add Webb-Swerve-Lib as a dependency to your project (see [Getting Started](getting-started.md))
2. Configure your robot's physical parameters and motor IDs (see [Configuring Your Robot](configuring-robot.md))
3. Create trajectory paths for autonomous (see [Creating Trajectories](creating-trajectories.md))
4. Generate trajectory files using the provided tools
5. Implement autonomous routines that follow the generated trajectories (see [Autonomous Programming](autonomous-programming.md))

## For Maintainers

If you're maintaining this library for future Team 1466 members:

1. Keep the documentation up-to-date with code changes
2. Add examples for common use cases
3. Document any issues or quirks discovered during competition
4. Update the [Common Issues](common-issues.md) page with new solutions

## Additional Resources

- [GitHub Repository](https://github.com/FRC1466/Webb-Swerve-Lib)
- [WPILib Documentation](https://docs.wpilib.org/)
- [AdvantageKit Documentation](https://github.com/Mechanical-Advantage/AdvantageKit)