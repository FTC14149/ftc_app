/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREVColorDistance;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="TankDrive", group="Iterative Opmode")
//@Disabled
public class TankDrive extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor left_tread;
    private DcMotor right_tread;
    private DcMotor hook;
    private DcMotor xslide;
    private DcMotor gobbler;
    private DcMotor extender;
    private ColorSensor color_sensor;
    private double gear = 0.4;
    private double turnmod = 1.75;
    private DigitalChannel hook_stop;
    private Servo park_servo;


    float hsvValues[] = {0F, 0F, 0F};
    float values[] = hsvValues;
    final double ScaleFactor = 255;
    boolean xSlideDown = false;
    boolean xSlideUp = false;
    int downTarget;
    int upTarget;

    static final float encoder_count_per_inch = 103.0f;
    static final float encoder_count_per_degree = 17.74f;

    boolean xsliderunning = false;
    float xSlidePositionToHold = 0.0f;

    boolean extenderrunning = false;
    float extenderPositionToHold = 0.0f;

    int keyCountdown;

    @Override
    public void init() {
        //Initialization. Some legacy color sensor code is left over.
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        left_tread  = hardwareMap.get(DcMotor.class, "left_tread");
        right_tread = hardwareMap.get(DcMotor.class, "right_tread");

        hook = hardwareMap.get(DcMotor.class, "hook");
        xslide = hardwareMap.get(DcMotor.class, "xslide");
        gobbler = hardwareMap.get(DcMotor.class, "gobbler");
        extender = hardwareMap.get(DcMotor.class, "extender");
        hook_stop = hardwareMap.get(DigitalChannel.class, "hook_stop");
        hook_stop.setMode(DigitalChannel.Mode.INPUT);
        color_sensor = hardwareMap.get(ColorSensor.class, "color_sensor");
        park_servo = hardwareMap.get(Servo.class, "park_servo");

        // Set xslide/extender motor position to zero
        xslide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        xslide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        extender.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //test_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        left_tread.setDirection(DcMotor.Direction.FORWARD);
        right_tread.setDirection(DcMotor.Direction.REVERSE);
        hook.setDirection(DcMotor.Direction.FORWARD);


        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        keyCountdown = 0;

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;
        double xSlidePower;
        double ExtenderPower;
        boolean hookstop_state = hook_stop.getState();
        telemetry.addData("hook_stop", Boolean.toString(hookstop_state));

        Color.RGBToHSV((int) (color_sensor.red() * ScaleFactor),
                (int) (color_sensor.green() * ScaleFactor),
                (int) (color_sensor.blue() * ScaleFactor),
                hsvValues);

        telemetry.addData("Alpha", color_sensor.alpha());
        telemetry.addData("Red  ", color_sensor.red());
        telemetry.addData("Green", color_sensor.green());
        telemetry.addData("Blue ", color_sensor.blue());
        telemetry.addData("Hue", hsvValues[0]);




        //Power calculation:

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double drive =  gamepad2.left_stick_y * gear;
        double turn  =  -gamepad2.right_stick_x * gear * turnmod;
        leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;


        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        // Send calculated power to wheels
        left_tread.setPower(leftPower);
        right_tread.setPower(rightPower);

        //Manual Mode for X-Slide Mechanism (Legacy):

        if(gamepad1.left_stick_y < -0.1 || gamepad1.left_stick_y > 0.1) {
            xslide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            double xSlideDrive =  gamepad1.left_stick_y * 0.45;
            xSlidePower = Range.clip(xSlideDrive, -1.0, 1.0);
            xslide.setPower(xSlidePower);
        }
        else {
            xslide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        if(gamepad1.right_stick_x < -0.1 || gamepad1.right_stick_x > 0.1) {
            extender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            double ExtenderDrive = gamepad1.right_stick_x;
            ExtenderPower = Range.clip(ExtenderDrive, -1.0, 1.0);
            extender.setPower(ExtenderPower);
        }
        else {
            extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }


        //Hook code:

        if (gamepad1.y){
            hook.setPower(1.0);
        }
        else {
            if(gamepad1.x){
                hook.setPower(-1.0);
            } else {
                hook.setPower(0);
            }
        }

        //Gobbler code:

        if (gamepad1.a) {
            gobbler.setPower(1.0);
        }
        else if (gamepad1.b){
            gobbler.setPower(-1.0);
        }
        else {
            gobbler.setPower(0.0);
        }

        if(gamepad2.dpad_up) {
            park_servo.setPosition(1.0);
        }
        if (gamepad2.dpad_down) {
            park_servo.setPosition(0.0);
        }

        //X-slide code:

        // make x-slide mechanism go up and down
        if (gamepad1.dpad_down){
            xSlidePositionToHold += 10;
            xsliderunning = true;
        }
        if (gamepad1.dpad_up) {
            xSlidePositionToHold -= 10;
            xsliderunning = true;
        }

        if(xsliderunning) {
            XSlideStart(xSlidePositionToHold, 0.6f);
        }

        telemetry.addData("XSlideEncoder", "Current: %d",
                xslide.getCurrentPosition());
        telemetry.addData( "xslideisrunning", "value: %b", xsliderunning);

        //Extender code:

        if (gamepad1.dpad_left){
            extenderPositionToHold -= 10;
            extenderrunning = true;
        }
        if (gamepad1.dpad_right) {
            extenderPositionToHold += 10;
            extenderrunning = true;
        }
        if(extenderrunning) {
            ExtenderStart(extenderPositionToHold, 0.7f);
        }

        telemetry.addData("ExtenderEncoder", "Current: %d",
                xslide.getCurrentPosition());
        telemetry.addData( "Extender is Running", "value: %b", extenderrunning);

        //Gear system...can set different powers for the motor. Slow for precise movements, medium for normal movement, fast for obstacles/blitzing past people (and stealing their minerals).

        //Normal Mode: Fast
        if (gamepad2.y) {
            gear = 0.4;
            turnmod = 1.45;
        }
        //Precise Mode: Slow
        if (gamepad2.x) {
            gear = 0.2;
            turnmod = 2.5;
        }
        //TURBO Mode: Faster
        if (gamepad2.b) {
            gear = 1;
            turnmod = 1.75;
        }


        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    //Complex here...but, sets a target (which is created by multiplying degrees/inches by a constant which turns them into the encoder pulses needed to go that far), sets mode, and runs to it, then turns off.

    public void DriveStraight(float inches, float power) {
        int newLeftTarget = left_tread.getCurrentPosition() + Math.round(inches * encoder_count_per_inch);
        int newRightTarget = right_tread.getCurrentPosition() + Math.round(inches * encoder_count_per_inch);
        left_tread.setTargetPosition(newLeftTarget);
        right_tread.setTargetPosition(newRightTarget);
        left_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        left_tread.setPower(power);
        right_tread.setPower(power);
        while (left_tread.isBusy() && right_tread.isBusy()) {
            telemetry.addData("Encoder", "Current: %d",
                    left_tread.getCurrentPosition());
            telemetry.update();
        }
        // Stop all motion;
        left_tread.setPower(0);
        // Turn off RUN_TO_POSITION
        left_tread.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void EncoderTurn(float degrees, float power) {
        int newLeftTarget = left_tread.getCurrentPosition() - Math.round(degrees * encoder_count_per_degree);
        int newRightTarget = right_tread.getCurrentPosition() + Math.round(degrees * encoder_count_per_degree);
        left_tread.setTargetPosition(newLeftTarget);
        right_tread.setTargetPosition(newRightTarget);
        left_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_tread.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        left_tread.setPower(power);
        right_tread.setPower(power);
        while (left_tread.isBusy() && right_tread.isBusy()) {
            telemetry.addData("Encoder",  "Current: %d",
                    left_tread.getCurrentPosition());
            telemetry.update();
        }
        // Stop all motion;
        left_tread.setPower(0);
        // Turn off RUN_TO_POSITION
        left_tread.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void XSlideStart(float degrees, float power) {
        xslide.setTargetPosition( Math.round(degrees * encoder_count_per_degree));
        xslide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        xslide.setPower(power);
    }
    public void XSlideFinish() {
        //xslide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void ExtenderStart(float inches, float power) {
        extender.setTargetPosition( Math.round(inches * encoder_count_per_inch * 2.5f));
        extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        extender.setPower(power);
    }
    public void ExtenderFinish() {
        //extender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void XSlideRotate(float degrees, float power) {
        int finalTarget = 0;
        if (xSlideDown == true) {
           finalTarget = upTarget;
        }
        else if (xSlideUp == true) {
            finalTarget = downTarget;
        }
        else {
            finalTarget =  xslide.getCurrentPosition() + Math.round(degrees * encoder_count_per_degree);
        }
        xslide.setTargetPosition(finalTarget);
        xslide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        xslide.setPower(power);
        while (xslide.isBusy()) {
            telemetry.addData("XSlideEncoder", "Current: %d",
                    xslide.getCurrentPosition());
            telemetry.update();
        }
        // Stop all motion;
        xslide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        xslide.setPower(0);
        // Turn off RUN_TO_POSITION
        xslide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (degrees > 0) {
            boolean xSlideDown = true;
        }
        else if (degrees < 0) {
            boolean xSlideUp = true;
        }
    }
}
