package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class KillDrive extends CommandBase {

    private final Drivetrain m_drivetrain;
    
    public KillDrive(Drivetrain drivetrain){
        this.m_drivetrain = drivetrain;
    }

    @Override
    public void initialize() {
        m_drivetrain.arcadeDrive(0, 0);
    }

    @Override
    public void execute(){
        m_drivetrain.arcadeDrive(0, 0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
