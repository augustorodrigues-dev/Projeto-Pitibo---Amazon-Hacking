# Projeto Pitibo

---

## Descrição

Este projeto implementa um sistema simples e eficiente de **notificação via Bluetooth Low Energy (BLE)** usando um módulo **ESP32**.  
A ideia é permitir que idosos possam enviar alertas de ajuda ou mensagens personalizadas para seus cuidadores de forma rápida e prática, por meio de botões físicos.

O ESP32 funciona como um servidor BLE que envia notificações ao aplicativo Android conectado sempre que um botão é pressionado, permitindo comunicação instantânea e confiável sem depender de conexão Wi-Fi ou internet.

---

## Objetivos

- Prover um meio acessível e seguro para idosos solicitarem ajuda.  
- Facilitar a comunicação entre idosos e cuidadores via dispositivo Bluetooth.  
- Oferecer possibilidade de enviar mensagens personalizadas pelo cuidador via BLE.  
- Garantir baixo consumo de energia e simplicidade de uso.  

---

## Componentes do Projeto

### Hardware

- ESP32  
- Dois botões físicos:
  - **Botão 1:** envia a mensagem fixa **"Ajuda"**  
  - **Botão 2:** envia uma mensagem personalizada configurada via BLE  
- LEDs ou indicadores opcionais para feedback visual  

### Software

- Código Arduino para ESP32 configurando o servidor BLE, controle dos botões e envio das notificações.  
- Aplicativo Android (não incluso neste README) para conexão via BLE, recebimento e exibição das notificações.  

---

## Funcionamento

1. O ESP32 inicia como servidor BLE, anunciando seu serviço com UUID específico.  
2. O cuidador conecta seu smartphone ao ESP32 via BLE.  
3. Quando o **Botão 1** é pressionado pelo idoso, o ESP32 envia imediatamente a mensagem **"Ajuda"** para o aplicativo.  
4. Quando o **Botão 2** é pressionado, o ESP32 envia uma mensagem personalizada, que pode ser atualizada dinamicamente via BLE pelo aplicativo.  
5. O cuidador recebe a notificação e pode agir conforme a necessidade, como entrar em contato ou deslocar-se até o idoso.  

---

## Benefícios para Idosos e Cuidadores

- **Simplicidade:** Botões físicos fáceis de usar, sem necessidade de lidar com smartphones.  
- **Imediatismo:** Mensagens enviadas instantaneamente via BLE, sem precisar de internet.  
- **Personalização:** Mensagens customizadas para diferentes tipos de solicitações.  
- **Segurança:** Comunicação local, evitando exposição a redes externas.  
- **Acessibilidade:** Permite que idosos com limitações usem um sistema simples para chamar ajuda.  

---

## Instalação e Uso

### Configurar o Produto

1. Energizar o mesmo com uma bateria portátil 

### Conectar App Android

1. Escanear dispositivos BLE com o nome do ESP32.  
2. Conectar e receber notificações.  
3. Enviar mensagens personalizadas via BLE se desejar.  

### Operação

- Idoso pressiona os botões para enviar mensagens.  
- Cuidador recebe e responde conforme necessário.  

---

## Extra

### Google Firebase Console

- Integração com Authentication para o cadastro e login de contas dos cuidadores.
- Uso do Firestore para armazenar as quando e qual notificação foi enviada, visando pesquisas e estudos para o governo.

---

## [Vídeo Demonstrativo](https://youtu.be/lmeroR5gzn8?si=jBsKDtVgi9e4Y5mv)

# <h1 align="center"> **LINKEDIN DOS INTEGRANTES:**

<br/>
<br/>

## [Augusto Rodrigues](https://www.linkedin.com/in/augusto-rodrigues-875b97356/)
## [Cauê Barroso](https://www.linkedin.com/in/cau%C3%AA-b-0b5284328/)
## [Cesar Ribeiro](https://www.linkedin.com/in/cesar-augusto-lopes-ribeiro-73b867206/)
## [Fernando Fonseca](https://www.linkedin.com/in/fernando-fonseca-dev/)
## [Julio Lindquist](https://www.linkedin.com/in/julio-cesar-lindquist-27a418276/)
