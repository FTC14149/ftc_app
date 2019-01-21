package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by rhill on 1/16/19.
 */

@Autonomous(name="RightSideLinear", group="Autonomous")
//@Disabled
public class RightSideLinear extends EncoderCameraLinearOpMode {

    @Override
    public void runOpMode() {
        super.runOpMode();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while(opModeIsActive()) {
            evaluateImage(); //Most deceptively simple code statement in the UNIVERSE.
            if((leftCount > middleCount) && (leftCount > rightCount)) {
                telemetry.addLine("The left mineral is gold!");
                if(isThisFirstReading == true) {
                    originalReading = 1;
                }
                isThisFirstReading = false;
            } else if((rightCount > middleCount) && (rightCount > leftCount)) {
                telemetry.addLine("The right mineral is gold!");
                if(isThisFirstReading == true) {
                    originalReading = 2;
                }
                isThisFirstReading = false;
            } else {
                telemetry.addLine("The middle mineral is gold!");
                if(isThisFirstReading == true) {
                    originalReading = 3;
                }
                isThisFirstReading = false;

            }
            telemetry.update();
        }
        //Left Path
        if (originalReading == 1) {

        }
        //Right Path
        else if (originalReading == 2) {

        }
        //Middle Path
        else if (originalReading == 3) {
            DriveStraight(48f, 0.5f);

        }


    }

}

