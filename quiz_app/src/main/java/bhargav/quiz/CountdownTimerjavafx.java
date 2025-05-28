package bhargav.quiz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CountdownTimerjavafx extends Application {

    private int timeLeft = 120;
    private Label timerLabel;
    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        timerLabel = new Label(formatTime(timeLeft));
        timerLabel.setFont(new Font("Arial", 40));
        updateTextColor();

        StackPane root = new StackPane();
        root.getChildren().add(timerLabel);

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timeLeft--;
                timerLabel.setText(formatTime(timeLeft));
                updateTextColor();

                if (timeLeft == 0) {
                    timerLabel.setText("Time's up!");
                    timeline.stop();
                }
            }
        };

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        primaryStage.setTitle("Countdown Timer");
        primaryStage.setScene(new Scene(root, 300, 150));
        primaryStage.show();
    }

    private void updateTextColor() {
        if (timeLeft > 60) {
            timerLabel.setTextFill(Color.GREEN);
        } else if (timeLeft > 30) {
            timerLabel.setTextFill(Color.ORANGE);
        } else {
            timerLabel.setTextFill(Color.RED);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
