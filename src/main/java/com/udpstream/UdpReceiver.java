package com.udpstream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.ByteArrayOutputStream;

public class UdpReceiver extends Thread {
    private static final int MAX_PACKET_SIZE = 65507; // Maximum UDP packet size
    private int port;
    private ImageDisplay imageDisplay;
    private DatagramSocket socket;
    private boolean running;
    private int imageCount = 0; // Counter for received images
    private long lastUpdateTime = System.currentTimeMillis(); // Last time the IPS was updated
    private long totalBytesReceived = 0; // Total bytes received
    private long lastBandwidthUpdateTime = System.currentTimeMillis(); // Last time bandwidth was updated


    // Constructor to initialize the UdpReceiver with the specified port and ImageDisplay instance
    public UdpReceiver(int port, ImageDisplay imageDisplay) {
        this.port = port;
        this.imageDisplay = imageDisplay;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port); // Create a DatagramSocket to listen on the specified port
            byte[] receiveBuffer = new byte[MAX_PACKET_SIZE]; // Buffer to store incoming packet data
            imageDisplay.logMessage("Listening for UDP packets on port " + port + "...");

            while (running) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Stream to assemble packet data
                    int expectedTotalPackets = -1;

                    while (running) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.receive(receivePacket); // Receive a packet

                        byte[] packetData = receivePacket.getData();
                        int packetLength = receivePacket.getLength();
                        totalBytesReceived += packetLength; // Update the total bytes received

                        // Calculate bandwidth usage every second
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastBandwidthUpdateTime >= 1000) {
                            double bandwidthKBps = (totalBytesReceived / 1024.0); // Convert to KB per second
                            imageDisplay.updateBandwidth(bandwidthKBps); // Update the UI with the bandwidth usage
                            totalBytesReceived = 0; // Reset the counter for the next second
                            lastBandwidthUpdateTime = currentTime; // Reset the timer
                        }

                        

                        // Determine the total number of packets if not already set
                        if (expectedTotalPackets == -1) {
                            expectedTotalPackets = packetData[1] & 0xFF;
                        }

                        // Extract the remaining data from the packet (excluding metadata)
                        byte[] remainingData = new byte[packetLength - 2];
                        System.arraycopy(packetData, 2, remainingData, 0, packetLength - 2);
                        baos.write(remainingData);

                        // Break the loop once all packets are received
                        if (--expectedTotalPackets == 0) {
                            break;
                        }
                    }

                    byte[] imageData = baos.toByteArray(); // Convert the assembled data to a byte array
                    imageDisplay.updateImage(imageData); // Update the displayed image

                    // Increment the image count
                    imageCount++;

                    // Update IPS every 3 seconds
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime >= 1000) {
                        int ips = (int) (imageCount / 1.0); // Calculate IPS
                        imageDisplay.updateIPS(ips); // Update the IPS display in the UI
                        imageCount = 0; // Reset the image count
                        lastUpdateTime = currentTime; // Reset the last update time
                    }

                } catch (SocketException e) {
                    // Handle socket closure during shutdown
                    if (running) {
                        e.printStackTrace();
                    } else {
                        imageDisplay.logMessage("Socket closed properly.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Close the socket when done
            }
            imageDisplay.logMessage("Stopped listening on port " + port);
        }
    }

    // Method to stop the UDP server
    public void stopServer() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close(); // Close the socket to stop listening
        }
    }
}
