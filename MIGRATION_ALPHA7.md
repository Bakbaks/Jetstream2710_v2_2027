# WPILib 2027 Alpha 7 Migration Report

## Summary

- Original project: `Jetstream2710_v1_2027`
- Migrated project: `Jetstream2710_v2_2027`
- Migration date: 2026-10-09
- Language/toolchain: Java 25, Gradle 9.4.1
- Target controller: Systemcore
- Result: Migration completed; clean build and automated simulation test passed.

The target was created as a normal sibling-directory copy. `.git`, `.gradle`, `build`, and generated caches were not copied. The target contains no symlinks, junctions, worktrees, or `.git` metadata. A SHA-256 inventory of all 69 original non-generated files was captured before the copy and compared after migration; the original inventory remained identical.

## Version Changes

| Component | Original | Migrated |
| --- | --- | --- |
| WPILib / GradleRIO | `2027.0.0-alpha-6` with `2027_alpha5` project config | `2027.0.0-alpha-7` / `2027_alpha7` |
| Commands | Commands V2 for Alpha 5 | Commands V2 for Alpha 7 (`2027.0.0-alpha-7`) |
| CTRE Phoenix 6 | `26.50.0-alpha-1` | `26.70.0-alpha-2` |
| AdvantageKit | `27.0.0-alpha-4` | `27.0.0-alpha-6` |
| PathPlannerLib | `2027.0.0-alpha-3` | `2027.0.0-alpha-4.1` |
| PhotonLib | `v2027.0.0-alpha-2` (`2027_alpha5`) | `dev-v2027.0.0-alpha-2-82-g47b879e2` (`2027_alpha7`) |
| AprilTag | Included in core WPILib | Alpha 7 `AprilTag.json` vendordep |
| Shadow plugin | `9.3.0` | `9.3.0` (preserved) |
| Gradle wrapper | `9.4.1` | `9.4.1` (unchanged) |

PhotonVision does not have a numbered Alpha 7 release at the time of migration. The Systemcore compatibility table specifies the latest Dev release, so the exact current official Dev asset was installed and pinned rather than changing the year on the incompatible Alpha 2 manifest.

## Changed Files

### Build and project configuration

- `.wpilib/wpilib_preferences.json`
- `build.gradle`
- `settings.gradle`

`build.gradle` now uses GradleRIO Alpha 7 and the Alpha 7 application/deployment configuration API. Java 25, Systemcore deployment, desktop simulation, AdvantageKit annotation processing, and Shadow JAR packaging remain enabled. The successful build produced both the application distribution and `build/libs/Jetstream2710_v2_2027-all.jar` with `Main-Class: first.Main`.

### Vendor dependencies

- Updated `vendordeps/AdvantageKit.json`
- Updated `vendordeps/CommandsV2.json`
- Updated `vendordeps/PathplannerLibSystemCoreAlpha.json`
- Updated `vendordeps/photonlib.json`
- Added `vendordeps/AprilTag.json`
- Added `vendordeps/Phoenix6-26.70.0-alpha-2.json`
- Removed `vendordeps/Phoenix6-26.50.0-alpha-1.json`

Every installed manifest declares `wpilibYear: 2027_alpha7`. The Alpha 5/6 manifests were replaced with actual compatible manifests; no manifest was made to appear compatible by editing only its year.

### Java source and tests

- `src/main/java/first/Main.java`
- `src/main/java/frc/robot/Ports.java`
- `src/main/java/frc/robot/Robot.java`
- `src/main/java/frc/robot/RobotContainer.java`
- `src/main/java/frc/robot/shooting/ShotCalculator.java`
- `src/main/java/frc/robot/shooting/ShotConstants.java`
- `src/main/java/frc/robot/subsystems/drivetrain/CommandSwerveDrivetrain.java`
- `src/main/java/frc/robot/subsystems/drivetrain/TunerConstants.java`
- `src/main/java/frc/robot/subsystems/vision/VisionConstants.java`
- `src/test/java/frc/robot/RobotContainerSimulationTest.java`

No CAN IDs, CAN bus assignments, motor inversion settings, encoder offsets, gear ratios, current/voltage limits, tuned gains, drivetrain geometry, camera transforms, shooter targets, path files, controller behavior, or team number were changed.

## Significant API Replacements

- Robot startup: `Robot.class` became the Alpha 7 factory `Robot::new`.
- Project metadata: AdvantageKit `ProjectName` is now `Jetstream2710_v2_2027`.
- Systemcore CAN: `CANBus.systemcore(n)` became `new CANBus(CANPort.CAN_Sn)` using `org.wpilib.hardware.bus.CANPort`. The original physical S0-S4 assignments are preserved.
- CTRE swerve network: `SwerveDrivetrainConstants.withCANBusName(...)` became `withNetwork(CANBus)`.
- CTRE operator perspective: `setOperatorPerspectiveForward(...)` became `setOperatorForwardDirection(...)`.
- Autonomous selection: removed `SendableChooser`/`SmartDashboard`; PathPlanner Alpha 7's `Selectable<Command>` is wrapped by AdvantageKit `LoggedNetworkChooser<Command>` and published as `Auto Chooser`. The `Taxi` default and all `NamedCommands` registrations are preserved.
- WPILib field data: `AprilTagFieldLayout`/`AprilTagFields` became `Field`/`Fields`. The shooting code still uses `FRC_2026_REBUILT_WELDED`; vision uses `Fields.DEFAULT_FIELD`.
- Gamepad face buttons: `westFace()` and `southFace()` became `faceLeft()` and `faceDown()`.
- WPILib constants: migrated removed constants such as `Rotation2d.kZero` to `Rotation2d.ZERO` and used `Rotation2d.PI` for 180 degrees.
- Geometry: handled Alpha 7's optional `Translation2d.getAngle()` result, preserving the previous zero-vector fallback behavior.
- HAL test setup: `HAL.initialize(500, 0)` became `HAL.initialize()`.

