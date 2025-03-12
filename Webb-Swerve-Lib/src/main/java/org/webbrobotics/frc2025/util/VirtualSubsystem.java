// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.util;

import java.util.ArrayList;
import java.util.List;

public abstract class VirtualSubsystem {
  private static List<VirtualSubsystem> subsystems = new ArrayList<>();

  public VirtualSubsystem() {
    subsystems.add(this);
  }

  public static void periodicAll() {
    for (VirtualSubsystem subsystem : subsystems) {
      subsystem.periodic();
    }
  }

  public abstract void periodic();
}
