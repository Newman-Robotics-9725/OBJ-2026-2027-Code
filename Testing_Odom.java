/*
Copyright 2026 FIRST Tech Challenge Team FTC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.util.Set;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
// import Math;
// X offset: 17cm
// Y offset: 4cm


/**
 * This file contains a minimal example of a Linear "OpMode". An OpMode is a 'program' that runs
 * in either the autonomous or the TeleOp period of an FTC match. The names of OpModes appear on
 * the menu of the FTC Driver Station. When an selection is made from the menu, the corresponding
 * OpMode class is instantiated on the Robot Controller and executed.
 *
 * Remove the @Disabled annotation on the next line or two (if present) to add this OpMode to the
 * Driver Station OpMode list, or add a @Disabled annotation to prevent this OpMode from being
 * added to the Driver Station.
 */
@TeleOp(name="Odom tests", group="Linear OpMode")

public class Testing_Odom extends LinearOpMode {


    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive  = null;
    private DcMotor leftBackDrive  = null;
    private DcMotor rightBackDrive  = null;
    
    // Pinpoint
    private GoBildaPinpointDriver odom;

    // Previous position, used to detect motion
    private double previousX = 0;
    private double previousY = 0;
    private double previousHeading = 0;
    private double initialX = 0;
    private double initialY = 0;
    private double initialHeading = 0;
    private double totalX = 0;
    private double totalY = 0;
    private double totalHeading = 0;
    private double xOdomOffset = -17.0;
    private double yOdomOffset = -4.0;
    
    public void setDrivePower(double leftFrontPower, double rightFrontPower, double rightBackPower, double leftBackPower) {
            // Output the values to the motor drives.
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            rightBackDrive.setPower(rightBackPower);
            leftBackDrive.setPower(leftBackPower);
        }   
        
    // Movement controller constants. Position values are in millimeters.
    private static final double KP_MOVE = 0.004;
    private static final double KP_TURN = 0.015;
    private double movementPower = 0.3;
    private static final double MAX_MOVE_POWER = 0.65;
    private static final double MIN_MOVE_POWER = 0.10;
    private static final double MAX_TURN_POWER = 0.35;
    private static final double MIN_TURN_POWER = 0.08;
    private static final double POSITION_TOLERANCE_MM = 10.0;
    private static final double HEADING_TOLERANCE_DEG = 1.0;

    private double angleError(double target, double current) {
        // Returns the shortest signed angle in [-180, 180).
        return ((target - current + 540.0) % 360.0) - 180.0;
    }

