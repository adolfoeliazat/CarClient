#include <Servo.h>

#define DIRRECTION_MASK 0b1000_0000
#define STOP_MASK 0b0100_0000
#define THROTTLE_MASK 0b0011_1111

#define HALL 2
#define BATTERY 0
#define MOTOR_A 7
#define MOTOR_B 4
#define MOTOR_PWM 5
#define STEER 6
#define LED 3

#define BAUD  9600

#define DEL 5
#define COMMAND_LIFETIME 200

Servo servo;
byte counter;
long lastCommand;

long lastHall;
long hallInterval = 9999999;

void setup() {
  Serial.begin(BAUD);
  Serial.setTimeout(50);

  servo.attach(STEER);

  attachInterrupt(digitalPinToInterrupt(HALL), hall, CHANGE);

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
  sendData("AT");
  delay(300);
  if (!Serial.available())
  {
    while (true){
      blinkLed(500, 500);
    }
  }
  //sendData("AT+CWSAP=\"esp_123\",\"1234test\",5,2\r\n");
  sendData("AT+RST\r\n");
  sendData("AT+CWMODE=2\r\n");
  sendData("AT+CIPMUX=1\r\n");
  sendData("AT+CIPSERVER=1,81\r\n");  
}

void sendData(String command)
{
  Serial.print(command);
 blinkLed(100, 0);
}

void blinkLed(long time, long postDelay){
   analogWrite(LED, 100);
  delay(time);
  analogWrite(LED, 0);
  delay(postDelay);
}

void hall() {
  long t = millis();
  hallInterval = t - lastHall;
  lastHall = t;
}

void applyThrottle(byte val)
{
  if(val & STOP_MASK)
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
    analogWrite(MOTOR_PWM, map(val & THROTTLE_MASK, 0, 64, 0, 255);
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
  if (counter % 32 == 0) {
    //TODO: check data length after changing stat data
    Serial.print("AT+CIPSEND=0,3\r\n");
    while (true) {
      if (Serial.find(">")) {
        break;
      }
    }
    Serial.print("$");
    Serial.write(map(analogRead(BATTERY), 0, 1023, 0, 255));
    Serial.write(min(hallInterval / 10, 255));
  }
  counter++;
}

void loop() {
  //stop car if no signal recieved long time
  if (millis() - lastCommand > COMMAND_LIFETIME)
  {
    stopCar();
  }

  if (Serial.available())
  {
    if (Serial.find("$"))
    {
      delay(DEL);
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
      }
      //else data is corrupted
    }
  }
}
