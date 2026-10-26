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
public class DriveForward extends Command {
  private Pose2d finalPose;
  private Translation2d travelLength;
  private CommandSwerveDrivetrain drivetrain; 
  private double remainingDistance;

  final double kSpeedMultiplier = 0.5;  // Multiplied by remaining distace to get motor speed
  final double kMinTolerance = 0.1; // Distance in metres where we say we are "at" the target (when we are "close enough")
  final double kMaxTolerance = 10; // Distance in metres where we say we are too far from the target

  /** Creates a new DriveForward. */
  public DriveForward(Translation2d travelLength, CommandSwerveDrivetrain drivetrain) {
    this.drivetrain = drivetrain;
    this.travelLength = travelLength;
  }

  // Called when the command is initially scheduled. Generates desired pose
  @Override
  public void initialize() {
    finalPose = drivetrain.getPose().plus(new Transform2d(travelLength, new Rotation2d()));
  }

  // Called every time the scheduler runs while the command is scheduled. 
  // Checks how close we are to target then applies force to motors based on the distance.
  @Override
  public void execute() {
    remainingDistance = finalPose.minus(drivetrain.getPose()).getMeasureX().magnitude();

    drivetrain.setControl(new SwerveRequest.ApplyRobotSpeeds().withSpeeds(
      new ChassisSpeeds(remainingDistance * kSpeedMultiplier, 0, 0)
    ));

  }

  // Called once the command ends or is interrupted. Sets motor speed to zero.
  @Override
  public void end(boolean interrupted) {
    drivetrain.setControl(new SwerveRequest.ApplyRobotSpeeds().withSpeeds(
      new ChassisSpeeds(0, 0, 0)
    ));
  }
  

  // Returns true when the command should end. Checks if we are within set threshhold of target
  @Override
  public boolean isFinished() {
    if (remainingDistance < kMinTolerance || remainingDistance > kMaxTolerance) {
      return true;
    } else {
      return false;
    }
  }
}