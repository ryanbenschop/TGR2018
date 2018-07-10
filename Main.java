package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorSimple;



@TeleOp(name="XDriveJava", group="Practice-Bot")
public class XDriveJava extends LinearOpMode {
    // Initialise the drivetrain motors
    DcMotor backLeft;
    DcMotor frontLeft;
    DcMotor backRight;
    DcMotor frontRight;

    // Initialise the conveyor motor
    DcMotor conveyorMotor;

    // Initialise the elevator motors
    DcMotor leftArmMotor;
    DcMotor rightArmMotor;

    // Initialise the elevator limit switch
    DigitalChannel elevatorArmLimit;

    Servo conveyorDoorServo;

    //Allow the controller to switch between single and double joystick driving
    String controlMode;

    //Allow the controller to adjust between full speed and half speed
    String speedMode;

    private ElapsedTime period = new ElapsedTime();

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome
     * with a regular periodic tick. This is used to compensate for varying
     * processing times for each cycle. The function looks at the elapsed cycle time,
     * and sleeps for the remaining time interval.
     *
     * @param periodMs Length of wait cycle in mSec.
     */
    private void waitForTick(long periodMs) throws java.lang.InterruptedException
    {
        long remaining = periodMs - (long)period.milliseconds();
// sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            Thread.sleep(remaining);
        }
// Reset the cycle clock for the next pass.
        period.reset();
    }

    @Override
    public void runOpMode() {
        // Store motor power values
        double frontLeftMotorPower = 0.0;
        double frontRightMotorPower = 0.0;
        double backLeftMotorPower = 0.0;
        double backRightMotorPower = 0.0;

        // Assign motors to motor variables
        backLeft = hardwareMap.dcMotor.get("back_left");
        backRight = hardwareMap.dcMotor.get("back_right");
        frontLeft = hardwareMap.dcMotor.get("front_left");
        frontRight = hardwareMap.dcMotor.get("front_right");

        conveyorMotor = hardwareMap.dcMotor.get("conveyor_motor");

        leftArmMotor = hardwareMap.dcMotor.get("left_arm_motor");
        rightArmMotor = hardwareMap.dcMotor.get("right_arm_motor");

        // Assign limit switch to limit switch variable
        elevatorArmLimit = hardwareMap.digitalChannel.get("elevator_arm_limit");

        // Assign conveyor servo to conveyor servo variable
        conveyorDoorServo = hardwareMap.servo.get("conveyor_door_servo");

        // Set the drivetrain and elevator motors to run using encoders (at the same speed)
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set motors to brake when power is 0
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reverse flipped motors
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        leftArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Track driver control mode -- double or single joystick
        controlMode = "Double";

        // Track speed mode -- full speed or slow
        speedMode = "Full";
        // Initialise all motors to zero power
        backLeft.setPower(0);
        frontLeft.setPower(0);
        backRight.setPower(0);
        frontRight.setPower(0);
        conveyorMotor.setPower(0);
        leftArmMotor.setPower(0);
        rightArmMotor.setPower(0);

// Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver"); //
        telemetry.update();
// Wait for the game to start (driver presses PLAY)
        waitForStart();
        try {
// run until the end of the match (driver presses STOP)
            while (opModeIsActive()) {

                // Set control mode to double joystick
                if (gamepad1.left_bumper) {
                    controlMode = "Double";
                    telemetry.update();
                } // Set control mode to single joystick
                else if (gamepad1.right_bumper) {
                    controlMode = "Single";
                    telemetry.update();
                }

                telemetry.addData("Control Mode:", controlMode + " joystick.");

                // Set drive mode to full speed
                if (gamepad1.left_trigger > 0) {
                    speedMode = "Full";
                    telemetry.update();
                } // Set drive mode to slow speed
                else if (gamepad1.right_trigger > 0) {
                    speedMode = "Slow";
                    telemetry.update();
                }

                // Double joystick
                if (controlMode == "Double") {
                    // Strafe & Rotate
                    frontLeftMotorPower = (-gamepad1.left_stick_y) + gamepad1.left_stick_x;
                    frontRightMotorPower = (-gamepad1.right_stick_y) - gamepad1.right_stick_x;
                    backLeftMotorPower = (-gamepad1.left_stick_y) - gamepad1.left_stick_x;
                    backRightMotorPower = (-gamepad1.right_stick_y) + gamepad1.right_stick_x;
                }

                // Single joystick
                if (controlMode == "Single") {
                    // Strafe
                    frontLeftMotorPower = (-gamepad1.left_stick_y) + gamepad1.left_stick_x;
                    frontRightMotorPower = (-gamepad1.left_stick_y) - gamepad1.left_stick_x;
                    backLeftMotorPower = (-gamepad1.left_stick_y) - gamepad1.left_stick_x;
                    backRightMotorPower = (-gamepad1.left_stick_y) + gamepad1.left_stick_x;
                }
                if (controlMode == "Single") {
                    // Rotate
                    frontLeftMotorPower = gamepad1.right_stick_x;
                    frontRightMotorPower = -gamepad1.right_stick_x;
                    backLeftMotorPower = gamepad1.right_stick_x;
                    backRightMotorPower = -gamepad1.right_stick_x;
                }

                if (speedMode == "Full") {
                    backLeft.setPower(backLeftMotorPower);
                    backRight.setPower(backRightMotorPower);
                    frontLeft.setPower(frontLeftMotorPower);
                    frontRight.setPower(frontRightMotorPower);
                }
                else if (speedMode == "Slow") {
                    backLeft.setPower(0.25 * backLeftMotorPower);
                    backRight.setPower(0.25 * backRightMotorPower);
                    frontLeft.setPower(0.25 * frontLeftMotorPower);
                    frontRight.setPower(0.25 * frontRightMotorPower);
                }

                // Run the conveyor
                conveyorMotor.setPower(gamepad2.left_stick_y);

                // Elevator - raises and lowers the conveyor
                if (elevatorArmLimit.getState()) {
                    leftArmMotor.setPower(gamepad2.right_stick_y);
                    rightArmMotor.setPower(-gamepad2.right_stick_y);

                    telemetry.clearAll();
                    telemetry.addData("Limit State:", "True");
                    telemetry.update();
                }
                if (!elevatorArmLimit.getState()) {
                    if (gamepad2.right_stick_y < 0) {
                        leftArmMotor.setPower(gamepad2.right_stick_y);
                        rightArmMotor.setPower(-gamepad2.right_stick_y);

                    } else {
                        leftArmMotor.setPower(0);
                        rightArmMotor.setPower(0);

                    }
                    telemetry.clearAll();
                    telemetry.addData("Limit State:", "False");
                    telemetry.update();
                 }

                // Open & close conveyor door
                if (gamepad2.y) {
                    conveyorDoorServo.setPosition(0.095);
                }
                if (gamepad2.b) {
                    conveyorDoorServo.setPosition(0.5);
                }
// Send telemetry message to signify robot running;
                //telemetry.addData("left", "%.2f", left);
                //telemetry.addData("right", "%.2f", right);
                //telemetry.update();
// Pause for metronome tick. 40 mS each cycle = update 25 times
// a second.
                waitForTick(40);
            }
        }
        catch (java.lang.InterruptedException exc) {
            return;
        }
        finally {
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);
            conveyorMotor.setPower(0);
            leftArmMotor.setPower(0);
            rightArmMotor.setPower(0);
        }
    }
}
