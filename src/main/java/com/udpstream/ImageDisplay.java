package com.udpstream;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageDisplay {
    private JFrame frame;
    private JLabel label;
    private JPanel imagePanel;
    private JTextField ipField;
    private JTextField portField;
    private JButton toggleServerButton;
    private JTextArea logArea;
    private UdpReceiver udpReceiver;
    private boolean isServerRunning = false; 

    public ImageDisplay() {
        frame = new JFrame("UDP Image Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        // Partie gauche - Affichage de l'image
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(640, 720)); 
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); 
        imagePanel.setBackground(Color.LIGHT_GRAY); 

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20)); 
        containerPanel.add(imagePanel, BorderLayout.CENTER);

        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER); 
        label.setVerticalAlignment(JLabel.CENTER);   
        imagePanel.add(label, BorderLayout.CENTER);  

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(640, 720));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 50, 20)); 

        JLabel titleLabel = new JLabel("UDP Image Viewer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel configPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));

        JLabel ipLabel = new JLabel("Adresse IP:");
        ipField = new JTextField("127.0.0.1");
        configPanel.add(ipLabel);
        configPanel.add(ipField);

        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField("3630");
        configPanel.add(portLabel);
        configPanel.add(portField);

        toggleServerButton = new JButton("Lancer le serveur");
        configPanel.add(new JLabel()); 
        configPanel.add(toggleServerButton);

        rightPanel.add(configPanel, BorderLayout.CENTER);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(640, 360));

        JLabel logTitle = new JLabel("Logs");
        logTitle.setFont(new Font("Arial", Font.BOLD, 18));
        logTitle.setHorizontalAlignment(SwingConstants.CENTER);
        logPanel.add(logTitle, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false); 
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        rightPanel.add(logPanel, BorderLayout.SOUTH);

        frame.setLayout(new GridLayout(1, 2)); 
        frame.add(containerPanel); 
        frame.add(rightPanel);     

        frame.setVisible(true);

        toggleServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerRunning) {
                    // Arrêter le serveur
                    if (udpReceiver != null) {
                        udpReceiver.stopServer();
                        logMessage("Serveur arrêté.");
                    }
                    toggleServerButton.setText("Lancer le serveur");
                    isServerRunning = false;
                } else {
                    // Démarrer le serveur
                    String ip = ipField.getText();
                    int port = Integer.parseInt(portField.getText());
                    logMessage("Lancement du serveur sur l'IP " + ip + " et le port " + port);

                    udpReceiver = new UdpReceiver(port, ImageDisplay.this);
                    udpReceiver.start();
                    toggleServerButton.setText("Arrêter le serveur");
                    isServerRunning = true;
                }
            }
        });
    }

    public void logMessage(String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String timestamp = formatter.format(new Date());

        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void updateImage(byte[] imageData) {
        SwingUtilities.invokeLater(() -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
                
                BufferedImage image = ImageIO.read(bais);
                if (image != null) {
                    
                    int originalWidth = image.getWidth();
                    int originalHeight = image.getHeight();
                    double aspectRatio = (double) originalWidth / originalHeight;
    
                    
                    int newWidth = 640;
                    int newHeight = (int) (newWidth / aspectRatio);
    
                
                    if (newHeight > 720) {
                        newHeight = 720;
                        newWidth = (int) (newHeight * aspectRatio);
                    }
                    Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                    label.repaint();
                } else {
                    logMessage("Échec de la lecture de l'image.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logMessage("Erreur lors de la mise à jour de l'image : " + e.getMessage());
            }
        });
    }
    
}
