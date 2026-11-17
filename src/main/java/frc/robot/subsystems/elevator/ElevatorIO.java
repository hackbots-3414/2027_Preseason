package frc.robot.subsystems.elevator;

public interface ElevatorIO {
    public void setTarget(double target);
    public void updateInfo();
    public boolean isReady();
}
