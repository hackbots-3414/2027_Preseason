// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.elevator;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.simulation.PWMSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Add your docs here. */
public class ElevatorIOSim implements ElevatorIO {
    private final PWMSparkMax m_motor = new PWMSparkMax(0);
    private final Encoder m_encoder = new Encoder(0, 1);
    private double m_setpoint;

    ElevatorFeedforward m_feedforward = new ElevatorFeedforward(
            0,
            0.762,
            0.762,
            0);
    public ElevatorSim sim = new ElevatorSim(0.762, 0.1, DCMotor.getVex775Pro(4), 0, 1.25, true, 0.01);
    private final EncoderSim m_encoderSim = new EncoderSim(m_encoder);
    private final PWMSim m_motorSim = new PWMSim(m_motor);
    private final ProfiledPIDController m_controller = new ProfiledPIDController(1.25, 0.25, 0.5,
            new TrapezoidProfile.Constraints(2.45, 2.45));

    private final Mechanism2d mech2d = new Mechanism2d(1, 1.5);
    private final MechanismRoot2d mech2droot = mech2d.getRoot("Elevator Root", 0.5, 0);
    private final MechanismLigament2d elevatorMech2d = mech2droot
            .append(new MechanismLigament2d("Elevator", sim.getPositionMeters(), 90));
    public ElevatorIOSim() {
        m_encoder.setDistancePerPulse(2.0 * Math.PI * Units.inchesToMeters(2) / 4096);
    }
    public void updateInputs(ElevatorIOInputs inputs) {
        sim.update(0.02);
        m_encoderSim.setDistance(sim.getPositionMeters());
        elevatorMech2d.setLength(m_encoder.getDistance());
        inputs.position = m_encoder.getDistance();
        inputs.setpoint = m_setpoint;
        SmartDashboard.putData("Elevator/Elevator Sim", mech2d);
        SmartDashboard.putNumber("Elevator/Elevator Height", elevatorMech2d.getLength());
        sim.setInput(m_motorSim.getSpeed() * RobotController.getBatteryVoltage());
    }

    public void setTarget(double target) {
        m_setpoint = target;
        m_controller.setGoal(target);
        double pidOutput = m_controller.calculate(m_encoder.getDistance());
        double feedforwardOutput = m_feedforward.calculate(m_controller.getSetpoint().velocity);
        m_motor.setVoltage(pidOutput + feedforwardOutput);
    }
}
