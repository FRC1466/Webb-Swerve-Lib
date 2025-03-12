// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.leds;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;

public class Leds extends SubsystemBase {
  private final Spark blinkin;
  private final boolean isSimulation;

  public Leds() {
    isSimulation = RobotBase.isSimulation();

    // Only create real hardware objects when not in simulation
    if (!isSimulation) {
      blinkin = new Spark(1);
    } else {
      // In simulation, just set to null but don't use it
      blinkin = null;
    }
  }

  public void loadingLights() {
    // Check if we're in simulation mode before using hardware
    if (!isSimulation && blinkin != null) {
      blinkin.set(-0.05);
      Logger.recordOutput("Lights", "Loading");
    }
  }

  // Other methods with similar checks
  public void setPattern(double pattern) {
    if (!isSimulation && blinkin != null) {
      blinkin.set(pattern);
    }
  }

  @Override
  public void periodic() {
    // Periodic code here
  }

  public void coralLights() {
    if (!isSimulation && blinkin != null) {
      blinkin.set(.89);
      Logger.recordOutput("Lights", "Coral");
    }
  }

  public void algaeLights() {
    if (!isSimulation && blinkin != null) {
      blinkin.set(.73);
      Logger.recordOutput("Lights", "Algae");
    }
  }

  public void rainbowPartyLights() {
    if (!isSimulation && blinkin != null) {
      blinkin.set(-.97);
      Logger.recordOutput("Lights", "Auto");
    }
  }

  public void warningLights() {
    if (!isSimulation && blinkin != null) {
      blinkin.set(-0.17);
      Logger.recordOutput("Lights", "Warning");
    }
  }
}
