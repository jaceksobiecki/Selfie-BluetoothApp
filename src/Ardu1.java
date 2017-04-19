/**
 * Created by Piotr Szubert on 19.04.2017.
 */


/*

    // Basic Bluetooth sketch HC-06_01
// Connect the Hc-06 module and communicate using the serial monitor
//
// The HC-06 defaults to AT mode when first powered on.
// The default baud rate is 9600
// The Hc-06 requires all AT commands to be in uppercase. NL+CR should not be added to the command string
//
#include <NewPing.h>
#include <AFMotor.h>
#include <SoftwareSerial.h>
    SoftwareSerial BTserial(0, 1); // RX | TX
// Connect the HC-06 TX to the Arduino RX on pin 2.
// Connect the HC-06 RX to the Arduino TX on pin 3 through a voltage divider.
//
#define TRIG_PIN A0
#define ECHO_PIN A4
#define MAX_DISTANCE 200
            #define COLL_DIST 20
    NewPing sonar(TRIG_PIN, ECHO_PIN, MAX_DISTANCE);

    AF_DCMotor motor1(1);
    AF_DCMotor motor2(2);

    int number;
    int ivalue;
    byte chvalue;

    unsigned long previousMillis1 = 0;
    long OnTime1 = 500;

    void setup()
    {
        Serial.begin(9600);
        BTserial.begin(9600);
    }

    void loop(){
        unsigned long currentMillis = millis();

        ivalue=readPing();
        chvalue=(byte)ivalue;
        Serial.println(ivalue);
        Serial.println(chvalue);

        if (BTserial.available())
        {

            if(currentMillis - previousMillis1 >= OnTime1){
                BTserial.write(chvalue);
                previousMillis1 = currentMillis;
            }

            number = BTserial.read();
            Serial.print(number);

            if(number==251){
                moveForward();
            }
            else if(number==252){
                moveBackward();
            }
            else if(number==253){
                moveLeft();
            }
            else if(number==254){
                moveRight();
            }
            else(number);{
            settSpeed();
        }
        }
    }

    void moveForward() {
        motor1.run(FORWARD);
        motor2.run(FORWARD);
    }

    void moveLeft(){

        motor1.run(RELEASE);
        motor2.run(FORWARD);
    }

    void moveRight(){
        motor1.run(RELEASE);
        motor2.run(FORWARD);
    }

    void moveBackward(){
        motor1.run(BACKWARD);
        motor2.run(BACKWARD);
    }

    void settSpeed(){
        motor1.setSpeed(number);
        motor2.setSpeed(number);
    }

    int readPing() {
        delay(2000);
        unsigned int uS = sonar.ping();
        int cm = uS / US_ROUNDTRIP_CM;
        return cm;
    }


*/