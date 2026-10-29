package frc.robot.subsystems.leds;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.EmptyAnimation;
import com.ctre.phoenix6.controls.FireAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.AnimationDirectionValue;
import com.ctre.phoenix6.signals.LarsonBounceValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import com.ctre.phoenix6.signals.StripTypeValue;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LedConstants;

/**
 * LED subsystem: one CTRE CANdle driving a 36-LED strip (left side, top, right side; see {@link
 * LedLayout}).
 *
 * <p>How it works:
 *
 * <ol>
 *   <li>A command asks for a {@link LedPattern}.
 *   <li>The pattern's animations are sent to the CANdle <i>once</i> (when the pattern changes). The
 *       CANdle then runs them on its own, so there's no CAN traffic while an animation plays.
 *   <li>The same animations are fed to {@link LedSimRenderer}, which works out what the strip should
 *       look like, and {@link LedDashboard} publishes that to NetworkTables for Elastic.
 * </ol>
 *
 * <p>Step 3 always runs (real robot or simulation), so the dashboard shows the LEDs even on a test
 * chassis that has no CANdle or strip attached.
 */
public class LedSubsystem extends SubsystemBase {
  /** The CANdle has 8 animation slots (0-7). */
  private static final int kNumSlots = 8;

  private final CANdle m_candle =
      new CANdle(LedConstants.kCandleId, new CANBus(LedConstants.kCanBusName));
  private final LedSimRenderer m_model = new LedSimRenderer(LedLayout.TOTAL_LEDS);
  private final LedDashboard m_dashboard = new LedDashboard(m_model);

  private LedPattern m_activePattern = null;
  private double m_lastUpdateSeconds = Timer.getFPGATimestamp();

  public LedSubsystem() {
    setName("LEDs");

    var config = new CANdleConfiguration();
    config.LED.StripType = StripTypeValue.GRB; // most WS2812-style strips are GRB
    config.LED.BrightnessScalar = LedConstants.kBrightness;
    config.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Disabled;
    m_candle.getConfigurator().apply(config);

    // Start from a clean slate: stop anything left running from a previous program.
    for (int slot = 0; slot < kNumSlots; slot++) {
      m_candle.setControl(new EmptyAnimation(slot));
    }
  }

  // ---------------------------------------------------------------- Commands

  /**
   * Shows a pattern for as long as the command runs. Runs while disabled too, so the LEDs (and the
   * dashboard buttons) work in the pits and in simulation without enabling the robot.
   */
  public Command showPattern(LedPattern pattern) {
    return run(() -> applyPattern(pattern)).ignoringDisable(true).withName(pattern.displayName());
  }

  /** Default behavior: purple trace up the left, across the top, down the right, and back. */
  public Command purpleTrace() {
    return showPattern(LedPattern.PURPLE_TRACE);
  }

  /** KITT scanner across the top with flames burning up both sides. */
  public Command kittAndFlames() {
    return showPattern(LedPattern.KITT_AND_FLAMES);
  }

  // ---------------------------------------------------------------- Periodic

  @Override
  public void periodic() {
    double now = Timer.getFPGATimestamp();
    m_model.update(now - m_lastUpdateSeconds);
    m_lastUpdateSeconds = now;
    m_dashboard.update(m_activePattern);
  }

  public LedPattern getActivePattern() {
    return m_activePattern;
  }

  // ---------------------------------------------------------------- Hardware

  /** Sends a pattern to the CANdle and the model. Does nothing if it's already showing. */
  private void applyPattern(LedPattern pattern) {
    if (pattern == m_activePattern) {
      return;
    }

    // Free any slots the old pattern used that the new one doesn't, or they'd keep animating.
    if (m_activePattern != null) {
      for (LedAnimation old : m_activePattern.animations()) {
        if (!pattern.usesSlot(old.slot())) {
          m_candle.setControl(new EmptyAnimation(old.slot()));
        }
      }
    }

    for (LedAnimation anim : pattern.animations()) {
      sendToCandle(anim);
    }

    m_model.setAnimations(pattern.animations());
    m_activePattern = pattern;
  }

  private void sendToCandle(LedAnimation anim) {
    int start = LedLayout.toCandleIndex(anim.startIndex());
    int end = LedLayout.toCandleIndex(anim.endIndex());

    switch (anim.type()) {
      case LARSON ->
          m_candle.setControl(
              new LarsonAnimation(start, end)
                  .withSlot(anim.slot())
                  .withColor(new RGBWColor(anim.red(), anim.green(), anim.blue(), 0))
                  .withSize(anim.size())
                  .withBounceMode(toCtre(anim.bounce()))
                  .withFrameRate(anim.frameRateHz()));
      case FIRE ->
          m_candle.setControl(
              new FireAnimation(start, end)
                  .withSlot(anim.slot())
                  .withDirection(toCtre(anim.direction()))
                  .withSparking(anim.sparking())
                  .withCooling(anim.cooling())
                  .withFrameRate(anim.frameRateHz()));
    }
  }

  private static LarsonBounceValue toCtre(LedAnimation.Bounce bounce) {
    return switch (bounce) {
      case FRONT -> LarsonBounceValue.Front;
      case CENTER -> LarsonBounceValue.Center;
      case BACK -> LarsonBounceValue.Back;
    };
  }

  private static AnimationDirectionValue toCtre(LedAnimation.Direction direction) {
    return switch (direction) {
      case FORWARD -> AnimationDirectionValue.Forward;
      case BACKWARD -> AnimationDirectionValue.Backward;
    };
  }
}
