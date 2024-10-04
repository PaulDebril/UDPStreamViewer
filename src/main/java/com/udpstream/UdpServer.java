package com.udpstream;

public class UdpServer {
    public static void main(String[] args) {
        // Initialize the GUI
        ImageDisplay imageDisplay = new ImageDisplay();

        // Start the UDP receiver
        UdpReceiver udpReceiver = new UdpReceiver(3630, imageDisplay);
        udpReceiver.start();
    }
}
