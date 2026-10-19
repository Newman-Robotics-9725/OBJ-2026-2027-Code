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
        
    public void moveUsingOdom(double moveToX, double moveToY, double moveToAngle){
        double forwardMovement = ((moveToX - totalX)/(moveToX));
        double straffMovement = ((moveToY - totalY)/(moveToY));
        double turnMovement = ((moveToAngle - totalHeading)/(moveToAngle));
        telemetry.addLine("Trying move at: " + forwardMovement + ", " + straffMovement + ", " + turnMovement);
        if (moveToX-1 > totalX || moveToX+1 < totalX){
            driveRobot(forwardMovement, 0, 0);
        }
        else if (moveToX-1 < totalX && moveToX+1 > totalX){
            //driveRobot(0, 0, 0);
            
        
            if (moveToY-1 > totalY || moveToY+1 < totalY){
                driveRobot(0, straffMovement, 0);
            }
            else if (moveToY-1 < totalY && moveToY+1 > totalY){
                //driveRobot(0, 0, 0);
            
                if (moveToAngle-1 > totalHeading || moveToAngle+1 > totalHeading){
                    driveRobot(forwardMovement, straffMovement, turnMovement);
                }
                else if (moveToAngle-1 < totalHeading && moveToAngle+1 < totalHeading){
                    driveRobot(0, 0, 0);
                }
            }
        }
    }   
        
        
    public void driveRobot(double axial, double lateral, double yaw) {
                                //Forward       side            turn
        // Combine drive and turn for blended motion.
        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double leftFrontPower  = axial - lateral + yaw;
        double rightFrontPower = axial + lateral - yaw;
        double leftBackPower   = axial + lateral + yaw;
        double rightBackPower  = axial - lateral - yaw;
        
        setDrivePower(leftFrontPower, rightFrontPower, rightBackPower, leftBackPower);
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

        previousX = odom.getPosX(DistanceUnit.CM);
        previousY = odom.getPosY(DistanceUnit.CM);
        
        previousHeading = -odom.getHeading(AngleUnit.DEGREES);

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
            double x = odom.getPosX(DistanceUnit.CM);
            double y = odom.getPosY(DistanceUnit.CM);
            double heading = -odom.getHeading(AngleUnit.DEGREES);

            // Current velocity
            double velocityX = odom.getVelX(DistanceUnit.CM);
            double velocityY = odom.getVelY(DistanceUnit.CM);

            // Detect changes in position
            double deltaX = x - previousX;
            double deltaY = y - previousY;
            double deltaHeading = heading - previousHeading;

            // Distance moved since previous loop
            double distanceMoved = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            // Small threshold to prevent tiny sensor noise
            boolean positionChanged = distanceMoved > 0.05;
            boolean rotationChanged = Math.abs(deltaHeading) > 0.1;
    
            //Drive to X:30cm, Y:10cm, turn:180
            moveUsingOdom(30, 10, 180);
            

            // Display position
            telemetry.addData("X", "%.2f cm: ", x);
            telemetry.addData("Y", "%.2f cm: ", y);
            telemetry.addData("Heading", "%.2f deg: ", heading);

            telemetry.addLine();

            telemetry.addData("X velocity", "%.2f cm/s: ", velocityX);
            telemetry.addData("Y velocity", "%.2f cm/s: ", velocityY);

            telemetry.addLine();

            telemetry.addData("Change X", "%.3f cm: ", deltaX);
            telemetry.addData("Change Y", "%.3f cm: ", deltaY);
            telemetry.addData("Distance moved", "%.3f cm: ", distanceMoved);

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
            //total is stored in x and y so this is mostly redundant but can be used outside of main loop
            totalX += deltaX;
            totalY += deltaY;
            totalHeading += deltaHeading;
            previousX = x;
            previousY = y;
            previousHeading = heading;
            
        }
        
    }
    
}
