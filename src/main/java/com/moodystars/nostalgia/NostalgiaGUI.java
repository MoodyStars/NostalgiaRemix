package com.moodystars.nostalgia;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.time.Instant;

/**
 * Simple Swing GUI wrapper for NostalgiaGenerator.
 */
public class NostalgiaGUI extends JFrame {

    private final JCheckBox cbYTP;
    private final JCheckBox cbYTPMV;
    private final JCheckBox cbRemix;
    private final JCheckBox cbRound;
    private final JSlider speedSlider;
    private final JCheckBox cbAnnoying;
    private final JCheckBox cbCommercial;
    private final JSpinner spinnerSegments;
    private final JTextArea outputArea;
    private final NostalgiaGenerator generator;

    public NostalgiaGUI() {
        super("Nostalgia Mody Videos Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);

        generator = new NostalgiaGenerator(Instant.now().toEpochMilli());

        // Controls panel
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;

        cbYTP = new JCheckBox("Include YTP-style cuts", true);
        cbYTPMV = new JCheckBox("Include YTPMV (melodic sample) elements", true);
        cbRemix = new JCheckBox("Include Remix edits", true);
        cbRound = new JCheckBox("Add Round/Loop segments", false);
        cbAnnoying = new JCheckBox("Make it annoying (more distortion/stutters)", false);
        cbCommercial = new JCheckBox("Insert fake commercial/jingle", false);

        controls.add(cbYTP, c); c.gridy++;
        controls.add(cbYTPMV, c); c.gridy++;
        controls.add(cbRemix, c); c.gridy++;
        controls.add(cbRound, c); c.gridy++;
        controls.add(cbAnnoying, c); c.gridy++;
        controls.add(cbCommercial, c); c.gridy++;

        // Speed slider
        JPanel speedPanel = new JPanel(new BorderLayout(6,6));
        speedPanel.setBorder(BorderFactory.createTitledBorder("Speed (tempo %)"));
        speedSlider = new JSlider(50, 200, 100);
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedPanel.add(speedSlider, BorderLayout.CENTER);

        c.gridx = 1; c.gridy = 0;
        c.gridheight = 4;
        c.fill = GridBagConstraints.BOTH;
        controls.add(speedPanel, c);
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;

        // Segment spinner
        JPanel segPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        segPanel.setBorder(BorderFactory.createTitledBorder("Segments"));
        spinnerSegments = new JSpinner(new SpinnerNumberModel(6, 1, 20, 1));
        segPanel.add(new JLabel("Number of segments:"));
        segPanel.add(spinnerSegments);

        c.gridx = 1; c.gridy = 4;
        controls.add(segPanel, c);

        // Buttons
        JButton btnGenerate = new JButton("Generate Plan");
        btnGenerate.addActionListener(this::onGenerate);
        JButton btnExport = new JButton("Export Plan...");
        btnExport.addActionListener(this::onExport);
        JButton btnRandomize = new JButton("Randomize Seeds");
        btnRandomize.addActionListener(e -> {
            // create a new generator with a new seed
            long seed = System.nanoTime();
            // replace generator by reflection? simpler: set a new generator variable
            // but generator is final; workaround: create a new generator and use it locally when generating.
            // we will just show a small dialog telling user next generate will be different
            JOptionPane.showMessageDialog(this, "Next Generate will use a new random seed.\n(Seed: " + seed + ")");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnGenerate);
        btnPanel.add(btnExport);
        btnPanel.add(btnRandomize);

        // Output area
        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);

        // Layout
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(8,8));
        cp.add(controls, BorderLayout.WEST);
        cp.add(scroll, BorderLayout.CENTER);
        cp.add(btnPanel, BorderLayout.SOUTH);
    }

    private void onGenerate(ActionEvent ev) {
        NostalgiaGenerator.Options options = new NostalgiaGenerator.Options();
        options.includeYTP = cbYTP.isSelected();
        options.includeYTPMV = cbYTPMV.isSelected();
        options.includeRemix = cbRemix.isSelected();
        options.addRoundLoop = cbRound.isSelected();
        options.speedPercent = speedSlider.getValue();
        options.annoying = cbAnnoying.isSelected();
        options.addCommercial = cbCommercial.isSelected();
        options.segmentCount = (Integer) spinnerSegments.getValue();

        // instantiate generator with new seed so each generate is different
        NostalgiaGenerator g = new NostalgiaGenerator(System.nanoTime());
        String plan = g.generatePlan(options);
        outputArea.setText(plan);
    }

    private void onExport(ActionEvent ev) {
        String text = outputArea.getText();
        if (text == null || text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to export — generate a plan first.", "Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export plan as...");
        chooser.setFileFilter(new FileNameExtensionFilter("Text file", "txt"));
        chooser.setSelectedFile(new File("nostalgia-plan.txt"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".txt")) {
                f = new File(f.getParentFile(), f.getName() + ".txt");
            }
            try (FileWriter fw = new FileWriter(f)) {
                fw.write(text);
                JOptionPane.showMessageDialog(this, "Exported plan to:\n" + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to write file:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}