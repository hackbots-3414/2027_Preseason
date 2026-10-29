package frc.robot.subsystems.leds;

/**
 * Physical layout of the LED strip. One continuous strip of 36 LEDs is wired in this order:
 *
 * <pre>
 *            TOP  12 ──────────────► 23
 *           ┌──────────────────────────┐
 *        11 │                          │ 24
 *         ▲ │                          │ │
 *   LEFT  │ │         (robot)          │ ▼  RIGHT
 *         │ │                          │
 *         0 │                          │ 35
 * </pre>
 *
 * <ul>
 *   <li>Left side: strip LEDs 0-11, from the bottom (0) up to the top (11)
 *   <li>Top: strip LEDs 12-23, from left (12) to right (23)
 *   <li>Right side: strip LEDs 24-35, from the top (24) down to the bottom (35)
 * </ul>
 *
 * <p>All indices in this project are <b>strip</b> indices (0-35). The CANdle numbers its 8 onboard
 * LEDs 0-7 and the external strip from 8, so {@link #toCandleIndex(int)} adds that offset only when
 * talking to the hardware.
 *
 * <p>This class is plain Java (no WPILib/CTRE imports) so it can be unit tested anywhere.
 */
public final class LedLayout {
  private LedLayout() {}

  public static final int LEDS_PER_SIDE = 12;
  public static final int LEDS_ACROSS_TOP = 12;
  public static final int TOTAL_LEDS = LEDS_PER_SIDE + LEDS_ACROSS_TOP + LEDS_PER_SIDE; // 36

  public static final int LEFT_BOTTOM = 0;
  public static final int LEFT_TOP = LEFT_BOTTOM + LEDS_PER_SIDE - 1; // 11
  public static final int TOP_LEFT = LEFT_TOP + 1; // 12
  public static final int TOP_RIGHT = TOP_LEFT + LEDS_ACROSS_TOP - 1; // 23
  public static final int RIGHT_TOP = TOP_RIGHT + 1; // 24
  public static final int RIGHT_BOTTOM = RIGHT_TOP + LEDS_PER_SIDE - 1; // 35

  public static final int FIRST = LEFT_BOTTOM;
  public static final int LAST = RIGHT_BOTTOM;

  /** CANdle indices 0-7 are the onboard LEDs; the external strip starts at 8. */
  public static final int CANDLE_STRIP_OFFSET = 8;

  public static int toCandleIndex(int stripIndex) {
    return stripIndex + CANDLE_STRIP_OFFSET;
  }
}
