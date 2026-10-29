// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.leds.LedSubsystem;

/** Creates the subsystems and wires up commands and dashboard buttons. */
public class RobotContainer {
  private final LedSubsystem m_leds = new LedSubsystem();

  public RobotContainer() {
    // Whenever no other command is using the LEDs, show the purple trace.
    m_leds.setDefaultCommand(m_leds.purpleTrace());

    // Dashboard button. Elastic shows this as a Command widget: press it to start KITT + Flames,
    // press it again to cancel. When it's cancelled the default command (purple trace) resumes.
    SmartDashboard.putData("LEDs/KITT + Flames", m_leds.kittAndFlames());

    // Handy for debugging: shows which commands are running and lets you cancel them.
    SmartDashboard.putData("Scheduler", CommandScheduler.getInstance());
    SmartDashboard.putData("LED Subsystem", m_leds);
  }
}
