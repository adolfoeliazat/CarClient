#include <Servo.h>

#define DIRRECTION_MASK 0b10000000
#define STOP_MASK 0b01000000
#define THROTTLE_MASK 0b00111111

#define COMMAND_INTERVAL 50

#define INDICATOR_POWER 50

//digital pins
#define LEFT_KEY 2
#define RIGHT_KEY 3
#define MENU_KEY 4
#define LED 13

//analog pins
#define THROTTLE_AXLE
#define STEER_AXLE

long lastCommand = 0;

int light;

void setup() {
	pinMode(LED, OUTPUT);


	initRadio();
}

void initRadio() {
	//TODO: implement
}

void blinkLed(long time) {
	analogWrite(LED, INDICATOR_POWER);
	delay(time);
	analogWrite(LED, 0);
}

void sendCommand(int throttle, int steer, int light) {
	byte data[5];
	data[0] = '$';
	data[1] =;
	data[2] =;
	data[3] =;
	data[4] = data[1] + data[2] + data[3];
	//TODO: send data
}

void loop() {
	if ((millis() - lastCommand) >= COMMAND_INTERVAL) {
		sendCommand(analogRead(THROTTLE_AXLE),
		            analogRead(STEER_AXLE),
		            light);
	}
}