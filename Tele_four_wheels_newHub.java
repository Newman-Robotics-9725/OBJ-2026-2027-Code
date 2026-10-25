package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
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
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
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
    //Variables
    //Color Sensor
    private RevColorSensorV3 ColorSensor = null;
    int ColorRed = 0;
    int ColorGreen = 0;
    int ColorBlue = 0;
    int ColorBall = 3;
    double hue;
    float red = 0;
    float green = 0;
    float blue = 0;
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
            ColorBall = 3;
            telemetry.update();
            //ColorSensor Detection
            if (ColorSensor.getDistance(DistanceUnit.CM) < 3){
            ColorSensor.setGain(2.0f);//adapt to different lighting environment
            NormalizedRGBA colors = ColorSensor.getNormalizedColors();
            float red = ColorSensor.red();  // Range: 0.0 to 1.0
            float green = ColorSensor.green(); // Range: 0.0 to 1.0
            float blue = ColorSensor.blue();  // Range: 0.0 to 1.0
            telemetry.addLine("Light Detected" + "Color Red" + red + "Color Green" + green + "Color Blue" + blue); 
            hue = JavaUtil.colorToHue(colors.toColor());
            telemetry.addLine("Hue" + hue); 
                if (hue < 30){
                    ColorBall = 0;
                }else if (60 < hue && hue < 95){
                    ColorBall = 1;
                }else if (150 < hue && hue < 230){
                    ColorBall = 2;
                }else{ColorBall = 3;}
                
                if (ColorBall == 0){
                    telemetry.addLine("Ball Color is Red");
                }else if (ColorBall == 1){
                    telemetry.addLine("Ball Color is Yellow");
                }else if (ColorBall == 2){
                    telemetry.addLine("Ball Color is Blue");
                }
            }else if (ColorBall == 3){telemetry.addLine("No detected");
                telemetry.update();
            }//
                    }
                    
            telemetry.update();
        }
        
    }
