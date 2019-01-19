package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.YuvImage;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
public class RightSideLinear extends LinearOpMode {
    private DcMotor left_tread;
    private DcMotor right_tread;
    public Bitmap image;
    private int width;
    private int height;
    private YuvImage yuvImage = null;
    private int looped = 0;
    private String data;

    static final float encoder_count_per_inch = 103.0f;

    static final float encoder_count_per_degree = 17.74f;

    private int red(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    private int green(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    private int blue(int pixel) {
        return pixel & 0xff;
    }

    public static void convertRGBtoHSV(float r, float g, float b, float[] hsv)
    {
        float h = 0;
        float s = 0;
        float v = 0;

        float max = (r > g) ? r : g;
        max = (max > b) ? max : b;

        float min = (r < g) ? r : g;
        min = (min < b) ? min : b;

        v = max;    // this is the value v

        // Calculate the saturation s
        if(max == 0)
        {
            s = 0;
            h = Float.NaN;  // h => UNDEFINED
        }
        else
        {
            // Chromatic case: Saturation is not 0, determine hue
            float delta = max - min;
            s = delta / max;

            if(r == max)
            {
                // resulting color is between yellow and magenta
                h = (g - b) / delta ;
            }
            else if(g == max)
            {
                // resulting color is between cyan and yellow
                h = 2 + (b - r) / delta;
            }
            else if(b == max)
            {
                // resulting color is between magenta and cyan
                h = 4 + (r - g) / delta;
            }

            // convert hue to degrees and make sure it is non-negative
            h *= 60;
            if(h < 0)
                h += 360;
        }

        // now assign everything....
        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;
    }

    private int getHue(int x, int y, int h, int w) {
        float hue = 0.0f;
        float hsv[] = new float[3];
        for(int i=x;i<x+w;i++) {
            for(int j=y;j<y+h;j++) {
                int pixel = image.getPixel(i,j);
                convertRGBtoHSV(red(pixel),green(pixel),blue(pixel),hsv);
                hue += hsv[0];
            }
        }
        return Math.round(hue/(h*w));
    }

    @Override
    public void runOpMode() {
        left_tread = hardwareMap.get(DcMotor.class, "left_tread");
        right_tread = hardwareMap.get(DcMotor.class, "right_tread");
        left_tread.setDirection(DcMotor.Direction.REVERSE);
        right_tread.setDirection(DcMotor.Direction.FORWARD);
        left_tread.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_tread.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right_tread.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_tread.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        //while(opModeIsActive()) {
            //convertImage();
            //int hue = getHue(0,0,20,20);
            //telemetry.addData("Hue", "Current: %d",
            //        hue);
            //telemetry.update();

        //}

        DriveStraight(12.0f,1.0f);
        TurnLeft(360.0f, 0.5f);
    }

    public void DriveStraight(float inches, float power) {
        int newLeftTarget = left_tread.getCurrentPosition() + Math.round(inches * encoder_count_per_inch);
        int newRightTarget = right_tread.getCurrentPosition() + Math.round(inches * encoder_count_per_inch);
        left_tread.setTargetPosition(newLeftTarget);
        right_tread.setTargetPosition(newRightTarget);
        left_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        left_tread.setPower(power);
        right_tread.setPower(power);
        while (opModeIsActive() && left_tread.isBusy() && right_tread.isBusy()) {
            telemetry.addData("Encoder", "Current: %d",
                    left_tread.getCurrentPosition());
            telemetry.update();
        }
        // Stop all motion;
        left_tread.setPower(0);
        // Turn off RUN_TO_POSITION
        left_tread.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    public void TurnLeft(float degrees, float power) {
        int newLeftTarget = left_tread.getCurrentPosition() - Math.round(degrees * encoder_count_per_degree);
        int newRightTarget = right_tread.getCurrentPosition() + Math.round(degrees * encoder_count_per_degree);
        left_tread.setTargetPosition(newLeftTarget);
        right_tread.setTargetPosition(newRightTarget);
        left_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        left_tread.setPower(power);
        right_tread.setPower(power);
        while (opModeIsActive() && left_tread.isBusy() && right_tread.isBusy()) {
            telemetry.addData("Encoder",  "Current: %d",
                    left_tread.getCurrentPosition());
            telemetry.update();
        }
        // Stop all motion;
        left_tread.setPower(0);
        // Turn off RUN_TO_POSITION
        left_tread.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}

