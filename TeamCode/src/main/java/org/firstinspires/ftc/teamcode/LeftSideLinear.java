package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by rhill on 1/20/19.
 */

@Autonomous(name="LeftSideLinear", group="Autonomous")
//@Disabled
public class LeftSideLinear extends EncoderCameraLinearOpMode {

    @Override
    public void runOpMode() {
        super.runOpMode();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        for(int i=0;i<10;i++) {
            boolean dummy = opModeIsActive();
            evaluateImage(); //Most deceptively simple code statement in the UNIVERSE.
            if((leftCount > middleCount) && (leftCount > rightCount)) {
                telemetry.addLine("The left mineral is gold!");
                originalReading = 1;
            } else if((rightCount > middleCount) && (rightCount > leftCount)) {
                telemetry.addLine("The right mineral is gold!");
                originalReading = 2;
            } else {
                telemetry.addLine("The middle mineral is gold!");
                originalReading = 3;
            }
            telemetry.update();
        }
        runtime.reset();
        while((runtime.time() < 8.6) && opModeIsActive()) {
            hook.setPower(1.0);
        }
        DriveStraight(3f, 0.5f);
        while((runtime.time() < 1) && opModeIsActive()) {

        }
        hook.setPower(0.0);

        //Left Path
        if (originalReading == 1) {
            DriveStraight(6f, 0.6f);
            EncoderTurn(36, 0.6f);
            DriveStraight(23f, 1.0f);
            DriveStraight(-16f, 0.8f);
            EncoderTurn(45f, 0.6f);
            DriveStraight(32f, 0.8f);
            DriveStraight(6f, 0.28f);
            EncoderTurn(63, 0.6f);
            DriveStraight(54f, 0.8f);
            elevator.setPower(0.32);
            runtime.reset();
            while((runtime.time() < 1.2) && opModeIsActive());
            EncoderTurn(2.5f, 0.7f);
            DriveStraight(-88f, 0.9f);
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 5) && opModeIsActive());
        }
        //Right Path
        else if (originalReading == 2) {
            DriveStraight(6f, 0.6f);
            EncoderTurn(-36f, 0.6f);
            DriveStraight(22f, 1.0f);
            DriveStraight(-15f, 0.8f);
            EncoderTurn(120f, 0.6f);
            DriveStraight(48f, 0.8f);
            DriveStraight(2f, 0.28f);
            EncoderTurn(57f, 0.5f);
            DriveStraight(54f, 0.8f);
            elevator.setPower(0.32);
            runtime.reset();
            while((runtime.time() < 1.2) && opModeIsActive());
            EncoderTurn(3.5f, 0.7f);
            DriveStraight(-88f, 0.9f);
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 5) && opModeIsActive()) ;
        }
        //Middle Path
        else if (originalReading == 3 || originalReading == 0) {
            DriveStraight(22f, 1.0f);
            DriveStraight(-11f, 0.8f);
            EncoderTurn(90f, 0.6f);
            DriveStraight(39f, 0.8f);
            DriveStraight(9f, 0.3f);
            EncoderTurn(48, 0.6f);
            DriveStraight(53f, 0.8f);
            elevator.setPower(0.32);
            runtime.reset();
            while((runtime.time() < 1.2) && opModeIsActive());
            EncoderTurn(2.5f, 0.7f);
            DriveStraight(-88f, 0.9f);
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 5) && opModeIsActive()) ;
        }
        runtime.reset();
        while((runtime.time() < 8.8) && opModeIsActive()) {
            elevator.setPower(0.05);
            hook.setPower(-1.0);
        }

    }
}
