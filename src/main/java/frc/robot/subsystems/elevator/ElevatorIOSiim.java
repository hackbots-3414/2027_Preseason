// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.elevator;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;

/** Add your docs here. */
public class ElevatorIOSiim implements ElevatorIO{

    private DCMotorSim motorSim;
    private EncoderSim encoderSim;
    private ProfiledPIDController pidcoControllerSim;
    private ElevatorFeedforward feedforwardSim;


    public void setTarget (double target) {
        throw new UnsupportedOperationException("Unimplemented method 'setTarget'");
    }

    @Override
    public void updateInfo() {
    
        throw new UnsupportedOperationException("Unimplemented method 'updateInfo'");
    }

    @Override
    public boolean isReady() {

        throw new UnsupportedOperationException("Unimplemented method 'isReady'");
    }
}
