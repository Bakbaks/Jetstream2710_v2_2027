package frc.robot.util;

import com.pathplanner.lib.util.PathPlannerLogging;
import frc.robot.RobotState;
import frc.robot.subsystems.shooter.Shooter;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.littletonrobotics.junction.Logger;
import org.wpilib.command2.Command;
import org.wpilib.driverstation.MatchState;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.smartdashboard.Field2d;
import org.wpilib.smartdashboard.FieldObject2d;
import org.wpilib.system.RobotController;
import org.wpilib.tunable.Tunables;

/** Publishes stable, driver-facing telemetry for the Elastic dashboard. */
public final class DashboardTelemetry {
  private static final String FIELD_TOPIC = "Elastic/Field";

  private final RobotState robotState;
  private final Shooter shooter;
  private final Supplier<Optional<Translation2d>> shootingTargetSupplier;
  private final Supplier<Command> selectedAutoSupplier;
  private final Field2d field = new Field2d();
  private final FieldObject2d trajectory = field.getObject("Trajectory");
  private final FieldObject2d shootingTarget = field.getObject("ShootingTarget");

  private Pose2d[] activeAutonPath = new Pose2d[0];

  public DashboardTelemetry(
      RobotState robotState,
      Shooter shooter,
      Supplier<Optional<Translation2d>> shootingTargetSupplier,
      Supplier<Command> selectedAutoSupplier) {
    this.robotState = robotState;
    this.shooter = shooter;
    this.shootingTargetSupplier = shootingTargetSupplier;
    this.selectedAutoSupplier = selectedAutoSupplier;

    PathPlannerLogging.setLogActivePathCallback(this::updateActivePath);
  }

  /** Publishes the Elastic Field after RobotBase has installed the NetworkTables backend. */
  public void publish() {
    Tunables.publish(FIELD_TOPIC, field);
  }

  /** Updates values that should remain available throughout all robot modes. */
  public void periodic() {
    Pose2d robotPose = robotState.getPose();
    boolean autonomous = org.wpilib.driverstation.RobotState.isAutonomousEnabled();
    field.setRobotPose(robotPose);

    if (!autonomous && activeAutonPath.length > 0) {
      clearActivePath();
    }

    Optional<Translation2d> target = shootingTargetSupplier.get();
    double distanceToGoalMeters = -1.0;
    if (target.isPresent()) {
      Translation2d targetPoint = target.get();
      distanceToGoalMeters = robotPose.getTranslation().getDistance(targetPoint);
      shootingTarget.setPose(new Pose2d(targetPoint, Rotation2d.ZERO));
      Logger.recordOutput("Dashboard/ShootingTarget", targetPoint);
    } else {
      shootingTarget.setPoses();
    }

    Logger.recordOutput("Dashboard/RobotPose", robotPose);
    Logger.recordOutput("Dashboard/AutonPath", activeAutonPath);
    Logger.recordOutput("Dashboard/AutonomousActive", autonomous);
    Logger.recordOutput("Dashboard/DistanceToGoalMeters", distanceToGoalMeters);
    Logger.recordOutput("Dashboard/FlywheelRPM", shooter.getAverageVelocityRPM());
    Logger.recordOutput("Dashboard/FlywheelTargetRPM", shooter.getRequestedShotRPM());
    Logger.recordOutput("Dashboard/SelectedAuto", getSelectedAutoName());
    Logger.recordOutput("Dashboard/BatteryVoltage", RobotController.getBatteryVoltage());
    Logger.recordOutput("Dashboard/MatchTimeSeconds", MatchState.getMatchTime());
  }

  private void updateActivePath(List<Pose2d> poses) {
    if (!org.wpilib.driverstation.RobotState.isAutonomousEnabled() || poses.isEmpty()) {
      clearActivePath();
      return;
    }

    activeAutonPath = poses.toArray(Pose2d[]::new);
    trajectory.setPoses(activeAutonPath);
  }

  private void clearActivePath() {
    activeAutonPath = new Pose2d[0];
    trajectory.setPoses();
  }

  private String getSelectedAutoName() {
    Command selectedAuto = selectedAutoSupplier.get();
    if (selectedAuto == null || "InstantCommand".equals(selectedAuto.getName())) {
      return "None";
    }
    return selectedAuto.getName();
  }
}
