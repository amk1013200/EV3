# EV3

EV3 Robot Project

This project is about building a robot using EV3 that can follow a line and avoid obstacles.

 What We Did
 
We created a simple algorithm for the robot:

.First, we initialized motors and sensors
.Then we set speed values
.After that, the robot continuously:
Reads distance (ultrasonic sensor)
Reads light (color sensor)

 Robot Behavior
 
If an object is close:
.Robot stops
.Waits for a short time
.Turns to avoid the obstacle

If the path is clear:
.Robot calculates line error
.Adjusts motor speed
.Moves forward

Our Learning Experience
We made many mistakes during this project, even stil making mistakes .
.Sometimes the robot didn’t move correctly
.Sometimes sensors gave wrong values
.We also faced issues with EV3 (red light problem)

But each time, we tried to fix the problem and improve our algorithm.
This helped us understand:
.How sensors actually work
.How important calibration is
.How small mistakes can affect the whole system

 Problems We Faced and facing
.EV3 brick showed red light and stopped
.Wrong speed values caused unstable movement
.Line following was not smooth at first

What We Improved
.Fixed sensor reading issues
.Adjusted motor speed step by step
.Improved turning and obstacle avoidance

Next Plan
.Test robot on real track
.Make movement smoother
.Combine everything properly (line + obstacle)
