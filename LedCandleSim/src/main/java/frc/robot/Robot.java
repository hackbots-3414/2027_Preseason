// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * Standard command-based TimedRobot. All the interesting wiring lives in {@link RobotContainer}.
 */
public class Robot extends TimedRobot {
  @SuppressWarnings("unused")
  private final RobotContainer m_robotContainer;

  public Robot() {
    m_robotContainer = new RobotContainer();

    // Serve the deploy directory on port 5800 so Elastic can pull our dashboard layout straight
    // from the robot (or from the simulator, at localhost): File > Load Layout from Robot.
    WebServer.start(5800, Filesystem.getDeployDirectory().getPath());
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }
}
