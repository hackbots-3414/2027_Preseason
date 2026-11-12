// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.elevator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.subsystems.elevator.ElevatorIO.ElevatorIOInputs;

public class Elevator extends SubsystemBase {
  /** Creates a new Elevator. */
 
     private final ElevatorIO m_io;
    private final ElevatorIOInputs m_inputs;

  public Elevator() {
    if (Robot.isReal()) {
      m_io = new ElevatorIOSim();
    } else {
      m_io = new ElevatorIOSim();
    }
    m_inputs = new ElevatorIOInputs();
    SmartDashboard.putData("Elevator/Set Elevator Top", top());
    SmartDashboard.putData("Elevator/Set Elevator Middle", middle());
    SmartDashboard.putData("Elevator/Set Elevator Bottom", zero());
  }

  @Override
  public void simulationPeriodic() {

   }

  public void setTarget(ElevatorState state) {
    double goal = state.position();
    m_io.setTarget(goal);
  }

  public Command top() {
    return Commands.run(() -> setTarget(ElevatorState.Top));
  }
  public Command middle() {
    return Commands.run(() -> setTarget(ElevatorState.Middle));
  }
  public Command zero() {
    return Commands.run(() -> setTarget(ElevatorState.Zero));
  }
  @Override
  public void periodic() {
    // m_io.setTarget(SmartDashboard.getNumber("Target Elevator Height", 0));
    m_io.updateInputs(m_inputs);
    // This method will be called once per scheduler run
  }
}
