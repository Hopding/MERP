# MERP
MERP (My Experimental Robotics Platform) is a robot built with a Raspberry Pi and an Arduino. It is intended to be an extensible base for future robotics projects/experiments. 

[![MERP Roving Demo](http://hopding.com/img/merp-video-screenshot.PNG)](https://www.youtube.com/watch?v=waLDvPaleoY)

# Overview
MERP is built from:

* 1 Raspberry Pi
* 1 Arduino Uno
* 2 Continuous Rotation Servos
* 1 Motor Battery Pack
* 1 Raspberry Pi/Arduino Battery Pack
* 1 Raspberry Pi Camera
* 1 Rear Swivel Wheel
* 2 Front Wheels
* Custom Wooden Frame

MERP is controller over WiFi. Control signals are sent to the Pi, which processes them and sends motor control signals to the Arduino as required. The Arduino then directly controls the servos.

This repository contains the code for the Pi ([`rpi-code/`](https://github.com/Hopding/MERP/tree/master/rpi-code)) and Arduino ([`https://github.com/Hopding/MERP/tree/master/arduino-code/SerialServoControlv1`]). A [separate](https://github.com/Hopding/Merp-Controller) repository contains the code for the desktop controller program.

![MERP Photo](http://hopding.com/img/merp-photo-3.jpg)