package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREVColorDistance;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/**
 * Created by rhill on 12/7/18.
 */

@Autonomous(name="RightSideAuto", group="Autonomous")
//@Disabled
public class RightSideAuto extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor left_tread;
    private DcMotor right_tread;
    private DcMotor hook;
    private DcMotor chain;
    private CRServo elevator;
    private DigitalChannel hook_stop;
    private ColorSensor color_sensor;
    private DistanceSensor distance_sensor;
    private double gear = 1;

    float hsvValues[] = {0F, 0F, 0F};
    final double ScaleFactor = 255;

    public enum state{
        //Universal (Beginning):
        LOWER,
        FWD1,
        SAMPLE,
        //Base Path (Gold Mineral in the middle):
        FWD2,
        CLAIM,
        BACK,
        TURN,
        FWD3,
        BACK2,
        TURN2,
        BACK3,
        //Alternate Path 1 (Gold Mineral to the right (looking from the view of the rover/robot)):
        ALT1BACK1,
        ALT1TURN1,
        ALT1FWD2,
        ALT1SAMPLE,
        ALT1TURN2,
        ALT1FWD3,
        ALT1FWD4,
        ALT1BACK2,
        ALT1CLAIM,
        //Alternate Path 2 (Gold Mineral to the left (looking from the view of the rover/robot)):
        ALT2BACK2,
        ALT2TURN2,
        ALT2FWD3,
        //Universal (END):
        END
    }
    state MyState;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        left_tread  = hardwareMap.get(DcMotor.class, "left_tread");
        right_tread = hardwareMap.get(DcMotor.class, "right_tread");
        elevator = hardwareMap.get(CRServo.class, "elevator");
        hook = hardwareMap.get(DcMotor.class, "hook");
        chain = hardwareMap.get(DcMotor.class, "chain");
        hook_stop = hardwareMap.get(DigitalChannel.class, "hook_stop");
        hook_stop.setMode(DigitalChannel.Mode.INPUT);
        color_sensor = hardwareMap.get(ColorSensor.class, "color_sensor");
        distance_sensor = hardwareMap.get(DistanceSensor.class, "color_sensor");


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        left_tread.setDirection(DcMotor.Direction.FORWARD);
        right_tread.setDirection(DcMotor.Direction.REVERSE);
        hook.setDirection(DcMotor.Direction.FORWARD);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
        telemetry.update();

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
        MyState=state.LOWER;
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        boolean hookstop_state = hook_stop.getState();
        telemetry.addData("hook_stop", Boolean.toString(hookstop_state));

        Color.RGBToHSV((int) (color_sensor.red() * ScaleFactor),
                (int) (color_sensor.green() * ScaleFactor),
                (int) (color_sensor.blue() * ScaleFactor),
                hsvValues);

        // send the info back to driver station using telemetry function.
        telemetry.addData("Alpha", color_sensor.alpha());
        telemetry.addData("Red  ", color_sensor.red());
        telemetry.addData("Green", color_sensor.green());
        telemetry.addData("Blue ", color_sensor.blue());
        telemetry.addData("Hue", hsvValues[0]);
        telemetry.addData("Distance (cm)", String.format(Locale.US, "%.02f", distance_sensor.getDistance(DistanceUnit.CM)));


        switch (MyState) {
            case LOWER:
                if (runtime.time() < 8.6) {
                    hook.setPower(1.0);
                    telemetry.addData("StateCount", runtime.time());
                } else {
                    MyState = state.FWD1;
                    hook.setPower(0.0);
                    runtime.reset();
                }
                break;

            case FWD1:
                left_tread.setPower(0.15);
                right_tread.setPower(0.15);
                if (distance_sensor.getDistance(DistanceUnit.CM) <= 19.5) {
                    MyState = state.SAMPLE;
                    runtime.reset();
                    stopMoving();
                }
                break;

            case SAMPLE:
                if (hsvValues[0] <= 95) {
                    telemetry.addLine("Detected!");
                    runtime.reset();
                    MyState = state.FWD2;
                } else {
                    MyState = state.ALT1BACK1;
                    runtime.reset();
                    telemetry.addLine("Not Detected!");

                }
                break;

            case FWD2:
                if (runtime.time() < 1.15) {
                    left_tread.setPower(0.32);
                    right_tread.setPower(0.232);
                }else{
                        MyState = state.FWD3;
                        runtime.reset();
                        stopMoving();
                    }
                break;

            case FWD3:
                if (runtime.time() < 5.6) {
                    left_tread.setPower(0.185);
                    right_tread.setPower(0.185);
                }else{
                    MyState = state.CLAIM;
                    runtime.reset();
                    stopMoving();
                }
                break;

            case CLAIM:
                if (runtime.time() < 1.5) {
                    elevator.setPower(0.32);
                }else{
                    MyState=state.BACK;
                    runtime.reset();
                }
                break;

            case BACK:
                if (runtime.time() < 0.01) {
                    right_tread.setPower(-0.3);
                    left_tread.setPower(-0.3);
                }else{
                    MyState=state.TURN;
                    stopMoving();
                    runtime.reset();
                }
                break;

            case TURN:
                if (runtime.time() < 0.37) {
                    right_tread.setPower(0.8);
                    left_tread.setPower(-0.8);
                    //will need to be reversed later on: crater on other side....
                }else{
                    MyState=state.BACK2;
                    stopMoving();
                    runtime.reset();
                }
                break;

            case BACK2:
                if(runtime.time() < 2.3) {
                    right_tread.setPower(-0.13);
                    left_tread.setPower(-0.13);
                }else{
                    MyState=state.TURN2;
                    stopMoving();
                    runtime.reset();
                }
                break;

            case TURN2:
                if(runtime.time() < 0.19) {
                    right_tread.setPower(-0.8);
                    left_tread.setPower(0.8);
                }else{
                    MyState=state.BACK3;
                    stopMoving();
                    runtime.reset();
                }
                break;

            case BACK3:
                if(runtime.time() < 1.62) {
                    right_tread.setPower(-0.7);
                    left_tread.setPower(-0.7);
                }else{
                    MyState=state.END;
                    stopMoving();
                    runtime.reset();
                }
                break;

            case ALT1BACK1:
                if (runtime.time() < 0.28) {
                    right_tread.setPower(-0.4);
                    left_tread.setPower(-0.4);
                } else {
                    MyState = state.ALT1TURN1;
                    runtime.reset();
                    stopMoving();
                }
                break;

            case ALT1TURN1:
                if (runtime.time() < 0.608) {
                    left_tread.setPower(0.4);
                    right_tread.setPower(-0.4);
                } else {
                    MyState = state.ALT1FWD2;
                    runtime.reset();
                    stopMoving();
                }
                break;

            case ALT1FWD2:
                left_tread.setPower(0.16);
                right_tread.setPower(0.16);
                if (distance_sensor.getDistance(DistanceUnit.CM) <= 20) {
                    MyState = state.ALT1SAMPLE;
                    runtime.reset();
                    stopMoving();
                }
                break;

            case ALT1SAMPLE:
                if(hsvValues[0] <= 95) {
                    MyState=state.ALT1FWD3;
                    runtime.reset();
                    stopMoving();
                }else{
                    MyState=state.ALT2BACK2;
                    runtime.reset();
                    stopMoving();
                }

                break;

            case ALT1FWD3:
                if (runtime.time() < 1.85) {
                    left_tread.setPower(0.2);
                    right_tread.setPower(0.2);
                } else {
                    MyState = state.ALT1TURN2;
                    runtime.reset();
                    stopMoving();
                }

                break;

            case ALT1TURN2:
                if (runtime.time() < 0.59) {
                    left_tread.setPower(-0.6);
                    right_tread.setPower(0.6);
                } else {
                    MyState = state.ALT1FWD4;
                    runtime.reset();
                    stopMoving();
                }

                break;

            case ALT1FWD4:
                if (runtime.time() < 0.8) {
                    left_tread.setPower(0.4);
                    right_tread.setPower(0.4);
                }else{
                    MyState=state.ALT1CLAIM;
                    runtime.reset();
                    stopMoving();
                }

                break;

            case ALT1CLAIM:
                if (runtime.time() < 1.5) {
                    elevator.setPower(0.32);
                }else{
                    MyState=state.ALT1BACK2;
                    runtime.reset();
                }
                break;


            case ALT1BACK2:
                if (runtime.time() < 2.3) {
                    right_tread.setPower(-0.5);
                    left_tread.setPower(-0.5);
                }else {
                    MyState = state.END;
                    runtime.reset();
                    stopMoving();
                }

                break;

            case END:
                if(runtime.time() < 8.3) {
                    hook.setPower(-1.0);
                    elevator.setPower(0.05);
                }else{
                    hook.setPower(0.0);
                }
                break;
        }

        /*
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double drive = -gamepad2.left_stick_y * gear;
        double turn  =  gamepad2.right_stick_x * gear;
        leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;

        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        // Send calculated power to wheels
        left_tread.setPower(leftPower);
        right_tread.setPower(rightPower);
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

        if (gamepad1.a) {
            elevator.setPower(-0.1);
        }
        if (gamepad1.b){
            elevator.setPower(-0.5);
        }

        // make elevator chain go up and down
        if (gamepad1.dpad_down){
            chain.setPower(1.0);
        } else {
            if (gamepad1.dpad_up) {
                chain.setPower(-1.0);
            } else {
                chain.setPower(0.0);
            }
        }

        //Normal Mode: Fast
        if (gamepad2.y) gear = 0.5;
        //Precise Mode: Slow
        if (gamepad2.x) gear = 0.2;
        //TURBO Mode: Faster
        if (gamepad2.b) gear = 1;


        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
        */
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    public void stopMoving() {
        left_tread.setPower(0.0);
        right_tread.setPower(0.0);
    }

}
