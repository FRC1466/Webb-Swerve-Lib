// Copyright (c) 2025 FRC Team 1466
// https://github.com/FRC1466
 
package org.webbrobotics.frc2025.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

/**
 * Constants related to the vision subsystem.
 */
public final class VisionConstants {
  // Camera names as configured in PhotonVision
  public static final String[] CAMERA_NAMES = {"Camera_FrontLeft", "Camera_FrontRight"};
  
  // Camera mounting locations relative to robot center
  // Cam mounted facing forward, half a meter forward of center, half a meter up from center.
  public static final Transform3d[] ROBOT_TO_CAMERAS = {
    new Transform3d(new Translation3d(.267, .292, .2), new Rotation3d(0, 0, 0)),
    new Transform3d(new Translation3d(.267, -.292, .2), new Rotation3d(0, 0, 0))
  };
  
  // The layout of the AprilTags on the field
  public static final AprilTagFieldLayout TAG_LAYOUT = 
      AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

  // The standard deviations of vision estimated poses, affecting correction rate
  public static final Matrix<N3, N1> SINGLE_TAG_STD_DEVS = VecBuilder.fill(4, 4, 8);
  public static final Matrix<N3, N1> MULTI_TAG_STD_DEVS = VecBuilder.fill(2, 2, 4);
  
  private VisionConstants() {}
}