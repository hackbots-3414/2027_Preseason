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

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */

public class Dr1v3F0rward extends Command {
  private Pose2d finalPose;
  private double remainingDistance;
  private Translation2d travelLength;
  public Dr1v3F0rward(Transform2d travelLength, CommandSwerveDrivetrain drivetrain)
  //use addrequirements()here to declare subsystem dependencies.
  this.drivetrain = drivetrain;
  this.travelLength = travelLength;


  //Called when the command is initially scheduled.
  @Override
  public void end(boolean interrupted) {
    drivetrain.applyRequest(() -> new SwerveRequest.ApplyRobotSpeeds().withSpeeds().withSpeeds(new ChassisSpeeds(baseSpeed * remaingDistance0, 0, 0 ))):
    //goal : Start drving forward
    // activate drive motors | which way? How fast? (direction,rotation/seocond, voltage)

  }

// Called once the command ends or is interrupted.
@Override
public void end(boolean interrupted) {
drivetrain.applyRequest(() -> new SwerveRequest.ApplyRobotSpeeds().withSpeeds(new ChassisSpeeds(0, 0, 0)));
// tell motors to stop | motors drive with power 0
}



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
  public void initialize() {init

    finalPose = drivetrain.getPose().plus(new Transform2d(travelLength, new Rotation2d()));
    drivetrain.applyRequest(new SwerveRequest.ApplyRobotSpeeds().withSpeeds(new ChassisSpeeds(baseSpead * remaingDistance 0)));
    // goal : start driving forward 
    // activate drive motors | which way? how fast? (direction, rotation/second, voltage)
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}
    drivetrain.applyRequest(() --> new SwerveRequest.ApplyRobotSpeeds().withSpeads(new ChassicsSpeeds(0,0,0)))
  // Returns true when the command should end.
  // remainingDistance = 0.1
  if{ }
  if (remainingDistance < tolerance) {
    return true;
  } else {
    return false;
  }
  }
  @Override
  public boolean isFinished() {
// if we were measuring seconds, has command been running for 5 seconds?
// if we're trying to get to get to a distance: are we at our desired point?
//return true
    return false