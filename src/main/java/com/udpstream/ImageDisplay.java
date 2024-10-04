package com.udpstream;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageDisplay {
    private JFrame frame;
    private JLabel label;

    public ImageDisplay() {
        frame = new JFrame("Received Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        label = new JLabel();
        frame.add(label);
        frame.setVisible(true);
    }

    public void updateImage(byte[] imageData) {
        SwingUtilities.invokeLater(() -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
                BufferedImage image = ImageIO.read(bais);
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                    label.repaint();
                    System.out.println("Image updated in the GUI.");
                } else {
                    System.out.println("Failed to read the image.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
