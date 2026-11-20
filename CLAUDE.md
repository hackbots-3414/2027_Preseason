# Hackbots 3414 — 2027 Preseason robot code

This repo is part of the team's AI Engineering experiment. Students and mentors each solve the same preseason challenges from their own prompts, each on their own branch. The point is to compare the results, so every branch has to stay independent.

## Project facts
- FRC team 3414, Java 17, WPILib command-based, GradleRIO 2026.2.1
- Vendor libraries: CTRE Phoenix 6 26.3.0 (see `vendordeps/`). Only use APIs that exist in these versions.
- `main` holds the shared starting point: a CTRE-generated swerve drivetrain (`generated/TunerConstants.java`, `subsystems/CommandSwerveDrivetrain.java`).
- Follow the Hackbots programming reference: https://therekrab.github.io/frc-programming-reference/
- The test chassis don't have every component. Anything a chassis lacks must work in the WPILib simulator and be visible on SmartDashboard / Elastic.

## Branch rules
- Work only on the branch you were started on. Never commit to `main`.
- Don't read, diff, check out, or copy code from other branches. Each solution has to come from its own prompt.
- Don't delete or rewrite the shared swerve code unless the prompt asks for it.
- This repo is public: no passwords, API keys, phone numbers, or full names.

## How I want the AI to work
<!-- Copied from hackbots-3414/AI-Engineering prompt-library/standard-ai-instructions.md. That file is the source of truth; update this copy when it changes. -->

Before writing any code:
1. Restate the requirements in your own words and list any assumptions you're making.
2. Ask me any clarifying questions. Wait for my answers if something important is unclear.
3. Propose a short plan: which files you'll create or change, and why.

When writing code:
- Only use APIs that exist in the library versions listed above. If you're unsure whether something exists, say so.
- Add comments that explain *why*, not just *what*.
- Build in testability. For each acceptance criterion, add what I'd need to check it: dashboard values (e.g. SmartDashboard / NetworkTables), logged data I can view in AdvantageScope, simulation support, or unit tests. Use clear, consistent names so I can find them.

When you're done:
- Tell me how to test each acceptance criterion, step by step.
- Say clearly which criteria you actually verified (e.g. it compiles, a unit test passes) and which ones I must test myself. Don't claim something works if you couldn't check it.
- Give me a walkthrough I can follow as a high school student: what each piece does, how it connects, and what I should test first.
