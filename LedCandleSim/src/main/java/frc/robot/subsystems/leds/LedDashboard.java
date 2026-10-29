package frc.robot.subsystems.leds;

import static frc.robot.subsystems.leds.LedLayout.*;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.OpenCvLoader;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.networktables.StringPublisher;
import frc.robot.Constants.LedConstants;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Publishes the modeled LED colors so Elastic can show them.
 *
 * <p>Under {@code /SmartDashboard/LEDs/}:
 *
 * <ul>
 *   <li>{@code Active Pattern} (string) - name of the pattern being shown
 *   <li>{@code Left (bottom to top)}, {@code Top (left to right)}, {@code Right (bottom to top)},
 *       {@code Full Strip} (string[] of "#RRGGBB") - use Elastic's <b>Multi Color View</b>
 * </ul>
 *
 * <p>Plus, if {@link LedConstants#kPublishLedImage} is on, a camera stream named {@code LED View}:
 * a picture of the LEDs laid out in the robot's actual shape. Use Elastic's <b>Camera Stream</b>
 * widget on {@code /CameraPublisher/LED View}.
 */
final class LedDashboard {
  // Picture geometry: a 14 x 13 grid of cells. Left side in column 0, top in row 0, right side in
  // column 13, robot body in between.
  private static final int kCell = 24;
  private static final int kLed = 20;
  private static final int kMargin = 20;
  private static final int kCols = 14;
  private static final int kRows = 13;
  private static final int kWidth = 2 * kMargin + kCols * kCell; // 376
  private static final int kHeight = kMargin + kRows * kCell + 40; // 372 (room for a caption)

  private static final Scalar kBackground = new Scalar(20, 20, 20); // OpenCV is BGR
  private static final Scalar kBody = new Scalar(55, 50, 50);
  private static final Scalar kUnlit = new Scalar(45, 45, 45);
  private static final Scalar kBodyText = new Scalar(90, 85, 85);
  private static final Scalar kText = new Scalar(235, 235, 235);

  private final LedSimRenderer m_model;

  private final StringPublisher m_patternPub;
  private final StringArrayPublisher m_leftPub;
  private final StringArrayPublisher m_topPub;
  private final StringArrayPublisher m_rightPub;
  private final StringArrayPublisher m_stripPub;

  private final CvSource m_imageSource;
  private final Mat m_image;
  private int m_loopCount = 0;

  LedDashboard(LedSimRenderer model) {
    m_model = model;

    NetworkTable table =
        NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable("LEDs");
    m_patternPub = table.getStringTopic("Active Pattern").publish();
    m_leftPub = table.getStringArrayTopic("Left (bottom to top)").publish();
    m_topPub = table.getStringArrayTopic("Top (left to right)").publish();
    m_rightPub = table.getStringArrayTopic("Right (bottom to top)").publish();
    m_stripPub = table.getStringArrayTopic("Full Strip").publish();

    if (LedConstants.kPublishLedImage) {
      m_imageSource = CameraServer.putVideo("LED View", kWidth, kHeight);
      OpenCvLoader.forceStaticLoad(); // make sure OpenCV's native library is loaded before new Mat
      m_image = new Mat(kHeight, kWidth, CvType.CV_8UC3);
    } else {
      m_imageSource = null;
      m_image = null;
    }
  }

  void update(LedPattern activePattern) {
    String patternName = activePattern == null ? "(none)" : activePattern.displayName();
    m_patternPub.set(patternName);

    String[] left = new String[LEDS_PER_SIDE];
    String[] top = new String[LEDS_ACROSS_TOP];
    String[] right = new String[LEDS_PER_SIDE];
    String[] strip = new String[TOTAL_LEDS];
    for (int i = 0; i < TOTAL_LEDS; i++) {
      strip[i] = m_model.hex(i);
    }
    for (int i = 0; i < LEDS_PER_SIDE; i++) {
      left[i] = m_model.hex(LEFT_BOTTOM + i);
      right[i] = m_model.hex(RIGHT_BOTTOM - i); // flip so both sides read bottom -> top
    }
    for (int i = 0; i < LEDS_ACROSS_TOP; i++) {
      top[i] = m_model.hex(TOP_LEFT + i);
    }
    m_leftPub.set(left);
    m_topPub.set(top);
    m_rightPub.set(right);
    m_stripPub.set(strip);

    // The picture only needs ~25 fps; draw it every other 20 ms loop.
    if (m_imageSource != null && (m_loopCount++ % 2 == 0)) {
      drawImage(patternName);
      m_imageSource.putFrame(m_image);
    }
  }

  private void drawImage(String caption) {
    m_image.setTo(kBackground);

    // Robot body, inside the U of LEDs.
    Imgproc.rectangle(
        m_image,
        new Point(kMargin + kCell + 6, kMargin + kCell + 6),
        new Point(kMargin + (kCols - 1) * kCell - 6, kMargin + kRows * kCell - 2),
        kBody,
        -1);
    Imgproc.putText(
        m_image,
        "ROBOT",
        new Point(kWidth / 2.0 - 38, kMargin + kRows * kCell / 2.0 + 12),
        Imgproc.FONT_HERSHEY_SIMPLEX,
        0.8,
        kBodyText,
        2,
        Imgproc.LINE_AA);

    for (int i = 0; i < TOTAL_LEDS; i++) {
      int[] cell = gridCell(i);
      double x = kMargin + cell[0] * kCell + (kCell - kLed) / 2.0;
      double y = kMargin + cell[1] * kCell + (kCell - kLed) / 2.0;
      int rgb = m_model.rgb(i);
      Scalar color =
          rgb == 0
              ? kUnlit
              : new Scalar(rgb & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 16) & 0xFF); // BGR
      Imgproc.rectangle(m_image, new Point(x, y), new Point(x + kLed, y + kLed), color, -1);
    }

    Imgproc.putText(
        m_image,
        caption,
        new Point(kMargin, kHeight - 14),
        Imgproc.FONT_HERSHEY_SIMPLEX,
        0.6,
        kText,
        1,
        Imgproc.LINE_AA);
  }

  /** Where strip LED i sits in the picture, as {column, row}. Row 0 is the top of the robot. */
  static int[] gridCell(int i) {
    if (i <= LEFT_TOP) {
      return new int[] {0, LEDS_PER_SIDE - (i - LEFT_BOTTOM)}; // 0 -> bottom row, 11 -> row 1
    } else if (i <= TOP_RIGHT) {
      return new int[] {1 + (i - TOP_LEFT), 0};
    } else {
      return new int[] {kCols - 1, 1 + (i - RIGHT_TOP)}; // 24 -> row 1, 35 -> bottom row
    }
  }
}
