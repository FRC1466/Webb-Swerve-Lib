// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.util.swerve;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public record SwerveSetpoint(ChassisSpeeds chassisSpeeds, SwerveModuleState[] moduleStates) {}
