package frc.robot.subsystems.vision;

import org.wpilib.fields.Field;
import org.wpilib.fields.Fields;
import org.wpilib.math.linalg.Matrix;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.math.geometry.Translation3d;
import org.wpilib.math.numbers.N1;
import org.wpilib.math.numbers.N3;
import org.wpilib.math.util.Units;

public final class VisionConstants {
  public static final Field TAG_LAYOUT = Fields.DEFAULT_FIELD.loadField();
  public static final String[] CAMERA_NAMES = {"LeftCAM", "CenterCAM", "RightCAM"};
  public static final Transform3d[] ROBOT_TO_CAMERAS = {
    new Transform3d(
        //new Translation3d(-0.0141, 0.383, 0.484),
        new Translation3d(-0.0156, 0.3835, 0.4879),
        new Rotation3d(Units.degreesToRadians(45), 0.0, Math.PI / 2.0)),
    new Transform3d(
        new Translation3d(-0.2441, 0.0, 0.163),
        new Rotation3d(Math.PI, Units.degreesToRadians(30), Math.PI)),
    new Transform3d(
        //new Translation3d(-0.0141, -0.383, 0.484),
        new Translation3d(-0.0156, -0.3835, 0.4879),
        new Rotation3d(Units.degreesToRadians(-45), 0.0, -Math.PI / 2.0))
  };
  public static final Matrix<N3, N1> SINGLE_TAG_STD_DEVS = VecBuilder.fill(0.7, 0.7, 0.6);
  public static final Matrix<N3, N1> MULTI_TAG_STD_DEVS = VecBuilder.fill(0.4, 0.4, 0.4);
  public static final double MAX_SINGLE_TAG_DISTANCE_METERS = 3;
  public static final double MAX_MULTI_TAG_DISTANCE_METERS = 4;

  private VisionConstants() {}
}
