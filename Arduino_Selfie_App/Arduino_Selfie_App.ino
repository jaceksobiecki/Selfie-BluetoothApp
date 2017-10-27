#include <SoftwareSerial.h>
SoftwareSerial BTSerial(10, 11); // RX | TX

int data[4];
byte sendData[4];
long onTime = 1000;
unsigned long previousMillis = 0;

boolean diagOn = false;



void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  Serial.println("Serial started");

  sendData[0] = 0;
  sendData[1] = 0;
  sendData[2] = 0;
  sendData[3] = 11;

}

void loop() {
  if (BTSerial.available()) {
    Serial.print("[");
    for (int i = 0; i < 4; i++) {
      data[i] = BTSerial.read();
      Serial.print(data[i]);
      if (i != 3)
        Serial.print(", ");
    }
    Serial.println("]");
    sendData[0]=data[0];
    BTSerial.write(sendData, 4);
  }

  if (data[0] == 110)
    diagOn = true;
  else if (data[0] == 111)
    diagOn = false;


  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= onTime) {
    if (diagOn) {
      BTSerial.write(sendData, 4);
      previousMillis = currentMillis;
      Serial.println("wysyłam");
    }
  }

}

