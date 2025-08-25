package com.example.nostalgiamody;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainApp extends Application {

    private File currentVideo;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Nostalgia Mody Videos Generator - Starter");

        Label fileLabel = new Label("No file selected");
        Button chooseBtn = new Button("Choose Video");
        Button speedBtn = new Button("Speed / Slow");
        Button randomBtn = new Button("Random Remix");
        Button roundBtn = new Button("Add Round Mask");
        Button annoyingBtn = new Button("Annoying / Faster");
        TextField speedField = new TextField("1.5");
        speedField.setPrefWidth(60);
        ProgressBar progress = new ProgressBar(0);
        progress.setPrefWidth(300);

        chooseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select video file");
            currentVideo = chooser.showOpenDialog(primaryStage);
            if (currentVideo != null) {
                fileLabel.setText(currentVideo.getAbsolutePath());
            }
        });

        speedBtn.setOnAction(e -> {
            if (requireFile()) return;
            double mult = 1.0;
            try { mult = Double.parseDouble(speedField.getText()); } catch (Exception ex) { showAlert("Invalid speed"); return; }
            File out = new File(currentVideo.getParent(), "out_speed_" + System.currentTimeMillis() + ".mp4");
            FFmpegProcessor.changeSpeed(currentVideo, out, mult, (p) -> progress.setProgress(p));
            showAlert("Started speed transform: " + out.getAbsolutePath());
        });

        randomBtn.setOnAction(e -> {
            if (requireFile()) return;
            File out = new File(currentVideo.getParent(), "out_random_" + System.currentTimeMillis() + ".mp4");
            FFmpegProcessor.randomRemix(currentVideo, out, 6, 2.0, (p) -> progress.setProgress(p));
            showAlert("Started random remix: " + out.getAbsolutePath());
        });

        roundBtn.setOnAction(e -> {
            if (requireFile()) return;
            File out = new File(currentVideo.getParent(), "out_round_" + System.currentTimeMillis() + ".mp4");
            FFmpegProcessor.roundMask(currentVideo, out, (p) -> progress.setProgress(p));
            showAlert("Started round mask: " + out.getAbsolutePath());
        });

        annoyingBtn.setOnAction(e -> {
            if (requireFile()) return;
            File out = new File(currentVideo.getParent(), "out_annoying_" + System.currentTimeMillis() + ".mp4");
            FFmpegProcessor.annoyingFaster(currentVideo, out, (p) -> progress.setProgress(p));
            showAlert("Started annoying preset: " + out.getAbsolutePath());
        });

        HBox speedBox = new HBox(10, new Label("speed:"), speedField, speedBtn);
        speedBox.setPadding(new Insets(8));

        VBox root = new VBox(10);
        root.setPadding(new Insets(12));
        root.getChildren().addAll(chooseBtn, fileLabel, speedBox, randomBtn, roundBtn, annoyingBtn, progress);

        primaryStage.setScene(new Scene(root, 520, 260));
        primaryStage.show();
    }

    private boolean requireFile() {
        if (currentVideo == null) {
            showAlert("Please choose a video first.");
            return true;
        }
        return false;
    }

    private void showAlert(String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, text, ButtonType.OK);
        a.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}