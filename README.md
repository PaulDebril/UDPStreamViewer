# UDPStreamViewer

UDPStreamViewer is a Java Swing application designed to receive and display real-time images via the UDP protocol. It provides a dynamic interface for visualizing live video streams transmitted over UDP.

## Features

- **Dynamic Live Streaming**: The application continuously updates to display the live video feed as it receives images from the UDP stream.
- **Configurable IP and Port**: Users can configure the listening IP address and port number directly within the interface to match the UDP stream source.
- **Logging**: Server activity and image updates are logged to provide feedback and assist with debugging.
- **Image Packet Combining**: The application includes a system that can reassemble fragmented image data transmitted over multiple UDP packets, combining them into a complete image.
- **Test Module**: A separate test module is available for testing image sending functionality over UDP.
<img width="1273" alt="image" src="https://github.com/user-attachments/assets/b7f9487c-79eb-468b-9954-5b864c9788dc">

## Project Structure

```bash
.
├── src
│   └── main
│       └── java
│           └── com
│               └── udpstream
│                   ├── # Core UDP stream functionalities and the Java Swing interface.
│   └── test
│       └── # Test cases for the image sender and stream functionalities.
├── pom.xml # Maven project configuration
```

## Prerequisites

- **Java 17** or later
- **Maven** installed

## Installation and Usage

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/PaulDebril/UDPStreamViewer.git
   cd UDPStreamViewer
   ```

2. **Build the Project**:
   Use Maven to compile the project and manage dependencies.

   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   After the build, you can run the application with the following Maven command:
   ```bash
   mvn exec:java -Dexec.mainClass="com.udpstream.Main"
   ```

4. **Configure IP and Port**:
   - On launching the application, configure the IP and port of the UDP server by entering the required details into the input fields.
   - Once configured, the application will listen for incoming images and update the interface dynamically.

## Running Tests

To test the image streaming functionality, there is a sender application available. This application allows you to send images over UDP to your receiver application for testing purposes.

### UDP Image Sender

The "UDP Image Sender" provides a graphical interface (as shown in the image) where you can configure the IP address, port, the folder containing the images, and the frames per second (FPS) to send. Use this tool to test the receiver by sending image data over UDP.


<img width="401" alt="image" src="https://github.com/user-attachments/assets/6096088c-3ae8-4554-8007-87c2451d62d2">

#### Configuration:
- **IP Address**: Specify the IP address of the machine where the receiver is running.
- **Port**: Specify the port on which the receiver listens for incoming data.
- **Images Folder**: Provide the folder path containing the images to be sent.
- **FPS**: Define the number of frames per second for the transmission.

Once configured, click on "Démarrer l'envoi" to start sending images. You can stop the transmission at any time using the "Arrêter" button.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
