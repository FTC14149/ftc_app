package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import org.firstinspires.ftc.robotcontroller.internal.CameraBackEndStuff;
import org.firstinspires.ftc.robotcontroller.internal.CameraPreview;

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
    private Camera camera;
    public CameraPreview preview;
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

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            Camera.Parameters parameters = camera.getParameters();
            width = parameters.getPreviewSize().width;
            height = parameters.getPreviewSize().height;
            yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            looped += 1;
        }
    };

    private void convertImage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 0, out);
        byte[] imageBytes = out.toByteArray();
        image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
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

        //DriveStraight(12.0f,1.0f);
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
            telemetry.addData("Encoder",  "Current: %d",
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

