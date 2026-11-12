// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.elevator;

/** Add your docs here. */
public enum ElevatorState {
    Zero(0),
    Top(1.25),
    Middle(0.75);

    private final double m_position;

    private ElevatorState(double position) {
        m_position = position;
    }

    protected double position() {
        return m_position;
    }

    


}
