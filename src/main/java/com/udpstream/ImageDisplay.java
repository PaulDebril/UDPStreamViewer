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
    private JLabel ipsValue;
    private JLabel imageSizeValue;
    private UdpReceiver udpReceiver;
    private JLabel serverStatus; // Added label for server status
    private boolean isServerRunning = false; 
    private JLabel bandwidthValue; // Label to show network usage


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
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER); 
        imagePanel.add(label, BorderLayout.CENTER);  

        // Right panel - Configuration, information, and logs
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
        // Server Status Label
        serverStatus = new JLabel("Status: Stopped");
        serverStatus.setForeground(Color.RED); // Initially set to red (Stopped)
        configPanel.add(new JLabel("Server Status:"));
        configPanel.add(serverStatus);


        rightPanel.add(configPanel, BorderLayout.NORTH);

        // Information panel for IPS and Image Size
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        // IPS label and field
        JLabel ipsLabel = new JLabel("IPS:");
        ipsValue = new JLabel("0");
        infoPanel.add(ipsLabel);
        infoPanel.add(ipsValue);

        // Image Size label and field
        JLabel imageSizeLabel = new JLabel("Image size:");
        imageSizeValue = new JLabel("0 x 0");
        infoPanel.add(imageSizeLabel);
        infoPanel.add(imageSizeValue);

        JLabel bandwidthLabel = new JLabel("Network usage (KB/s):");
        bandwidthValue = new JLabel("0");
        infoPanel.add(bandwidthLabel);
        infoPanel.add(bandwidthValue);

        rightPanel.add(infoPanel, BorderLayout.CENTER);

        // Log panel to display server logs
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(640, 240));

        // Title label for the log panel
        JLabel logTitle = new JLabel("Logs");
        logTitle.setFont(new Font("Arial", Font.BOLD, 18));
        logTitle.setHorizontalAlignment(SwingConstants.CENTER);
        logPanel.add(logTitle, BorderLayout.NORTH);

        // Text area to display log messages
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        rightPanel.add(logPanel, BorderLayout.SOUTH);

        // Set up the main frame layout
        frame.setLayout(new GridLayout(1, 2)); // Two columns: left for image, right for configuration, information, and logs
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
                    serverStatus.setText("Status: Stopped");
                    serverStatus.setForeground(Color.RED); // Set status to red when stopped
                    isServerRunning = false;
                } else {
                    // Start the server
                    String ip = ipField.getText();
                    int port = Integer.parseInt(portField.getText());
                    logMessage("Starting server on IP " + ip + " and port " + port);
        
                    udpReceiver = new UdpReceiver(port, ImageDisplay.this);
                    udpReceiver.start();
                    toggleServerButton.setText("Stop Server");
                    serverStatus.setText("Status: Running");
                    serverStatus.setForeground(Color.BLUE); // Set status to green when running
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
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // Method to update the IPS display
    public void updateIPS(int ips) {
        SwingUtilities.invokeLater(() -> {
            ipsValue.setText(String.valueOf(ips));
        });
    }


    public void updateBandwidth(double bandwidthKBps) {
        SwingUtilities.invokeLater(() -> {
            bandwidthValue.setText(String.format("%.2f KB/s", bandwidthKBps)); // Update the label with formatted bandwidth value
        });
    }
    
    // Method to update the displayed image
    public void updateImage(byte[] imageData) {
        SwingUtilities.invokeLater(() -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
                // Read the image from the byte array
                BufferedImage image = ImageIO.read(bais);
                if (image != null) {
                    // Update image size information
                    int originalWidth = image.getWidth();
                    int originalHeight = image.getHeight();
                    imageSizeValue.setText(originalWidth + " x " + originalHeight);

                    // Calculate the aspect ratio to maintain the original proportions
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
