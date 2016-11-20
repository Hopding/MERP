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

![MERP Photo](http://hopding.com/img/merp-photo-3.jpg)

This repository contains the code for the Pi ([`rpi-code/`](https://github.com/Hopding/MERP/tree/master/rpi-code)) and Arduino ([`/arduino-code/SerialServoControlv1`](https://github.com/Hopding/MERP/tree/master/arduino-code/SerialServoControlv1`). A [separate](https://github.com/Hopding/Merp-Controller) repository contains the code for the desktop controller program.

# Raspberry Pi code
The code for the Pi is structured as a Gradle project with a task for creating a runnable JAR file. When this JAR file is run on the PI, it first begins looking for the Arduino by trying to handshake with the various serial devices that are available (anything listed under `/dev/` as `tty...` - e.g. `/dev/ttyACM0`). If the Arduino is successfully found, it then launches a server listening on port `12345`. Once a client connects, the Pi then listens for commands. Whenever a motor command is received, the Pi relays the message over serial to the Arduino, which in turn sends PWM signals to the servos.

To build the runnable JAR for the Pi:

The following should work for both Windows Powershell and Linux:
1. **Clone** this repo: `$ git clone https://github.com/Hopding/MERP.git`
2. **CD** into the rpi-code directory: `$ cd MERP/rpi-code`
3. **Execute** the task to create the JAR: `$ ./gradlew runnableJAR`

The JAR is located in the `rpi-code/build/libs` directory. It can now be executed with the following command:
```
$ java -jar build/libs/merp-server-1.0.jar
```

I generally use a headless setup for MERP, so I find it useful to build the JAR on my desktop, send it to the Pi, and then execute it over SSH. Though, the Pi could be set to simply run the JAR on startup as well.