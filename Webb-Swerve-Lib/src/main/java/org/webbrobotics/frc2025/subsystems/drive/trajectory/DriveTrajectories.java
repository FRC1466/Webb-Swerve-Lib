// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.drive.trajectory;

import static org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.*;
import static org.webbrobotics.frc2025.FieldConstants.*;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import java.util.*;
import java.util.function.Function;
import lombok.experimental.ExtensionMethod;
import org.webbrobotics.frc2025.FieldConstants;
import org.webbrobotics.frc2025.subsystems.drive.DriveConstants;
import org.webbrobotics.frc2025.util.GeomUtil;

@ExtensionMethod({TrajectoryGenerationHelpers.class})
public class DriveTrajectories {
  public static final Map<String, List<PathSegment>> paths = new HashMap<>();
  public static final List<Function<Set<String>, Map<String, List<PathSegment>>>> suppliedPaths =
      new ArrayList<>(); // List of functions that take a set of completed paths and return a map of

  // trajectories to generate (or null if they cannot be generated yet)

  // Drive straight path
  // (Used for preload of trajectory classes in drive constructor)
  static {
    paths.put(
        "driveStraight",
        List.of(
            PathSegment.newBuilder()
                .addPoseWaypoint(Pose2d.kZero)
                .addPoseWaypoint(new Pose2d(3.0, 2.0, Rotation2d.fromDegrees(180.0)))
                .build()));

    paths.put(
        "BLOB",
        List.of(
            PathSegment.newBuilder()
                .addPoseWaypoint(Pose2d.kZero)
                .addTranslationWaypoint(new Translation2d(2, 3))
                .addPoseWaypoint(new Pose2d(0, 3, Rotation2d.fromDegrees(270.0)))
                .addPoseWaypoint(new Pose2d(2, 0.6, Rotation2d.fromDegrees(30.0)))
                .build()));
  }

  private static Pose2d getNearestIntakingPose(Pose2d pose) {
    return CoralStation.rightCenterFace.transformBy(
        GeomUtil.toTransform2d(
            DriveConstants.robotWidth / 2.0 + Units.inchesToMeters(3.0),
            MathUtil.clamp(
                pose.relativeTo(CoralStation.rightCenterFace).getY(),
                -FieldConstants.CoralStation.stationLength / 2 + Units.inchesToMeters(16),
                FieldConstants.CoralStation.stationLength / 2 - Units.inchesToMeters(16))));
  }

  /** Returns the last waypoint of a trajectory. */
  public static Waypoint getLastWaypoint(String trajectoryName) {
    List<PathSegment> trajectory = paths.get(trajectoryName);
    return trajectory
        .get(trajectory.size() - 1)
        .getWaypoints(trajectory.get(trajectory.size() - 1).getWaypointsCount() - 1);
  }
}
