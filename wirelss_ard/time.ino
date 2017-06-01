/*!
* @file QuadMotorDriverShield.ino
* @brief QuadMotorDriverShield.ino  Motor control program
*
* Every 2 seconds to control motor positive inversion
*
* @author linfeng(490289303@qq.com)
* @version  V1.0
* @date  2016-4-5
*/

#include <SoftwareSerial.h>

const int E1 = 3; ///
const int E2 = 11;///

const int M1 = 4; ///
const int M2 = 12;///
int i = 0;
char cupSize = 0;
char sojuRate = 0;
char endPara = 0;
unsigned long cup;
unsigned long soju;
unsigned long beer;

SoftwareSerial BTSerial(9, 10); //Connect HC-06. Use your (TX, RX) settings
 
void M1_advance(unsigned long Msec, char Speed) ///
{
  unsigned long startMillis;
  unsigned long currentMillis;
  Serial.println("M1");
  startMillis =  millis();
    Serial.print( "time 1 :" );
    Serial.println(Msec);
  while(1)
  { 
    currentMillis = millis();
    if(currentMillis - startMillis >= Msec){
      break;
    }
    digitalWrite(M1,HIGH);
    analogWrite(E1,Speed);
  }
  digitalWrite(M1,LOW);
  analogWrite(E1,0);
}

void M2_advance(unsigned long Msec, char Speed) ///
{
  unsigned long startMillis;
  unsigned long currentMillis;
  Serial.println("M2");
  startMillis =  millis();
  while(1)
  {
    currentMillis = millis();
    if(currentMillis - startMillis >= Msec)
      break;
    digitalWrite(M2,HIGH);
    analogWrite(E2,Speed);
  }
  digitalWrite(M2,LOW);
  analogWrite(E2,0);
  Serial.println("stop Motor");
}
 
 
void setup() {
  Serial.begin(9600);
  pinMode(M1,OUTPUT);
  pinMode(M2,OUTPUT);
  pinMode(E1,OUTPUT);
  pinMode(E2,OUTPUT);
  BTSerial.begin(9600);  // set the data rate for the BT port
  BTSerial.write("AT+NAMEsomac");
}
 
void loop() {
  cup = 130;
  soju = 10;
  beer = 0;
  if(BTSerial.available()){
//  if(Serial.available()){
      cupSize = sojuRate;
      sojuRate = endPara;
      endPara = BTSerial.read();
      //endPara = Serial.read();
      if(endPara == '|'){
        if(cupSize == '1')
          cup = 25;
        else if(cupSize == '2')
          cup = 80;
        else
          cup = 130;
        if(sojuRate == '0')
          soju = 0;
        else if(sojuRate == '1')
          soju = 1;
        else if(sojuRate == '2')
          soju = 2;
        else if(sojuRate == '3')
          soju = 3;
        else if(sojuRate == '4')  
          soju = 4;
        else if(sojuRate == '5')
          soju = 5;
        else if(sojuRate == '6')
          soju = 6;
        else if(sojuRate == '7')
          soju = 7;
        else if(sojuRate == '8')
          soju = 8;
        else if(sojuRate == '9')
          soju = 9;
        else
          soju =10;
        
        beer = 10 - soju;
        Serial.print("cupSizez : ");
        Serial.println(cup);
        
        Serial.print("soju : ");
        Serial.println(soju);
        
        Serial.print("beer : ");
        Serial.println(beer);
        
        M1_advance(cup*soju*100,255); ///cpuSIze/10 * sojuRate 
        M2_advance(cup*beer*25*1000,255);
      }
    }
}
