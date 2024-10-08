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
    // UI components
    private JFrame frame;
    private JLabel label;
    private JPanel imagePanel;
    private JTextField ipField;
    private JTextField portField;
    private JButton toggleServerButton;
    private JTextArea logArea;
    private UdpReceiver udpReceiver;
    private boolean isServerRunning = false; 

    // Constructor to set up the UI components and event listeners
    public ImageDisplay() {
        frame = new JFrame("UDP Image Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        // Left part - Image display
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(640, 720)); 
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Border around the image panel
        imagePanel.setBackground(Color.LIGHT_GRAY); // Background color for the image panel

        // Container panel for the image
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20)); // Padding around the image panel
        containerPanel.add(imagePanel, BorderLayout.CENTER);

        // Label to display the image
        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER); // Center the image horizontally
        label.setVerticalAlignment(JLabel.CENTER);   // Center the image vertically
        imagePanel.add(label, BorderLayout.CENTER);  

        // Right panel - Configuration and logs
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(640, 720));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 50, 20)); // Padding for the right panel

        // Title label for the right panel
        JLabel titleLabel = new JLabel("UDP Image Viewer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        // Configuration panel for IP and port
        JPanel configPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuration")); // Title border for configuration section

        // IP address label and text field
        JLabel ipLabel = new JLabel("IP Address:");
        ipField = new JTextField("127.0.0.1");
        configPanel.add(ipLabel);
        configPanel.add(ipField);

        // Port label and text field
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField("3630");
        configPanel.add(portLabel);
        configPanel.add(portField);

        // Button to start/stop the server
        toggleServerButton = new JButton("Start Server");
        configPanel.add(new JLabel()); // Empty label for spacing
        configPanel.add(toggleServerButton);

        rightPanel.add(configPanel, BorderLayout.CENTER);

        // Log panel to display server logs
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(640, 360));

        // Title label for the log panel
        JLabel logTitle = new JLabel("Logs");
        logTitle.setFont(new Font("Arial", Font.BOLD, 18));
        logTitle.setHorizontalAlignment(SwingConstants.CENTER);
        logPanel.add(logTitle, BorderLayout.NORTH);

        // Text area to display log messages
        logArea = new JTextArea();
        logArea.setEditable(false); // Logs should not be editable by the user
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        rightPanel.add(logPanel, BorderLayout.SOUTH);

        // Set up the main frame layout
        frame.setLayout(new GridLayout(1, 2)); // Two columns: left for image, right for configuration and logs
        frame.add(containerPanel); 
        frame.add(rightPanel);     

        frame.setVisible(true);

        // Action listener for the start/stop server button
        toggleServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerRunning) {
                    // Stop the server
                    if (udpReceiver != null) {
                        udpReceiver.stopServer();
                        logMessage("Server stopped.");
                    }
                    toggleServerButton.setText("Start Server");
                    isServerRunning = false;
                } else {
                    // Start the server
                    String ip = ipField.getText();
                    int port = Integer.parseInt(portField.getText());
                    logMessage("Starting server on IP " + ip + " and port " + port);

                    udpReceiver = new UdpReceiver(port, ImageDisplay.this);
                    udpReceiver.start();
                    toggleServerButton.setText("Stop Server");
                    isServerRunning = true;
                }
            }
        });
    }

    // Method to log messages to the log area
    public void logMessage(String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String timestamp = formatter.format(new Date());

        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // Auto-scroll to the latest log message
    }

    // Method to update the displayed image
    public void updateImage(byte[] imageData) {
        SwingUtilities.invokeLater(() -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
                // Read the image from the byte array
                BufferedImage image = ImageIO.read(bais);
                if (image != null) {
                    // Calculate the aspect ratio to maintain the original proportions
                    int originalWidth = image.getWidth();
                    int originalHeight = image.getHeight();
                    double aspectRatio = (double) originalWidth / originalHeight;
    
                    // Set new dimensions for the scaled image
                    int newWidth = 640;
                    int newHeight = (int) (newWidth / aspectRatio);
    
                    // Ensure the height does not exceed the panel height
                    if (newHeight > 720) {
                        newHeight = 720;
                        newWidth = (int) (newHeight * aspectRatio);
                    }
                    // Scale the image smoothly
                    Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                    label.repaint();
                } else {
                    logMessage("Failed to read image.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logMessage("Error updating image: " + e.getMessage());
            }
        });
    }
}