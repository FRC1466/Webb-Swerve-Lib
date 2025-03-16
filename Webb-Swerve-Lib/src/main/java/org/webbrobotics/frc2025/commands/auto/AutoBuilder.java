// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.commands.auto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import lombok.RequiredArgsConstructor;
import org.webbrobotics.frc2025.RobotState;
import org.webbrobotics.frc2025.commands.*;
import org.webbrobotics.frc2025.subsystems.drive.Drive;
import org.webbrobotics.frc2025.util.AllianceFlipUtil;

@RequiredArgsConstructor
public class AutoBuilder {
  private final Drive drive;

  private final double intakeTimeSeconds = 0.35;
  private final double coralEjectTimeSeconds = 0.3;

  public Command upInTheInspirationalAuto() {
    return Commands.runOnce(
            () ->
                RobotState.getInstance()
                    .resetPose(
                        AllianceFlipUtil.apply(
                            new Pose2d(
                                RobotState.getInstance().getEstimatedPose().getTranslation(),
                                Rotation2d.kPi))))
        .andThen(
            new DriveToPose(
                    drive,
                    () -> RobotState.getInstance().getEstimatedPose(),
                    () -> RobotState.getInstance().getEstimatedPose(),
                    () ->
                        new Translation2d((AllianceFlipUtil.shouldFlip() ? -1.0 : 1.0) * -1.0, 0.0),
                    () -> 0.0)
                .withTimeout(0.6));
  }
}
