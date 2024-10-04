package com.udpstream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.ByteArrayOutputStream;

public class UdpReceiver extends Thread {
    private static final int MAX_PACKET_SIZE = 65467;
    private int port;
    private ImageDisplay imageDisplay;

    public UdpReceiver(int port, ImageDisplay imageDisplay) {
        this.port = port;
        this.imageDisplay = imageDisplay;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveBuffer = new byte[MAX_PACKET_SIZE];
            System.out.println("Listening for UDP packets on port " + port + "...");

            while (true) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int expectedTotalPackets = -1;

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivePacket);

                    byte[] packetData = receivePacket.getData();
                    int packetLength = receivePacket.getLength();

                    // Extract the total number of expected packets from the second byte
                    if (expectedTotalPackets == -1) {
                        expectedTotalPackets = packetData[1] & 0xFF;
                        System.out.println("Total packets expected: " + expectedTotalPackets);
                    }

                    // Remove the first two bytes (header) from the packet data
                    byte[] remainingData = new byte[packetLength - 2];
                    System.arraycopy(packetData, 2, remainingData, 0, packetLength - 2);
                    baos.write(remainingData);

                    if (--expectedTotalPackets == 0) {
                        System.out.println("All packets received for current image.");
                        break;
                    }
                }

                byte[] imageData = baos.toByteArray();
                System.out.println("Total data received: " + imageData.length + " bytes");

                // Update the image in the GUI
                imageDisplay.updateImage(imageData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
