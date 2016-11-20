#include <Servo.h>

Servo leftServo;
Servo rightServo;

const String handshakeMsg = "Are you my Arduino?";
String input;

void setup() {
	Serial.begin(9600);
	leftServo.attach(9);
	rightServo.attach(10);
	leftServo.write(90);
	rightServo.write(91);
}

void loop() {
	if(Serial.available() > 0) {
		//See if this serial input is the start of the handshake message
		if(Serial.peek() == 'A') {
			String message = Serial.readStringUntil('\n');
			//If we got the handshake message, respond in the affirmative
			if(message.equals(handshakeMsg)) {
				Serial.write("Yes, I am!\n");
			}
		}
		else {
			input = Serial.readStringUntil(']');
			if(input.startsWith("[")) {
				leftServo.write(180 - input.substring(1, input.indexOf(',')).toInt());
				rightServo.write(input.substring(input.indexOf(',') + 1).toInt() + 1);
			}
		}
	}
}
