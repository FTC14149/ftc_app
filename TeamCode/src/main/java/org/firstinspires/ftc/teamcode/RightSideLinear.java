package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.YuvImage;

import org.firstinspires.ftc.teamcode.EncoderCameraLinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import android.util.Log;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import java.io.ByteArrayOutputStream;

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

@TeleOp(name="RightSideLinear", group="Autonomous")
//@Disabled
public class RightSideLinear extends EncoderCameraLinearOpMode {
    private int width;
    private int height;
    private int looped = 0;
    private String data;

    int ds2 = 2;  // additional downsampling of the image

    @Override
    public void runOpMode() {
        super.runOpMode();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while(opModeIsActive()) {
            //convertImage();
            if (imageReady()) { // only do this if an image has been returned from the camera
                int redValue = 0;
                int blueValue = 0;
                int greenValue = 0;

                // get image, rotated so (0,0) is in the bottom left of the preview window
                Bitmap rgbImage;
                rgbImage = convertYuvImageToRgb(yuvImage, width, height, ds2);
                int hue = pixelsOfHue(rgbImage, 0,0,20,20,25,75);
                telemetry.addData("Hue", "Current: %d",
                        hue);
                telemetry.update();
            }
        }


        //DriveStraight(12.0f,1.0f);
        //TurnLeft(360.0f, 0.5f);
    }

}

