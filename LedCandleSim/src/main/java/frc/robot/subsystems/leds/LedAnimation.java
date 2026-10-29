package frc.robot.subsystems.leds;

/**
 * A description of one CANdle animation slot: what animation, on which LEDs, with what settings.
 *
 * <p>This is the single source of truth for both outputs:
 *
 * <ul>
 *   <li>{@link LedSubsystem} turns it into a Phoenix 6 control request for the real CANdle.
 *   <li>{@link LedSimRenderer} uses it to draw what the CANdle should be showing, so the dashboard
 *       works in simulation and on test chassis that don't have LEDs.
 * </ul>
 *
 * <p>Indices are strip indices (see {@link LedLayout}), inclusive on both ends. Plain Java so it can
 * be unit tested without WPILib.
 *
 * @param type which CANdle animation
 * @param slot CANdle animation slot, 0-7. Each slot runs one animation at a time.
 * @param startIndex first strip LED controlled (inclusive)
 * @param endIndex last strip LED controlled (inclusive)
 * @param rgb color as 0xRRGGBB (Larson only; CTRE's fire uses a fixed flame palette)
 * @param size Larson: number of LEDs in the moving pocket of light, 1-15
 * @param bounce Larson: when the pocket turns around at the ends
 * @param direction Fire: FORWARD = flame base at startIndex; BACKWARD = flame base at endIndex
 * @param frameRateHz frames per second, 2-1000. For Larson each frame moves the pocket one LED.
 * @param sparking Fire: how often new sparks ignite at the base, 0-1
 * @param cooling Fire: how fast the flame cools as it rises, 0-1 (higher = shorter flames)
 */
public record LedAnimation(
    Type type,
    int slot,
    int startIndex,
    int endIndex,
    int rgb,
    int size,
    Bounce bounce,
    Direction direction,
    double frameRateHz,
    double sparking,
    double cooling) {

  public enum Type {
    LARSON,
    FIRE
  }

  /** Mirrors CTRE's LarsonBounceValue. */
  public enum Bounce {
    /** Turn around as soon as the leading LED reaches the end (pocket always fully visible). */
    FRONT,
    /** Turn around once the middle of the pocket reaches the end. */
    CENTER,
    /** Turn around once the whole pocket has run off the end. */
    BACK
  }

  /** Mirrors CTRE's AnimationDirectionValue. */
  public enum Direction {
    FORWARD,
    BACKWARD
  }

  public LedAnimation {
    if (slot < 0 || slot > 7) {
      throw new IllegalArgumentException("CANdle slot must be 0-7, got " + slot);
    }
    if (startIndex < 0 || endIndex < startIndex) {
      throw new IllegalArgumentException("Bad LED range " + startIndex + ".." + endIndex);
    }
    if (frameRateHz < 2 || frameRateHz > 1000) {
      throw new IllegalArgumentException("Frame rate must be 2-1000 Hz, got " + frameRateHz);
    }
    if (type == Type.LARSON && (size < 1 || size > 15)) {
      throw new IllegalArgumentException("Larson size must be 1-15, got " + size);
    }
  }

  /** A pocket of light that bounces back and forth between startIndex and endIndex. */
  public static LedAnimation larson(
      int slot, int startIndex, int endIndex, int rgb, int size, Bounce bounce, double frameRateHz) {
    return new LedAnimation(
        Type.LARSON, slot, startIndex, endIndex, rgb, size, bounce, Direction.FORWARD,
        frameRateHz, 0, 0);
  }

  /** A flickering flame. The base of the flame is at startIndex (FORWARD) or endIndex (BACKWARD). */
  public static LedAnimation fire(
      int slot,
      int startIndex,
      int endIndex,
      Direction direction,
      double sparking,
      double cooling,
      double frameRateHz) {
    return new LedAnimation(
        Type.FIRE, slot, startIndex, endIndex, 0, 0, Bounce.FRONT, direction, frameRateHz,
        sparking, cooling);
  }

  public int length() {
    return endIndex - startIndex + 1;
  }

  public int red() {
    return (rgb >> 16) & 0xFF;
  }

  public int green() {
    return (rgb >> 8) & 0xFF;
  }

  public int blue() {
    return rgb & 0xFF;
  }
}
