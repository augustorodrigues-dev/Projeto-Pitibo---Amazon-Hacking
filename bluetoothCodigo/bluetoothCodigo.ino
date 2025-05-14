#include "BluetoothSerial.h"

#define BOTAO 12

BluetoothSerial SerialBT;

void setup() {
  pinMode(BOTAO, INPUT_PULLUP);
  Serial.begin(115200);
  SerialBT.begin("ESP32");  
  Serial.println("Bluetooth pronto. Digite algo:");
}

void loop() {
  int leituraBotao;
  leituraBotao = digitalRead(BOTAO);
  if(leituraBotao == LOW) {
      SerialBT.println("Ajuda");
      leituraBotao = HIGH;
      delay(2000);
    }
  if (Serial.available()) {
    
    String mensagem = Serial.readStringUntil('\n');
    mensagem.trim(); 
    
    if (mensagem.length() > 0) { 
      SerialBT.println(mensagem);
    }
  }
}
