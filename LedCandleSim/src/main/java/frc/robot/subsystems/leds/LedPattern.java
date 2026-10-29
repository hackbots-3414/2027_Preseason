package frc.robot.subsystems.leds;

import static frc.robot.subsystems.leds.LedLayout.*;

import frc.robot.subsystems.leds.LedAnimation.Bounce;
import frc.robot.subsystems.leds.LedAnimation.Direction;
import java.util.List;

/**
 * Everything the LEDs can show. Each pattern is a set of CANdle animations, one per slot, that run
 * together. To add a pattern, add an entry here and a command for it in {@link LedSubsystem}.
 *
 * <p>Plain Java (no WPILib/CTRE) so patterns can be unit tested anywhere.
 */
public enum LedPattern {
  /**
   * Default: a purple pocket of light starts at the bottom of the left side, climbs up, runs across
   * the top, down the right side, then bounces back. 12 LEDs/s, so one pass takes about 3 s.
   */
  PURPLE_TRACE(
      "Purple Trace",
      List.of(LedAnimation.larson(0, FIRST, LAST, Colors.PURPLE, 4, Bounce.FRONT, 12))),

  /**
   * Party mode: a KITT (Knight Rider) red scanner bouncing across the top, with flames burning
   * upward on both sides.
   */
  KITT_AND_FLAMES(
      "KITT + Flames",
      List.of(
          // KITT scanner across the top: 3-LED red eye, 24 LEDs/s (~0.4 s per sweep).
          LedAnimation.larson(0, TOP_LEFT, TOP_RIGHT, Colors.RED, 3, Bounce.FRONT, 24),
          // Left side is wired bottom (0) -> top (11): flame base at the start index.
          LedAnimation.fire(1, LEFT_BOTTOM, LEFT_TOP, Direction.FORWARD, 0.6, 0.3, 60),
          // Right side is wired top (24) -> bottom (35): flame base at the END index so it burns up.
          LedAnimation.fire(2, RIGHT_TOP, RIGHT_BOTTOM, Direction.BACKWARD, 0.6, 0.3, 60)));

  private final String displayName;
  private final List<LedAnimation> animations;

  LedPattern(String displayName, List<LedAnimation> animations) {
    this.displayName = displayName;
    this.animations = animations;
  }

  public String displayName() {
    return displayName;
  }

  public List<LedAnimation> animations() {
    return animations;
  }

  public boolean usesSlot(int slot) {
    return animations.stream().anyMatch(a -> a.slot() == slot);
  }

  /** Team colors as 0xRRGGBB. */
  public static final class Colors {
    private Colors() {}

    public static final int PURPLE = 0x8000FF;
    public static final int RED = 0xFF0000;
  }
}
