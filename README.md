# UDPStreamViewer

UDPStreamViewer is a Java Swing application designed to receive and display real-time images via the UDP protocol. It provides a dynamic interface for visualizing live video streams transmitted over UDP.

## Features

- **Dynamic Live Streaming**: The application continuously updates to display the live video feed as it receives images from the UDP stream.
- **Configurable IP and Port**: Users can configure the listening IP address and port number directly within the interface to match the UDP stream source.
- **Logging**: Server activity and image updates are logged to provide feedback and assist with debugging.
- **Image Packet Combining**: The application includes a system that can reassemble fragmented image data transmitted over multiple UDP packets, combining them into a complete image.
- **Test Module**: A separate test module is available for testing image sending functionality over UDP.

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

To run the test cases for the project, use the following Maven command:

```bash
mvn test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
