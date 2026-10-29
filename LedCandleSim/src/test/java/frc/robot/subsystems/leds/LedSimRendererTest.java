package frc.robot.subsystems.leds;

import static frc.robot.subsystems.leds.LedLayout.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the LED patterns and the software model. These are plain Java, so they run with
 * {@code ./gradlew test} without a robot or simulator.
 */
class LedSimRendererTest {
  private static final double kLoop = 0.02; // one 20 ms robot loop

  // ------------------------------------------------------------------ layout

  @Test
  void layoutIs12Left12Top12Right() {
    assertEquals(36, TOTAL_LEDS);
    assertEquals(0, LEFT_BOTTOM);
    assertEquals(11, LEFT_TOP);
    assertEquals(12, TOP_LEFT);
    assertEquals(23, TOP_RIGHT);
    assertEquals(24, RIGHT_TOP);
    assertEquals(35, RIGHT_BOTTOM);
    assertEquals(8, toCandleIndex(0)); // CANdle onboard LEDs are 0-7
    assertEquals(43, toCandleIndex(35));
  }

  @Test
  void patternsFitOnTheStripAndDontShareSlots() {
    for (LedPattern p : LedPattern.values()) {
      long distinctSlots = p.animations().stream().mapToInt(LedAnimation::slot).distinct().count();
      assertEquals(p.animations().size(), distinctSlots, p + " reuses a slot");
      for (LedAnimation a : p.animations()) {
        assertTrue(a.endIndex() <= LAST, p + " runs off the end of the strip");
      }
    }
  }

  @Test
  void kittAndFlamesSegmentsDontOverlap() {
    boolean[] used = new boolean[TOTAL_LEDS];
    for (LedAnimation a : LedPattern.KITT_AND_FLAMES.animations()) {
      for (int i = a.startIndex(); i <= a.endIndex(); i++) {
        assertFalse(used[i], "LED " + i + " is driven by two animations");
        used[i] = true;
      }
    }
  }

  // ------------------------------------------------------------------ purple trace

  @Test
  void purpleTraceStartsAtBottomLeft() {
    var model = new LedSimRenderer(TOTAL_LEDS);
    model.setAnimations(LedPattern.PURPLE_TRACE.animations());

    assertEquals(LedPattern.Colors.PURPLE, model.rgb(LEFT_BOTTOM));
    assertEquals(0, model.rgb(RIGHT_BOTTOM));
    assertEquals(0, model.rgb(TOP_LEFT));
  }

  @Test
  void purpleTraceReachesBottomRightThenBouncesBack() {
    var model = new LedSimRenderer(TOTAL_LEDS);
    model.setAnimations(LedPattern.PURPLE_TRACE.animations());

    // Track where the pocket's lowest lit LED is, loop by loop.
    int maxSeen = 0;
    boolean cameBack = false;
    for (int loop = 0; loop < 400; loop++) { // 8 seconds
      model.update(kLoop);
      int first = firstLit(model);
      assertTrue(first >= 0, "trace disappeared");
      maxSeen = Math.max(maxSeen, first);
      if (model.rgb(RIGHT_BOTTOM) != 0 && first < maxSeen) {
        fail("pocket moved backward before reaching the end");
      }
      if (maxSeen > 0 && model.rgb(RIGHT_BOTTOM) == 0 && first < maxSeen - 5) {
        cameBack = true;
      }
    }
    assertEquals(LAST - 3, maxSeen, "4-LED pocket should reach the bottom-right corner");
    assertTrue(cameBack, "trace should bounce back toward the left");
  }

  @Test
  void purpleTraceIsSlow() {
    var model = new LedSimRenderer(TOTAL_LEDS);
    model.setAnimations(LedPattern.PURPLE_TRACE.animations());
    model.update(1.0); // 1 second at 12 LEDs/s
    assertEquals(12, firstLit(model), "should have moved 12 LEDs in one second");
  }

  // ------------------------------------------------------------------ KITT + flames

