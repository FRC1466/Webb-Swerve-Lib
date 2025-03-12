// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import org.littletonrobotics.junction.AutoLog;

public interface GyroIO {
  @AutoLog
  public static class GyroIOInputs {
    public GyroIOData data = new GyroIOData(false, Rotation2d.kZero, 0);
    public double[] odometryYawTimestamps = new double[] {};
    public Rotation2d[] odometryYawPositions = new Rotation2d[] {};
  }

  public record GyroIOData(
      boolean connected, Rotation2d yawPosition, double yawVelocityRadPerSec) {}

  public default void updateInputs(GyroIOInputs inputs) {}
}
