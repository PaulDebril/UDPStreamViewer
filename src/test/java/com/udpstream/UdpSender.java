package com.udpstream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class UdpSender extends JFrame {

    private JTextField ipField;
    private JTextField portField;
    private JTextField folderField;
    private JTextField fpsField;
    private JButton startButton;
    private JButton stopButton;
    private boolean isRunning = false;
    private Thread senderThread;

    public UdpSender() {
        setTitle("UDP Image Sender");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new GridLayout(6, 2));

        // IP input
        add(new JLabel("IP Address:"));
        ipField = new JTextField("127.0.0.1");
        add(ipField);

        // Port input
        add(new JLabel("Port:"));
        portField = new JTextField("3630");
        add(portField);

        // Folder input
        add(new JLabel("Images Folder:"));
        folderField = new JTextField("");
        add(folderField);

        // FPS input
        add(new JLabel("FPS:"));
        fpsField = new JTextField("30");
        add(fpsField);

        // Start button
        startButton = new JButton("Démarrer l'envoi");
        startButton.addActionListener(new StartAction());
        add(startButton);

        // Stop button
        stopButton = new JButton("Arrêter");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new StopAction());
        add(stopButton);
    }

    private class StartAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isRunning) {
                isRunning = true;
                startButton.setEnabled(false);
                stopButton.setEnabled(true);

                senderThread = new Thread(() -> {
                    try {
                        String serverIp = ipField.getText();
                        int serverPort = Integer.parseInt(portField.getText());
                        String folderPath = folderField.getText();
                        int fps = Integer.parseInt(fpsField.getText());
                        if (fps > 60) {
                            fps = 60;
                        }
                        
                        final long DELAY_BETWEEN_FRAMES = 1000 / fps;

                        InetAddress serverAddress = InetAddress.getByName(serverIp);
                        DatagramSocket socket = new DatagramSocket();

                        List<Path> imagePaths = Files.list(Paths.get(folderPath))
                        .filter(Files::isRegularFile)
                        .sorted((p1, p2) -> Integer.compare(extractNumber(p1.getFileName().toString()), extractNumber(p2.getFileName().toString())))
                        .collect(Collectors.toList());

                        if (imagePaths.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Aucune image trouvée dans le dossier", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        while (isRunning) {
                            for (Path imagePath : imagePaths) {
                                if (!isRunning) break;

                                byte[] imageBytes = Files.readAllBytes(imagePath);
                                int totalPackets = (int) Math.ceil((double) imageBytes.length / 65507);

                                if (totalPackets > 255) {
                                    System.err.println("L'image " + imagePath + " est trop grande et nécessite plus de 255 paquets.");
                                    continue;
                                }

                                for (int i = 0; i < totalPackets; i++) {
                                    int start = i * 65470;
                                    int end = Math.min(start + 65470, imageBytes.length);
                                    int currentDataSize = end - start;

                                    byte[] packetData = new byte[2 + currentDataSize];
                                    packetData[0] = (byte) (i + 1);
                                    packetData[1] = (byte) totalPackets;
                                    System.arraycopy(imageBytes, start, packetData, 2, currentDataSize);

                                    DatagramPacket packet = new DatagramPacket(packetData, packetData.length, serverAddress, serverPort);
                                    socket.send(packet);

                                    System.out.println("Envoyé le paquet " + (i + 1) + "/" + totalPackets + " de l'image " + imagePath.getFileName());
                                }

                                System.out.println("Image " + imagePath.getFileName() + " envoyée.");
                                TimeUnit.MILLISECONDS.sleep(DELAY_BETWEEN_FRAMES);
                            }
                        }
                        socket.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                senderThread.start();
            }
        }
    }

    private class StopAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            if (senderThread != null) {
                senderThread.interrupt();
            }
        }
    }

    private static int extractNumber(String fileName) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0; // Si aucun numéro n'est trouvé, retourner 0 (ou ajuster selon le besoin)
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UdpSender UdpSender = new UdpSender();
            UdpSender.setVisible(true);
        });
    }
}
