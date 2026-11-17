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
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.simulation.PWMSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;

/** Add your docs here. */
public class ElevatorIOSim implements ElevatorIO{

    private PWMSim motorSim;
    private PWMSparkMax motor;

    private EncoderSim encoderSim;
    private ProfiledPIDController pidControllerSim;
    private ElevatorFeedforward feedforwardSim;
    private ElevatorSim elevatorSim;
    private Mechanism2d mechanism2d;
    private MechanismRoot2d mechanism2dRoot;
    private MechanismLigament2d elevatorArm;

    
    public ElevatorIOSim() {
        motor = new PWMSparkMax(1);
        motorSim = new PWMSim(motor);
        encoderSim = new EncoderSim(new Encoder(0, 1));
        pidControllerSim = new ProfiledPIDController(0,0,0, new TrapezoidProfile.Constraints(2.45, 2.45));
        feedforwardSim = new ElevatorFeedforward(0,0.762, 0.762, 0);
        elevatorSim = new ElevatorSim(0, 0, DCMotor.getVex775Pro(4), 0, 1.25, true, 0);
        mechanism2d = new Mechanism2d(1, 1.5);
        mechanism2dRoot = mechanism2d.getRoot("Elevator Root", 0.5, 0);   
        elevatorArm = new MechanismLigament2d("Elevator Arm", 0, 90); 
        mechanism2dRoot.append(elevatorArm);

        encoderSim.setDistancePerPulse(2.0 * Math.PI * Units.inchesToMeters(2) / 4096);
    }
    
    public void setTarget (double target) {
        pidControllerSim.setGoal(target);
        double pidOutput = pidControllerSim.calculate(encoderSim.getDistance());
        double feedforwardOutput = feedforwardSim.calculate(pidControllerSim.getGoal().velocity);
        motor.setVoltage(feedforwardOutput + pidOutput);
    }

    @Override
    public void updateInfo() {
    // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateInfo'");
    }

    @Override
    public boolean isReady() {

        throw new UnsupportedOperationException("Unimplemented method 'isReady'");
    }
}
