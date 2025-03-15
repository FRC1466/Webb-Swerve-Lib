// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025;

import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.RobotBase;

/** This class contains global configuration describing the current robot and runtime mode. */
public final class Constants {
  public static final double loopPeriodSecs = 0.02;
  private static RobotType robotType = RobotType.SIMBOT;
  public static final boolean tuningMode = true;

  @SuppressWarnings("resource")
  public static RobotType getRobot() {
    if (!disableHAL && RobotBase.isReal() && robotType == RobotType.SIMBOT) {
      new Alert("Invalid robot selected, using competition robot as default.", AlertType.kError)
          .set(true);
      robotType = RobotType.COMPBOT;
    }
    return robotType;
  }

  public static Mode getMode() {
    return switch (robotType) {
      case DEVBOT, COMPBOT -> RobotBase.isReal() ? Mode.REAL : Mode.REPLAY;
      case SIMBOT -> Mode.SIM;
    };
  }

  public enum Mode {
    /** Running on a real robot. */
    REAL,

    /** Running a physics simulator. */
    SIM,

    /** Replaying from a log file. */
    REPLAY
  }

  public enum RobotType {
    SIMBOT,
    DEVBOT,
    COMPBOT
  }

  public static boolean disableHAL = false;

  public static void disableHAL() {
    disableHAL = true;
  }

  /** Checks whether the correct robot is selected when deploying. */
  public static class CheckDeploy {
    public static void main(String... args) {
      if (robotType == RobotType.SIMBOT) {
        System.err.println("Cannot deploy, invalid robot selected: " + robotType);
        System.exit(1);
      }
    }
  }

  /** Checks that the default robot is selected and tuning mode is disabled. */
  public static class CheckPullRequest {
    public static void main(String... args) {
      if (robotType != RobotType.COMPBOT || tuningMode) {
        System.err.println("Do not merge, non-default constants are configured.");
        System.exit(1);
      }
    }
  }
}
