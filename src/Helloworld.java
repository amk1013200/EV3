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

        // Motors
        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);

        // Small delay (important for Bluetooth run)
        Delay.msDelay(1000);

        // Sensors
        EV3UltrasonicSensor us = new EV3UltrasonicSensor(SensorPort.S1);
        EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);

        SampleProvider distance = us.getDistanceMode();
        SampleProvider light = colorSensor.getRedMode();

        float[] distSample = new float[distance.sampleSize()];
        float[] lightSample = new float[light.sampleSize()];

        float threshold = 0.4f; // adjust based on your testing

        // 🔵 Bluetooth
        LCD.drawString("Waiting BT...", 0, 0);

        BTConnector btc = new BTConnector();
        NXTConnection conn = btc.waitForConnection(0, NXTConnection.RAW);

        LCD.clear();
        LCD.drawString("Connected!", 0, 0);

        DataInputStream dis = conn.openDataInputStream();

        String mode = "STOP";

        while (!Button.ESCAPE.isDown()) {

            // 🔵 RECEIVE COMMANDS
            if (dis.available() > 0) {
                String cmd = dis.readUTF();

                if (cmd.equals("AUTO")) mode = "AUTO";
                else if (cmd.equals("MANUAL")) mode = "MANUAL";
                else if (cmd.equals("STOP")) mode = "STOP";

                // Manual control
                else if (cmd.equals("W")) {
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

            // 🔴 STOP MODE
            if (mode.equals("STOP")) {
                leftMotor.stop(true);
                rightMotor.stop(true);
            }

            // 🟢 AUTO MODE (LINE + OBSTACLE)
            else if (mode.equals("AUTO")) {

                // Read sensors
                distance.fetchSample(distSample, 0);
                light.fetchSample(lightSample, 0);

                float dist = distSample[0];
                float lightValue = lightSample[0];

                // 🚧 Obstacle detection FIRST
                if (dist < 0.2) {
                    leftMotor.stop(true);
                    rightMotor.stop(true);

                    Delay.msDelay(500);

                    // Turn right
                    leftMotor.rotate(200);
                    rightMotor.rotate(-200);
                } 
                else {
                    // 👇 SIMPLE LINE FOLLOWING

                    if (lightValue < threshold) {
                        // On black line → straight
                        leftMotor.setSpeed(200);
                        rightMotor.setSpeed(200);
                    } else {
                        // Off line → turn
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