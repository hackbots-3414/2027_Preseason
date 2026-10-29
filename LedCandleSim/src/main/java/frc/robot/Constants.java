// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * Robot-wide constants. LED geometry (which LED is where) lives in
 * {@link frc.robot.subsystems.leds.LedLayout}; patterns live in
 * {@link frc.robot.subsystems.leds.LedPattern}.
 */
public final class Constants {
  private Constants() {}

  public static final class LedConstants {
    private LedConstants() {}

    /** CAN ID of the CANdle (set in Phoenix Tuner X). */
    public static final int kCandleId = 1;

    /** CAN bus name. "rio" is the roboRIO's built-in bus; use the CANivore's name if on one. */
    public static final String kCanBusName = "rio";

    /** Global brightness scalar applied by the CANdle to everything it drives (0.0 - 1.0). */
    public static final double kBrightness = 0.5;

    /**
     * Also render the LEDs as a robot-shaped picture and serve it as a camera stream ("LED View")
     * that Elastic's Camera Stream widget can show. Turn off if you need the CPU on a real roboRIO.
     */
    public static final boolean kPublishLedImage = true;
  }
}
