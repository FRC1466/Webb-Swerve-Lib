package org.webbrobotics.frc2025.subsystems.vision;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class AprilTagVisionIOInputsAutoLogged extends VisionIO.AprilTagVisionIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("Timestamps", timestamps);
    table.put("Frames", frames);
    table.put("Fps", fps);
  }

  @Override
  public void fromLog(LogTable table) {
    timestamps = table.get("Timestamps", timestamps);
    frames = table.get("Frames", frames);
    fps = table.get("Fps", fps);
  }

  public AprilTagVisionIOInputsAutoLogged clone() {
    AprilTagVisionIOInputsAutoLogged copy = new AprilTagVisionIOInputsAutoLogged();
    copy.timestamps = this.timestamps.clone();
    copy.frames = this.frames.clone();
    copy.fps = this.fps;
    return copy;
  }
}
