#include <Servo.h>

#define DIRRECTION_MASK 0b10000000
#define STOP_MASK 0b01000000
#define THROTTLE_MASK 0b00111111

//digital pins
#define HALL 2
#define LED 3
#define MOTOR_B 4
#define MOTOR_PWM 5
#define STEER 6
#define MOTOR_A 7

//analog pins
#define BATTERY 0

#define BAUD  9600

#define COMMAND_LIFETIME 200

Servo servo;
byte counter;
long lastCommand;

long lastHall;
long hallInterval = 9999999;

void hall();

void setup() {
  Serial.begin(BAUD);
  Serial.setTimeout(30);

  servo.attach(STEER);

  //attachInterrupt(digitalPinToInterrupt(HALL), hall, CHANGE);

  pinMode(HALL, INPUT);
  pinMode(STEER, OUTPUT);
  pinMode(MOTOR_A, OUTPUT);
  pinMode(MOTOR_B, OUTPUT);
  pinMode(MOTOR_PWM, OUTPUT);
  pinMode(LED, OUTPUT);

  initWifi();

  lastHall = millis();
}

void initWifi()
{
  sendEsp("AT");
  if (!Serial.find("OK"))
  {
    while (true) {
      blinkLed(500);
      delay(500);
    }
  }
  sendEsp("AT+RST");
  sednEsp("ATE0");
  sendEsp("AT+CWMODE=1");
  sendEsp("AT+CIPMUX=0");
  sendEsp("AT+CWJAP=\"fly\",\"12345678\"");
  sendEsp("AT+CIPMODE=1");
  sendEsp("AT+CIPSTART=\"UDP\",\"192.168.43.12\"");
  sendEsp("AT+CIPSEND");
}

void sendEsp(String command)
{
  Serial.println(command);  
  blinkLed(200);
}

void blinkLed(long time) {
  analogWrite(LED, 75);
  delay(time);
  analogWrite(LED, 0);  
}

void hall() {
  long t = millis();
  hallInterval = t - lastHall;
  lastHall = t;
}

void applyThrottle(byte val)
{
  if (val & STOP_MASK)
  {
    stopCar();
  }
  else
  {
    if (val & DIRRECTION_MASK)
    {
      digitalWrite(MOTOR_A, HIGH);
      digitalWrite(MOTOR_B, LOW);
    }
    else
    {
      digitalWrite(MOTOR_A, LOW);
      digitalWrite(MOTOR_B, HIGH);
    }
    analogWrite(MOTOR_PWM, map(val & THROTTLE_MASK, 0, 63, 0, 255));
  }
}

void stopCar()
{
  analogWrite(MOTOR_PWM, 0);
  digitalWrite(MOTOR_A, HIGH);
  digitalWrite(MOTOR_B, HIGH);
}

void sendSensors() {
  //make this call every x command
  if (counter % 8 == 0) {
    Serial.print("$");
    Serial.write(map(analogRead(BATTERY), 0, 1023, 0, 255));
    Serial.write(min(hallInterval / 10, 255));

  }
  counter++;
}

void loop() {
  if (millis() - lastCommand > COMMAND_LIFETIME)
  {
    stopCar();
  }
  if (Serial.available())
  {
    if (Serial.find("$"))
    {
      int throttle = Serial.read();
      int steer = Serial.read();
      int light = Serial.read();
      int crc = Serial.read();
      //check crc (last byte of command must be equal to summ of previous)
      if (crc == (byte)(throttle + steer + light)) {
        applyThrottle(throttle);
        servo.write(steer);
        analogWrite(LED, light);
        lastCommand = millis();
        sendSensors();
      }//else data is corrupted
    }
  }
}
