package bhargav.quiz;

import bhargav.client.LeaderboardClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.animation.PauseTransition;

/**
 * This class represents a graphical quiz application using JavaFX.
 * It displays quiz questions, tracks the timer, handles user input,
 * and shows the final score along with a leaderboard.
 */

public class QuizGUI extends Application {
    private int timeLeft = 120;
    private Label timerLabel = new Label();
    private Timeline timeline;
    private ProgressBar progressBar = new ProgressBar(1.0);
    private QuizQuestions quizQuestions = new QuizQuestions();
    private int questionIndex = 0;
    private int score = 0;
    private Question<?>[] selectedQuestions;
    private Label questionLabel = new Label();
    private VBox optionsBox = new VBox(10);
    private Stage primaryStage;
    private String username;
    private MediaPlayer backgroundMusic;
    private VBox quizLayout;
    private Label liveScoreLabel;
    private static final int TOTAL_TIME = 120;
    private boolean isPaused = false;
    private StackPane rootStack;
    private BorderPane quizWrapper;
    private StackPane root;
    private String serverIp = "localhost";
    private int serverPort = 12345;
    private boolean onlineMode = false;
    private ProgressIndicator loadingIndicator;
    private Label serverResponseLabel;

    /**
     * Starts the quiz application by displaying the username input screen.
     *
     * @param stage The primary stage for the JavaFX application.
     */

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username...");
        usernameField.setPrefWidth(300);
        usernameField.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-background-radius: 15px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-padding: 10px;" +
                        "-fx-border-color: white;" +
                        "-fx-background-color: rgba(255,255,255,0.15);" +
                        "-fx-text-fill: white;" +
                        "-fx-border-width: 2px;");

        usernameField.focusedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                usernameField.setStyle(
                        "-fx-font-size: 18px;" +
                                "-fx-font-family: 'Lucida Console';" +
                                "-fx-background-radius: 15px;" +
                                "-fx-border-radius: 15px;" +
                                "-fx-padding: 10px;" +
                                "-fx-border-color: #ffd6ff;" +
                                "-fx-background-color: rgba(255,255,255,0.15);" +
                                "-fx-text-fill: white;" +
                                "-fx-border-width: 2px;" +
                                "-fx-effect: dropshadow(three-pass-box, #ffd6ff, 5, 0.3, 0, 0);");
            } else {
                usernameField.setStyle(
                        "-fx-font-size: 18px;" +
                                "-fx-font-family: 'Lucida Console';" +
                                "-fx-background-radius: 15px;" +
                                "-fx-border-radius: 15px;" +
                                "-fx-padding: 10px;" +
                                "-fx-border-color: white;" +
                                "-fx-background-color: rgba(255,255,255,0.15);" +
                                "-fx-text-fill: white;" +
                                "-fx-border-width: 2px;" +
                                "-fx-effect: none;");
            }
        });

        // Clear (X) button
        // Load your custom X icon
        ImageView clearIcon = new ImageView();
        try {
            Image iconImg = new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//xiconcircle.png");
            clearIcon.setImage(iconImg);
            clearIcon.setFitWidth(20);
            clearIcon.setFitHeight(20);
        } catch (Exception e) {
            System.out.println("Error loading X icon: " + e.getMessage());
        }

        // Create the button using the image
        Button clearButton = new Button();
        clearButton.setGraphic(clearIcon);
        clearButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 1px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, black, 4, 0.3, 1, 1);");

        // Clear the text field when clicked
        clearButton.setOnAction(_ -> usernameField.clear());

        // Container for username + X button
        HBox usernameFieldContainer = new HBox(5); // spacing between field and X
        usernameFieldContainer.setAlignment(Pos.CENTER);
        usernameFieldContainer.getChildren().addAll(usernameField, clearButton);

        Button startButton = new Button("Start Quiz (Offline)"); // Rename button for clarity

        // ... (keep existing startButton styling - adjust text if needed) ...
        // Base purple style
        startButton.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: purple;" + // Keep original color
                        "-fx-padding: 10px 30px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, black, 5, 0.3, 2, 2);");
        // Hover effect: neon-ish lavender glow
        startButton.setOnMouseEntered(_ -> startButton.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: #ffd6ff;" + // soft pinkish-lavender
                        "-fx-background-color: #8000ff;" + // rich purple
                        "-fx-padding: 10px 30px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-color: #ffd6ff;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, #ffd6ff, 10, 0.5, 0, 0);"));

        // Exit hover
        startButton.setOnMouseExited(_ -> startButton.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: purple;" + // Keep original color
                        "-fx-padding: 10px 30px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, black, 5, 0.3, 2, 2);"));

        // --- New "Play Online" Button ---
        Button playOnlineButton = new Button("Play Online");
        // Style similarly to the start button, maybe a different color hint
        playOnlineButton.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: darkblue;" + // Different color
                        "-fx-padding: 10px 30px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, black, 5, 0.3, 2, 2);");
        playOnlineButton.setOnMouseEntered(_ -> playOnlineButton.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: #add8e6;" + // Soft blue
                        "-fx-background-color: royalblue;" + // Richer blue
                        "-fx-padding: 10px 30px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-color: #add8e6;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, #add8e6, 10, 0.5, 0, 0);"));
        playOnlineButton.setOnMouseExited(_ -> playOnlineButton.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: darkblue;" + // Different color
                        "-fx-padding: 10px 30px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, black, 5, 0.3, 2, 2);"));

        playOnlineButton.setOnAction(_ -> showServerConnectDialog()); // Call a new method for the dialog

        try {
            stage.getIcons().add(new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//icon.jpeg"));
        } catch (Exception e) {
            System.out.println("Error loading icon: " + e.getMessage());
        }

        Label usernameLabel = new Label("(note: theres a two minute timer)");
        usernameLabel.setStyle("-fx-font-family: 'Lucida Console';-fx-text-fill: white; -fx-font-size: 20px;");

        ImageView welcomeImage = new ImageView(); // Load and display a welcome image
        try {
            Image welcome = new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//welcome.jpg"); // path to it
            welcomeImage.setImage(welcome);
            welcomeImage.setFitWidth(300); // size adjuster
            welcomeImage.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("error loading WELCOME image: " + e.getMessage());
        }

        Image alwaysComeBackImage = null; // Load the "I always come back" image

        try {
            alwaysComeBackImage = new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//always come back.png");
        } catch (Exception e) {
            System.out.println("Error loading 'I always come back' image: " + e.getMessage());
        }

        ImageView leftImage = new ImageView(alwaysComeBackImage); // left purple guy image (the flipped one)
        leftImage.setFitWidth(300);
        leftImage.setPreserveRatio(true);
        leftImage.setScaleX(-1); // flips horizantally

        ImageView rightImage = new ImageView(alwaysComeBackImage); // right purple guy image
        rightImage.setFitWidth(300);
        rightImage.setPreserveRatio(true);

        // --- Arrange Buttons in a VBox or HBox ---
        VBox buttonBox = new VBox(15); // Spacing between buttons
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startButton, playOnlineButton);

        VBox centerLayout = new VBox(10); // VBox for welcome image, username input, and buttons
        centerLayout.getChildren().addAll(welcomeImage, usernameLabel, usernameFieldContainer, buttonBox); // Add
                                                                                                           // buttonBox
                                                                                                           // here
        centerLayout.setStyle(
                "-fx-font-family: 'Lucida Console'; -fx-padding: 20px; -fx-alignment: center; -fx-font-size: 20px;");
        centerLayout.setId("centerLayout");

        // Load the background GIF
        ImageView backgroundGIF = new ImageView();
        try {
            Image bgGif = new Image("file:..//quiz_app//src//main//java//bhargav//resources//gifs//fnafsl.gif");
            backgroundGIF.setImage(bgGif);
            backgroundGIF.setFitWidth(1920); // Adjust if needed
            backgroundGIF.setPreserveRatio(true);
            backgroundGIF.setOpacity(0.8); // Optional for mood
        } catch (Exception e) {
            System.out.println("Error loading background GIF: " + e.getMessage());
        }

        // Compose the root layout with purple guys and center content
        BorderPane rootLayout = new BorderPane();
        rootLayout.setCenter(centerLayout); // Set the center layout containing everything
        rootLayout.setLeft(leftImage);
        rootLayout.setRight(rightImage);
        BorderPane.setAlignment(leftImage, Pos.CENTER_LEFT);
        BorderPane.setAlignment(rightImage, Pos.CENTER_RIGHT);
        BorderPane.setMargin(leftImage, new Insets(10));
        BorderPane.setMargin(rightImage, new Insets(10));

        root = new StackPane(); // assign to class-level variable
        root.getChildren().addAll(backgroundGIF, rootLayout); // same content

        Scene startScene = new Scene(root, 1280, 720); // fixed dimensions work well too

        // --- Modify startButton Action ---
        startButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            public void handle(javafx.event.ActionEvent event) {
                username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty!");
                    // Optional: Show an alert to the user
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Username cannot be empty!");
                    alert.showAndWait();
                    return;
                }
                onlineMode = false; // Set online mode to false for offline play
                startQuizFlow(); // Call a new method to start the quiz
            }
        });

        // Request focus on the scene itself to prevent any node from being initially
        // focused
        startScene.setOnMouseClicked(_ -> startScene.getRoot().requestFocus());

        // Initially focus the root after scene loads, so nothing is selected
        Platform.runLater(() -> startScene.getRoot().requestFocus());

        primaryStage.setScene(startScene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(false);
        primaryStage.setTitle("Quiz App");
        primaryStage.show();

    }

    // --- New method to show server connection dialog ---
    private void showServerConnectDialog() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Connect to Leaderboard Server");
        // Set icon for the dialog stage
        try {
            dialogStage.getIcons().add(new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//icon.jpeg"));
        } catch (Exception e) {
            System.out.println("Error loading icon for dialog stage: " + e.getMessage());
        }

        TextField ipField = new TextField("localhost"); // Default IP
        ipField.setPromptText("Server IP Address");
        TextField portField = new TextField("12345"); // Default Port
        portField.setPromptText("Server Port");

        Label ipLabel = new Label("Server IP:");
        Label portLabel = new Label("Port:");
        Label statusLabel = new Label(""); // Label to show connection status

        // Optional: Style the labels and fields similarly to your main UI
        ipLabel.setStyle("-fx-font-family: 'Lucida Console'; -fx-text-fill: black;");
        portLabel.setStyle("-fx-font-family: 'Lucida Console'; -fx-text-fill: black;");
        statusLabel.setStyle("-fx-font-family: 'Lucida Console'; -fx-text-fill: black;"); // Style for status
        ipField.setStyle("-fx-font-family: 'Lucida Console';");
        portField.setStyle("-fx-font-family: 'Lucida Console';");

        Button connectButton = new Button("Connect and Start Quiz");
        // Style the connect button
        connectButton.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: green;" +
                        "-fx-padding: 8px 15px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-cursor: hand;");
        connectButton.setOnMouseEntered(_ -> connectButton.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: lightgreen;" +
                        "-fx-background-color: darkgreen;" +
                        "-fx-padding: 8px 15px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, lightgreen, 5, 0.3, 0, 0);"));
        connectButton.setOnMouseExited(_ -> connectButton.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: green;" +
                        "-fx-padding: 8px 15px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-cursor: hand;"));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(ipLabel, ipField, portLabel, portField, statusLabel, connectButton); // Added statusLabel
        layout.setStyle("-fx-background-color: lightgrey;"); // Simple background for dialog

        Scene dialogScene = new Scene(layout);
        dialogStage.setScene(dialogScene);

        connectButton.setOnAction(_ -> {
            username = ((TextField) ((HBox) ((VBox) primaryStage.getScene().getRoot().lookup("#centerLayout"))
                    .getChildren().get(2)).getChildren().get(0)).getText().trim(); // Access username field from main
                                                                                   // scene
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty!");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Username cannot be empty!");
                alert.showAndWait();
                return;
            }

            serverIp = ipField.getText().trim();
            try {
                serverPort = Integer.parseInt(portField.getText().trim());
                onlineMode = true; // Set online mode to true

                // --- Attempt to connect to the server FIRST ---
                statusLabel.setText("Connecting to server..."); // Update status label
                connectButton.setDisable(true); // Disable button while connecting

                // Perform connection attempt in a background thread to keep UI responsive
                javafx.concurrent.Task<String> connectionTask = new javafx.concurrent.Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        LeaderboardClient client = new LeaderboardClient();
                        return client.connectAndNotify(username, serverIp, serverPort);
                    }
                };

                connectionTask.setOnSucceeded(_ -> {
                    String response = connectionTask.getValue();
                    Platform.runLater(() -> { // Update UI on FX thread
                        if ("CONNECTED".equals(response)) {
                            statusLabel.setText("Connected!");
                            dialogStage.close(); // Close the dialog on successful connection
                            startQuizFlow(); // Start the quiz with online mode enabled
                        } else {
                            statusLabel.setText("Connection Failed: " + response); // Show server error
                            statusLabel.setStyle("-fx-font-family: 'Lucida Console'; -fx-text-fill: red;");
                            connectButton.setDisable(false); // Re-enable button
                        }
                    });
                });

                connectionTask.setOnFailed(_ -> {
                    Throwable exception = connectionTask.getException();
                    Platform.runLater(() -> { // Update UI on FX thread
                        statusLabel.setText("Connection Error: " + exception.getMessage()); // Show exception message
                        statusLabel.setStyle("-fx-font-family: 'Lucida Console'; -fx-text-fill: red;");
                        connectButton.setDisable(false); // Re-enable button
                        System.err.println("Connection task failed: " + exception.getMessage());
                        // exception.printStackTrace(); // Uncomment for debugging
                    });
                });

                // Start the connection task in a new thread
                new Thread(connectionTask).start();

            } catch (NumberFormatException e) {
                System.out.println("Invalid port number.");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Invalid port number. Please enter a number.");
                alert.showAndWait();
            }
        });

        dialogStage.show();
    }

    // --- New method to encapsulate quiz startup logic ---
    private void startQuizFlow() {
        try {
            // Load the music (same as before)
            String musicPath = new File("..//quiz_app//src//main//java//bhargav//resources//audio//FNAF Security Breach OST Daycare Theme.m4a")
                    .toURI().toString();
            Media media = new Media(musicPath);
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            backgroundMusic.play();

            // Load and select questions (same as before)
            quizQuestions.load("..//quiz_app//src//main//java//bhargav//resources//txt//questionsBase.txt");
            quizQuestions.select(10);
            List<Question<?>> questionsList = quizQuestions.getSelectedQuestions();
            selectedQuestions = questionsList.toArray(new Question<?>[0]);

            // Start the timer (same as before)
            timeLeft = 120; // Reset timer
            startTimer();

            // Show the first question (same as before)
            showfirstQuestion();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading questions or music.");
            // Optional: Show an error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not start the quiz. Error loading resources.");
            alert.showAndWait();
        }
    }

    private void showfirstQuestion() {
        if (questionIndex >= selectedQuestions.length) {
            showFinalScore(false);
            return;
        }

        Question<?> question = selectedQuestions[questionIndex];

        // 🟡 Question setup
        questionLabel.setText(question.getQuestion());
        questionLabel.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-font-weight: bold;");
        questionLabel.setWrapText(true);
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setMaxWidth(800);

        VBox questionBox = new VBox(questionLabel);
        questionBox.setStyle("-fx-background-color: black; -fx-padding: 20px; -fx-background-radius: 10px;");
        questionBox.setAlignment(Pos.CENTER);

        // 🟡 Options setup
        optionsBox.getChildren().clear();
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setSpacing(15);

        Object answers = question.getAllAnswers();
        if (answers instanceof String[]) {
            for (String option : (String[]) answers) {
                addOptionButton(option, question);
            }
        }

        // 🟡 Progress bar
        progressBar.setMaxWidth(300);

        quizLayout = new VBox(30);
        quizLayout.setAlignment(Pos.CENTER);
        quizLayout.getChildren().addAll(questionBox, optionsBox, progressBar);
        quizLayout.setStyle("-fx-padding: 30px; -fx-background-color: transparent;");
        quizLayout.setMaxWidth(900);
        quizLayout.setMaxHeight(Region.USE_COMPUTED_SIZE);

        // 🟢 Restore your original styled timer & score label
        liveScoreLabel = createLiveScoreLabel();
        // Styled score label
        // Styled score label
        liveScoreLabel.setFont(Font.font("Lucida Console", FontWeight.BOLD, 20));
        liveScoreLabel.setTextFill(Color.GOLD);
        liveScoreLabel.setStyle(
                "-fx-background-color: black; -fx-padding: 8px 16px; -fx-border-color: gold; -fx-border-width: 2px; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        // Timer box styling with rounded background
        Label timerBackground = new Label();
        timerBackground.setStyle(
                "-fx-background-color: black; -fx-border-color: gold; -fx-border-width: 2px; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        timerBackground.setPrefSize(120, 40);

        ImageView quizBackground = new ImageView();
        try {
            Image bgImage = new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//quizbackground.jpg");
            quizBackground.setImage(bgImage);
            quizBackground.setFitWidth(1920); // adjust to your window size
            quizBackground.setPreserveRatio(true);
            quizBackground.setOpacity(0.9); // optional fade for readability
        } catch (Exception e) {
            System.out.println("Error loading quiz background: " + e.getMessage());
        }

        // 🟡 Volume Slider setup
        Slider volumeSlider = new Slider(0, 1, 0.5); // Min, Max, Default
        volumeSlider.setPrefWidth(120);
        volumeSlider.setStyle(
                "-fx-background-color: black; -fx-padding: 5px; -fx-border-color: gold; -fx-border-width: 2px; -fx-border-radius: 8px;");

        // You'll need to handle the volume change in your music player logic
        volumeSlider.valueProperty().addListener((_, _, newValue) -> {
            if (backgroundMusic != null) {
                backgroundMusic.setVolume(newValue.doubleValue());
            }
        });

        timerLabel.setFont(Font.font("Lucida Console", FontWeight.BOLD, 18));
        timerLabel.setTextFill(Color.LIMEGREEN);
        timerLabel.setStyle("-fx-background-color: transparent;");

        StackPane styledTimer = new StackPane(timerBackground, timerLabel);
        styledTimer.setAlignment(Pos.CENTER);

        // Score and timer layout
        VBox scoreTimerVBox = new VBox(8, liveScoreLabel, styledTimer);
        scoreTimerVBox.setAlignment(Pos.TOP_RIGHT);

        HBox scoreTimerBox = new HBox(scoreTimerVBox);
        scoreTimerBox.setPadding(new Insets(10, 20, 10, 10));
        scoreTimerBox.setAlignment(Pos.TOP_RIGHT);
        scoreTimerBox.setStyle("-fx-background-color: transparent;");

        // 🔵 BorderPane to hold top & center
        quizWrapper = new BorderPane();
        quizWrapper.setTop(scoreTimerBox);
        quizWrapper.setCenter(quizLayout);
        quizWrapper.setStyle("-fx-background-color: transparent;");

        // 🟢 Pause icon setup
        ImageView pauseIcon = new ImageView(new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//pause-icon.png"));
        pauseIcon.setFitWidth(50);
        pauseIcon.setFitHeight(50);

        Button pauseButton = new Button();
        pauseButton.setGraphic(pauseIcon);
        pauseButton.setStyle("-fx-background-color: transparent;");
        pauseButton.setOnAction(_ -> togglePauseMenu());

        StackPane.setAlignment(pauseButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(pauseButton, new Insets(0, 20, 60, 0)); // move above taskbar

        // Always create a new rootStack when showing the first question/restarting
        // This ensures a fresh instance is used for the new scene.
        rootStack = new StackPane(); // Create a new StackPane instance every time

        rootStack.getChildren().addAll(quizBackground, quizWrapper, pauseButton);

        Scene quizScene = new Scene(rootStack, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(quizScene); // Set the new scene with the new rootStack
        primaryStage.setMaximized(true);

        // --- Unified Escape key handler for Pause/Resume ---
        quizScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                togglePauseMenu(); // togglePauseMenu handles pause/resume based on isPaused flag
                event.consume(); // Consume the event so it doesn't propagate further
            }
        });
    }

    /**
     * Displays the next quiz question.
     * If all questions are answered, it shows the final score.
     */

    private void showNextQuestion() {
        if (questionIndex >= selectedQuestions.length) {
            showFinalScore(false);
            return;
        }

        Question<?> question = selectedQuestions[questionIndex];
        questionLabel.setText(question.getQuestion());

        optionsBox.getChildren().clear();

        Object answers = question.getAllAnswers();
        if (answers instanceof String[]) {
            for (String option : (String[]) answers) {
                addOptionButton(option, question);
            }
        }
        if (quizLayout == null) {
            quizLayout = new VBox(30);
            quizLayout.setAlignment(Pos.CENTER);
            quizLayout.setStyle("-fx-background-color: transparent; -fx-padding: 30px;");

            quizWrapper.setStyle("-fx-background-color: transparent;"); // This removes the white bar from BorderPane

        }

        quizLayout.getChildren().setAll(createQuestionBox(question), optionsBox, progressBar);

    }

    /**
     * Toggles the pause menu on and off.
     * - If the pause menu is currently open, it resumes the quiz.
     * - If the pause menu is currently closed, it shows the pause menu.
     */

    private void togglePauseMenu() {
        if (isPaused) {
            resumeQuiz();
        } else {
            showPauseMenu();
        }
    }

    private void showPauseMenu() {
        if (isPaused)
            return;
        isPaused = true;

        if (timeline != null)
            timeline.pause();

        quizLayout.setEffect(new BoxBlur(10, 10, 3));

        Rectangle dim = new Rectangle(primaryStage.getWidth(), primaryStage.getHeight());
        dim.setFill(Color.rgb(0, 0, 0, 0.6));

        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPadding(new Insets(40));
        pauseMenu.setMaxWidth(400);
        pauseMenu.setMaxHeight(300);

        // Use GIF as background
        pauseMenu.setStyle(
                "-fx-background-image: url('file:..//quiz_app//src//main//java//bhargav//resources//gifs//pause.gif');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-radius: 20px;" +
                        "-fx-effect: dropshadow(gaussian, black, 15, 0.5, 0, 0);");
        Button resumeButton = createStyledPauseButton("Resume", this::resumeQuiz);

        Button restartButton = createStyledPauseButton("Restart", () -> {
            resumeQuiz();
            restartQuiz();
        });

        Button quitButton = createStyledPauseButton("Quit", () -> primaryStage.close());

        pauseMenu.getChildren().addAll(resumeButton, restartButton, quitButton);

        StackPane overlay = new StackPane(dim, pauseMenu);
        overlay.setId("pauseOverlay"); // Keep the ID for identification

        rootStack.getChildren().add(overlay);
    }

    /**
     * Creates a styled button with the given text and action.
     * - The button has a gold border and a white text color.
     * - The button is 15 pixels wide and 15 pixels high.
     * - The button has a rounded border and background.
     * - The button's background color is a shade of gold.
     * - The button's text color is white.
     * - The button's font is Lucida Console.
     *
     * @param text   The text to display on the button.
     * @param action The action to perform when the button is clicked.
     * @return The styled button.
     */

    private Button createStyledPauseButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFont(Font.font("Lucida Console", FontWeight.BOLD, 16));
        button.setStyle(
                "-fx-background-color: #3b0066;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 10 20;" +
                        "-fx-cursor: hand;");

        button.setOnMouseEntered(_ -> button.setStyle(
                "-fx-background-color: #8a2be2;" +
                        "-fx-text-fill: gold;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 10 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, gold, 6, 0.3, 0, 0);"));

        button.setOnMouseExited(_ -> button.setStyle(
                "-fx-background-color: #3b0066;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 10 20;" +
                        "-fx-cursor: hand;"));

        button.setOnAction(_ -> action.run());

        return button;
    }

    /**
     * Resumes the quiz after the pause menu is closed.
     * - Stops the timer and shows the quiz layout. The timer is restarted.
     * - Removes the pause overlay from the root stack.
     */
    private void resumeQuiz() {
        isPaused = false;
        if (timeline != null)
            timeline.play();
        quizLayout.setEffect(null);

        // Remove the pause overlay by its ID
        rootStack.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("pauseOverlay"));

        // Request focus back to the main quiz content or scene if needed
        // primaryStage.getScene().getRoot().requestFocus(); // Uncomment if needed
    }

    private VBox createQuestionBox(Question<?> question) {
        questionLabel = new Label(question.getQuestion());
        questionLabel.setWrapText(true);
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setTextAlignment(TextAlignment.CENTER);
        questionLabel.setMaxWidth(800);
        questionLabel.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: black;" +
                        "-fx-padding: 20px;" +
                        "-fx-background-radius: 10px;");

        VBox questionBox = new VBox(questionLabel);
        questionBox.setAlignment(Pos.CENTER);
        questionBox.setMaxWidth(820); // Or something just a bit more than your label
        questionBox.setStyle("-fx-background-color: transparent; -fx-padding: 0px;");

        return questionBox;
    }

    /**
     * Creates a button for each answer option.
     * If the selected option is correct, the score increases.
     *
     * @param option   The answer choice.
     * @param question The current quiz question.
     */

    private void addOptionButton(final String option, final Question<?> question) {
        Button optionButton = new Button(option);
        optionButton.setAlignment(Pos.CENTER);
        optionButton.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-color: #3b0066;" + // Deep indigo
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 1px;" +
                        "-fx-cursor: hand;");

        optionButton.setOnMouseEntered(_ -> {
            if (!optionButton.isDisabled()) {
                optionButton.setStyle(
                        "-fx-font-size: 18px;" +
                                "-fx-font-family: 'Lucida Console';" +
                                "-fx-padding: 10px 20px;" +
                                "-fx-background-radius: 10px;" +
                                "-fx-border-radius: 10px;" +
                                "-fx-background-color: #8a2be2;" +
                                "-fx-text-fill: gold;" +
                                "-fx-border-color: gold;" +
                                "-fx-border-width: 1px;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(three-pass-box, gold, 6, 0.3, 0, 0);");
            }
        });

        optionButton.setOnMouseExited(_ -> {
            if (!optionButton.isDisabled()) {
                optionButton.setStyle(
                        "-fx-font-size: 18px;" +
                                "-fx-font-family: 'Lucida Console';" +
                                "-fx-padding: 10px 20px;" +
                                "-fx-background-radius: 10px;" +
                                "-fx-border-radius: 10px;" +
                                "-fx-background-color: #3b0066;" +
                                "-fx-text-fill: white;" +
                                "-fx-border-color: white;" +
                                "-fx-border-width: 1px;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: none;");
            }
        });

        optionButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            public void handle(javafx.event.ActionEvent event) {
                boolean isCorrect = option.equalsIgnoreCase(question.getCorrectAnswer());

                if (isCorrect) {
                    optionButton.setStyle("""
                                -fx-background-color: #4CAF50;
                                -fx-text-fill: white;
                                -fx-font-size: 16px;
                                -fx-font-family: 'Lucida Console';
                                -fx-padding: 10px 20px;
                                -fx-background-radius: 8px;
                            """);
                    score++;
                    liveScoreLabel.setText("Score: " + score + " / " + selectedQuestions.length);
                } else {
                    // Highlight the selected wrong answer in red
                    optionButton.setStyle("""
                                -fx-background-color: #D32F2F;
                                -fx-text-fill: white;
                                -fx-font-size: 16px;
                                -fx-font-family: 'Lucida Console';
                                -fx-padding: 10px 20px;
                                -fx-background-radius: 8px;
                            """);

                    // Highlight the correct answer in green
                    for (javafx.scene.Node node : optionsBox.getChildren()) {
                        if (node instanceof Button) {
                            Button btn = (Button) node;
                            if (btn.getText().equalsIgnoreCase(question.getCorrectAnswer())) {
                                btn.setStyle("""
                                            -fx-background-color: #4CAF50;
                                            -fx-text-fill: white;
                                            -fx-font-size: 16px;
                                            -fx-font-family: 'Lucida Console';
                                            -fx-padding: 10px 20px;
                                            -fx-background-radius: 8px;
                                        """);
                                break;
                            }
                        }
                    }
                }

                // Disable all buttons to prevent further input
                for (javafx.scene.Node node : optionsBox.getChildren()) {
                    node.setDisable(true);
                }

                // Delay before next question
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(_ -> {
                    questionIndex++;
                    showNextQuestion();
                });
                pause.play();
            }
        });

        optionsBox.getChildren().add(optionButton);
    }

    /**
     * Updates the text color of the timer label based on the remaining time.
     * - Green if more than 60 seconds are left.
     * - Orange if between 31 and 60 seconds.
     * - Red if 30 seconds or less remain.
     */

    private void updateTextColor() {
        if (timeLeft > 60) {
            timerLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            progressBar.setStyle("-fx-accent: green;");
        } else if (timeLeft > 30) {
            timerLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
            progressBar.setStyle("-fx-accent: orange;");
        } else {
            timerLabel.setTextFill(javafx.scene.paint.Color.RED);
            progressBar.setStyle("-fx-accent: red;");
        }
    }

    /**
     * Starts the quiz timer with a countdown from the initial time limit.
     * - Ensures any existing timer is stopped before starting a new one.
     * - Updates the timer label and text color every second.
     * - Stops the timer and shows the final score when time reaches zero.
     */

    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }

        timerLabel.setText(formatTime(timeLeft));

        progressBar.setPrefWidth(300); // Optional: Adjust size
        progressBar.setStyle("-fx-accent: #4CAF50;"); // Green

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), _ -> {
            timeLeft--;
            timerLabel.setText(formatTime(timeLeft));
            updateTextColor();

            double progress = (double) timeLeft / 120;
            Platform.runLater(() -> progressBar.setProgress(progress));

            if (timeLeft <= 0) {
                timeline.stop();
                showFinalScore(true);
            }
        });

        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Restarts the quiz by resetting the timer, score, and question index.
     * - Stops any running timer to prevent conflicts.
     * - Stops and reloads the background music, ensuring it loops indefinitely.
     * - Resets the timer to its initial value.
     * - Selects a new set of questions for the quiz.
     * - Starts the timer and displays the first question.
     */

     private void restartQuiz() {
        if (timeline != null) {
            timeline.stop();
        }

        // Stop and reset music
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }

        String musicPath = new File("..//quiz_app//src//main//java//bhargav//resources//audio//FNAF Security Breach OST Daycare Theme.m4a").toURI()
                .toString();
        Media media = new Media(musicPath);
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();

        // Reset state
        questionIndex = 0;
        score = 0;
        timeLeft = 120;
        isPaused = false; // Ensure isPaused is false on restart
        quizLayout = null; // ✅ This ensures a fresh layout is created again

        // Load new questions
        quizQuestions.select(10);
        selectedQuestions = quizQuestions.getSelectedQuestions().toArray(new Question<?>[0]);

        // Restart timer and show quiz
        startTimer();
        showfirstQuestion();
    }

    /**
     * Displays the final score and leaderboard after the quiz ends.
     * - Stops the background music when the quiz finishes.
     * - Saves the player's score and loads the leaderboard from a file.
     * - Determines the player's rank based on the leaderboard.
     * - Creates a visually styled VBox to display the final score, rank, and top 5
     * leaderboard.
     * - If the timer runs out before the quiz ends, a "Time ran out!" message is
     * shown.
     * - Includes a "THE END" image at the bottom and Golden Freddy images on the
     * sides.
     * - Provides a "Play Again" button to restart the quiz.
     * - Updates the scene to display the final score screen.
     */
    private void showFinalScore(boolean timedOut) {
        if (timeline != null) {
            timeline.stop();
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            // Stop music when the quiz ends
        }

        // Calculate score and time taken
        int timeTaken = TOTAL_TIME - timeLeft;
        int calculatedScore = Math.max(0, score * 100 - timeTaken);
        int correctAnswers = score; // Store the raw correct answer count

        // Background Image Setup (same as before)
        ImageView backgroundImage = new ImageView();
        try {
            Image bg = new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//game_over.png");
            backgroundImage.setImage(bg);
            backgroundImage.setFitWidth(primaryStage.getWidth());
            backgroundImage.setFitHeight(primaryStage.getHeight());
            backgroundImage.setPreserveRatio(false);
            backgroundImage.setOpacity(0.9);
        } catch (Exception e) {
            System.out.println("Error loading game over background: " + e.getMessage());
        }

        // --- Conditional Logic for Online vs. Offline ---
        if (onlineMode) {
            // --- Online Mode: Send score to server and display server response ---

            // Prepare UI elements for loading and response
            loadingIndicator = new ProgressIndicator();
            loadingIndicator.setMaxSize(50, 50); // Adjust size
            // Optional: Style the loading indicator
            loadingIndicator.setStyle("-fx-progress-color: gold;");

            serverResponseLabel = new Label("Sending score to server...");
            serverResponseLabel.setStyle(
                    "-fx-font-family: 'Lucida Console';" +
                            "-fx-text-fill: gold;" +
                            "-fx-font-size: 18px;" +
                            "-fx-effect: dropshadow(gaussian, gold, 3, 0.5, 0, 0);");

            VBox statusBox = new VBox(10, loadingIndicator, serverResponseLabel);
            statusBox.setAlignment(Pos.CENTER);

            // Main layout VBox to hold status box and potentially other elements later
            VBox onlineLayout = new VBox(20, statusBox);
            onlineLayout.setAlignment(Pos.CENTER);
            onlineLayout.setPadding(new Insets(50)); // Add padding

            // BorderPane to hold content and background image
            BorderPane finalScoreLayout = new BorderPane();
            finalScoreLayout.setCenter(onlineLayout);

            // Set the scene immediately with loading indicator
            rootStack.getChildren().clear();
            rootStack.getChildren().addAll(backgroundImage, finalScoreLayout);
            primaryStage.getScene().setRoot(rootStack); // Ensure the scene is updated

            // --- Perform network call in a background thread ---
            // Using javafx.concurrent.Task is a good practice for background work
            javafx.concurrent.Task<String> sendScoreTask = new javafx.concurrent.Task<String>() {
                @Override
                protected String call() throws Exception {
                    LeaderboardClient client = new LeaderboardClient();
                    // Pass the correct answers count here
                    return client.sendScoreToServer(username, calculatedScore, timeTaken, correctAnswers, serverIp,
                            serverPort);
                }
            };

            // Define what happens when the task succeeds
            sendScoreTask.setOnSucceeded(_ -> {
                String serverResponse = sendScoreTask.getValue();
                // Update the UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    serverResponseLabel.setText(serverResponse); // Display the response
                    onlineLayout.getChildren().remove(loadingIndicator); // Remove loading indicator

                    // Add Play Again button
                    Button playAgainButton = new Button("Play Again");
                    // Style the button (reuse style from showFinalScore offline)
                    String playAgainNormalStyle = """
                            -fx-background-color: rgba(0, 0, 0, 0.7);
                            -fx-text-fill: white;
                            -fx-font-family: 'Lucida Console';
                            -fx-background-radius: 15;
                            -fx-border-radius: 15;
                            -fx-border-color: white;
                            -fx-border-width: 1;
                            -fx-padding: 10 20;
                            -fx-cursor: hand;
                            """;
                    String playAgainHoverStyle = """
                            -fx-background-color: rgba(30, 30, 30, 0.8);
                            -fx-text-fill: gold;
                            -fx-font-family: 'Lucida Console';
                            -fx-background-radius: 15;
                            -fx-border-radius: 15;
                            -fx-border-color: gold;
                            -fx-border-width: 1;
                            -fx-padding: 10 20;
                            -fx-cursor: hand;
                            -fx-effect: dropshadow(three-pass-box, gold, 5, 0.5, 0, 0);
                            """;
                    playAgainButton.setStyle(playAgainNormalStyle);
                    playAgainButton.setOnMouseEntered(_ -> playAgainButton.setStyle(playAgainHoverStyle));
                    playAgainButton.setOnMouseExited(_ -> playAgainButton.setStyle(playAgainNormalStyle));

                    playAgainButton.setOnAction(_ -> restartQuiz()); // Or go back to start scene

                    onlineLayout.getChildren().add(playAgainButton); // Add button below response
                    onlineLayout.setAlignment(Pos.CENTER); // Re-center layout
                });
            });

            // Define what happens if the task fails
            sendScoreTask.setOnFailed(_ -> {
                // Get the exception that occurred
                Throwable exception = sendScoreTask.getException();
                System.err.println("Task failed: " + exception.getMessage());
                // Update UI with an error message on the JavaFX Application Thread
                Platform.runLater(() -> {
                    serverResponseLabel
                            .setText("Failed to get leaderboard data from server: " + exception.getMessage());
                    serverResponseLabel
                            .setStyle("-fx-font-family: 'Lucida Console'; -fx-text-fill: red; -fx-font-size: 18px;");
                    onlineLayout.getChildren().remove(loadingIndicator); // Remove loading indicator

                    // Add a "Try Again" or "Play Offline" or "Back to Start" button
                    Button backButton = new Button("Back to Start");
                    backButton.setStyle("""
                            -fx-background-color: rgba(0, 0, 0, 0.7);
                            -fx-text-fill: white;
                            -fx-font-family: 'Lucida Console';
                            -fx-background-radius: 15;
                            -fx-border-radius: 15;
                            -fx-border-color: white;
                            -fx-border-width: 1;
                            -fx-padding: 10 20;
                            -fx-cursor: hand;
                            """);
                    backButton.setOnMouseEntered(_ -> backButton.setStyle("""
                            -fx-background-color: rgba(30, 30, 30, 0.8);
                            -fx-text-fill: gold;
                            -fx-font-family: 'Lucida Console';
                            -fx-background-radius: 15;
                            -fx-border-radius: 15;
                            -fx-border-color: gold;
                            -fx-border-width: 1;
                            -fx-padding: 10 20;
                            -fx-cursor: hand;
                            -fx-effect: dropshadow(three-pass-box, gold, 5, 0.5, 0, 0);
                            """));
                    backButton.setOnMouseExited(_ -> backButton.setStyle("""
                            -fx-background-color: rgba(0, 0, 0, 0.7);
                            -fx-text-fill: white;
                            -fx-font-family: 'Lucida Console';
                            -fx-background-radius: 15;
                            -fx-border-radius: 15;
                            -fx-border-color: white;
                            -fx-border-width: 1;
                            -fx-padding: 10 20;
                            -fx-cursor: hand;
                            """));

                    backButton.setOnAction(_ -> {
                        // Go back to the initial scene
                        Stage startStage = new Stage(); // Create a new stage for the start screen
                        start(startStage); // Call the start method to show the initial screen
                        primaryStage.close(); // Close the current quiz stage
                    });
                    onlineLayout.getChildren().add(backButton); // Add button
                });
            });

            // Run the task in a background thread
            new Thread(sendScoreTask).start();

        } else {
            // --- Offline Mode: Save locally and display local leaderboard (Keep existing
            // logic) ---
            saveScore(); // Save to local leaderboard.txt

            // Load leaderboard (local file)
            List<String> leaderboard = new ArrayList<>();
            try (Scanner scanner = new Scanner(new File("..//quiz_app//src//main//java//bhargav//resources//txt//leaderboard.txt"))) {
                while (scanner.hasNextLine()) {
                    leaderboard.add(scanner.nextLine());
                }
            } catch (IOException e) {
                System.out.println("Error reading local leaderboard.");
            }

            // Create the leaderboard pane (this already has its own background and border)
            VBox leaderboardPane = createLeaderboardPane(leaderboard);

            // Labels for Score and Rank (based on local data)
            Label scoreLabel = new Label("Quiz Over! Points: " + calculatedScore + " | Correct: " + score + "/"
                    + selectedQuestions.length + " | Time: " + timeTaken + "s");
            scoreLabel.setStyle(
                    "-fx-font-family: 'Lucida Console'; -fx-text-fill: gold; -fx-effect: dropshadow(gaussian, gold, 3, 0.5, 0, 0);");

            Label rankLabel = new Label();
            int rank = getUserRank(leaderboard); // Get rank from local data
            if (rank != -1) {
                rankLabel.setText("Your Local Rank: " + rank + " / " + leaderboard.size()); // Indicate local rank
            } else {
                rankLabel.setText("You are not currently ranked locally.");
            }
            rankLabel.setStyle(
                    "-fx-font-family: 'Lucida Console'; -fx-text-fill: gold; -fx-effect: dropshadow(gaussian, gold, 3, 0.5, 0, 0);");

            // VBox specifically for the score and rank labels with a background
            VBox scoreRankBox = new VBox(5); // Small spacing between score and rank
            scoreRankBox.setAlignment(Pos.CENTER);
            scoreRankBox.setMaxWidth(Region.USE_PREF_SIZE);
            scoreRankBox.setPadding(new Insets(15)); // Padding around the text
            scoreRankBox.setStyle(
                    "-fx-background-color: rgba(15, 15, 30, 0.8);" + // Semi-transparent dark background
                            "-fx-background-radius: 10px;" +
                            "-fx-border-color: #666666;" + // Subtle border
                            "-fx-border-width: 1px;" +
                            "-fx-border-radius: 10px;");

            if (timedOut) {
                Label timeOutLabel = new Label("Time ran out!");
                timeOutLabel.setStyle(
                        "-fx-font-family: 'Lucida Console'; -fx-text-fill: gold; -fx-effect: dropshadow(gaussian, gold, 3, 0.5, 0, 0)");
                scoreRankBox.getChildren().add(timeOutLabel); // Add timeout label here
            }
            scoreRankBox.getChildren().addAll(scoreLabel, rankLabel);

            // Main layout VBox containing the score/rank box and the leaderboard pane
            VBox scoreLayout = new VBox(20); // Increased spacing between score/rank and leaderboard
            scoreLayout.setPadding(new Insets(20)); // Padding around this main content area
            scoreLayout.setAlignment(Pos.TOP_CENTER); // Align content to top
            scoreLayout.setStyle(
                    "-fx-padding: 20px;" + // Padding around this main content area
                            "-fx-alignment: top-center;" + // Align content to top
                            "-fx-background-color: transparent;" + // Ensure no background here
                            "-fx-border-color: transparent;" + // Ensure no border here
                            "-fx-effect: dropshadow(gaussian, black, 10, 0.3, 0, 0);" + // Keep shadow effect if desired
                            "-fx-font-size: 18px;" // Your preferred font size
            );

            // Add the score/rank box and the leaderboard pane to the main scoreLayout
            scoreLayout.getChildren().addAll(scoreRankBox, leaderboardPane);

            // THE END image setup (same as before)
            ImageView endImageView = new ImageView();
            try {
                Image endImage = new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//the end.png");
                endImageView.setImage(endImage);
                endImageView.setFitWidth(200);
                endImageView.setPreserveRatio(true);
            } catch (Exception e) {
                System.out.println("Error loading the end image: " + e.getMessage());
            }

            // --- Play Again Button --- (same as before)
            Button playAgainButton = new Button("Play Again");
            playAgainButton.setFont(Font.font("Lucida Console", FontWeight.BOLD, 16));
            playAgainButton.setOnAction(_ -> restartQuiz());

            // Style for Play Again Button (Normal)
            String playAgainNormalStyle = """
                    -fx-background-color: rgba(0, 0, 0, 0.7);
                    -fx-text-fill: white;
                    -fx-font-family: 'Lucida Console';
                    -fx-background-radius: 15;
                    -fx-border-radius: 15;
                    -fx-border-color: white;
                    -fx-border-width: 1;
                    -fx-padding: 10 20;
                    -fx-cursor: hand;
                    """;
            // Style for Play Again Button (Hover)
            String playAgainHoverStyle = """
                    -fx-background-color: rgba(30, 30, 30, 0.8);
                    -fx-text-fill: gold;
                    -fx-font-family: 'Lucida Console';
                    -fx-background-radius: 15;
                    -fx-border-radius: 15;
                    -fx-border-color: gold;
                    -fx-border-width: 1;
                    -fx-padding: 10 20;
                    -fx-cursor: hand;
                    -fx-effect: dropshadow(three-pass-box, gold, 5, 0.5, 0, 0);
                    """;

            playAgainButton.setStyle(playAgainNormalStyle);
            playAgainButton.setOnMouseEntered(_ -> playAgainButton.setStyle(playAgainHoverStyle));
            playAgainButton.setOnMouseExited(_ -> playAgainButton.setStyle(playAgainNormalStyle));

            // --- Show Full Leaderboard Button --- (same as before, but maybe clarify it's
            // local)
            Button showLeaderboardButton = new Button("Show Full Local Leaderboard");
            showLeaderboardButton.setFont(Font.font("Lucida Console", FontWeight.BOLD, 16));
            showLeaderboardButton.setOnAction(_ -> displayFullLeaderboard(new Stage())); // This method still reads the
                                                                                         // local file

            // Style for Leaderboard Button (Normal) - Same as Play Again
            String leaderboardNormalStyle = playAgainNormalStyle; // Reuse the same style string

            // Style for Leaderboard Button (Hover) - Same as Play Again
            String leaderboardHoverStyle = playAgainHoverStyle; // Reuse the same style string

            showLeaderboardButton.setStyle(leaderboardNormalStyle);
            showLeaderboardButton.setOnMouseEntered(_ -> showLeaderboardButton.setStyle(leaderboardHoverStyle));
            showLeaderboardButton.setOnMouseExited(_ -> showLeaderboardButton.setStyle(leaderboardNormalStyle));

            HBox buttonBox = new HBox(15); // Changed to HBox for side-by-side buttons
            buttonBox.getChildren().addAll(playAgainButton, showLeaderboardButton);
            buttonBox.setAlignment(Pos.CENTER);

            // Main layout VBox containing the score/leaderboard content and the buttons
            VBox minilayout = new VBox(0); // Increased spacing between content and buttons
            minilayout.getChildren().addAll(scoreLayout, buttonBox);
            minilayout.setAlignment(Pos.TOP_CENTER); // Center the content horizontally
            minilayout.setPadding(new Insets(0, 20, 0, 20)); // Add horizontal padding

            // Main layout BorderPane
            BorderPane finalScoreLayout = new BorderPane();
            finalScoreLayout.setCenter(minilayout);
            BorderPane.setAlignment(endImageView, Pos.BOTTOM_CENTER);

            // No need to create a new StackPane, reuse root
            rootStack.getChildren().clear();
            rootStack.getChildren().addAll(backgroundImage, finalScoreLayout);
            primaryStage.getScene().setRoot(rootStack); // Ensure the scene is updated

        }
    }

    // New method to display the full leaderboard
    private void displayFullLeaderboard(Stage leaderboardStage) {
        // Load leaderboard data (keep existing logic)
        List<String> leaderboard = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("..//quiz_app//src//main//java//bhargav//resources//txt//leaderboard.txt"))) {
            while (scanner.hasNextLine()) {
                leaderboard.add(scanner.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading leaderboard.");
            // Optional: Show an error alert to the user
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Could not load leaderboard file.");
            errorAlert.showAndWait();
            return;
        }

        // --- Thematic Layout ---
        StackPane rootPane = new StackPane();

        // Background - Use a fitting image or GIF
        ImageView fullLeaderboardBg = new ImageView();
        try {
            // Example: using a static or glitch effect image
            Image bgImg = new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//static_background.png");
            fullLeaderboardBg.setImage(bgImg);
            fullLeaderboardBg.setFitWidth(primaryStage.getWidth()); // Cover full width of primary stage (or current
                                                                    // maximized stage)
            fullLeaderboardBg.setFitHeight(primaryStage.getHeight()); // Cover full height
            fullLeaderboardBg.setPreserveRatio(false); // Stretch to fill

            fullLeaderboardBg.setFitHeight(600); // Match scene height
            fullLeaderboardBg.setOpacity(0.8); // Adjust opacity as needed
            rootPane.getChildren().add(fullLeaderboardBg);
        } catch (Exception e) {
            System.out.println("Error loading full leaderboard background: " + e.getMessage());
            // Use a fallback color if image fails
            rootPane.setStyle("-fx-background-color: #101015;");
        }

        // Content VBox (similar styling to the main leaderboard pane)
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.TOP_CENTER); // Align content to the top
        contentBox.setStyle(
                "-fx-padding: 25px;" +
                        "-fx-background-color: rgba(10, 10, 25, 0.85);" + // Slightly more opaque background
                        "-fx-border-color: #444444;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 0px;" +
                        "-fx-background-radius: 0px;" +
                        "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0.1, 0, 0);");
        contentBox.setMaxSize(1000, Region.USE_COMPUTED_SIZE); // Increase max width, let height be computed

        // Title Label
        Label titleLabel = new Label("LEADERBOARD HALL OF FAME"); // More thematic title?
        titleLabel.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-text-fill: #CCCCCC;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-effect: dropshadow(gaussian, #AA0000, 2, 0.5, 0, 1);" +
                        "-fx-padding: 0 0 15 0;" // Padding at the bottom
        );
        contentBox.getChildren().add(titleLabel);

        // Scrollable Area for Leaderboard Entries
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); // Make content inside fit width
        scrollPane.setStyle(
                "-fx-background: transparent;" + // Make scrollpane background transparent
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: #444444;" + // Border for the scroll area
                        "-fx-border-width: 1;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hide horizontal scrollbar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show vertical if needed

        VBox leaderboardEntriesBox = new VBox(5); // Box to hold the rows inside scrollpane
        leaderboardEntriesBox.setStyle("-fx-background-color: transparent; -fx-padding: 10px;");

        // Add Header Row (using the same createRow method)
        leaderboardEntriesBox.getChildren().add(createRow("RANK", "NAME", "CORRECT", "TIME", "SCORE", true));
        // Add Divider
        javafx.scene.shape.Line divider = new javafx.scene.shape.Line(0, 0, 650, 0); // Adjust width
        divider.setStroke(Color.web("#444444"));
        divider.setStrokeWidth(1);
        VBox.setMargin(divider, new Insets(5, 0, 5, 0));
        leaderboardEntriesBox.getChildren().add(divider);

        // Add actual entries
        for (int i = 0; i < leaderboard.size(); i++) {
            String entry = leaderboard.get(i);
            int rankNum = i + 1;
            // --- Assuming extraction methods ---
            String name = entry.split(" - ")[0].trim();
            int scoreVal = extractScore(entry);
            int timeVal = extractTime(entry);
            int correctVal = extractCorrect(entry);
            // --- End Assumption ---
            boolean isUser = name.equalsIgnoreCase(username.trim()); // Highlight user here too
            leaderboardEntriesBox.getChildren().add(
                    createRow(String.valueOf(rankNum), name, String.valueOf(correctVal), timeVal + "s",
                            String.valueOf(scoreVal), false, isUser));
        }

        scrollPane.setContent(leaderboardEntriesBox);
        contentBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // Allow scrollpane to grow vertically

        // Close Button
        Button closeButton = new Button("CLOSE");
        // Style it like the results screen buttons (dark with white/gold)
        String closeButtonNormalStyle = """
                -fx-background-color: rgba(0, 0, 0, 0.7); -fx-text-fill: white; -fx-font-family: 'Lucida Console';
                -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: white;
                -fx-border-width: 1; -fx-padding: 8 18; -fx-cursor: hand; -fx-font-size: 14px;
                """;
        String closeButtonHoverStyle = """
                -fx-background-color: rgba(30, 30, 30, 0.8); -fx-text-fill: gold; -fx-font-family: 'Lucida Console';
                -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: gold;
                -fx-border-width: 1; -fx-padding: 8 18; -fx-cursor: hand; -fx-font-size: 14px;
                -fx-effect: dropshadow(three-pass-box, gold, 5, 0.5, 0, 0);
                """;
        closeButton.setStyle(closeButtonNormalStyle);
        closeButton.setOnMouseEntered(_ -> closeButton.setStyle(closeButtonHoverStyle));
        closeButton.setOnMouseExited(_ -> closeButton.setStyle(closeButtonNormalStyle));
        // Action to close this specific window (Stage)
        closeButton.setOnAction(_ -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
        contentBox.getChildren().add(closeButton);
        VBox.setMargin(closeButton, new Insets(15, 0, 0, 0)); // Add margin above the button

        // Add content box to the root pane
        rootPane.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER); // Center the content box

        // Create a new scene and stage for the popup
        Scene fullLeaderboardScene = new Scene(rootPane); // Fixed size for the popup window

        leaderboardStage.setTitle("Leaderboard Hall of Fame");
        // Optional: Make it a utility window or modal
        // leaderboardStage.initModality(Modality.APPLICATION_MODAL);
        // leaderboardStage.initStyle(StageStyle.UTILITY);

        // Try setting the icon for the leaderboard window too
        try {
            leaderboardStage.getIcons().add(new Image("file:..//quiz_app//src//main//java//bhargav//resources//images//icon.jpeg"));
        } catch (Exception e) {
            System.out.println("Error loading icon for leaderboard stage: " + e.getMessage());
        }

        leaderboardStage.setScene(fullLeaderboardScene);
        leaderboardStage.setMaximized(true); // Maximize the leaderboard stage
        leaderboardStage.setFullScreen(false); // Ensure it's maximized, not full screen

        leaderboardStage.setResizable(false); // Prevent resizing
        leaderboardStage.show();
    }

    // New method to display the full leaderboard

    /**
     * Creates a leaderboard pane with the given leaderboard.
     * - The pane has a background color of rgba(0, 0, 0, 0.5).
     * - The pane has a padding of 20 pixels.
     * - The pane has a border color of white.
     * - The pane has a border width of 2 pixels.
     * - The pane has a border radius of 10 pixels.
     * - The pane has a background radius of 10 pixels.
     * - The pane has a title label with the text "HIGH SCORES".
     * - The pane has a header row with the text "RANK", "NAME", "CORRECT", "TIME",
     * and "SCORE".
     * - The pane has rows for each entry in the leaderboard, with the rank, name,
     * correct, time, and score.
     *
     * @param leaderboard A list of leaderboard entries, where each entry is
     *                    formatted as "username - score".
     * @return A VBox containing the leaderboard pane.
     */

    private VBox createLeaderboardPane(List<String> leaderboard) {
        VBox container = new VBox(15); // Increased spacing
        container.setStyle(
                // Use a darker, less translucent background that fits the theme better
                "-fx-background-color: rgba(20, 20, 40, 0.9);" + // This background remains for the leaderboard pane
                                                                 // itself
                        "-fx-padding: 30px;" + // More padding
                        // Keep the border style or make it more prominent
                        "-fx-border-color: #666666;" + // Slightly lighter border
                        "-fx-border-width: 3px;" + // Thicker border
                        "-fx-border-radius: 5px;" + // Slightly rounded corners
                        "-fx-background-radius: 5px;" + // Slightly rounded corners
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 15, 0.3, 0, 0);");
        container.setMaxWidth(1200); // Increase max width for a wider look
        container.setAlignment(Pos.TOP_CENTER); // Ensure content is top-aligned within container

        // Title - Maybe a dim red or static-like white
        Label title = new Label("LEADERBOARD HALL OF FAME"); // Use the more thematic title
        title.setStyle(
                "-fx-font-size: 36px;" + // Larger title
                        "-fx-text-fill: #FFFF00;" + // Bright Yellow/Gold
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-effect: dropshadow(gaussian, #FFA500, 5, 0.5, 0, 0);" // Gold glow effect
        );
        title.setAlignment(Pos.CENTER);
        // Add some bottom margin to the title
        VBox.setMargin(title, new Insets(0, 0, 15, 0));
        container.getChildren().add(title);

        // Header Row - Use the modified createRow
        HBox header = createRow("RANK", "NAME", "CORRECT", "TIME", "SCORE", true);
        container.getChildren().add(header);

        // Divider Line (Optional, adds structure)
        javafx.scene.shape.Line divider = new javafx.scene.shape.Line(0, 0, 1140, 0); // Adjusted width for wider
                                                                                      // container
        divider.setStroke(Color.web("#444444"));
        divider.setStrokeWidth(1);
        VBox.setMargin(divider, new Insets(5, 0, 5, 0)); // Add spacing around the line
        container.getChildren().add(divider);

        // Leaderboard Rows
        int displayCount = Math.min(leaderboard.size(), 10); // Limit to top 10 on this screen
        for (int i = 0; i < displayCount; i++) {
            String entry = leaderboard.get(i);
            int rank = i + 1;
            // --- Assuming your extraction methods work correctly ---
            String name = entry.split(" - ")[0].trim(); // Basic split, adjust if needed
            int scoreVal = extractScore(entry);
            int timeVal = extractTime(entry);
            int correctVal = extractCorrect(entry);
            // --- End Assumption ---

            boolean isUser = name.equalsIgnoreCase(username.trim());
            HBox row = createRow(String.valueOf(rank), name, String.valueOf(correctVal), timeVal + "s",
                    String.valueOf(scoreVal),
                    false, isUser);
            container.getChildren().add(row);
        }

        return container;
    }

    /**
     * Creates a row with the given values.
     * - The row has a padding of 15 pixels.
     * - The row has a background color of rgba(0, 0, 0, 0.5).
     * - The row has a border color of white.
     * - The row has a border width of 2 pixels.
     * - The row has a border radius of 5 pixels.
     * - The row has a background radius of 5 pixels.
     * - The row has a text color of white.
     * - The row has a font size of 16 pixels.
     * - The row has a font family of Lucida Console.
     * - The row has a font weight of bold.
     *
     * @param rank     The rank of the entry.
     * @param name     The name of the entry.
     * @param correct  The number of correct answers.
     * @param time     The time taken to answer the quiz.
     * @param score    The score of the entry.
     * @param isHeader Whether the row is a header row.
     * @return A HBox containing the row.
     */

    private HBox createRow(String rank, String name, String correct, String time, String score, boolean isHeader) {
        return createRow(rank, name, correct, time, score, isHeader, false);
    }

    private HBox createRow(String rank, String name, String correct, String time, String score, boolean isHeader,
            boolean highlight) {
        HBox row = new HBox(); // Remove default spacing, control with label padding/width
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 2, 0)); // Reduce vertical padding slightly

        String headerTextColor = "#FFFF00"; // Bright Yellow/Gold for header
        String normalTextColor = "#CCCCCC"; // Off-white/Light Grey for normal text
        String highlightTextColor = "#FFFF00"; // Bright Yellow for user highlight
        String userHighlightEffect = "dropshadow(gaussian, yellow, 5, 0.7, 0, 0)";

        String currentTextColor = isHeader ? headerTextColor : (highlight ? highlightTextColor : normalTextColor);
        String effect = highlight ? userHighlightEffect : "none";

        // Define preferred widths for columns (adjust as needed)
        double rankWidth = 100;
        double nameWidth = 100; // Give name more space
        double correctWidth = 100;
        double timeWidth = 100;
        double scoreWidth = 150; // Give score a bit more space

        // Helper lambda to create styled labels
        java.util.function.BiFunction<String, Double, Label> createLabel = (text, width) -> {
            Label label = new Label(text);
            label.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-font-family: 'Lucida Console';" +
                            "-fx-text-fill: " + currentTextColor + ";" +
                            "-fx-font-weight: " + (isHeader || highlight ? "bold;" : "normal;") + // Bold
                                                                                                  // header/highlight
                            "-fx-pref-width: " + width + "px;" + // Use specific widths
                            "-fx-alignment: "
                            + (text.matches("\\d+s?") || text.matches("\\d+") ? "CENTER_RIGHT;" : "CENTER_LEFT;") + // Right-align
                                                                                                                    // numbers
                            "-fx-padding: 0 5 0 5;" + // Add horizontal padding within label
            // Wrapped the ternary expression in parentheses
                            ((effect.equals("none") ? "" : "-fx-effect: " + effect + ";")));
            return label;
        };

        row.getChildren().addAll(
                createLabel.apply(rank, rankWidth),
                createLabel.apply(name, nameWidth),
                createLabel.apply(correct, correctWidth),
                createLabel.apply(time, timeWidth),
                createLabel.apply(score, scoreWidth));

        return row;
    }

    /**
     * Extracts the number of correct answers from the given entry.
     * - The entry is expected to be in the format "Correct: N", where N is an
     * integer.
     * - If the entry contains the correct number, the method returns the integer
     * value.
     * - If the entry does not contain the correct number, the method returns 0.
     *
     * @param entry The entry to extract the correct number from.
     * @return The number of correct answers, or 0 if the entry does not contain the
     *         correct number.
     */
    private int extractCorrect(String entry) {
        try {
            Pattern pattern = Pattern.compile("Correct:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(entry);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Creates a live score label with the current score and total questions.
     * - The label has a font size of 20 pixels.
     * - The label has a font family of Lucida Console.
     * - The label has a text color of white.
     * - The label has a font weight of bold.
     * - The label has a padding of 10 pixels.
     * - The label has a background color of rgba(0, 0, 0, 0.5).
     * - The label has a border color of white.
     * - The label has a border width of 2 pixels.
     * - The label has a border radius of 5 pixels.
     * - The label has a background radius of 5 pixels.
     *
     * @return A Label containing the live score.
     */
    private Label createLiveScoreLabel() {
        Label label = new Label("Score: 0 / 10");
        label.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Lucida Console';" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10px;" +
                        "-fx-background-color: rgba(0, 0, 0, 0.5);" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;");
        return label;
    }

    /**
     * Saves the user's score to the leaderboard file.
     * - Reads the existing leaderboard from the file.
     * - Adds the current user's score to the leaderboard.
     * - Sorts the leaderboard in descending order based on scores.
     * - Writes the updated leaderboard back to the file.
     * If the leaderboard file is missing or cannot be accessed, an error message is
     * printed.
     */

    private void saveScore() {
        int timeTaken = TOTAL_TIME - timeLeft; // Use the correct TOTAL_TIME constant
        int calculatedScore = Math.max(0, score * 100 - timeTaken);
        String newEntry = username + " - Score: " + calculatedScore + " - Correct: " + score + " - Time: " + timeTaken
                + "s";

        List<String> leaderboard = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("..//quiz_app//src//main//java//bhargav//resources//txt//leaderboard.txt"))) {
            while (scanner.hasNextLine()) {
                leaderboard.add(scanner.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading leaderboard.");
        }

        leaderboard.removeIf(entry -> {
            String entryName = entry.split("-")[0].trim().toLowerCase();
            return entryName.equals(username.trim().toLowerCase());
        });

        // Add new entry
        leaderboard.add(newEntry);

        // Sort leaderboard
        leaderboard.sort((a, b) -> {
            int scoreA = extractScore(a);
            int scoreB = extractScore(b);
            if (scoreA != scoreB)
                return Integer.compare(scoreB, scoreA);

            int timeA = extractTime(a);
            int timeB = extractTime(b);
            return Integer.compare(timeA, timeB); // Faster time wins tie
        });

        try (FileWriter writer = new FileWriter("..//quiz_app//src//main//java//bhargav//resources//txt//leaderboard.txt")) {
            for (String entry : leaderboard) {
                writer.write(entry + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving to leaderboard.");
        }

        System.out.println("Looking for matches to: " + username);
        for (String entry : leaderboard) {
            System.out.println("Entry: '" + entry + "'");
        }
    }

    /**
     * Extracts the time taken to answer the quiz from the given entry.
     * - The entry is expected to be in the format "Time: N seconds", where N is an
     * integer.
     * - If the entry contains the time, the method returns the integer value.
     * - If the entry does not contain the time, the method returns 0.
     *
     * @param entry The entry to extract the time from.
     * @return The time taken to answer the quiz, or 0 if the entry does not contain
     *         the time.
     */
    private int extractTime(String entry) {
        try {
            Pattern pattern = Pattern.compile("Time:\\s*(\\d+)s");
            Matcher matcher = pattern.matcher(entry);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extracts the score of the entry from the given entry.
     * - The entry is expected to be in the format "Score: N", where N is an
     * integer.
     * - If the entry contains the score, the method returns the integer value.
     * - If the entry does not contain the score, the method returns 0.
     *
     * @param entry The entry to extract the score from.
     * @return The score of the entry, or 0 if the entry does not contain the score.
     */
    private int extractScore(String entry) {
        try {
            Pattern pattern = Pattern.compile("Score:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(entry);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Determines the rank of the current user in the leaderboard.
     * This method iterates through the leaderboard list and checks if
     * any entry starts with the user's username. If found, it returns
     * the 1-based rank of the user; otherwise, it returns -1.
     *
     * @param leaderboard A list of leaderboard entries, where each entry is
     *                    formatted as "username - score".
     * @return The 1-based rank of the user if found, otherwise -1.
     */

    private int getUserRank(List<String> leaderboard) {
        int calculatedScore = Math.max(0, score * 100 - (TOTAL_TIME - timeLeft));

        for (int i = 0; i < leaderboard.size(); i++) {
            String entry = leaderboard.get(i);
            if (entry.toLowerCase().startsWith(username.toLowerCase() + " - ")) {
                int entryScore = extractScore(entry);
                if (entryScore == calculatedScore) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    /**
     * Formats the given time in seconds into a MM:SS string representation.
     * This method converts the total seconds into minutes and remaining seconds,
     * then returns a string formatted as "MM:SS", ensuring two-digit formatting
     * for both minutes and seconds.
     * 
     * @param seconds The total time in seconds.
     * @return A formatted string representing the time in "MM:SS" format.
     */

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}