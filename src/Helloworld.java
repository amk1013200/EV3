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

        LCD.clear();
        LCD.drawString("START", 0, 0);
        Delay.msDelay(2000);

    
        EV3LargeRegulatedMotor leftMotor =
                new EV3LargeRegulatedMotor(MotorPort.A);

        EV3LargeRegulatedMotor rightMotor =
                new EV3LargeRegulatedMotor(MotorPort.B);

        int baseSpeed = 180;

        EV3UltrasonicSensor us =
                new EV3UltrasonicSensor(SensorPort.S1);

        EV3ColorSensor colorSensor =
                new EV3ColorSensor(SensorPort.S2);

        Delay.msDelay(500);

        SampleProvider distance = us.getDistanceMode();
        SampleProvider light = colorSensor.getRedMode();

        float[] distSample = new float[distance.sampleSize()];
        float[] lightSample = new float[light.sampleSize()];

        float threshold = 0.55f;

        String mode = "AUTO";

        LCD.clear();
        LCD.drawString("MODE: AUTO", 0, 0);

        while (!Button.ESCAPE.isDown()) {

        
            if (Button.UP.isDown()) {
                mode = "MANUAL";
                LCD.clear();
                LCD.drawString("MODE: MANUAL", 0, 0);
                Delay.msDelay(300);
            }

            if (Button.DOWN.isDown()) {
                mode = "AUTO";
                LCD.clear();
                LCD.drawString("MODE: AUTO", 0, 0);
                Delay.msDelay(300);
            }

            distance.fetchSample(distSample, 0);
            light.fetchSample(lightSample, 0);

            float dist = distSample[0];
            float lightValue = lightSample[0];

            LCD.drawString("Dist: " + (int)(dist * 100) + "cm   ", 0, 2);
            LCD.drawString("Light: " + (int)(lightValue * 100) + "%   ", 0, 3);

            if (mode.equals("AUTO")) {

                if (dist < 0.20f) {

                    Sound.beep();

                    leftMotor.stop(true);
                    rightMotor.stop(true);

                    Delay.msDelay(300);

                    leftMotor.setSpeed(150);
                    rightMotor.setSpeed(150);

                    leftMotor.rotate(180, true);
                    rightMotor.rotate(-180);

                } else {

                    leftMotor.forward();
                    rightMotor.forward();

                    if (lightValue > threshold) {
                        leftMotor.setSpeed(120);
                        rightMotor.setSpeed(180);
                    } else {
                        leftMotor.setSpeed(baseSpeed);
                        rightMotor.setSpeed(baseSpeed);
                    }
                }
            }
            else {

                if (Button.UP.isDown()) {
                    leftMotor.setSpeed(baseSpeed);
                    rightMotor.setSpeed(baseSpeed);
                    leftMotor.forward();
                    rightMotor.forward();
                }

                else if (Button.DOWN.isDown()) {
                    leftMotor.setSpeed(baseSpeed);
                    rightMotor.setSpeed(baseSpeed);
                    leftMotor.backward();
                    rightMotor.backward();
                }

                else if (Button.LEFT.isDown()) {
                    leftMotor.setSpeed(120);
                    rightMotor.setSpeed(180);
                    leftMotor.backward();
                    rightMotor.forward();
                }

                else if (Button.RIGHT.isDown()) {
                    leftMotor.setSpeed(180);
                    rightMotor.setSpeed(120);
                    leftMotor.forward();
                    rightMotor.backward();
                }

                else {
                    leftMotor.stop(true);
                    rightMotor.stop(true);
                }
            }

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
