package src;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class HelloWorld {

    public static void main(String[] args) throws Exception {

        
        EV3LargeRegulatedMotor leftMotor =
                new EV3LargeRegulatedMotor(MotorPort.A);

        EV3LargeRegulatedMotor rightMotor =
                new EV3LargeRegulatedMotor(MotorPort.B);

        int baseSpeed = 140;

    
        EV3UltrasonicSensor us =
                new EV3UltrasonicSensor(SensorPort.S1);

        EV3ColorSensor colorSensor =
                new EV3ColorSensor(SensorPort.S2);

        SampleProvider distance = us.getDistanceMode();
        SampleProvider light = colorSensor.getRedMode();

        float[] distSample = new float[distance.sampleSize()];
        float[] lightSample = new float[light.sampleSize()];

    
        LCD.clear();
        LCD.drawString("PUT ON WHITE", 0, 0);
        Button.waitForAnyPress();

        light.fetchSample(lightSample, 0);
        float white = lightSample[0];

        LCD.clear();
        LCD.drawString("PUT ON BLACK", 0, 0);
        Button.waitForAnyPress();

        light.fetchSample(lightSample, 0);
        float black = lightSample[0];

        float threshold = (white + black) / 2;

        LCD.clear();
        LCD.drawString("READY", 0, 0);
        Delay.msDelay(1000);

        
        while (!Button.ESCAPE.isDown()) {

    
            distance.fetchSample(distSample, 0);
            light.fetchSample(lightSample, 0);

            float dist = distSample[0];
            float lightValue = lightSample[0];

            LCD.drawString("D:" + (int)(dist * 100) + "cm   ", 0, 2);
            LCD.drawString("L:" + (int)(lightValue * 100) + "%   ", 0, 3);

    
            if (dist < 0.20f) {

                Sound.beep();

                leftMotor.stop(true);
                rightMotor.stop(true);

                Delay.msDelay(200);

                leftMotor.rotate(180, true);
                rightMotor.rotate(-180);

                continue;
            }

    
            if (lightValue > threshold + 0.15) {

                
                leftMotor.setSpeed(120);
                rightMotor.setSpeed(60);

                leftMotor.forward();
                rightMotor.backward();

                Delay.msDelay(120);

                continue;
            }

            if (lightValue < threshold - 0.15) {

            
                leftMotor.setSpeed(60);
                rightMotor.setSpeed(120);

                leftMotor.backward();
                rightMotor.forward();

                Delay.msDelay(120);

                continue;
            }

            float error = lightValue - threshold;

            int turn = (int)(error * 400);

            int leftSpeed = baseSpeed - turn;
            int rightSpeed = baseSpeed + turn;

        
            if (leftSpeed < 80) leftSpeed = 80;
            if (rightSpeed < 80) rightSpeed = 80;
            if (leftSpeed > 200) leftSpeed = 200;
            if (rightSpeed > 200) rightSpeed = 200;

            leftMotor.setSpeed(leftSpeed);
            rightMotor.setSpeed(rightSpeed);

            leftMotor.forward();
            rightMotor.forward();

            Delay.msDelay(20);
        }

        
        leftMotor.stop();
        rightMotor.stop();

        leftMotor.close();
        rightMotor.close();
        us.close();
        colorSensor.close();
    }
}
