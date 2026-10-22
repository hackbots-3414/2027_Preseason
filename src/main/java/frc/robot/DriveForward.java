// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class DriveForward extends Command {
 final CommandSwerveDrivetrain drivetrain = new CommandSwerveDrivetrain(null,null);
  /** Creates a new DriveForward. */
  private Pose2d finalPose;
  private Translation2d travelLength;
  public DriveForward(Translation2d travelLength) {
    // Use addRequirements() here to declare subsystem dependencies.
   this.travelLength = travelLength;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
   finalPose = drivetrain.getPose().plus(new Transform2d(travelLength, new Rotation2d()));
    // Get the position of the robot, this our start position or point "a"
      // include the position & heading
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // goal : start driving forward
    // activate drive motors | Which way? How fast? (directions, rotations / secoonds, voltage)
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    //tell motors to stop: | motors drive with power 0
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // if we're measuring seconds has he command been running for 5 seconds?
    // if we're trying to get to a distance: are we at our desired point?
    // return true
    return false;
  }
}
