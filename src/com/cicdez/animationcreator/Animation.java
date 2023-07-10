package com.cicdez.animationcreator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Animation {
    public final Thread animation;
    public final AnimationCreatorScreen screen;
    
    public final BufferedImage[] images;
    public final ImagePixelDensity density;
    public final int frameTime;
    public final boolean interpolate;
    public final Integer[] frames;
    
    public Animation(AnimationCreatorScreen screen, File[] images, ImagePixelDensity density, int frameTime,
                     boolean interpolate, Integer[] frames) {
        this.animation = new Thread(this::animate, "Animation" + next());
        this.screen = screen;
        this.density = density;
        this.images = createImages(images);
        this.frameTime = frameTime;
        this.interpolate = interpolate;
        this.frames = frames;
    }
    
    public static BufferedImage[] createImages(File[] files) {
        try {
            BufferedImage[] images = new BufferedImage[files.length];
            for (int index = 0; index < images.length; index++) {
                images[index] = ImageIO.read(files[index]);
            }
            return images;
        } catch (IOException e) {
            return new BufferedImage[0];
        }
    }
    
    public void start() {
        animation.start();
    }
    public void stop() {
        animation.interrupt();
    }
    
    public BufferedImage scale(BufferedImage image) {
        return density.scale(image);
    }
    
    public void animate() {
        int index = -1;
        while (true) {
            index++;
            if (frames == null || frames.length == 0) {
                index %= images.length;
                screen.setTexture(images[index]);
                screen.imagesList.setSelectedIndex(index);
            } else {
                index %= frames.length;
                screen.setTexture(images[frames[index]]);
                screen.imagesList.setSelectedIndex(frames[index]);
            }
            try {
                Thread.sleep(50L * frameTime);
            } catch (InterruptedException e) {
                threadInterrupted();
                break;
            }
        }
    }
    
    private void threadInterrupted() {
        if (frames == null || frames.length == 0) {
            screen.setTexture(images[0]);
            screen.imagesList.setSelectedIndex(0);
        }
        else {
            screen.setTexture(images[frames[0]]);
            screen.imagesList.setSelectedIndex(frames[0]);
        }
    }
    
    private static int threadCount = 0;
    private static int next() {
        return threadCount++;
    }
    public static Color colorInterpolation(Color c0, Color c1, double t) {
        return new Color(
                (int) (c0.getRed() + (c1.getRed() - c0.getRed()) * t),
                (int) (c0.getGreen() + (c1.getGreen() - c0.getGreen()) * t),
                (int) (c0.getBlue() + (c1.getBlue() - c0.getBlue()) * t)
        );
    }
    public static BufferedImage imageInterpolation(BufferedImage i0, BufferedImage i1, double t) {
        if (i0.getWidth() != i1.getWidth() || i0.getHeight() != i1.getHeight()) return null;
        BufferedImage image = new BufferedImage(i0.getWidth(), i0.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < i0.getWidth(); x++) {
            for (int y = 0; y < i0.getHeight(); y++) {
                Color c0 = new Color(i0.getRGB(x, y)), c1 = new Color(i1.getRGB(x, y));
                Color color = colorInterpolation(c0, c1, t);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }
}
