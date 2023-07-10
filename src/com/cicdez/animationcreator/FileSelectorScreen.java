package com.cicdez.animationcreator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileSelectorScreen extends JFrame {
    private final JFileChooser chooser;
    
    public FileSelectorScreen(boolean multiple) {
        this(new HashMap<>(), Mode.FILES_AND_DIRECTORIES, multiple);
    }
    public FileSelectorScreen(Map<String, String[]> extensions, Mode mode, boolean multiple) {
        super("Select " + mode.name);
        this.setSize(800, 700);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.chooser = new JFileChooser();
        this.chooser.setFileSelectionMode(mode.mode);
        this.chooser.setMultiSelectionEnabled(multiple);
        createFilter(extensions);
        init();
    }
    
    public void init() {
        chooser.setBounds(5, 5, 750, 650);
        this.add(chooser);
    }
    private void createFilter(Map<String, String[]> extensions) {
        for (String description : extensions.keySet()) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions.get(description));
            this.chooser.setFileFilter(filter);
        }
    }
    
    public void setOpenListener(ActionListener listener) {
        this.chooser.addActionListener(event -> {
            if (event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                listener.actionPerformed(event);
            }
        });
    }
    public void setCancelListener(ActionListener listener) {
        this.chooser.addActionListener(event -> {
            if (event.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                listener.actionPerformed(event);
            }
        });
    }
    
    public void defaultOpenListener(JTextField field) {
        setOpenListener(event -> {
            field.setText(getFile().getAbsolutePath());
            this.setVisible(false);
        });
    }
    public void defaultCancelListener() {
        setCancelListener(event -> this.setVisible(false));
    }
    
    public JFileChooser getChooser() {
        return chooser;
    }
    public File getFile() {
        return chooser.getSelectedFile();
    }
    public File[] getFiles() {
        return chooser.getSelectedFiles();
    }
    
    public static enum Mode {
        FILES("File", JFileChooser.FILES_ONLY),
        DIRECTORIES("Directory", JFileChooser.DIRECTORIES_ONLY),
        FILES_AND_DIRECTORIES("File or Directory", JFileChooser.FILES_AND_DIRECTORIES)
        ;
        private final String name;
        private final int mode;
        
        Mode(String name, int mode) {
            this.name = name;
            this.mode = mode;
        }
    }
}
