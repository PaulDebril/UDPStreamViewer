package com.udpstream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.ByteArrayOutputStream;

public class UdpReceiver extends Thread {
    private static final int MAX_PACKET_SIZE = 65507;
    private int port;
    private ImageDisplay imageDisplay;
    private DatagramSocket socket;
    private boolean running;

    public UdpReceiver(int port, ImageDisplay imageDisplay) {
        this.port = port;
        this.imageDisplay = imageDisplay;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            byte[] receiveBuffer = new byte[MAX_PACKET_SIZE];
            imageDisplay.logMessage("Écoute pour les paquets UDP sur le port " + port + "...");

            while (running) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int expectedTotalPackets = -1;

                    while (running) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.receive(receivePacket);

                        byte[] packetData = receivePacket.getData();
                        int packetLength = receivePacket.getLength();

                        if (expectedTotalPackets == -1) {
                            expectedTotalPackets = packetData[1] & 0xFF;
                        }

                        byte[] remainingData = new byte[packetLength - 2];
                        System.arraycopy(packetData, 2, remainingData, 0, packetLength - 2);
                        baos.write(remainingData);

                        if (--expectedTotalPackets == 0) {
                            break;
                        }
                    }

                    byte[] imageData = baos.toByteArray();
                    imageDisplay.updateImage(imageData);
                } catch (SocketException e) {
                    if (running) {
                        e.printStackTrace();
                    } else {
                        imageDisplay.logMessage("Socket fermé proprement.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            imageDisplay.logMessage("Arrêt de l'écoute sur le port " + port);
        }
    }

    public void stopServer() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