Commands V2 remains installed and all `org.wpilib.command2` code is preserved. No Commands V3 dependency was added.

## Dependency Resolution

Gradle's resolved runtime classpath contains:

- WPILib Java libraries, fields, telemetry, tunables, and AprilTag: `2027.0.0-alpha-7`
- Commands V2: `2027.0.0-alpha-7`
- CTRE Phoenix 6: `26.70.0-alpha-2`
- AdvantageKit: `27.0.0-alpha-6`
- PathPlannerLib: `2027.0.0-alpha-4.1`
- PhotonLib and PhotonTargeting: `dev-v2027.0.0-alpha-2-82-g47b879e2`

Systemcore and desktop JNI/native dependencies resolved successfully, including CTRE simulation, PhotonTargeting, AprilTag, fields, telemetry, and tunables natives. AdvantageKit `@AutoLog` classes generated successfully during compilation.

## Build and Test Results

Command executed from this project:

```powershell
.\gradlew.bat clean build
```

Result: **BUILD SUCCESSFUL**. The build completed Java compilation, AdvantageKit annotation processing, application distributions, Shadow JAR packaging, native extraction, and tests.

`RobotContainerSimulationTest.constructsWithoutRobotHardware()` passed. Its startup path initialized HAL, constructed `RobotContainer`, initialized CTRE simulation, configured PathPlanner, constructed the autonomous chooser, initialized the simulated IO implementations, and shut down CTRE simulation cleanly.

## Simulation Results

- `simulateJava` generated the Alpha 7 simulation launch configuration successfully.
- A desktop `run` smoke test launched `first.Main`; the robot process remained running without an immediate startup exception and was manually stopped after the startup observation period.
- Simulation-generated CTRE files, logs, NetworkTables state, and GUI state were removed afterward; the original `simgui-ds.json` content was restored unchanged.

This validates desktop startup and construction only. It does not validate real sensors, motor direction, CAN traffic, timing under load, field networking, or mechanism behavior.

## Unresolved Issues and Compatibility Notes

- No compile, test, dependency-resolution, or desktop-startup blockers remain.
- PhotonVision Alpha 7 support is a development build. Keep robot-side PhotonLib and coprocessor PhotonVision versions aligned, and re-verify the pinned Dev version before an event or after updating the coprocessor.
- The autonomous chooser default remains `Taxi`, exactly as in the original code. Confirm the desired PathPlanner auto/path assets are present for the field test configuration.

## Hardware and Firmware Requirements

Before deploying this Alpha 7 project:

1. Update Systemcore to OS image **14 or later**.
2. Use the **2027 Driver Station Alpha 7** release for the complete Alpha 7 feature set.
3. Update all Phoenix 6 devices used by this robot to compatible **26.70.x firmware** before running Phoenix API `26.70.0-alpha-2`.
4. Use Phoenix Tuner X Preview `2026.10.x` or the currently documented compatible successor.
5. If a CANivore is used, install the matching Systemcore **canivore-usb 1.18** package for the exact Alpha/Beta Systemcore image. Alpha image 14 and Beta image 14/15 use separately labeled packages.
6. Use a PhotonVision coprocessor image compatible with the pinned Alpha 7 Dev PhotonLib build.

No Systemcore image, Driver Station, CANivore package, PhotonVision image, CTRE firmware, or other hardware firmware was installed or flashed during this migration.

## Recommended Controlled Robot Validation

1. Back up current device configurations and confirm Systemcore/Driver Station/API/firmware versions match the requirements above.
2. Power the robot disabled and verify all expected devices appear on CAN S0-S4 with the same IDs as before.
3. Check Phoenix configuration status and verify steer encoder offsets and absolute angles before enabling.
4. Confirm the `Auto Chooser` is visible and selectable from the intended dashboard, with `Taxi` as the default.
5. Run each subsystem at low output while elevated or otherwise mechanically secured; verify direction, inversion, current limiting, and stop behavior.
6. Validate field-relative drive and alliance perspective on both alliances.
7. Validate PhotonVision connectivity, timestamps, tag IDs, pose estimates, and rejection of distant single-tag estimates.
8. Run PathPlanner autos at reduced speed, then verify named events and alliance flipping.
9. Confirm AdvantageKit NT4 and WPILOG output, then inspect a complete disabled/teleop/auto log.
10. Only after those checks, perform full-speed mechanism and autonomous validation in a controlled field environment.

## Authoritative References

- [WPILib 2027.0.0 Alpha 7 release](https://github.com/wpilibsuite/allwpilib/releases/tag/v2027.0.0-alpha-7)
- [Systemcore compatibility matrix](https://github.com/wpilibsuite/SystemCoreTesting/#third-party-library-compatibility-by-wpilib-version)
- [CTRE Phoenix Systemcore compatibility](https://github.com/wpilibsuite/SystemCoreTesting/blob/main/CTR-Phoenix.md)
- [CTRE Phoenix API changelog](https://api.ctr-electronics.com/changelog)
- [AdvantageKit Systemcore compatibility](https://github.com/wpilibsuite/SystemCoreTesting/blob/main/AdvantageKit.md)
- [PathPlanner Systemcore Alpha manifest](https://3015rangerrobotics.github.io/pathplannerlib/PathplannerLibSystemCoreAlpha.json)
- [PhotonVision releases](https://github.com/PhotonVision/PhotonVision/releases)
- [WPILib 2027 documentation](https://docs.wpilib.org/en/2027/)
