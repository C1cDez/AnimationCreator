package com.cicdez.animationcreator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum ImagePixelDensity {
    x4, x8, x16, x32, x64, x128, x256
    ;
    public final int size;
    
    ImagePixelDensity() {
        this.size = Integer.parseInt(name().substring(1));
    }
    
    public BufferedImage scale(BufferedImage original) {
        Image image = original.getScaledInstance(size, size, Image.SCALE_FAST);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        bufferedImage.createGraphics().drawImage(image, 0, 0, null);
        return bufferedImage;
    }
}
