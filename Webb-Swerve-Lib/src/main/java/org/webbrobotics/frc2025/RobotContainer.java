// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025;

import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;
import org.littletonrobotics.junction.networktables.LoggedNetworkNumber;
import org.photonvision.PhotonCamera;
import org.photonvision.proto.Photon.ProtobufPhotonPipelineResult;
import org.webbrobotics.frc2025.FieldConstants.AprilTagLayoutType;
import org.webbrobotics.frc2025.subsystems.drive.Drive;
import org.webbrobotics.frc2025.subsystems.drive.DriveConstants;
import org.webbrobotics.frc2025.subsystems.drive.GyroIO;
import org.webbrobotics.frc2025.subsystems.drive.GyroIOPigeon2;
import org.webbrobotics.frc2025.subsystems.drive.ModuleIOComp;
import org.webbrobotics.frc2025.subsystems.drive.ModuleIODev;
import org.webbrobotics.frc2025.subsystems.drive.ModuleIOSim;
import org.webbrobotics.frc2025.subsystems.vision.Vision;
import org.webbrobotics.frc2025.util.DoublePressTracker;
import org.webbrobotics.frc2025.util.OverrideSwitches;
import org.webbrobotics.frc2025.util.TriggerUtil;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({DoublePressTracker.class, TriggerUtil.class})
public class RobotContainer {
  // Subsystems
  private Drive drive;
  private Vision vision;

  // Controllers
  private final CommandXboxController driver = new CommandXboxController(0);
  private final OverrideSwitches overrides = new OverrideSwitches(5);
  private final Trigger robotRelative = overrides.driverSwitch(0);

  private final Alert driverDisconnected =
      new Alert("Driver controller disconnected (port 0).", AlertType.kWarning);
  private final Alert operatorDisconnected =
      new Alert("Operator controller disconnected (port 1).", AlertType.kWarning);
  private final Alert overrideDisconnected =
      new Alert("Override controller disconnected (port 5).", AlertType.kInfo);
  private final LoggedNetworkNumber endgameAlert1 =
      new LoggedNetworkNumber("/SmartDashboard/Endgame Alert #1", 30.0);
  private final LoggedNetworkNumber endgameAlert2 =
      new LoggedNetworkNumber("/SmartDashboard/Endgame Alert #2", 15.0);

  // Dashboard inputs
  private final LoggedDashboardChooser<Command> autoChooser = new LoggedDashboardChooser<Command>("autoChooser");

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    if (Constants.getMode() != Constants.Mode.REPLAY) {
      switch (Constants.getRobot()) {
        case COMPBOT -> {
          drive =
              new Drive(
                  new GyroIOPigeon2(),
                  new ModuleIOComp(DriveConstants.moduleConfigsComp[0]),
                  new ModuleIOComp(DriveConstants.moduleConfigsComp[1]),
                  new ModuleIOComp(DriveConstants.moduleConfigsComp[2]),
                  new ModuleIOComp(DriveConstants.moduleConfigsComp[3]));
          vision = new Vision();
        }
        case DEVBOT -> {
          drive =
              new Drive(
                  new GyroIOPigeon2(),
                  new ModuleIODev(DriveConstants.moduleConfigsDev[0]),
                  new ModuleIODev(DriveConstants.moduleConfigsDev[1]),
                  new ModuleIODev(DriveConstants.moduleConfigsDev[2]),
                  new ModuleIODev(DriveConstants.moduleConfigsDev[3]));
          vision =
              new Vision();
        }
        case SIMBOT -> {
          drive =
              new Drive(
                  new GyroIO() {},
                  new ModuleIOSim(),
                  new ModuleIOSim(),
                  new ModuleIOSim(),
                  new ModuleIOSim());
          vision =
              new Vision();
          }
      }
    }

    configureBindings();
  }

  private void configureBindings() {}

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
