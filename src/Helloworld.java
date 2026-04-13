import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class HelloWorld {

    public static void main(String[] args) {

        // Initialize ultrasonic sensor on port S1
        EV3UltrasonicSensor us = new EV3UltrasonicSensor(SensorPort.S1);
        SampleProvider distance = us.getDistanceMode();
        float[] sample = new float[distance.sampleSize()];

        // Set motor power
        Motor.A.setSpeed(50);
        Motor.B.setSpeed(50);

        // Start moving forward
        Motor.A.forward();
        Motor.B.forward();

        while (true) {

            // Get distance
            distance.fetchSample(sample, 0);
            float dist = sample[0]; // in meters

            // If object is closer than 0.2m (20 cm)
            if (dist < 0.2) {
                Motor.A.stop(true);
                Motor.B.stop(true);
                break;
            }

            Delay.msDelay(100);
        }

        Button.waitForAnyPress();
    }
}