package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.lang.Thread;
//ColorSensor
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;



/*
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name="Main Teleop", group="Linear OpMode")

public class MainCode extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive  = null;
    private DcMotor leftBackDrive  = null;
    private DcMotor rightBackDrive  = null;
    private DcMotor inTake = null;
    private DcMotor launchMotorLeft = null; 
    private DcMotor launchMotorRight = null;
    private Servo servol = null;
    private Servo servor = null;
    private CRServo plateServoL = null;
    private CRServo plateServoR = null;
    
    double slPos = 0;
    double srPos = 0;
    double slinPos = 0;
    double srinPos = 0;
    double maxPow = 1;
    int power = 0;
    int direction = 0; 
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
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;
            
        setDrivePower(leftFrontPower, rightFrontPower, rightBackPower, leftBackPower);
        }
        
        public void moveIntake(boolean move, boolean directiony){
            if (move){
                power = 1;
            }
            else{
                power = 0;
            }
            if (directiony){
                direction = -1;
            }
            else{
                direction = 0;
            }
            inTake.setPower(power + direction);
        }
        
        public void moveAdjusters (boolean posONE, boolean posTWO){
            if (posONE){
                //servol.setPosition(servol.getPosition()+0.1);
                //servor.setPosition(servor.getPosition()+0.1);
                servol.setPosition(1);
                servor.setPosition(1);
            }
            else if (posTWO){
                //servol.setPosition(servol.getPosition()-0.1);
                //servor.setPosition(servor.getPosition()-0.1);
                servol.setPosition(0);
                servor.setPosition(0);
            }
            else{
                telemetry.addLine("nothing new");
            }
            slPos = servol.getPosition();
            srPos = servor.getPosition();
            telemetry.addLine("SR Pos is: " + srPos + ". SL Pos is: " + slPos);
        }
        
        public void launchBall (double posPower, double negPower){
            if (maxPow < (posPower - negPower)){
                launchMotorRight.setPower(maxPow); 
                launchMotorLeft.setPower(maxPow);
            }
            else{
            launchMotorRight.setPower(posPower - negPower); 
            launchMotorLeft.setPower(posPower - negPower);
            }
        }
        
        public void adjustLaunch (boolean right, boolean left){
            if (right){
                maxPow += 0.05;
            }
            if (left){
                maxPow -=0.05;
            }
            sleep (100);
        }
        
        public void plateLift (boolean locOne, boolean locTwo){
            if (locOne == true){
                //servo encoded poorly
                plateServoL.setPower(1);
                plateServoR.setPower(1);
            }
            else if (locTwo == true){
                plateServoL.setPower(-1);
                plateServoR.setPower(-1);
            }
            else{
                telemetry.addLine("nothing new lift");
                plateServoL.setPower(0);
                plateServoR.setPower(0);
            }
            //srinPos = plateServoR.getPosition();
            //telemetry.addLine("SRin Power is: " + slinPos + ". SLin Pos is: " + srinPos);
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
        inTake = hardwareMap.get(DcMotor.class, "intake");//port 1 ex
        launchMotorLeft = hardwareMap.get(DcMotor.class, "launchLeft");// port 0 ex
        launchMotorRight = hardwareMap.get(DcMotor.class, "launchRight");// port 2 ex
        servol = hardwareMap.get(Servo.class, "leftServo");// port 0
        servor = hardwareMap.get(Servo.class, "rightServo");// port 1
        plateServoL = hardwareMap.get(CRServo.class, "intakeLeft"); //2
        plateServoR = hardwareMap.get(CRServo.class, "intakeRight"); //3
        

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);//FORWARD
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        inTake.setDirection(DcMotor.Direction.FORWARD);
        launchMotorLeft.setDirection(DcMotor.Direction.FORWARD);
        launchMotorRight.setDirection(DcMotor.Direction.REVERSE);
        servol.setDirection(Servo.Direction.REVERSE); //flips 1 and 0 position
        servor.setDirection(Servo.Direction.FORWARD);
        plateServoL.setDirection(CRServo.Direction.FORWARD);
        plateServoR.setDirection(CRServo.Direction.REVERSE);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //driveRobot(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x);
            driveRobot(gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);
            moveIntake(gamepad1.left_bumper, gamepad1.right_bumper);
            launchBall(gamepad2.right_trigger, gamepad2.left_trigger);
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
            //Test
            if (ColorBall == 0 || ColorBall == 2){
                //servol.setPosition(servol.getPosition()+0.1);
                //servor.setPosition(servor.getPosition()+0.1);
                servol.setPosition(1);
                servor.setPosition(1);
            }else if (ColorBall == 1){
                servol.setPosition(0);
                servor.setPosition(0);
            }else{
                telemetry.addLine("Nothing new lift");
                servol.setPosition(0.5);
                servor.setPosition(0.5);
            }
            //
            moveAdjusters(gamepad2.dpad_up, gamepad2.dpad_down);
            adjustLaunch(gamepad2.dpad_right, gamepad2.dpad_left);
            plateLift(gamepad2.y, gamepad2.x);
            telemetry.addLine("launcher had a max power of " + maxPow);
            telemetry.addLine("CRservo power R: " + plateServoR.getPower() + " L: " + plateServoR.getPower());
            telemetry.update();
        }
    }
}
