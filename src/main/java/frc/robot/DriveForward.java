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
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class DriveForward extends Command {

  /** Creates a new Fajita. */
  private Pose2d finalPose;
  private CommandSwerveDrivetrain drivetrain;
  private double remainingDistance;
  private Translation2d travelLength;
  public DriveForward(Translation2d travelLength, CommandSwerveDrivetrain drivetrain) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.travelLength = travelLength;
    this.drivetrain = drivetrain;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    finalPose = drivetrain.getPose().plus(new Transform2d(travelLength, new Rotation2d()));
    //Get pos of robot, this is our start pos AKA "a"
    // include pos and heading

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    remainingDistance = finalPose.minus(drivetrain.getPose()).getMeasureX().magnitude();
    final double baseSpeed = .7;
    drivetrain.setControl(new SwerveRequest.ApplyRobotSpeeds().withSpeeds(new ChassisSpeeds(baseSpeed * remainingDistance, 0, 0)));
    //goal: drive forward
    //activate drive motors | which way? how fast? (direction, rotations/ second, voltage)

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drivetrain.setControl(new SwerveRequest.ApplyRobotSpeeds().withSpeeds(new ChassisSpeeds(0, 0, 0)));
    //tell motors to stop!: | motors drive with power: 0
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    final double tolerance = 0.1;
    if (remainingDistance > 15||remainingDistance < tolerance) {
      return true;
    } else {
      return false;
    }
    //if we're measuring secs, has the command been running for 5 seconds?
    //if we're trying ot get to a distance or point: are we at our desired point?
    //return true
    //else false
  }
}
