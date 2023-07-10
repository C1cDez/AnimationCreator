package com.cicdez.animationcreator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Main {
    public static final String[] IMAGE_EXTENSIONS = {"png", "jpg", "jpeg"};
    public static final AnimationCreatorScreen SCREEN = new AnimationCreatorScreen();
    
    public static void main(String[] args) {
        SCREEN.setVisible(true);
    }
    
    public static JLabel makeLine(int x, int y, int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        JLabel label = new JLabel(new ImageIcon(image));
        label.setBounds(x, y, width, height);
        return label;
    }
    public static JLabel label(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        return label;
    }
    
    
    public static boolean isImage(String name) {
        for (String extension : IMAGE_EXTENSIONS) {
            if (name.endsWith(extension)) return true;
        }
        return false;
    }
}
