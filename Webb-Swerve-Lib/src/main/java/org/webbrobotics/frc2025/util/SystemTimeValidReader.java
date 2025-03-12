// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.util;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotController;

public class SystemTimeValidReader {
  private static Notifier notifier = null;
  private static boolean ready = false;

  public static void start() {
    if (notifier != null) return;
    notifier =
        new Notifier(
            () -> {
              boolean readyNew = RobotController.isSystemTimeValid();
              synchronized (SystemTimeValidReader.class) {
                ready = readyNew;
              }
            });
    notifier.startPeriodic(3.0);
  }

  public static synchronized boolean isValid() {
    return ready;
  }
}