    private double clip(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void stopDrive() {
        setDrivePower(0, 0, 0, 0);
    }

    public void moveUsingOdom(double moveToX, double moveToY, double moveToAngle) {
        // Read the current field position from Pinpoint. X/Y and targets are in mm.
        double x = odom.getX(DistanceUnit.MM);
        double y = odom.getY(DistanceUnit.MM);

        // Pinpoint heading is positive counterclockwise; keep that convention here.
        double heading = odom.getHeading(AngleUnit.DEGREES);

        // Error vector from the robot's current location to the target location.
        double errorX = moveToX - x;
        double errorY = moveToY - y;
        double distance = Math.hypot(errorX, errorY);
        double headingError = angleError(moveToAngle, heading);

        // Stop only when both position and heading are within tolerance.
        if (distance <= POSITION_TOLERANCE_MM
                && Math.abs(headingError) <= HEADING_TOLERANCE_DEG) {
            stopDrive();
            telemetry.addLine("Target reached");
            return;
        }

        double forwardMovement = 0.0;
        double straffMovement = 0.0;

        // If position is already close enough, rotate without translating.
        if (distance > POSITION_TOLERANCE_MM) {
            // Scale power with distance while preserving the vector's direction.
            double movementPower = clip(
                    KP_MOVE * distance,
                    MIN_MOVE_POWER,
                    MAX_MOVE_POWER);

            // Normalize the field-space error vector to the requested movement power.
            double moveFieldX = (errorX / distance) * movementPower;
            double moveFieldY = (errorY / distance) * movementPower;

            // Rotate field-space movement into robot-space forward/left commands.
            double headingRad = Math.toRadians(heading);
            forwardMovement = moveFieldX * Math.cos(headingRad) + moveFieldY * Math.sin(headingRad);
            straffMovement = -moveFieldX * Math.sin(headingRad)+ moveFieldY * Math.cos(headingRad);
        }

        // Positive heading error commands a counterclockwise turn.
        // Do not turn when already within heading tolerance; otherwise use a small
        // minimum power to help the robot overcome motor friction near the target.
        double turnMovement = 0.0;
        if (Math.abs(headingError) > HEADING_TOLERANCE_DEG) {
            double turnPower = clip(
                    Math.abs(KP_TURN * headingError),
                    MIN_TURN_POWER,
                    MAX_TURN_POWER);
            turnMovement = Math.copySign(turnPower, headingError);
        }

        telemetry.addData("Movement vector", "forward %.2f, strafe %.2f, turn %.2f", forwardMovement, straffMovement, turnMovement);
        telemetry.addData("Target error", "X %.1f mm, Y %.1f mm, distance %.1f mm", errorX, errorY, distance);

        driveRobot(forwardMovement, straffMovement, turnMovement);
    }

    public void driveRobot(double axial, double lateral, double yaw) {
        // Mecanum mixing: forward, strafe-left, and turn.
        double leftFrontPower  = axial - lateral + yaw;
        double rightFrontPower = axial + lateral - yaw;
        double leftBackPower   = axial + lateral + yaw;
        double rightBackPower  = axial - lateral - yaw;

        // Normalize all powers together so ratios are preserved and no value exceeds 1.
        double denominator = Math.max(1.0, Math.max(Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)), Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower))));

        setDrivePower(leftFrontPower / denominator, rightFrontPower / denominator, rightBackPower / denominator, leftBackPower / denominator);
    }

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "drivefl");//port 0
        rightFrontDrive = hardwareMap.get(DcMotor.class, "drivefr");//port 1
        leftBackDrive = hardwareMap.get(DcMotor.class, "drivebl");//port 2
        rightBackDrive = hardwareMap.get(DcMotor.class, "drivebr");//port 3
        odom = hardwareMap.get(GoBildaPinpointDriver.class,"odom");//
        // Set pod offsets
        // X offset = -17 cm
        // Y offset = -4 cm
        // Positive X offset = X pod is LEFT of robot center
        // Negative X offset = X pod is RIGHT of robot center
        // Positive Y offset = Y pod is FORWARD of robot center
        // Negative Y offset = Y pod is BEHIND robot center

        odom.setOffsets(xOdomOffset,yOdomOffset,DistanceUnit.CM);

        //Tell the Pinpoint which odometry pods you are using
        odom.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        
        // Set encoder directions
        // FORWARD means:
        // X pod should increase when robot moves FORWARD
        // Y pod should increase when robot strafes LEFT
        odom.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        // Reset position and calibrate IMU
        // Robot MUST be completely stationary here.
        odom.resetPosAndIMU();

        telemetry.addLine("Odometry initialized, Keep robot still");
        telemetry.update();

        // Give the IMU time to calibrate
        sleep(500);

        // Get initial position
        odom.update();

        // Sets the robot's current spot as the (0,0) origin, facing forward
        Pose2D startingPose = new Pose2D(DistanceUnit.MM, 0.0, 0.0, AngleUnit.DEGREES, 0.0);
        odom.setPosition(startingPose);
        previousX = odom.getX(DistanceUnit.MM);
        previousY = odom.getY(DistanceUnit.MM);
        
        previousHeading = odom.getHeading(AngleUnit.DEGREES);

        telemetry.addLine("Ready!");
        telemetry.update();

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);//FORWARD
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);//REVERSE
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);//FORWARD
        
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            //driveRobot(-gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
            telemetry.update();
            
            // update() gets the newest data from the Pinpoint.
            odom.update();

            // Current position
            double x = odom.getX(DistanceUnit.MM);
            double y = odom.getY(DistanceUnit.MM);
            double heading = odom.getHeading(AngleUnit.DEGREES);

            // Current velocity
            double velocityX = odom.getVelX(DistanceUnit.MM);
            double velocityY = odom.getVelY(DistanceUnit.MM);

            // Detect changes in position
            double deltaX = x - previousX;
            double deltaY = y - previousY;
            double deltaHeading = heading - previousHeading;

            // Distance moved since previous loop
            double distanceMoved = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            // Small threshold to prevent tiny sensor noise
            boolean positionChanged = distanceMoved > 0.5;
            boolean rotationChanged = Math.abs(deltaHeading) > 0.1;
    
            // Drive to X:300 mm, Y:100 mm, turn:180 degrees.
            moveUsingOdom(300, 100, 180);
            

            // Display position
            telemetry.addData("X", "%.2f mm", x);
            telemetry.addData("Y", "%.2f mm", y);
            telemetry.addData("Heading", "%.2f deg: ", heading);

            telemetry.addLine();

            telemetry.addData("X velocity", "%.2f mm/s", velocityX);
            telemetry.addData("Y velocity", "%.2f mm/s", velocityY);

            telemetry.addLine();

            telemetry.addData("Change X", "%.3f mm", deltaX);
            telemetry.addData("Change Y", "%.3f mm", deltaY);
            telemetry.addData("Distance moved", "%.3f mm", distanceMoved);

            telemetry.addLine();

            if (positionChanged) {
                telemetry.addLine("POS CHANGED");
            } 
            else {
                telemetry.addLine("POS STATIC");
            }

            if (rotationChanged) {
                telemetry.addLine("ROTATION CHANGED");
            }
            else{
                telemetry.addLine("ROTATION STATIC");
            }

            telemetry.update();

            // Save this loop's values for comparison next loop
            // Keep accumulated movement for existing diagnostics; moveUsingOdom reads Pinpoint directly.
            totalX += deltaX;
            totalY += deltaY;
            totalHeading += deltaHeading;
            previousX = x;
            previousY = y;
            previousHeading = heading;
            
        }
        
    }
    
}
