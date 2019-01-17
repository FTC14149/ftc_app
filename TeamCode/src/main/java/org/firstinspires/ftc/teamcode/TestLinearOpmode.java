package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by rhill on 1/16/19.
 */


/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="TestLinearOpmode", group="LinearOpmode")
//@Disabled
public class TestLinearOpmode extends LinearOpMode {
    private DcMotor test_motor;
    @Override
    public void runOpMode() {
        test_motor  = hardwareMap.get(DcMotor.class, "test_motor");
        test_motor.setDirection(DcMotor.Direction.FORWARD);
        test_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        test_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        int newLeftTarget = test_motor.getCurrentPosition() + 100;
        test_motor.setTargetPosition(newLeftTarget);
        test_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        test_motor.setPower(0.5);
        while (opModeIsActive() && test_motor.isBusy()) {
            telemetry.addData("Encoder",  "Current: %d",
                    test_motor.getCurrentPosition());
            telemetry.update();
        }
        // Stop all motion;
        test_motor.setPower(0);
        // Turn off RUN_TO_POSITION
        test_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
}

