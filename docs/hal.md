# Hardware Abstraction Layer (HAL)

## Overview
The Hardware Abstraction Layer (HAL) isolates hardware-specific code from the rest of the robot code. This allows for easier testing and development, as well as better code organization.

## Disabling HAL
In some cases, you may need to disable the HAL, such as when running simulations or using certain testing frameworks. To disable the HAL, set the `disableHAL` flag to `true` in your configuration:
```java
Constants.disableHAL = true;
```

## When to Disable HAL
- **Simulation**: When running robot code in a simulated environment.
- **Testing**: When using testing frameworks that do not support the HAL.
- **Development**: When developing code that does not interact with hardware.

## Example
To disable the HAL for simulation, update your robot initialization code:
```java
public class Robot extends TimedRobot {
    @Override
    public void robotInit() {
        Constants.disableHAL = true;
        // Other initialization code
    }
}
```
