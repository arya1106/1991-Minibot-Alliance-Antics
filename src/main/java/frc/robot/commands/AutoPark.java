package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.sensors.RomiGyro;
import frc.robot.subsystems.Drivetrain;

public class AutoPark extends SequentialCommandGroup {
    public AutoPark(Drivetrain drivetrain, RomiGyro gyro){
        addCommands(
            new DriveDistance(-.8, 6, drivetrain),
            new TurnDegrees(0.8, 90, drivetrain),
            new DriveArc(0.8, 12, 8, drivetrain)
        );
    }
}
