# Android client
This Android implementation lets you connect to the backend server and communicate with other devices that are on the same server.

When openning the app for the first time you would be asked to input the URL for the backend server, and the ports for the relevant services:
![Log in screen](https://github.com/user-attachments/assets/74dfb5f2-b7ef-47dc-96c8-f57c553c73b9)
![image](https://github.com/user-attachments/assets/83368ef8-7b18-4fad-9c68-4e46600b260f)

## In the app you have two tabs:

![image](https://github.com/user-attachments/assets/4455da52-bdcd-460c-aae4-a19b562e1dbc)

* Devices - in which you will see all the devices currently associated with the connected account, their status (offline or online) and some relevant information.
 
![image](https://github.com/user-attachments/assets/2df3b011-6c28-471f-b3f7-a0e1d6275021)

Each device has it own topics which specifies what commands it can receive and what data it sends.

Each topic has: data type (int, float, string, bool, etc...), and the topic's type (i.e., does it provide data, receive commands or both)

![image](https://github.com/user-attachments/assets/6fe530a2-2a74-43db-8f5d-3a7e4fde43fc)


* Control Panels - A user created panels that contain buttons, labels and other varius controllers to send/receive data from/to a target device.
 
![image](https://github.com/user-attachments/assets/50a230c4-da5b-4a28-8ef4-4f84e2187a83)
![image](https://github.com/user-attachments/assets/c87cb326-572d-43f1-9e96-acf9756041fd)

When creating a new component on a control panel, you select what type of component it is and to which device and device's topic it is associated with.

![image](https://github.com/user-attachments/assets/20948088-9c05-464f-bf66-e596f0b0b6b0)

After creating it, you can place it anywhere on the panel.

