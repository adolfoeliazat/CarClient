#include <Servo.h>

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

#define LOG false

Servo servo;
byte counter;
long lastCommand;

long lastHall;
long hallInterval = 9999999;

bool pendingSend = false;

void setup() {
  Serial.begin(BAUD);
  Serial.setTimeout(200);

  servo.attach(STEER);

  attachInterrupt(digitalPinToInterrupt(HALL), hall, CHANGE);

  pinMode(HALL, INPUT);
  pinMode(STEER, OUTPUT);
  pinMode(MOTOR_A, OUTPUT);
  pinMode(MOTOR_B, OUTPUT);
  pinMode(MOTOR_PWM, OUTPUT);
  pinMode(LED, OUTPUT);

  initWifi();

  delay(1000);

  lastHall = millis();
}

void initWifi()
{
  sendData("AT");
  delay(300);
  if (!Serial.available())
  {
    while (true);
  }
  //sendData("AT+CWSAP=\"esp_123\",\"1234test\",5,2\r\n");
  sendData("AT+RST\r\n");
  sendData("AT+CWMODE=2\r\n");
  sendData("AT+CIPMUX=1\r\n");
  sendData("AT+CIPSERVER=1,81\r\n");
  delay(1000);
}

void sendData(String command)
{
  Serial.print(command);
  analogWrite(LED, 100);
  delay(200);
  analogWrite(LED, 0);
}

void hall() {
  long t = millis();
  hallInterval = t - lastHall;
  lastHall = t;
}

void applyThrottle(int val)
{
  int v = (val - 127) * 2 - 1;
  if (v > 0)
  {
    digitalWrite(MOTOR_A, HIGH);
    digitalWrite(MOTOR_B, LOW);
  }
  else
  {
    digitalWrite(MOTOR_A, LOW);
    digitalWrite(MOTOR_B, HIGH);
  }
  analogWrite(MOTOR_PWM, abs(v));
}

void stopCar()
{
  //stop throttle
  analogWrite(MOTOR_PWM, 0);
  digitalWrite(MOTOR_A, HIGH);
  digitalWrite(MOTOR_B, HIGH);
}

void logger(int v) {
  if (LOG) {
    logger(String(v));
  }
}

void logger(String str) {
  if (LOG) {
    Serial.println(str);
  }
}

void sendSensors() {
  if (pendingSend) {

    pendingSend = false;
  }
  //make this call every 8 command
  if (counter % 16 == 0) {
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

    pendingSend = true;
  }
  counter++;
}

void loop() {
  //stops car if no signal recieved long time
  if (millis() - lastCommand > COMMAND_LIFETIME)
  {
    stopCar();
  }

  if (Serial.available())
  {
    bool c = false; //command supplied
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
        c = true;
        sendSensors();
        //logger("Signal consumed.");
      }
      else {
        //logger("CRC Failed!!");
      }
    }
    if (c)
    {
      lastCommand = millis();
    }
  }
}
