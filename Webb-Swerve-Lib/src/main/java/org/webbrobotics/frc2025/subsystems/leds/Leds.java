// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.leds;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import org.littletonrobotics.junction.Logger;

public class Leds {

  static Spark blinkin;
  
    public Leds() {
      blinkin = new Spark(1);
    }
  
    public static void coralLights() {
      blinkin.set(.89);
      Logger.recordOutput("Lights", "Coral");
    }
  
    public static void algaeLights() {
      blinkin.set(.73);
      Logger.recordOutput("Lights", "Algae");
    }
  
    public static void rainbowPartyLights() {
      blinkin.set(-.97);
      Logger.recordOutput("Lights", "Auto");
    }
  
    public static void warningLights() {
      blinkin.set(-0.17);
      Logger.recordOutput("Lights", "Warning");
    }

    public static void loadingLights() {
        blinkin.set(-0.05);
        Logger.recordOutput("Lights", "Loading");
    }
}
