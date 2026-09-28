package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.CRServo;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.lang.Thread;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name="four wheels slop", group="Linear OpMode")

public class Tele_four_wheels extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Limelight3A limelight;
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive  = null;
    private DcMotor leftBackDrive  = null;
    private DcMotor rightBackDrive  = null;
    private DcMotor launchMotor = null;
    private DcMotor servol = null;
    private DcMotor servor = null;
    
    
    
    
    public void setDrivePower(double leftFrontPower, double rightFrontPower, double rightBackPower, double leftBackPower) {
            // Output the values to the motor drives.
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            rightBackDrive.setPower(rightBackPower);
            leftBackDrive.setPower(leftBackPower);
        }   
        
    public void launchTheBall(double flyWheelPower){
        launchMotor.setPower(flyWheelPower);
    }
    
    public void adjustLauncherSize( "ballType"){
        if (ballType == )
    }
        
    public void driveRobot(double axial, double lateral, double yaw) {
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
        launchMotor = hardwareMap.get(DcMotor.class, "flywheel");//port 0 (expansion)
        
        
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);//FORWARD
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);//REVERSE
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);//FORWARD
        launchMotor.setDirection(DcMotor.Direction.FORWARD);
        
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            driveRobot(-gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
            launchTheBall(gamepad1.right_trigger);
        }
        
    }
    
}
