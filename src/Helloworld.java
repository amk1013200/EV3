import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;

import java.io.DataInputStream;

public class HelloWorld {

    public static void main(String[] args) throws Exception {

        LCD.drawString("START", 0, 0);
        Delay.msDelay(2000);

        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);

        EV3UltrasonicSensor us = new EV3UltrasonicSensor(SensorPort.S1);
        EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);

        SampleProvider distance = us.getDistanceMode();
        SampleProvider light = colorSensor.getRedMode();

        float[] distSample = new float[distance.sampleSize()];
        float[] lightSample = new float[light.sampleSize()];

        float threshold = 0.4f;

        LCD.clear();
        LCD.drawString("Waiting BT...", 0, 0);

        BTConnector btc = new BTConnector();
        NXTConnection conn = btc.waitForConnection(0, NXTConnection.RAW);

        LCD.clear();
        LCD.drawString("Connected!", 0, 0);

        DataInputStream dis = conn.openDataInputStream();

        String mode = "STOP";

        while (!Button.ESCAPE.isDown()) {

            if (dis.available() > 0) {
                String cmd = dis.readUTF();

                if (cmd.equals("AUTO")) {
                    mode = "AUTO";
                    LCD.clear();
                    LCD.drawString("Mode: AUTO", 0, 0);
                }
                else if (cmd.equals("MANUAL")) {
                    mode = "MANUAL";
                    LCD.clear();
                    LCD.drawString("Mode: MANUAL", 0, 0);
                }
                else if (cmd.equals("STOP")) {
                    mode = "STOP";
                    LCD.clear();
                    LCD.drawString("Mode: STOP", 0, 0);
                }

                // MANUAL control
                if (mode.equals("MANUAL")) {

                    if (cmd.equals("W")) {
                        leftMotor.forward();
                        rightMotor.forward();
                    } 
                    else if (cmd.equals("S")) {
                        leftMotor.backward();
                        rightMotor.backward();
                    } 
                    else if (cmd.equals("A")) {
                        leftMotor.backward();
                        rightMotor.forward();
                    } 
                    else if (cmd.equals("D")) {
                        leftMotor.forward();
                        rightMotor.backward();
                    }
                }
            }

            if (mode.equals("STOP")) {
                leftMotor.stop(true);
                rightMotor.stop(true);
            }

            else if (mode.equals("AUTO")) {

                distance.fetchSample(distSample, 0);
                light.fetchSample(lightSample, 0);

                float dist = distSample[0];
                float lightValue = lightSample[0];

                LCD.drawString("Dist:" + (int)(dist * 100) + "cm ", 0, 1);
                LCD.drawString("Light:" + (int)(lightValue * 100) + "% ", 0, 2);

                if (dist < 0.2) {
                    leftMotor.stop(true);
                    rightMotor.stop(true);

                    Delay.msDelay(500);

                    leftMotor.rotate(200);
                    rightMotor.rotate(-200);
                } 
                else {
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
