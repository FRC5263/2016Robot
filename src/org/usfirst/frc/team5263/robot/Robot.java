package org.usfirst.frc.team5263.robot;

import java.io.IOException;

//import java.io.IOException;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CameraServer;
//import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
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
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
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
    
    Talon leftWheel;
    Talon rightWheel;
    Victor flyWheelRight;
    Victor flyWheelLeft;
    Victor arm;
    Jaguar lift;
   
    
    Ultrasonic ultrasonic;
    Encoder encoder;
    ADXRS450_Gyro gyroRobot;
    AnalogGyro gyroArm;
    Servo servo1;
    Servo servo2;
    RobotDrive mainDrive;
    CameraServer server;
    Encoder test;
    Encoder encoArm;
    Counter encoFWL;
    Counter encoFWR;

    public class MyPIDRotationOutput implements PIDOutput {
	    @Override
	    public void pidWrite(double output) {
	    	leftWheel.set((output) * -1);
	    	rightWheel.set(output);
	    	//myRobot.drive(output, 0); //drive robot from PID output
	    }
    }
    public class MyPIDOutputEncoder implements PIDOutput {
	    @Override
	    public void pidWrite(double output) {
	    	rightWheel.set(output * 1);
	    	leftWheel.set(output * 1);
	    	
	    }
    }
    public class MyPIDOutputEncoderArm implements PIDOutput {
	    @Override
	    public void pidWrite(double output) {
	    	arm.set(output);
	    	
	    }
    }
    public class MyPIDOutputFlyWheelLeft implements PIDOutput {
	    @Override
	    public void pidWrite(double output) {
	    	System.out.println("flyWheelLeft.setoutput was" + output);
	    	flyWheelLeft.set(output);

	    }
    }
    public class MyPIDOutputFlyWheelRight implements PIDOutput {
	    @Override
	    public void pidWrite(double output) {
	    	flyWheelRight.set(output);
	    	
	    }
    }
    public class LeftFWPIDSource implements PIDSource {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			// TODO Auto-generated method stub
			return PIDSourceType.kRate;
		}

		@Override
		public double pidGet() {
			// TODO Auto-generated method stub
			return currentRateL;
		}
    	
    }
    public class RightFWPIDSource implements PIDSource {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			// TODO Auto-generated method stub
			return PIDSourceType.kRate;
		}

		@Override
		public double pidGet() {
			// TODO Auto-generated method stub
			System.out.println("HI!!!");
			return currentRateR;
		}
    	
    }
    
    PIDController pidGyro;
    final double pGain = .5, iGain = 0, dGain = 0; 
    
    PIDController pidEncoder;
    final double pGainE = .001, iGainE = 0, dGainE = 0; 
    
    PIDController pidEncoderArm;
    final double pGainEA = 7, iGainEA = 0, dGainEA = 0; 

    PIDController pidFlyWheelLeft;
    final double pGainFWL = .001, iGainFWL = 0, dGainFWL = 0;
    
    PIDController pidFlyWheelRight;
    final double pGainFWR = .001, iGainFWR = 0, dGainFWR = 0; 
        @Override
        
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        stick = new Joystick(0);
        shoot = new Joystick(1);

        leftWheel = new Talon(0);
        leftWheel.setInverted(true);
        rightWheel = new Talon(1);
        flyWheelRight = new Victor(2);
        flyWheelLeft = new Victor(3);
        flyWheelRight.setInverted(true);
        arm = new Victor(4);
        encoArm = new Encoder(4, 5);
        encoFWL = new Counter(0);
        encoFWR = new Counter(1);
        
        lift = new Jaguar(5);
       
        
        ultrasonic = new Ultrasonic(6, 7);
        ultrasonic.setAutomaticMode(true);
        
        //encoder = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
        //encoder.setMinRate(10);
       
        servo1 = new Servo(9);
        //servo1.setAngle(180);
        servo2 = new Servo(8);
        servo2.setAngle(180);
        //ryan sawinski
        test = new Encoder(2,3);
        test.reset();
        
        mainDrive = new RobotDrive(leftWheel, rightWheel);
        
        gyroRobot = new ADXRS450_Gyro();
        gyroArm = new AnalogGyro(0);
        pidGyro = new PIDController(pGain, iGain, dGain, gyroRobot, new MyPIDRotationOutput());
        pidGyro.disable();
        pidEncoder = new PIDController(pGainE, iGainE, dGainE , test, new MyPIDOutputEncoder());
        pidEncoder.disable();
        pidEncoderArm = new PIDController(pGainE, iGainE, dGainE , encoArm, new MyPIDOutputEncoderArm());
        pidFlyWheelLeft = new PIDController(pGainFWL, iGainFWL, dGainFWL , new LeftFWPIDSource(), new MyPIDOutputFlyWheelLeft());
        pidFlyWheelRight = new PIDController(pGainFWR, iGainFWR, dGainFWR , new RightFWPIDSource(), new MyPIDOutputFlyWheelRight());
        
        
        server = CameraServer.getInstance();
        server.setQuality(25);
        server.startAutomaticCapture();
        
      	
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
		if (false/*pidGyro.getError()< 1*/){
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
    	if (!pidEncoder.isEnabled()) pidEncoder.setSetpoint(distance);
    	if (!pidEncoder.isEnabled()) pidEncoder.enable();
		
		System.out.println(Double.toString(distance) + autonStatus + "Encoder");
		if (pidEncoder.getError() < 1){
			Timer.delay(.5);
			//pidEncoder.disable();
			SmartDashboard.putString("!", autonStatus + " encoder done");
			test.reset();
			return true;
		}
		return false;
    }
    @Override
	public void teleopInit() {
		// TODO Auto-generated method stub
		super.teleopInit();
		pidGyro.disable();
		pidEncoder.disable();
		timeStampL = System.currentTimeMillis();
		timeStampR = System.currentTimeMillis();
		pidFlyWheelLeft.disable();
		pidFlyWheelRight.disable();	
	}
    
    double timeStampL = System.currentTimeMillis();
    double currentRateL = 0;
    public double FlyRPML(double interval) {
    	
    	if ((System.currentTimeMillis() - timeStampL) > interval){
    		double countL = encoFWL.get();
    		encoFWL.reset();
    		timeStampL = System.currentTimeMillis();
    		currentRateL = (((countL * 1000) / interval) * 10); 
    	}
    	return currentRateL;
    }
    double timeStampR = System.currentTimeMillis();
    double currentRateR = 0;
    public double FlyRPMR(double interval) {
    	//System.out.println(System.currentTimeMillis() - timeStamp);
    	if ((System.currentTimeMillis() - timeStampR) > interval){
    		double countR = encoFWR.get();
    		System.out.println(countR);
    		encoFWR.reset();
    		timeStampR = System.currentTimeMillis();
    		currentRateR = (((countR * 1000) / interval) * 10); 
    	}
    	return currentRateR;
    }
    
	public void autonomousPeriodic() {
    	SmartDashboard.putNumber("Encoder: ", test.get());
    	SmartDashboard.putNumber("Gyro: ", gyroRobot.getAngle());
    	
    	//pidGyro.setPID(SmartDashboard.getNumber("G P: ", 7), SmartDashboard.getNumber("G I: ", 0), SmartDashboard.getNumber("G D: ", 0));
    	
    	SmartDashboard.putNumber("Gyro P:", pidGyro.getP());
    	SmartDashboard.putNumber("Gyro I:", pidGyro.getI());
    	SmartDashboard.putNumber("Gyro D:", pidGyro.getD());
    	
    	SmartDashboard.putNumber("Enco P:", pidEncoder.getP());
    	SmartDashboard.putNumber("Enco I:", pidEncoder.getI());
    	SmartDashboard.putNumber("Enco D:", pidEncoder.getD());
    	
    	SmartDashboard.putNumber("EncoA P:", pidEncoderArm.getP());
    	SmartDashboard.putNumber("EncoA I:", pidEncoderArm.getI());
    	SmartDashboard.putNumber("EncoA D:", pidEncoderArm.getD());
    	
    	SmartDashboard.putNumber("Left: ", leftWheel.get());
    	SmartDashboard.putNumber("Right: ", rightWheel.get());
    	
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
    			if (gyroGo(90)){
    				autonStatus = AutonStatus.ROTATE_RIGHT_1;
    			}
    			break;
    		case ROTATE_RIGHT_1:
    			if (gyroGo(90)){
    				autonStatus = AutonStatus.DRIVE_FORWARD_400;
    			}
    			break;
    		case DRIVE_FORWARD_400:
    			if (encoderGo(42)){
    				autonStatus = AutonStatus.ROTATE_RIGHT_2;
    			}
    			break;
    		case ROTATE_RIGHT_2:
    			if (gyroGo(180)){
    				autonStatus = AutonStatus.DRIVE_FORWARD_300_1;
    			}
    			break;
    		case DRIVE_FORWARD_300_1:
    			if (encoderGo(42)){
    				autonStatus = AutonStatus.ROTATE_RIGHT_3;
    			}
    			break;
    		case ROTATE_RIGHT_3:
    			if (gyroGo(270)){
    				autonStatus = AutonStatus.DRIVE_FORWARD_400_1;
    			}
    			break;
    		case DRIVE_FORWARD_400_1:
    			if (encoderGo(42)){
    				autonStatus = AutonStatus.STOP;
    			}
    			break;
    		case STOP:
    			break;
    	} 
    }
    
    

    /**
     * This function is called periodically during operator control
     */
	/*
	private double shootBall(double dist) {
		test.
		return dist;
		
	}*/
	double timeStampOff = 0;
	int leftStick = 1;
	int rightStick = 5;
	double speed = 1;
    boolean leftyFlippy = true;
    public void teleopPeriodic() {
    	SmartDashboard.putNumber("Gyro Robot Angle: ", gyroRobot.getAngle());
    	SmartDashboard.putNumber("Encoder !!!: ", test.get());
    	SmartDashboard.putNumber("ARM!!!!: ", encoArm.get());
    	SmartDashboard.putNumber("Time: ", System.currentTimeMillis());
    	SmartDashboard.putNumber("FWL RPM: ", FlyRPML(250));
    	SmartDashboard.putNumber("FWR RPM: ", FlyRPMR(250));
    	SmartDashboard.putNumber("FWL Count: ", encoFWL.get());
    	SmartDashboard.putNumber("FWR Count: ", encoFWR.get());
    	SmartDashboard.putNumber("Distance: ", ultrasonic.getRangeInches());
    	
    	if (stick.getRawButton(8)){
    		leftyFlippy = false;
    	} else if (stick.getRawButton(7)){
    		leftyFlippy = true;
    	}
    	
    	if (leftyFlippy == true){
    		mainDrive.setSafetyEnabled(false);
    		rightWheel.setInverted(false);
    		leftWheel.set(stick.getRawAxis(1));
    		rightWheel.set(stick.getRawAxis(5));
    	} else if (leftyFlippy == false){
    		rightWheel.setInverted(true);
    		mainDrive.setMaxOutput(.5);
    		mainDrive.arcadeDrive(stick, 5, stick, 0);
    	}
    	
    	if (shoot.getRawButton(1) /*&& (System.currentTimeMillis()-timeStampOff > 250)*/) {
    		System.out.println("We in the if fam>>>>>");
    		if (pidFlyWheelLeft.isEnabled()){

    			System.out.println("Im disabled######");
    			pidFlyWheelLeft.disable();
    			pidFlyWheelRight.disable();
    		} else {
    			System.out.println("We bout to be enabled:::::");
    			pidFlyWheelLeft.enable();
        		pidFlyWheelRight.enable();
        		pidFlyWheelLeft.setSetpoint(1000);
        		pidFlyWheelRight.setSetpoint(1000);
        		
    		}
    		
    		timeStampOff = System.currentTimeMillis();
    		
    	}
    	if (((shoot.getRawAxis(5) > .1) || (shoot.getRawAxis(5) < -.1)) && pidFlyWheelLeft.isEnabled()){
    		pidFlyWheelLeft.disable();
    		pidFlyWheelRight.disable();
    	}
    	
     	SmartDashboard.putNumber("Left Joystick: ", stick.getRawAxis(1));
     	SmartDashboard.putNumber("Right Joystick: ", stick.getRawAxis(5));
     	
    	//mainDrive.arcadeDrive(stick);
    	
    	arm.set(shoot.getRawAxis(1));
    	SmartDashboard.putNumber("Arm: ", arm.get());
    	if (shoot.getRawAxis(5) > 0 && !pidFlyWheelLeft.isEnabled()){
    		servo1.setAngle(0);
    		servo2.setAngle(180);
    		flyWheelRight.set(shoot.getRawAxis(5) * .5);
    		flyWheelLeft.set(shoot.getRawAxis(5) * .5);
    	} else if (shoot.getRawAxis(5) < 0 && !pidFlyWheelLeft.isEnabled()) {
    		flyWheelLeft.set(shoot.getRawAxis(5));
    		flyWheelRight.set(shoot.getRawAxis(5));
    	}
    	 if (stick.getRawButton(6))
    	 	lift.set(1);
    	 	
    	 	else if (stick.getRawButton(5))
    	 	lift.set(-1);
    	 
    	 	else
    	 	lift.set(0);
    	 	
    	if (shoot.getRawButton(6)) {
    		servo1.setAngle(90);
    		servo2.setAngle(90);
    	} else if(shoot.getRawButton(5)) {
    		servo1.setAngle(0);
    		servo2.setAngle(180);
    	}
    	
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {

    }
    
}
