# LedCandleSim: CANdle LED subsystem, testable in simulation

A WPILib command-based Java project that models a 36-LED strip driven by a CTRE CANdle. It is built so you can **see the LEDs in Elastic while running in simulation**, or on a test chassis that has no LEDs.

| | |
|---|---|
| WPILib | 2026.2.1 (GradleRIO) |
| CTRE | Phoenix 6 `26.3.0`, the Phoenix 6 CANdle API (Phoenix 5's CANdle API is deprecated) |
| Language | Java 17 |

## LED map

One continuous strip, wired in this order:

```
             TOP  12 ──────────────► 23
            ┌──────────────────────────┐
         11 │                          │ 24
          ▲ │                          │ │
    LEFT  │ │         (robot)          │ ▼  RIGHT
          │ │                          │
          0 │                          │ 35
```

- **Left side:** strip LEDs 0–11, bottom → top
- **Top:** strip LEDs 12–23, left → right
- **Right side:** strip LEDs 24–35, top → bottom

The code uses strip indices 0–35 everywhere. The CANdle numbers its own 8 onboard LEDs 0–7, so the code adds 8 only when it talks to the hardware. The CANdle sees the strip as indices 8–43.

## Patterns

| Pattern | When | What |
|---|---|---|
| **Purple Trace** | Default command | A 4-LED purple pocket starts at the bottom-left, climbs, runs across the top, goes down the right side, and bounces back. It moves 12 LEDs/s (about 3 s per pass). |
| **KITT + Flames** | Dashboard button | A red 3-LED Knight Rider/KITT scanner bounces across the top (about 0.4 s per sweep). Flames burn **upward** on both sides. |

Press **KITT + Flames** in Elastic to start it. Press it again to cancel it, and the purple trace comes back automatically. Both commands use `ignoringDisable(true)`, so **you don't have to enable the robot**.

## Running it in simulation

1. Open the folder in **WPILib VS Code (2026)**.
2. Start the simulator: **Ctrl+Shift+P → "WPILib: Simulate Robot Code"**, then pick **Sim GUI**. From a terminal you can run `./gradlew simulateJava` instead.
3. Start **Elastic**: **Ctrl+Shift+P → "WPILib: Start Tool" → Elastic**.
4. In Elastic, open **Settings → IP Address Mode** and choose **localhost (127.0.0.1)**.
5. Load the dashboard. Either:
   - **File → Download From Robot** and pick **elastic-layout**. The robot code serves `src/main/deploy` on port 5800, and this works on the real robot too. Or:
   - **File → Open Layout** and pick `src/main/deploy/elastic-layout.json`.

   Loading a layout **replaces** your current Elastic layout, so save yours first if you need it.

### What's on the "LEDs" tab

- **LED View** (Camera Stream): the 36 LEDs drawn in the robot's real shape. It's the easiest way to check that the trace runs up the left and that the flames rise on both sides.
- **KITT + Flames** (Command): the button.
- **Active Pattern**: the name of the pattern that's showing.
- **Scheduler**: the running commands. You can also cancel commands here.
- **Left / Top / Right / Full Strip** (Multi Color View): each segment as a color bar. These work even if you turn the camera image off. Both side bars read bottom → top, left → right.

If you add widgets by hand, the topics are under `/SmartDashboard/LEDs/` and the picture is at `/CameraPublisher/LED View`.

## How it works

```
LedPattern ──(animations)──► LedSubsystem ──► CANdle  (real hardware runs the animation itself)
                                   │
                                   └──► LedSimRenderer ──► LedDashboard ──► NetworkTables ──► Elastic
```

- **`LedLayout`**: which LED is where.
- **`LedAnimation`**: one CANdle animation slot: Larson or Fire, which LEDs, color, speed and direction.
- **`LedPattern`**: the named patterns. Each one is a list of animations that run together in separate slots.
- **`LedSubsystem`**: sends a pattern to the CANdle **once**, when the pattern changes. After that the CANdle animates by itself, so there's no CAN traffic while it plays. It provides `purpleTrace()`, `kittAndFlames()` and `showPattern(...)` commands.
- **`LedSimRenderer`**: a software model of what the CANdle is showing. The CANdle doesn't report its pixel colors back, and Phoenix 6 sim has no LED output, so this model computes them from the same animation settings sent to the hardware. It always runs, in sim and on the real robot.
- **`LedDashboard`**: publishes the model's colors to NetworkTables and draws the "LED View" picture.

To add a pattern, add an entry to `LedPattern` and a command for it, then bind it in `RobotContainer`.

## Things to know

- **The sim is a close model, not a pixel-exact copy** of CTRE's firmware. Positions, speed, bounce, direction and colors follow CTRE's documented parameters. The exact flicker of the fire, and any fading the firmware adds to the Larson pocket, will look a little different on a real strip.
- **The flame colors are CTRE's built-in fire palette.** `FireAnimation` has no color setting. It goes from black to red to orange to yellow, and is hottest (yellow/white) at the base. If you want a *pure* yellow flame, the code would need to render frames itself and send pixel colors. That's possible, but it costs CAN bandwidth.
- **Hardware settings** are in `Constants.LedConstants` and `LedSubsystem`: CAN ID **1** on the **rio** bus, strip type **GRB**, brightness **0.5**. Change them to match the robot (check the strip type in Tuner X if the colors look wrong). The dashboard shows colors at full brightness.
- **Set your team number** in `.wpilib/wpilib_preferences.json` (it's `0` now) before deploying.
- **On a roboRIO**, if CPU is tight, set `kPublishLedImage = false` to turn off the camera picture. The color bars keep working.

## Tests

```
./gradlew test
```

`LedSimRendererTest` checks the layout (12/12/12), that the purple trace starts bottom-left, reaches bottom-right and bounces back, that it moves at about 12 LEDs/s, that KITT stays on the top and sweeps it end to end, that both flames burn upward, and that switching patterns clears the old LEDs. These tests are plain Java, so they don't need the simulator.
