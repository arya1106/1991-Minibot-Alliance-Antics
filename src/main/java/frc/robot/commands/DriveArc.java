package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class DriveArc extends CommandBase {
    
    private final double m_speed;
    private final double m_leftDistance;
    private final double m_RightDistance;
    private final Drivetrain m_drivetrain;
    private final boolean m_isTurningRight;

    public DriveArc(double speed, double leftDistance, double rightDistance, Drivetrain drivetrain) {
        m_speed = speed;
        m_leftDistance = leftDistance;
        m_RightDistance = rightDistance;
        m_drivetrain = drivetrain;
        m_isTurningRight = m_leftDistance>m_RightDistance;
        addRequirements(drivetrain);
    }


    @Override
    public void initialize(){
        m_drivetrain.arcadeDrive(0, 0);
        m_drivetrain.resetEncoders();
    }

    @Override
    public void execute(){
        if(m_isTurningRight) m_drivetrain.tankDrive(m_speed, (m_RightDistance/m_leftDistance)*m_speed);
        else m_drivetrain.tankDrive( (m_leftDistance/m_RightDistance)*m_speed, m_speed);
    }

    @Override
    public void end(boolean interrupted){
        m_drivetrain.arcadeDrive(0, 0);
    }

    @Override
    public boolean isFinished(){
        return (Math.abs(m_drivetrain.getLeftDistanceInch()) >= m_leftDistance) && (Math.abs(m_drivetrain.getRightDistanceInch()) >= m_RightDistance);
    }
    
}
