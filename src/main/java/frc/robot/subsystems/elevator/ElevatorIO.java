package frc.robot.subsystems.elevator;

public interface ElevatorIO {
    void updateInputs(ElevatorIOInputs inputs);

    public class ElevatorIOInputs {
        public double position;
        public double setpoint;
    }

    void setTarget(double target);

}
