package frc.robot;

import com.ctre.phoenix6.CANBus;
import org.wpilib.hardware.bus.CANPort;

public final class Ports {
  public static final CANBus FLOOREXTENDO_CAN_BUS = new CANBus(CANPort.CAN_S1);
  public static final CANBus INTAKE_CAN_BUS = new CANBus(CANPort.CAN_S2);
  public static final CANBus FEEDER_CAN_BUS = new CANBus(CANPort.CAN_S3);
  public static final CANBus FLYWHEEL_CAN_BUS = new CANBus(CANPort.CAN_S4);


  private Ports() {}
}
