package frc.robot.subsystems.leds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Software model of the CANdle: given the animations we sent it, works out what color every LED
 * should be right now. This is what feeds the dashboard, so you can watch the LEDs in simulation or
 * on a test chassis without a CANdle.
 *
 * <p>The CANdle runs its animations in its own firmware and doesn't report pixel colors back, so
 * this is an <b>approximation</b> of CTRE's animations: timing, positions, direction and colors
 * follow the documented parameters, but the exact flicker of the fire and any fading the firmware
 * does will differ a little from the real strip.
 *
 * <p>Plain Java (no WPILib/CTRE imports) so it can be unit tested anywhere.
 */
public final class LedSimRenderer {
  private final int[] pixels; // 0xRRGGBB per strip LED
  private final List<Runner> runners = new ArrayList<>();

  public LedSimRenderer(int ledCount) {
    pixels = new int[ledCount];
  }

  /** Replace the running animations, restarting each from its first frame. */
  public void setAnimations(List<LedAnimation> animations) {
    runners.clear();
    for (LedAnimation a : animations) {
      if (a.endIndex() >= pixels.length) {
        throw new IllegalArgumentException("Animation runs past the end of the strip: " + a);
      }
      runners.add(a.type() == LedAnimation.Type.FIRE ? new FireRunner(a) : new LarsonRunner(a));
    }
    draw();
  }

  /** Advance all animations by dtSeconds and redraw. */
  public void update(double dtSeconds) {
    for (Runner r : runners) {
      r.advance(Math.max(0, dtSeconds));
    }
    draw();
  }

  public int size() {
    return pixels.length;
  }

  /** Color of one LED as 0xRRGGBB. */
  public int rgb(int index) {
    return pixels[index];
  }

  /** Color of one LED as "#RRGGBB" (the format Elastic's color widgets expect). */
  public String hex(int index) {
    return String.format("#%06X", pixels[index]);
  }

  private void draw() {
    java.util.Arrays.fill(pixels, 0);
    for (Runner r : runners) {
      r.draw(pixels);
    }
  }

  // ---------------------------------------------------------------------------------------------

  private abstract static class Runner {
    final LedAnimation anim;
    double elapsed; // seconds since the animation started
    long framesDone; // whole frames processed so far

    Runner(LedAnimation anim) {
      this.anim = anim;
    }

    void advance(double dt) {
      elapsed += dt;
      long target = (long) Math.floor(elapsed * anim.frameRateHz());
      while (framesDone < target) {
        step();
        framesDone++;
      }
    }

    /** Advance one animation frame. */
    abstract void step();

    abstract void draw(int[] pixels);
  }

  /**
   * Larson ("KITT"/Cylon scanner): a pocket of {@code size} LEDs that moves one LED per frame from
   * the start index toward the end index and bounces back.
   */
  static final class LarsonRunner extends Runner {
    private final int minPos; // leftmost position (relative to startIndex) of the pocket's tail
    private final int maxPos;
    private int pos;
    private int dir = +1;

    LarsonRunner(LedAnimation a) {
      super(a);
      int n = a.length();
      int size = Math.min(a.size(), n);
      switch (a.bounce()) {
        case FRONT -> { // pocket never leaves the strip
          minPos = 0;
          maxPos = n - size;
        }
        case CENTER -> { // turn around when the middle of the pocket hits the end
          minPos = -(size / 2);
          maxPos = n - 1 - (size / 2);
        }
        default -> { // BACK: turn around once the pocket has fully run off the end
          minPos = -size;
          maxPos = n;
        }
      }
      pos = minPos; // start at startIndex, heading toward endIndex
    }

    @Override
    void step() {
      if (maxPos <= minPos) {
        return; // pocket fills the whole range; nothing to move
      }
      pos += dir;
      if (pos >= maxPos) {
        pos = maxPos;
        dir = -1;
      } else if (pos <= minPos) {
        pos = minPos;
        dir = +1;
      }
    }

    @Override
    void draw(int[] pixels) {
      for (int i = 0; i < anim.size(); i++) {
        int rel = pos + i;
        if (rel >= 0 && rel < anim.length()) {
          pixels[anim.startIndex() + rel] = anim.rgb();
        }
      }
    }

    /** Position of the pocket's tail, relative to startIndex. Visible for tests. */
    int position() {
      return pos;
    }
  }

  /**
   * Fire: the classic "Fire2012" heat simulation. Each frame every cell cools a little, heat drifts
   * away from the base, and new sparks randomly ignite near the base. Heat maps to a black -> red ->
   * yellow -> white flame palette.
   */
  static final class FireRunner extends Runner {
    private final int[] heat;
    private final Random random;

    FireRunner(LedAnimation a) {
      super(a);
      heat = new int[a.length()];
      random = new Random(1234L + a.slot()); // deterministic so tests are repeatable
    }

    @Override
    void step() {
      int n = heat.length;
      // 1. Cool every cell a little. Shorter strips cool faster per cell so flames scale with length.
      int maxCooling = (int) (anim.cooling() * 200.0 * 10.0 / n) + 2;
      for (int i = 0; i < n; i++) {
        heat[i] = Math.max(0, heat[i] - random.nextInt(maxCooling + 1));
      }
      // 2. Heat rises: each cell takes on heat from the two cells below it.
      for (int k = n - 1; k >= 2; k--) {
        heat[k] = (heat[k - 1] + 2 * heat[k - 2]) / 3;
      }
      // 3. Randomly ignite new sparks near the base.
      if (random.nextDouble() < anim.sparking()) {
        int y = random.nextInt(Math.min(3, n));
        heat[y] = Math.min(255, heat[y] + 160 + random.nextInt(96));
      }
    }

    @Override
    void draw(int[] pixels) {
      int n = heat.length;
      for (int h = 0; h < n; h++) {
        // h = 0 is the base of the flame.
        int index =
            anim.direction() == LedAnimation.Direction.FORWARD
                ? anim.startIndex() + h
                : anim.endIndex() - h;
        pixels[index] = heatColor(heat[h]);
      }
    }

    /** Heat at height h above the flame base (0 = base). Visible for tests. */
    int heatAt(int h) {
      return heat[h];
    }

    static int heatColor(int temperature) {
      int t192 = temperature * 191 / 255;
      int ramp = (t192 & 0x3F) << 2;
      if (t192 >= 0x80) {
        return 0xFFFF00 | ramp; // hottest: yellow -> white
      } else if (t192 >= 0x40) {
        return 0xFF0000 | (ramp << 8); // middle: red -> yellow
      } else {
        return ramp << 16; // coolest: black -> red
      }
    }
  }
}
