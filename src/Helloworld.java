package src;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class HelloWorld {

    public static void main(String[] args) throws Exception {

        LCD.drawString("START", 0, 0);
        Delay.msDelay(2000);

        
        EV3LargeRegulatedMotor leftMotor =
                new EV3LargeRegulatedMotor(MotorPort.A);

        EV3LargeRegulatedMotor rightMotor =
                new EV3LargeRegulatedMotor(MotorPort.B);

        leftMotor.setSpeed(200);
        rightMotor.setSpeed(200);

        EV3UltrasonicSensor us =
                new EV3UltrasonicSensor(SensorPort.S1);

        EV3ColorSensor colorSensor =
                new EV3ColorSensor(SensorPort.S2);

        Delay.msDelay(500);

        SampleProvider distance = us.getDistanceMode();
        SampleProvider light = colorSensor.getAmbientMode();

        float[] distSample = new float[distance.sampleSize()];
        float[] lightSample = new float[light.sampleSize()];

        float threshold = 0.4f;

        String mode = "AUTO";
        LCD.clear();
        LCD.drawString("Mode: AUTO", 0, 0);
        while (!Button.ESCAPE.isDown()) {
            if (mode.equals("AUTO")) {

                distance.fetchSample(distSample, 0);
                light.fetchSample(lightSample, 0);

                float dist = distSample[0];
                float lightValue = lightSample[0];

                LCD.drawString(
                        "Dist: " + (int)(dist * 100) + "cm   ",
                        0, 1
                );

                LCD.drawString(
                        "Light: " + (int)(lightValue * 100) + "%   ",
                        0, 2
                );

                if (dist < 0.2f) {

                    leftMotor.stop(true);
                    rightMotor.stop(true);

                    Delay.msDelay(500);

                    leftMotor.setSpeed(150);
                    rightMotor.setSpeed(150);

                    leftMotor.rotate(200, true);
                    rightMotor.rotate(-200);

                    Delay.msDelay(300);

                } else {

                    if (lightValue < threshold) {
                        leftMotor.setSpeed(200);
                        rightMotor.setSpeed(200);
                    } else {
                        leftMotor.setSpeed(100);
                        rightMotor.setSpeed(200);
                    }

                    leftMotor.forward();
                    rightMotor.forward();
                }
            }

            Delay.msDelay(50);
        }

        leftMotor.close();
        rightMotor.close();
        us.close();
        colorSensor.close();
    }
}
