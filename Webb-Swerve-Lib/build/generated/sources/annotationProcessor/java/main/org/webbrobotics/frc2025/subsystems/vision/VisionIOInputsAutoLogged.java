package org.webbrobotics.frc2025.subsystems.vision;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class VisionIOInputsAutoLogged extends VisionIO.VisionIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("NtConnected", ntConnected);
  }

  @Override
  public void fromLog(LogTable table) {
    ntConnected = table.get("NtConnected", ntConnected);
  }

  public VisionIOInputsAutoLogged clone() {
    VisionIOInputsAutoLogged copy = new VisionIOInputsAutoLogged();
    copy.ntConnected = this.ntConnected;
    return copy;
  }
}
