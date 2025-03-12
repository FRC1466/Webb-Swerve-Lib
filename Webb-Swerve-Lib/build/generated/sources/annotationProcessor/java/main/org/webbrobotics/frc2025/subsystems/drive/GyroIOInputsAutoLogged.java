package org.webbrobotics.frc2025.subsystems.drive;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class GyroIOInputsAutoLogged extends GyroIO.GyroIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("Data", data);
    table.put("OdometryYawTimestamps", odometryYawTimestamps);
    table.put("OdometryYawPositions", odometryYawPositions);
  }

  @Override
  public void fromLog(LogTable table) {
    data = table.get("Data", data);
    odometryYawTimestamps = table.get("OdometryYawTimestamps", odometryYawTimestamps);
    odometryYawPositions = table.get("OdometryYawPositions", odometryYawPositions);
  }

  public GyroIOInputsAutoLogged clone() {
    GyroIOInputsAutoLogged copy = new GyroIOInputsAutoLogged();
    copy.data = this.data;
    copy.odometryYawTimestamps = this.odometryYawTimestamps.clone();
    copy.odometryYawPositions = this.odometryYawPositions.clone();
    return copy;
  }
}
