package frc.robot.commands;

import frc.robot.subsystems.Drivetrain;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TurnGyro extends CommandBase {

    private final Drivetrain m_drive;
    private final double m_turnDegrees;
    private final double m_speed;
    private double m_initialDegrees;
    private double m_currentDegrees;

    public TurnGyro(Drivetrain drivetrain, double turnDegrees, double speed) {
        this.m_drive = drivetrain;
        // weird gyro behavior, so divide by 2
        this.m_turnDegrees = turnDegrees/2;
        this.m_speed = speed;
        this.m_initialDegrees = 0;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize(){
        m_drive.arcadeDrive(0, 0);
        m_initialDegrees = m_drive.getGyroAngleZ();
    }

    @Override
    public void execute(){
        m_drive.arcadeDrive(0, m_speed);
        m_currentDegrees = m_drive.getGyroAngleZ();
    }
    @Override
    public void end(boolean interrupted){
        m_drive.arcadeDrive(0, 0);
    }

    @Override
    public boolean isFinished(){
        // check if difference between intial and current degrees is greater than or equal to 
        // the amount of degrees we need to turn
        return Math.abs( (m_initialDegrees - m_currentDegrees) ) >= m_turnDegrees;
    }
    
}
