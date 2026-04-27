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

import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;

import java.io.DataInputStream;

public class HelloWorld {

    public static void main(String[] args) throws Exception {

        LCD.drawString("START", 0, 0);
        Delay.msDelay(2000);

        // =========================
        // MOTORS
        // =========================

        EV3LargeRegulatedMotor leftMotor =
                new EV3LargeRegulatedMotor(MotorPort.A);

        EV3LargeRegulatedMotor rightMotor =
                new EV3LargeRegulatedMotor(MotorPort.B);

        leftMotor.setSpeed(200);
        rightMotor.setSpeed(200);

        // =========================
        // SENSORS
        // =========================

        // Ultrasonic Sensor -> S1
        EV3UltrasonicSensor us =
                new EV3UltrasonicSensor(SensorPort.S1);

        // Color Sensor -> S2
        EV3ColorSensor colorSensor =
                new EV3ColorSensor(SensorPort.S2);

        // Sensor startup delay
        Delay.msDelay(500);

        SampleProvider distance =
                us.getDistanceMode();

        // FIXED: safer than getRedMode()
        SampleProvider light =
                colorSensor.getAmbientMode();

        float[] distSample =
                new float[distance.sampleSize()];

        float[] lightSample =
                new float[light.sampleSize()];

        float threshold = 0.4f;

        // =========================
        // BLUETOOTH CONNECTION
        // =========================

        LCD.clear();
        LCD.drawString("Waiting BT...", 0, 0);

        BTConnector btc = new BTConnector();

        // FIXED: safer connection method
       NXTConnection conn =
        btc.waitForConnection(0, NXTConnection.LCP);

        LCD.clear();
        LCD.drawString("BT Connected!", 0, 0);

        DataInputStream dis =
                conn.openDataInputStream();

        String mode = "STOP";

        // =========================
        // MAIN LOOP
        // =========================

        while (!Button.ESCAPE.isDown()) {

            // Receive Bluetooth command
            String cmd = dis.readUTF();

            // =========================
            // MODE CHANGE
            // =========================

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

                leftMotor.stop(true);
                rightMotor.stop(true);
            }

            // =========================
            // MANUAL MODE
            // =========================

            else if (mode.equals("MANUAL")) {

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

                else if (cmd.equals("X")) {
                    leftMotor.stop(true);
                    rightMotor.stop(true);
                }
            }

            // =========================
            // AUTO MODE
            // =========================

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

                // Obstacle found
                if (dist < 0.2f) {

                    leftMotor.stop(true);
                    rightMotor.stop(true);

                    Delay.msDelay(500);

                    leftMotor.setSpeed(150);
                    rightMotor.setSpeed(150);

                    // Turn robot
                    leftMotor.rotate(200, true);
                    rightMotor.rotate(-200);

                    Delay.msDelay(300);
                }

                else {

                    // Line / light following logic
                    if (lightValue < threshold) {

                        leftMotor.setSpeed(200);
                        rightMotor.setSpeed(200);
                    }

                    else {

                        leftMotor.setSpeed(100);
                        rightMotor.setSpeed(200);
                    }

                    leftMotor.forward();
                    rightMotor.forward();
                }
            }

            Delay.msDelay(50);
        }

        // =========================
        // SAFE CLOSE
        // =========================

        leftMotor.close();
        rightMotor.close();

        us.close();
        colorSensor.close();

        dis.close();
        conn.close();
    }
}
