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

@TeleOp(name="Testing_Intake", group="Linear OpMode")

public class Testing_Intake extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor FlywheelMotor1 = null;
    private DcMotor FlywheelMotor2 = null;
    
    
    
    
    /*public void setDrivePower(double leftFrontPower, double rightFrontPower) {
            Output the values to the motor drives.
            
        }   
        
    public void launchTheBall(double flyWheelPower){
        launchMotor.setPower(***flyWheelPower);
    }
    
    public void adjustLauncherSize( "ballType"){
        if (ballType == )
    }
        
    public void driveRobot(double axial, double lateral, double yaw) {
        // Combine drive and turn for blended motion.
        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double FlywheelMotor1  = axial - lateral + yaw;
        double FlywheelMotor2 = axial + lateral - yaw;
        double leftBackPower   = axial + lateral + yaw;
        double rightBackPower  = axial - lateral - yaw;
        
        setDrivePower(leftFrontPower, rightFrontPower, rightBackPower, leftBackPower);
        } */       
    
    
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        FlywheelMotor1  = hardwareMap.get(DcMotor.class, "FlywheelMotor1");//port 2
        FlywheelMotor2 = hardwareMap.get(DcMotor.class, "FlywheelMotor2");//port 3
        
        
        //Undecided.setDirection(DcMotor.Direction.REVERSE);//FORWARD
        //Undecided.setDirection(DcMotor.Direction.REVERSE);
        
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            //driveRobot(-gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
            //launchTheBall(gamepad1.right_trigger);
            if (gamepad1.right_trigger == 1){
                FlywheelMotor1.setPower(1);
                FlywheelMotor2.setPower(1);
        }
        
    }
    }
    
}
