package com.cicdez.animationcreator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AnimationCreatorScreen extends JFrame {
    public final int width = 610, height = 900;
    public final Map<String, String[]> imageExtensions = new HashMap<>();
    
    public final DefaultListModel<File> images = new DefaultListModel<>();
    public final JList<File> imagesList = new JList<>(images);
    
    public final JButton addImage = new JButton("Add Image"), removeImage = new JButton("Remove Image");
    public final JButton moveImageUp = new JButton("↑"), moveImageDown = new JButton("↓");
    
    public final JComboBox<ImagePixelDensity> pixelDensityChooser = new JComboBox<>(ImagePixelDensity.values());
    
    public final JSpinner frameTime = new JSpinner(new SpinnerNumberModel(5, 0, Integer.MAX_VALUE,
            1));
    public final JCheckBox interpolate = new JCheckBox("Interpolate");
    public final JCheckBox enableFramesArray = new JCheckBox("Enable Custom Frames");
    public final JTextArea framesArray = new JTextArea();
    
    public Animation animation;
    public final int textureSize = 256;
    public final BufferedImage animationTexture = new BufferedImage(textureSize, textureSize,
            BufferedImage.TYPE_INT_ARGB);
    public final JLabel animationTextureLabel = new JLabel();
    
    public final JButton playAnimation = new JButton("Play"), stopAnimation = new JButton("Stop");
    
    public final JTextField outputFolder = new JTextField(), outputName = new JTextField();
    public final JButton selectFolder = new JButton("...");
    public final JButton save = new JButton("Save");
    
    public final JTextArea output = new JTextArea();
    
    public AnimationCreatorScreen() {
        super("Animation Creator - 1.0");
        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fillExtensions();
        init();
    }
    
    public void init() {
        imagesList.setBounds(10, 10, 250, 400);
        
        addImage.setBounds(10, imagesList.getY() + imagesList.getHeight() + 10, 150, 25);
        addImage.addActionListener(e -> {
            FileSelectorScreen fileSelectorScreen = new FileSelectorScreen(imageExtensions, FileSelectorScreen.Mode.FILES,
                    true);
            fileSelectorScreen.defaultCancelListener();
            fileSelectorScreen.setOpenListener(ev -> {
                for (File file : fileSelectorScreen.getFiles()) {
                    addImage(file);
                }
                fileSelectorScreen.setVisible(false);
            });
            fileSelectorScreen.setVisible(true);
        });
        removeImage.setBounds(10, addImage.getY() + addImage.getHeight() + 5, 150, 25);
        removeImage.addActionListener(e -> {
            if (images.getSize() > 0) images.removeElementAt(imagesList.getSelectedIndex());
        });
        
        moveImageUp.setBounds(175, imagesList.getY() + imagesList.getHeight() + 10, 50, 30);
        moveImageUp.addActionListener(e -> {
            try {
                if (images.getSize() > 1) {
                    int upIndex = imagesList.getSelectedIndex() - 1;
                    File up = getFileAt(upIndex);
                    File thisFile = getFileAt(imagesList.getSelectedIndex());
                    setFileAt(imagesList.getSelectedIndex(), up);
                    setFileAt(upIndex, thisFile);
                    imagesList.setSelectedIndex(upIndex);
                }
            } catch (IndexOutOfBoundsException exception) {
                System.err.println(exception.getMessage());
            }
        });
        moveImageDown.setBounds(175, moveImageUp.getY() + moveImageUp.getHeight() + 5, 50, 30);
        moveImageDown.addActionListener(e -> {
            try {
                if (images.getSize() > 1) {
                    int downIndex = imagesList.getSelectedIndex() + 1;
                    File down = getFileAt(downIndex);
                    File thisFile = getFileAt(imagesList.getSelectedIndex());
                    setFileAt(imagesList.getSelectedIndex(), down);
                    setFileAt(downIndex, thisFile);
                    imagesList.setSelectedIndex(downIndex);
                }
            } catch (IndexOutOfBoundsException exception) {
                System.err.println(exception.getMessage());
            }
        });
        
        pixelDensityChooser.setBounds(10, moveImageDown.getY() + moveImageDown.getHeight() + 43, 100, 20);
        pixelDensityChooser.setSelectedIndex(ImagePixelDensity.x16.ordinal());
        
        frameTime.setBounds(100, pixelDensityChooser.getY() + pixelDensityChooser.getHeight() + 43, 50, 20);
        interpolate.setBounds(10, frameTime.getY() + frameTime.getHeight() + 5, 100, 20);
        framesArray.setBounds(10, interpolate.getY() + interpolate.getHeight() + 30, 250, 100);
        framesArray.setLineWrap(true);
        framesArray.setEnabled(false);
        enableFramesArray.setBounds(10, framesArray.getY() + framesArray.getHeight() + 5, 200, 20);
        enableFramesArray.addActionListener(e -> framesArray.setEnabled(enableFramesArray.isSelected()));
        
        animationTextureLabel.setBounds(300, 10, textureSize, textureSize);
        playAnimation.setBounds(300, animationTextureLabel.getY() + animationTextureLabel.getHeight() + 5,
                75, 20);
        playAnimation.addActionListener(e -> {
            if (animation == null) {
                this.animation = new Animation(this, getFiles(), getPixelDensity(), getFrameTime(),
                        isInterpolate(), getImageFrames());
                this.animation.start();
            }
        });
        stopAnimation.setBounds(playAnimation.getX() + playAnimation.getWidth() + 5, animationTextureLabel.getY() +
                animationTextureLabel.getHeight() + 5, 75, 20);
        stopAnimation.addActionListener(e -> {
            if (animation != null) {
                animation.stop();
                animation = null;
            }
        });
        
        outputFolder.setBounds(290, playAnimation.getY() + playAnimation.getHeight() + 43, 250, 20);
        selectFolder.setBounds(outputFolder.getX() + outputFolder.getWidth() + 5, playAnimation.getY() +
                playAnimation.getHeight() + 43, 40, 20);
        selectFolder.addActionListener(e -> {
            FileSelectorScreen fileSelectorScreen = new FileSelectorScreen(new HashMap<>(),
                    FileSelectorScreen.Mode.DIRECTORIES, false);
            fileSelectorScreen.defaultOpenListener(outputFolder);
            fileSelectorScreen.defaultCancelListener();
            fileSelectorScreen.setVisible(true);
        });
        outputName.setBounds(290, outputFolder.getY() + outputFolder.getHeight() + 30, 250, 20);
        save.setBounds(290, outputName.getY() + outputName.getHeight() + 5, 100, 20);
        save.addActionListener(e -> Saver.save(getFiles(), getPixelDensity(), getFrameTime(), isInterpolate(),
                getImageFrames(), outputFolder.getText(), outputName.getText(), output));
        output.setBounds(290, save.getY() + save.getHeight() + 10, 300, 100);
        output.setLineWrap(true);
        output.setBackground(this.getBackground());
        output.setEditable(false);
        
        
        this.add(Main.makeLine(275, 0, 3, 900, Color.BLACK));
        this.add(Main.makeLine(0, moveImageDown.getY() + moveImageDown.getHeight() + 10, 275, 3,
                Color.BLACK));
        this.add(Main.makeLine(0, pixelDensityChooser.getY() + pixelDensityChooser.getHeight() + 10, 275, 3,
                Color.BLACK));
        this.add(Main.makeLine(275, playAnimation.getY() + playAnimation.getHeight() + 5, 300, 3,
                Color.BLACK));
        
        this.add(imagesList);
        this.add(addImage);
        this.add(removeImage);
        this.add(moveImageUp);
        this.add(moveImageDown);
        
        this.add(Main.label("Pixel Density", 10, moveImageDown.getY() + moveImageDown.getHeight() + 18,
                100, 20));
        this.add(pixelDensityChooser);
        
        this.add(Main.label("Animation Properties", 10, pixelDensityChooser.getY() +
                        pixelDensityChooser.getHeight() + 18, 150, 20));
        this.add(Main.label("Frame Time:", 10, pixelDensityChooser.getY() +
                pixelDensityChooser.getHeight() + 43, 100, 20));
        this.add(frameTime);
        this.add(interpolate);
        this.add(Main.label("Frames (separate ','):", 10, interpolate.getY() + interpolate.getHeight() + 5,
                150, 20));
        this.add(enableFramesArray);
        this.add(framesArray);
        
        this.add(animationTextureLabel);
        this.add(playAnimation);
        this.add(stopAnimation);
        
        this.add(Main.label("Output Folder:", 290, playAnimation.getY() + playAnimation.getHeight() + 18,
                150, 20));
        this.add(outputFolder);
        this.add(Main.label("File Name:", 290, outputFolder.getY() + outputFolder.getHeight() + 5,
                150, 20));
        this.add(outputName);
        this.add(selectFolder);
        
        this.add(save);
        
        this.add(output);
    }
    
    public void fillExtensions() {
        imageExtensions.put("Image " + Arrays.stream(Main.IMAGE_EXTENSIONS).map(ext -> "*." + ext)
                .collect(Collectors.joining(", ", "(", ")")), Main.IMAGE_EXTENSIONS);
    }
    
    public File getFileAt(int index) {
        return images.elementAt(index);
    }
    public File[] getFiles() {
        return Collections.list(images.elements()).toArray(new File[0]);
    }
    public void setFileAt(int index, File file) {
        images.setElementAt(file, index);
    }
    
    public void addImage(File file) {
        if (file.canRead() && Main.isImage(file.getName())) {
            images.addElement(file);
            System.out.println("Add image '" + file + "'");
        }
    }
    
    public void setTexture(BufferedImage image) {
        Graphics2D graphics = animationTexture.createGraphics();
        graphics.clearRect(0, 0, textureSize, textureSize);
        graphics.drawImage(getPixelDensity().scale(image), 0, 0, textureSize, textureSize,
                null);
        animationTextureLabel.setIcon(new ImageIcon(animationTexture));
    }
    
    public ImagePixelDensity getPixelDensity() {
        return pixelDensityChooser.getItemAt(pixelDensityChooser.getSelectedIndex());
    }
    public int getFrameTime() {
        return (int) frameTime.getValue();
    }
    public boolean isInterpolate() {
        return interpolate.isSelected();
    }
    public Integer[] getImageFrames() {
        if (enableFramesArray.isSelected()) return Arrays.stream(framesArray.getText().split(","))
                .map(Integer::parseInt).toArray(Integer[]::new);
        else return null;
    }
}
