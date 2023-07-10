package com.cicdez.animationcreator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class Saver {
    public static void save(File[] images, ImagePixelDensity density, int frameTime, boolean interpolate, Integer[] frames,
                            String folder, String name, JTextArea output) {
        try {
            saveTexture(Animation.createImages(images), density, folder, name);
            saveAnimation(frameTime, interpolate, frames, folder, name, output);
        } catch (Exception e) {
            output.setText(e.getMessage());
            output.setForeground(Color.RED);
            return;
        }
        output.setText("Successful create Texture and Animation!");
        output.setForeground(Color.GREEN);
    }
    
    private static void saveTexture(BufferedImage[] images, ImagePixelDensity density, String folder, String name)
            throws IOException {
        int width = density.size, height = density.size * images.length;
        BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = texture.createGraphics();
        for (int index = 0; index < images.length; index++) {
            graphics.drawImage(density.scale(images[index]), 0, index * density.size,
                    density.size, density.size, null);
        }
        ImageIO.write(texture, "png", new File(folder, name + ".png"));
    }
    
    private static final String
            ANIMATION_BASE =
                    "{\n" +
                    "\t\"animation\": {\n" +
                    "\t\t\"frametime\": %s\n" +
                    "\t}\n" +
                    "}\n",
            ANIMATION_INTERPOLATED =
                    "{\n" +
                    "\t\"animation\": {\n" +
                    "\t\t\"frametime\": %s,\n" +
                    "\t\t\"interpolate\": true\n" +
                    "\t}\n" +
                    "}\n",
            ANIMATION_FRAMES =
                    "{\n" +
                    "\t\"animation\": {\n" +
                    "\t\t\"frametime\": %s,\n" +
                    "\t\t\"frames\": %s\n" +
                    "\t}\n" +
                    "}\n",
            ANIMATION_INTERPOLATED_FRAMES =
                    "{\n" +
                    "\t\"animation\": {\n" +
                    "\t\t\"frametime\": %s,\n" +
                    "\t\t\"interpolate\": true,\n" +
                    "\t\t\"frames\": %s\n" +
                    "\t}\n" +
                    "}\n"
            ;
    private static void saveAnimation(int frameTime, boolean interpolate, Integer[] frames,
                                      String folder, String name, JTextArea output) {
        String text;
        if (frames == null) {
            if (interpolate) {
                text = String.format(ANIMATION_INTERPOLATED, frameTime);
            } else {
                text = String.format(ANIMATION_BASE, frameTime);
            }
        } else {
            String framesFormat = Arrays.stream(frames).map(String::valueOf)
                    .collect(Collectors.joining(", ", "[", "]"));
            if (interpolate) {
                text = String.format(ANIMATION_INTERPOLATED_FRAMES, frameTime, framesFormat);
            } else {
                text = String.format(ANIMATION_FRAMES, frameTime, framesFormat);
            }
        }
        
        File file = new File(folder, name + ".png.mcmeta");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            output.setText(e.getMessage());
            output.setForeground(Color.RED);
        }
    }
}
