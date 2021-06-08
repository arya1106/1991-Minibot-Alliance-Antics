package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.sensors.RomiGyro;
import frc.robot.subsystems.Drivetrain;

public class CaptureBall extends SequentialCommandGroup {
    
    public CaptureBall(Drivetrain drivetrain, RomiGyro gyro, boolean shouldTurnLeft, int ballCount){
        if(ballCount == 1){
            addCommands(
                new DriveDistance(-1, 6, drivetrain),
                new TurnDegrees(0.5, 163, drivetrain),
                new DriveDistance(-0.8, 5, drivetrain)
            );
        }
        else if(ballCount == 2){
            addCommands(
                new DriveDistance(-1, 6, drivetrain),
                new TurnDegrees(-0.5, 166, drivetrain),
                new DriveDistance(-0.6, 4, drivetrain),
                new DriveArc(-0.6, 17, 13, drivetrain)
            );
        }
        else if(ballCount == 3){
            addCommands(
                new DriveDistance(-1, 4, drivetrain),
                new TurnDegrees(0.5, 180, drivetrain),
                new DriveArc(-0.8, 17, 8, drivetrain),
                new DriveDistance(-0.8, 3.25, drivetrain),
                new KillDrive(drivetrain)
            );
        }
    }

    @Override
    public void end(boolean interrupted) {
    }
}
