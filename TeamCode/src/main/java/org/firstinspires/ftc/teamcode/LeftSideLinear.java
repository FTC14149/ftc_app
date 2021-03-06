package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by rhill on 1/20/19.
 */

@Autonomous(name="LeftSideLinear", group="Autonomous")
//@Disabled
public class LeftSideLinear extends EncoderCameraLinearOpMode {

    //Runs our superclass which initalizes all of our motors/servos and sets modes for them.
    @Override
    public void runOpMode() {
        super.runOpMode();

        //Very complex code behind the scenes here, but it takes 10 images and then decides which mineral is the gold one, via drawing boxes on them and counting the yellow pixels in each box.

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

        //Left Path - if the gold is on the left side.
        if (originalReading == 1) {
            DriveStraight(6f, 0.6f);
            EncoderTurn(36.5f, 0.6f);
            DriveStraight(23f, 1.0f);
            DriveStraight(-16f, 0.8f);
            EncoderTurn(45f, 0.6f);
            DriveStraight(33f, 0.8f);
            DriveStraight(2f, 0.28f);
            EncoderTurn(62.75f, 0.6f);
            DriveStraight(47f, 0.9f);
            runtime.reset();
            XSlideStart(115f, 0.6f);
            gobbler.setPower(0.65);
            while((runtime.time() < 1.2) && opModeIsActive ());
            EncoderTurn(2.5f, 0.7f);
            DriveStraight(-81f, 0.9f);
            runtime.reset();
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 12) && opModeIsActive());
        }
        //Right Path - if the gold is on the right side.
        else if (originalReading == 2) {
            DriveStraight(6f, 0.6f);
            EncoderTurn(-36f, 0.6f);
            DriveStraight(22f, 0.9f);
            DriveStraight(-15f, 0.8f);
            EncoderTurn(120f, 0.6f);
            DriveStraight(46.25f, 0.8f);
            DriveStraight(1.5f, 0.28f);
            EncoderTurn(57f, 0.5f);
            DriveStraight(47f, 0.9f);
            elevator.setPower(0.32);
            runtime.reset();
            XSlideStart(115f, 0.6f);
            gobbler.setPower(0.65);
            while((runtime.time() < 1.2) && opModeIsActive());
            EncoderTurn(3.75f, 0.7f);
            DriveStraight(-77f, 0.9f);
            runtime.reset();
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 12) && opModeIsActive()) ;
        }
        //Middle Path - if the gold is in the middle.
        else if (originalReading == 3 || originalReading == 0) {
            DriveStraight(24f, 1.0f);
            DriveStraight(-13f, 0.8f);
            EncoderTurn(90f, 0.6f);
            DriveStraight(43f, 0.8f);
            DriveStraight(2f, 0.3f);
            EncoderTurn(49.5f, 0.6f);
            DriveStraight(45f, 0.8f);
            elevator.setPower(0.32);
            runtime.reset();
            XSlideStart(115f, 0.6f);
            gobbler.setPower(0.65);
            while((runtime.time() < 1.2) && opModeIsActive());
            EncoderTurn(3.5f, 0.7f);
            DriveStraight(-77f, 0.9f);
            runtime.reset();
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 12) && opModeIsActive()) ;
        }
        runtime.reset();
        while((runtime.time() < 8.8) && opModeIsActive()) {
            park_servo.setPosition(-1.0);
            elevator.setPower(0.05);
            hook.setPower(-1.0);
        }

    }
}
