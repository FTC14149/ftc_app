package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by rhill on 1/16/19.
 */

@Autonomous(name="RightSideLinear", group="Autonomous")
//@Disabled
public class RightSideLinear extends EncoderCameraLinearOpMode {

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

        //Dehooks.

        runtime.reset();
        while((runtime.time() < 8.6) && opModeIsActive()) {
            hook.setPower(1.0);
        }
        DriveStraight(3f, 0.5f);
        while((runtime.time() < 1) && opModeIsActive()) {

        }
        hook.setPower(0.0);

        //Left Path - if the gold is on the left side.
        //Also, if you're wondering, a lot of these commands are from EncoderCameraLinearOpMode. Saves a lot of time with the methods, kind of like MyBlocks from FLL.
        if (originalReading == 1) {
            DriveStraight(6f, 0.5f);
            EncoderTurn(31, 0.5f);
            DriveStraight(40f, 0.5f);
            EncoderTurn(-78.75f, 0.5f);
            DriveStraight(24f, 0.5f);
            runtime.reset();
            XSlideStart(115f, 0.6f);
            gobbler.setPower(0.65);
            while ((runtime.time() < 1.5) && opModeIsActive()) ;
            DriveStraight(-85f, 1.0f);
            runtime.reset();
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 12) && opModeIsActive()) ;
        }
        //Right Path - if the gold is on the right side.
        else if (originalReading == 2) {
            DriveStraight(6f, 0.5f);
            EncoderTurn(-36f, 0.5f);
            DriveStraight(38f, 0.7f);
            EncoderTurn(76f, 0.5f);
            DriveStraight(28f, 0.7f);
            EncoderTurn(-103f, 0.5f);
            runtime.reset();
            XSlideStart(115f, 0.6f);
            gobbler.setPower(0.65);
            while ((runtime.time() < 1.5) && opModeIsActive()) ;
            DriveStraight(-85f, 1.0f);
            runtime.reset();
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 12) && opModeIsActive());
        }
        //Middle Path - if the gold is in the middle
        else if (originalReading == 3 || originalReading == 0) {
            DriveStraight(55f, 0.5f);
            runtime.reset();
            XSlideStart(115f, 0.6f);
            gobbler.setPower(0.65);
            while((runtime.time() < 1.5) && opModeIsActive());
            EncoderTurn(-46.5f, 0.5f);
            DriveStraight(-85f, 0.9f);
            runtime.reset();
            park_servo.setPosition(-1.0);
            while ((runtime.time() < 12) && opModeIsActive());
        }
        runtime.reset();
        while((runtime.time() < 8.8) && opModeIsActive()) {
            park_servo.setPosition(-1.0);
            elevator.setPower(0.05);
            hook.setPower(-1.0);
        }

    }

}

