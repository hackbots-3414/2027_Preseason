// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.elevator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;

public class Elevator extends SubsystemBase {
  private ElevatorIO io;
  /** Creates a new Elevator. */
  public Elevator() {
    if (Robot.isReal()) {
      // make real IO
    } else {
      io = new ElevatorIOSim();
    }
    SmartDashboard.putData("Elevator/Go To Top", goTop());
    SmartDashboard.putData("Elevator/Go To Middle", goMiddle());
    SmartDashboard.putData("Elevator/Go To Bottom", goBottom());

  }


 public Command goTop() {
  return Commands.run(() -> io.setTarget(1.25));
}

 public Command goMiddle() {
  return Commands.run(() -> io.setTarget(0.75));
}

 public Command goBottom() {
  return Commands.run(() -> io.setTarget(0));
}

  @Override
  public void periodic() {
    io.updateInfo();
  }
}
