// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import lombok.Getter;
import org.webbrobotics.frc2025.util.AllianceFlipUtil;

public class PathfindConstants {
  // Red Target Poses for Reef
  // 6-11 as 0-5 , First is Left and Second is Right
  public static Pose2d[][] blueTargetPoseReef = {
    {
      AllianceFlipUtil.apply(new Pose2d(13.550, 2.837, Rotation2d.fromDegrees(120)), true),
      AllianceFlipUtil.apply(new Pose2d(13.853, 3.003, Rotation2d.fromDegrees(120)), true)
    },
    {
      AllianceFlipUtil.apply(new Pose2d(14.347, 3.860, Rotation2d.fromDegrees(-180)), true),
      AllianceFlipUtil.apply(new Pose2d(14.347, 4.190, Rotation2d.fromDegrees(-180)), true)
    },
    {
      AllianceFlipUtil.apply(new Pose2d(13.66, 4.95, Rotation2d.fromDegrees(-120)), true),
      AllianceFlipUtil.apply(new Pose2d(13.49, 5.23, Rotation2d.fromDegrees(-120)), true)
    },
    {
      AllianceFlipUtil.apply(new Pose2d(12.52, 5.17, Rotation2d.fromDegrees(-60)), true),
      AllianceFlipUtil.apply(new Pose2d(12.28, 5.04, Rotation2d.fromDegrees(-60)), true)
    },
    {
      AllianceFlipUtil.apply(new Pose2d(11.695, 4.169, Rotation2d.fromDegrees(0)), true),
      AllianceFlipUtil.apply(new Pose2d(11.7, 3.83, Rotation2d.fromDegrees(0)), true)
    },
    {
      AllianceFlipUtil.apply(new Pose2d(12.283, 2.998, Rotation2d.fromDegrees(60)), true),
      AllianceFlipUtil.apply(new Pose2d(12.554, 2.850, Rotation2d.fromDegrees(60)), true)
    }
  };

  // Red Target Poses for Station
  public static Pose2d[] blueTargetPoseStation = {
    AllianceFlipUtil.apply(new Pose2d(16.288, 7.064, Rotation2d.fromDegrees(-125.000)), true),
    AllianceFlipUtil.apply(new Pose2d(16.288, 0.937, Rotation2d.fromDegrees(125.000)), true)
  };

  @Getter
  public static Pose2d blueTargetPoseProcessor =
      AllianceFlipUtil.apply(new Pose2d(5.93, 0.45, Rotation2d.fromDegrees(-90.000)));

  public static double blueTargetPoseXBarge = 10.3;
}
