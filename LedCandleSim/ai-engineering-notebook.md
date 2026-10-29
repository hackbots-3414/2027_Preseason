# AI Engineering Notebook

This is the place to document vibe coding prompts, output or robot behavior we like and do not like.  It's not science unless we write it down!



## Initial Prompt

Please create a WPILib robot project that models an LED subsystem using a CTRE CANdle LED controller.  We must test via Simulation, so indicating LED output on the Elastic SmartDashboard is needed.



Let's model an LED array of 36 elements, with 12 on the robot's left side, 12 across the top, and 12 down the right.



Default behavior is a purple trace starting at the bottom of the left and slowly flowing across the top, down the right, and bouncing back.



Let's use a button press (Command) on Elastic to change the LEDs to show a red trace bouncing back and forth on the top, Night Rider KITT style, and a yellow flame pattern burning upwards on both the left and right sides.  When this command is disabled, default behavior resumes.



### Output

Complete WPILib Robot project with LED subsystem, unit test, and ready for deployment to a test chassis with CTRE CANdle.



Elastic dashboard was downloaded from the robot, per Claude instructions.

