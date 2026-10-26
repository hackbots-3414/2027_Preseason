//process to make robot go forward
//1st
//initialise
  //get position of robot
    //include both position and heading

//2nd
//execute
  //start driving forwards
    //activate drive motors | Which way? How fast? (directio, rotation/second, voltage)

//3rd
//isFinished
  //if we were measuring seconds, has comand been running for desired time?
  //if we were trying to get to a distance, sre we at our desired point?
  //return true

//4th
//end
  //tell motors to stop | motors dive with power 0










// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */

public class Dr1v3F0rward extends Command {
  /** Creates a new Dr1v3F0rward. */
  final CommandSwerveDrivetrain drivetrain = new CommandSwerveDrivetrain(null, null); 
  private Pose2d finalPose;
  private Translation2d travelLength;
  public Dr1v3F0rward(Translation2d travelLength) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.travelLength = travelLength;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    finalPose = drivetrain.getPose().plus(new Transform2d(travelLength, new Rotation2d()));
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}

