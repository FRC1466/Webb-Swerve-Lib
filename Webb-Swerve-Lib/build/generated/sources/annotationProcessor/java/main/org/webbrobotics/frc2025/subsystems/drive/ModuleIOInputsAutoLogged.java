package org.webbrobotics.frc2025.subsystems.drive;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class ModuleIOInputsAutoLogged extends ModuleIO.ModuleIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("Data", data);
    table.put("OdometryDrivePositionsRad", odometryDrivePositionsRad);
    table.put("OdometryTurnPositions", odometryTurnPositions);
  }

  @Override
  public void fromLog(LogTable table) {
    data = table.get("Data", data);
    odometryDrivePositionsRad = table.get("OdometryDrivePositionsRad", odometryDrivePositionsRad);
    odometryTurnPositions = table.get("OdometryTurnPositions", odometryTurnPositions);
  }

  public ModuleIOInputsAutoLogged clone() {
    ModuleIOInputsAutoLogged copy = new ModuleIOInputsAutoLogged();
    copy.data = this.data;
    copy.odometryDrivePositionsRad = this.odometryDrivePositionsRad.clone();
    copy.odometryTurnPositions = this.odometryTurnPositions.clone();
    return copy;
  }
}
