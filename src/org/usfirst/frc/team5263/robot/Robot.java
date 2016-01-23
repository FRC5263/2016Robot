
package org.usfirst.frc.team5263.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
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
    Jaguar motor;
    Jaguar motor1;
    Ultrasonic ultrasonic;
    Encoder encoder;
    AnalogInput pot;
    Compressor mainCompressor;
    Solenoid solenoid;
    
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        stick = new Joystick(0);
        motor = new Jaguar(0);
        motor1 = new Jaguar(1);
        
        ultrasonic = new Ultrasonic(0, 1);
        ultrasonic.setAutomaticMode(true);
        
        encoder = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
        encoder.setMinRate(10);
        
        pot = new AnalogInput(0); //pot in analong port 0
        
        //mainCompressor = new Compressor(0);
        //mainCompressor.start();
        //solenoid = new Solenoid(0);
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
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
            break;
    	}
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	motor.set(stick.getRawAxis(1));
    	motor1.set(stick.getRawAxis(5));
    	
    	
    	SmartDashboard.putNumber("Sensor", ultrasonic.getRangeInches());
    	SmartDashboard.putNumber("Encoder: ", encoder.get());
    	SmartDashboard.putNumber("Pot: ", pot.getAverageVoltage());
    	if (encoder.getStopped() == true) {
    		encoder.reset();
    	}
    	
    	/*if (stick.getRawButton(2)) {
    		solenoid.set(true);
    	} else if (stick.getRawButton(3)) {
    		solenoid.set(false);
    	}*/
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {

    }
    
}
