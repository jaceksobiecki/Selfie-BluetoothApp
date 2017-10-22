#include <SoftwareSerial.h>
SoftwareSerial BTSerial(10, 11); // RX | TX

int data[5];
byte sendData[34];
long onTime = 1000;
unsigned long previousMillis = 0;

short liczba = 4500;
boolean diagOn = false;



void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  Serial.println("Serial started");

  sendData[0] = 0;
  sendData[33] = 1;
  for (int i = 1; i < 33; i += 2) {
    sendData[i] = highByte(liczba);
    sendData[i + 1] = lowByte(liczba);
  }

}

void loop() {
  if (BTSerial.available()) {
    Serial.print("[");
    for (int i = 0; i < 5; i++) {
      data[i] = BTSerial.read();
      Serial.print(data[i]);
      if (i != 4)
        Serial.print(", ");
    }
    Serial.println("]");
  }

  if (data[1] == 110)
    diagOn = true;
  else if (data[1] == 111)
    diagOn = false;


  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= onTime) {
    if (diagOn) {
      BTSerial.write(sendData, 34);
      previousMillis = currentMillis;
      Serial.println("wysyłam");
    }
  }

}

