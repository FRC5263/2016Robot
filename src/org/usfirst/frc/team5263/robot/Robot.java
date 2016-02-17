package org.usfirst.frc.team5263.robot;

import java.io.IOException;

//import java.io.IOException;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CameraServer;
//import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
//import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
//import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
//import edu.wpi.first.wpilibj.interfaces.Gyro;
//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
    Joystick stick;
    Joystick shoot;
    
    Jaguar leftWheel;
    Jaguar rightWheel;
    Victor flyWheelRight;
    Victor flyWheelLeft;
    Victor arm;
    Jaguar lift;
   
    
    Ultrasonic ultrasonic;
    Encoder encoder;
    //AnalogInput pot;
    Compressor mainCompressor;
    DoubleSolenoid solenoid;
    DoubleSolenoid solenoid1;
    ADXRS450_Gyro gyroRobot;
    AnalogGyro gyroArm;
    Servo servo1;
    Servo servo2;
    RobotDrive mainDrive;
    CameraServer server;
    Encoder test;
    
    
    /*private final static String GRIP_CMD =
            "/usr/local/frc/JRE/bin/java -jar /home/lvuser/grip.jar /home/lvuser/project.grip";

        private NetworkTable grip;*/

    
    public class MyPIDRotationOutput implements PIDOutput {
	    @Override
	    public void pidWrite(double output) {
	    	leftWheel.set(output);
	    	rightWheel.set(output * -1) ;
	    	//myRobot.drive(output, 0); //drive robot from PID output
	    }
    }
    public class MyPIDOutputEncoder implements PIDOutput {
	    @Override
	    public void pidWrite(double output) {
	    	leftWheel.set(output);
	    	rightWheel.set(output) ;
	    	
	    }
    }
    PIDController pidGyro;
    final double pGain = 7, iGain = 0, dGain = 0; 
    
    PIDController pidEncoder;
    final double pGainE = 7, iGainE = 0, dGainE = 0; 

        @Override
        
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        stick = new Joystick(0);
        shoot = new Joystick(1);

        leftWheel = new Jaguar(0);
        leftWheel.setInverted(true);
        rightWheel = new Jaguar(1);
        flyWheelRight = new Victor(2);
        flyWheelLeft = new Victor(3);
        flyWheelRight.setInverted(true);
        arm = new Victor(4);
        arm.setInverted(true);
        lift = new Jaguar(5);
       
        
        ultrasonic = new Ultrasonic(0, 1);
        ultrasonic.setAutomaticMode(true);
        
        //encoder = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
        //encoder.setMinRate(10);
        
       
        //pot = new AnalogInput(0); //pot in analong port 0
        
        /*mainCompressor = new Compressor(0);
        mainCompressor.start();
        solenoid = new DoubleSolenoid(0,1);
        solenoid1 = new DoubleSolenoid(2,3);*/
        servo1 = new Servo(9);
        servo1.setAngle(180);
        servo2 = new Servo(8);
        servo2.set(0);
        servo2.setAngle(180);
        //ryan sawinski
        test = new Encoder(2,3);
        
        gyroRobot = new ADXRS450_Gyro();
        gyroArm = new AnalogGyro(0);
        pidGyro = new PIDController(pGain, iGain, dGain, gyroRobot, new MyPIDRotationOutput());
        pidEncoder = new PIDController(pGainE, iGainE, dGainE , test, new MyPIDOutputEncoder());
        pidGyro.disable();
        /*
        server = CameraServer.getInstance();
        server.setQuality(50);
        server.startAutomaticCapture();
      	*/
      
        /* Run GRIP in a new process */
        /*try {
            new ProcessBuilder(GRIP_CMD).inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("127.0.0.1");
        NetworkTable.setPort(1735);*/
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		test.reset();
		gyroRobot.reset();
		autonStatus = AutonStatus.DRIVE_FORWARD_300;
    }
    	
    /**
     * This function is called periodically during autonomous
     */
    public enum AutonStatus {
    	DRIVE_FORWARD_300, DRIVE_FORWARD_300_1, DRIVE_FORWARD_400, DRIVE_FORWARD_400_1, ROTATE_RIGHT_1,  ROTATE_RIGHT_2, ROTATE_RIGHT_3, STOP
    }
    AutonStatus autonStatus;
    private boolean gyroGo(double angle){
    	pidGyro.enable();
		pidGyro.setSetpoint(angle);
		System.out.println(Double.toString(angle) + autonStatus + "Gyro");
		if (pidGyro.getError()< 1){
			Timer.delay(.5);
			//autonStatus = AutonStatus.DRIVE_FORWARD_400;
			pidGyro.disable();
			SmartDashboard.putString("!", autonStatus + " gyro done");
			test.reset();
			return true;
		}
		return false;
    }
    private boolean encoderGo(double distance){
    	int cRev = 360;
    	distance = distance / ((8 * Math.PI) / cRev);
    	pidEncoder.enable();
		pidEncoder.setSetpoint(distance);
		System.out.println(Double.toString(distance) + autonStatus + "Encoder");
		if (pidEncoder.getError() < 1){
			Timer.delay(.5);
//			autonStatus = AutonStatus.ROTATE_RIGHT_600;
			pidEncoder.disable();
			SmartDashboard.putString("!", autonStatus + " encoder done");
			test.reset();
			return true;
		}
		return false;
    }
    public void autonomousPeriodic() {
    	SmartDashboard.putNumber("Encoder: ", test.get());
    	SmartDashboard.putNumber("Gyro: ", gyroRobot.getAngle());
    	/*leftWheel.set(0);
    	rightWheel.set(0);
    	flyWheel.set(0);
    	arm.set(0);
    	servo1.set(0);*/
    	
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
            break;
            
    	}
    	
    	switch(autonStatus){
    		case DRIVE_FORWARD_300:
    			if (encoderGo(400)){
    				autonStatus = AutonStatus.ROTATE_RIGHT_1;
    			}
    			break;
    		case ROTATE_RIGHT_1:
    			if (gyroGo(90)){
    				autonStatus = AutonStatus.DRIVE_FORWARD_400;
    			}
    			break;
    		case DRIVE_FORWARD_400:
    			if (encoderGo(400)){
    				autonStatus = AutonStatus.ROTATE_RIGHT_2;
    			}
    			break;
    		case ROTATE_RIGHT_2:
    			if (gyroGo(180)){
    				autonStatus = AutonStatus.DRIVE_FORWARD_300_1;
    			}
    			break;
    		case DRIVE_FORWARD_300_1:
    			if (encoderGo(300)){
    				autonStatus = AutonStatus.ROTATE_RIGHT_3;
    			}
    			break;
    		case ROTATE_RIGHT_3:
    			if (gyroGo(270)){
    				autonStatus = AutonStatus.DRIVE_FORWARD_400_1;
    			}
    			break;
    		case DRIVE_FORWARD_400_1:
    			if (encoderGo(400)){
    				autonStatus = AutonStatus.STOP;
    			}
    			break;
    		case STOP:
    			break;
    	} 
 
    	//grip = NetworkTable.getTable("GRIP");
    	
    
        /*for (double area : grip.getNumberArray("targets/area", new double[0])) {
            //System.out.println("Got contour with area=" + area);
            SmartDashboard.putNumber("Contour=", area);
        }*/
    }
    
    

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//motor.set(stick.getRawAxis(1));
    	//motor1.set(stick.getRawAxis(5));
    	
    	//SmartDashboard.putNumber("Sensor", ultrasonic.getRangeInches());
    	//SmartDashboard.putNumber("Pot: ", pot.getAverageVoltage());
    	SmartDashboard.putNumber("Gyro Robot Angle: ", gyroRobot.getAngle());
    	SmartDashboard.putNumber("Gyro Robot Rate: ", gyroRobot.getRate());
    	SmartDashboard.putNumber("Gyro Arm Angle: ", gyroArm.getAngle());
    	SmartDashboard.putNumber("Gyro Arm Rate: ", gyroArm.getRate());
    	/*if (encoder.getStopped() == true) {
    		encoder.reset();
    	}*/
    	//SmartDashboard.putNumber("Shoot Speed: ", shootSpeed);
    	
     	//mainDrive.arcadeDrive(stick);
    	leftWheel.set(stick.getRawAxis(1) * .5);
    	rightWheel.set(stick.getRawAxis(5) * .5);
    	
    	arm.set(shoot.getRawAxis(1));
    	SmartDashboard.putNumber("Arm: ", arm.get());
    	if (shoot.getRawAxis(5) > 0){
    		servo1.setAngle(0);
    		servo2.setAngle(180);
    		flyWheelRight.set(shoot.getRawAxis(5) * .65);
    		flyWheelLeft.set(shoot.getRawAxis(5) * .65);
    	} else if (shoot.getRawAxis(5) < 0) {
    		flyWheelRight.set(shoot.getRawAxis(5));
    		flyWheelLeft.set(shoot.getRawAxis(5));
    	}
    	 if (stick.getRawButton(4))
    	 	lift.set(1);
    	 	
    	 	else if (stick.getRawButton(1))
    	 	lift.set(-1);
    	 
    	 	else 
    	 	lift.set(0);
    	 	
    	 	
    	 	
    	
    	if (shoot.getRawButton(2)) {
    		servo1.setAngle(90);
    		servo2.setAngle(90);
    	} else if(shoot.getRawButton(3)) {
    		servo1.setAngle(0);
    		servo2.setAngle(180);
    	}
    	pidGyro.disable();
    	pidEncoder.disable();
    	/*
    	if (stick.getRawButton(2)){
    		servo1.setAngle(0);
    		int shortest_angle = 0;	
			int shortest_distance = 999;
    		for (int i = 0; i<=180; i++){
    			servo1.setAngle(i);
    			Timer.delay(.1);
    			if (ultrasonic.getRangeInches() < shortest_distance){
    				shortest_angle = (int) servo1.getAngle();
    				shortest_distance = (int) ultrasonic.getRangeInches();
    				SmartDashboard.putNumber("Shortest Distance: ", shortest_distance);
    				SmartDashboard.putNumber("Shortest Angle: ", shortest_angle);
    			}
    		}
    		SmartDashboard.putString("Done searching: ", "yes");
    		Timer.delay(.25);
    		servo1.setAngle(shortest_angle);
    	}
    	*/
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {

    }
    
}
