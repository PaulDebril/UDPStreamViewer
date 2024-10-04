package com.udpstream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.ByteArrayOutputStream;

public class UdpClientTest {
    // Server port
    private static final int SERVER_PORT = 3630;
    // Maximum buffer size per packet (in bytes)
    private static final int DATA_SIZE = 65470;
    // Path to the image in the resources folder
    private static final String IMAGE_PATH = "/images/test.jpg";

    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            // Create the UDP socket
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");

            // Load the image from the classpath resources
            InputStream inputStream = UdpClientTest.class.getResourceAsStream(IMAGE_PATH);
            if (inputStream == null) {
                System.err.println("Image not found in resources!");
                return;
            }

            // Read the image as a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = baos.toByteArray();
            inputStream.close();

            int totalPackets = (int) Math.ceil((double) imageBytes.length / DATA_SIZE);

            // Check if the image requires more than 255 packets (UDP limit in this case)
            if (totalPackets > 255) {
                System.err.println("The image is too large and requires more than 255 packets.");
                return;
            }

            System.out.println("Sending the image in " + totalPackets + " packets...");

            // Loop through and send each packet
            for (int i = 0; i < totalPackets; i++) {
                // Calculate the current segment size
                int start = i * DATA_SIZE;
                int end = Math.min(start + DATA_SIZE, imageBytes.length);
                int currentDataSize = end - start;

                // Create the byte array for the current packet
                byte[] packetData = new byte[2 + currentDataSize];
                // First byte: packet number (starting from 1)
                packetData[0] = (byte) (i + 1);
                // Second byte: total number of packets
                packetData[1] = (byte) totalPackets;
                // Copy the image data into the packet
                System.arraycopy(imageBytes, start, packetData, 2, currentDataSize);

                // Create and send the DatagramPacket
                DatagramPacket packet = new DatagramPacket(packetData, packetData.length, serverAddress, SERVER_PORT);
                socket.send(packet);

                System.out.println("Sent packet " + (i + 1) + "/" + totalPackets);
            }

            System.out.println("Image transfer completed.");

        } catch (Exception e) {
            System.err.println("UDP client error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the socket when done
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
