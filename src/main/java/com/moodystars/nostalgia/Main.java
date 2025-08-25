package com.moodystars.nostalgia;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            NostalgiaGUI gui = new NostalgiaGUI();
            gui.setVisible(true);
        });
    }
}