  @Test
  void kittScannerStaysOnTopAndIsRed() {
    var model = new LedSimRenderer(TOTAL_LEDS);
    model.setAnimations(List.of(LedPattern.KITT_AND_FLAMES.animations().get(0)));

    boolean hitLeftEnd = false;
    boolean hitRightEnd = false;
    for (int loop = 0; loop < 200; loop++) {
      model.update(kLoop);
      int lit = 0;
      for (int i = 0; i < TOTAL_LEDS; i++) {
        if (model.rgb(i) != 0) {
          assertTrue(i >= TOP_LEFT && i <= TOP_RIGHT, "KITT lit LED " + i + " off the top");
          assertEquals(LedPattern.Colors.RED, model.rgb(i));
          lit++;
        }
      }
      assertEquals(3, lit, "KITT eye is 3 LEDs");
      hitLeftEnd |= model.rgb(TOP_LEFT) != 0;
      hitRightEnd |= model.rgb(TOP_RIGHT) != 0;
    }
    assertTrue(hitLeftEnd && hitRightEnd, "KITT should sweep the full top");
  }

  @Test
  void flamesBurnUpwardOnBothSides() {
    var model = new LedSimRenderer(TOTAL_LEDS);
    model.setAnimations(LedPattern.KITT_AND_FLAMES.animations());

    // Average brightness at each height over a few seconds: the base should be hotter than the tip.
    double[] leftByHeight = new double[LEDS_PER_SIDE];
    double[] rightByHeight = new double[LEDS_PER_SIDE];
    for (int loop = 0; loop < 250; loop++) {
      model.update(kLoop);
      for (int h = 0; h < LEDS_PER_SIDE; h++) {
        leftByHeight[h] += brightness(model.rgb(LEFT_BOTTOM + h));
        rightByHeight[h] += brightness(model.rgb(RIGHT_BOTTOM - h));
      }
    }
    assertTrue(leftByHeight[0] > 0, "left flame never lit");
    assertTrue(rightByHeight[0] > 0, "right flame never lit");
    assertTrue(leftByHeight[1] > leftByHeight[LEDS_PER_SIDE - 1], "left flame should burn upward");
    assertTrue(
        rightByHeight[1] > rightByHeight[LEDS_PER_SIDE - 1], "right flame should burn upward");
  }

  @Test
  void flameColorsAreFirePalette() {
    assertEquals(0x000000, LedSimRenderer.FireRunner.heatColor(0));
    int warm = LedSimRenderer.FireRunner.heatColor(120); // mid heat: red/orange
    assertEquals(0xFF, (warm >> 16) & 0xFF);
    assertEquals(0, warm & 0xFF);
    int hot = LedSimRenderer.FireRunner.heatColor(255); // hottest: yellow-white
    assertEquals(0xFFFF00, hot & 0xFFFF00);
  }

  @Test
  void switchingPatternsClearsOldLeds() {
    var model = new LedSimRenderer(TOTAL_LEDS);
    model.setAnimations(LedPattern.KITT_AND_FLAMES.animations());
    for (int loop = 0; loop < 50; loop++) {
      model.update(kLoop);
    }
    model.setAnimations(LedPattern.PURPLE_TRACE.animations());
    for (int i = 0; i < TOTAL_LEDS; i++) {
      int expected = i < 4 ? LedPattern.Colors.PURPLE : 0;
      assertEquals(expected, model.rgb(i), "LED " + i);
    }
  }

  @Test
  void hexFormatMatchesElastic() {
    var model = new LedSimRenderer(TOTAL_LEDS);
    model.setAnimations(LedPattern.PURPLE_TRACE.animations());
    assertEquals("#8000FF", model.hex(0));
    assertEquals("#000000", model.hex(35));
  }

  // ------------------------------------------------------------------ helpers

  private static int firstLit(LedSimRenderer model) {
    for (int i = 0; i < model.size(); i++) {
      if (model.rgb(i) != 0) {
        return i;
      }
    }
    return -1;
  }

  private static int brightness(int rgb) {
    return ((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF);
  }
}
