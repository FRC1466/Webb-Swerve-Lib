// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.commands.auto;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.webbrobotics.frc2025.RobotState;
import org.webbrobotics.frc2025.subsystems.drive.trajectory.HolonomicTrajectory;
import org.webbrobotics.frc2025.util.AllianceFlipUtil;
import org.webbrobotics.frc2025.util.MirrorUtil;

public class AutoCommands {
  private AutoCommands() {}

  public static Command resetPoseCommand(HolonomicTrajectory trajectory, boolean mirror) {
    return Commands.runOnce(
        () ->
            RobotState.getInstance()
                .resetPose(
                    AllianceFlipUtil.apply(
                        mirror
                            ? MirrorUtil.apply(trajectory.getStartPose())
                            : trajectory.getStartPose())));
  }

  public static void resetPose(HolonomicTrajectory trajectory, boolean mirror) {
    RobotState.getInstance()
        .resetPose(
            AllianceFlipUtil.apply(
                mirror ? MirrorUtil.apply(trajectory.getStartPose()) : trajectory.getStartPose()));
  }

  public static boolean isXCrossed(double x, boolean towardsDriverStation) {
    double flippedX = AllianceFlipUtil.apply(RobotState.getInstance().getEstimatedPose()).getX();
    return towardsDriverStation ? flippedX <= x : flippedX >= x;
  }
}
