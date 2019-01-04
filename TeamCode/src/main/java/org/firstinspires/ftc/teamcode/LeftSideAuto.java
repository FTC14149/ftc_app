package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by rhill on 12/9/18.
 */

@Autonomous(name="LeftSideAuto", group="Autonomous")
//@Disabled
public class LeftSideAuto extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor left_tread;
    private DcMotor right_tread;
    private DcMotor hook;
    private DcMotor chain;
    private CRServo elevator;
    private DigitalChannel hook_stop;


    public enum state {
        LOWER,
        UNHOOK,
        TURN1,
        FWD1,
        TURN2,
        FWD2,
        END
    }

    LeftSideAuto.state MyState;
    int StateCount;

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
        runtime.reset();
        MyState=LeftSideAuto.state.LOWER;
        StateCount=3000;
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        boolean hookstop_state = hook_stop.getState();
        telemetry.addData("hook_stop", Boolean.toString(hookstop_state));

        switch (MyState) {
            case LOWER:
                if (StateCount>0) {
                    hook.setPower(1.0);
                    StateCount=StateCount-1;
                }else{
                    MyState= LeftSideAuto.state.UNHOOK;
                    hook.setPower(0.0);
                    StateCount = 20;
                }
                break;

            case UNHOOK:
                if (StateCount>0) {
                    left_tread.setPower(0.3);
                    right_tread.setPower(0.3);
                    StateCount=StateCount-1;
                }else{
                    MyState= LeftSideAuto.state.TURN1;
                    left_tread.setPower(0.0);
                    right_tread.setPower(0.0);
                    StateCount = 180;
                }
            case TURN1:
                if (StateCount>0) {
                    right_tread.setPower(0.5);
                    left_tread.setPower(-0.5);
                    StateCount=StateCount-1;
                }else{
                    MyState= LeftSideAuto.state.FWD1;
                    left_tread.setPower(0.0);
                    right_tread.setPower(0.0);
                    StateCount=500;
                }
                break;
            case FWD1:
                if(StateCount>0) {
                    right_tread.setPower(0.15);
                    left_tread.setPower(0.15);
                    StateCount = StateCount - 1;
                }else{
                    MyState= LeftSideAuto.state.TURN2;
                    left_tread.setPower(0.0);
                    right_tread.setPower(0.0);
                    StateCount=180;
                }
                break;

            case TURN2:
                if (StateCount>0) {
                    right_tread.setPower(0.5);
                    left_tread.setPower(-0.5);
                    StateCount=StateCount-1;
                }else{
                    MyState= LeftSideAuto.state.FWD2;
                    left_tread.setPower(0.0);
                    right_tread.setPower(0.0);
                    StateCount=500;
                }
                break;

            case FWD2:
                if(StateCount>0) {
                    right_tread.setPower(0.15);
                    left_tread.setPower(0.15);
                    StateCount = StateCount - 1;
                }else{
                    MyState= LeftSideAuto.state.END;
                    left_tread.setPower(0.0);
                    right_tread.setPower(0.0);
                }
                break;

            case END:
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

}
