package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.hardware.rev.NormalizedRGBA;
//Test
//import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp(name="Basic 4 wheels", group="Linear OpMode")

public class Tele_four_wheels_newHub extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive  = null;
    private DcMotor leftBackDrive  = null;
    private DcMotor rightBackDrive  = null;
    //Test
    private RevColorSensorV3 ColorSensor = null;
    int ColorRed = 0;
    int ColorGreen = 0;
    int ColorBlue = 0;
    int ColorBall = 3
    //
    public void setDrivePower(double leftFrontPower, double rightFrontPower, double rightBackPower, double leftBackPower) {
            // Output the values to the motor drives.
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            rightBackDrive.setPower(rightBackPower);
            leftBackDrive.setPower(leftBackPower);
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
        //Test
        ColorSensor = hardwareMap.get(RevColorSensorV3.class, "ColorSensor");//Expansion Hub Port 1
        
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);//FORWARD
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);//REVERSE
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);//FORWARD
        
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            driveRobot(-gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
            if (gamepad1.dpad_right){
                int OriginalRed = ColorSensor.red();
                int OriginalGreen = ColorSensor.green();
                int OriginalBlue = ColorSensor.blue();
                //if
                //adjustLauncherSize(ballType);
                int MaxOriginalColor = Math.max(OriginalBlue, Math.max(OriginalRed, OriginalGreen));
                if (MaxOriginalColor > 0){
                    ColorRed = (OriginalRed * 255)/MaxOriginalColor;
                    ColorGreen = (OriginalGreen * 255)/MaxOriginalColor;
                    ColorBlue = (OriginalBlue * 255)/MaxOriginalColor;
                    telemetry.addLine("Color sensor detects: from red: " + ColorRed + " green: " + ColorGreen + " and blue: " + ColorBlue);
                    if (colorSensor.getProximity() > 300){
                        Color rgb = colorSensor.getColor();
                        float [] hsv = new float[3];
                        Color.RGBToHSV((int)(rgb.red * 225)(int)(rgb.green * 225)(int)(rgb.blue * 225);
                        float hue = hsv[0]
                    }
                    if (hue >= 40 && hue <= 70){
                        //ColorBall: Red = 0, Green = 1, Blue = 2;
                        ColorBall = 0;
                        telemetry.addLine("Color of the ball: " = "Yellow");
                    } else if (hue >= 200 && hue <= 240){
                        ColorBall = 1;
                        telemetry.addLine("Color of the ball: " = "Blue");
                    } else if (hue >= 340 || hue <= 20){
                        ColorBall = 2;
                        telemetry.addLine("Color of the ball: " = "Red");
                    }
                    
                }
                
            }
            telemetry.update();
        }
        
    }
    
}
