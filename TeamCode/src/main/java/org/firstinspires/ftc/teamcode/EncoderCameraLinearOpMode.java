package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcontroller.internal.LinearOpModeCamera;
import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by rhill on 1/19/19.
 */

// Superclass for all linear opmodes that use camera and encoder motors

public class EncoderCameraLinearOpMode extends LinearOpModeCamera {
    private DcMotor left_tread;
    private DcMotor right_tread;
    static final float encoder_count_per_inch = 103.0f;
    static final float encoder_count_per_degree = 17.74f;

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

        if (isCameraAvailable()) {

            setCameraDownsampling(8);
            // parameter determines how downsampled you want your images
            // 8, 4, 2, or 1.
            // higher number is more downsampled, so less resolution but faster
            // 1 is original resolution, which is detailed but slow
            // must be called before super.init sets up the camera

            telemetry.addLine("Wait for camera to finish initializing!");
            telemetry.update();
            startCamera();  // can take a while.
            // best started before waitForStart
            telemetry.addLine("Camera ready!");
            telemetry.update();
        }

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

