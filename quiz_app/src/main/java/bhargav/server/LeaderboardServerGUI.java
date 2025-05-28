package bhargav.server;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean; // To safely stop the server thread
import java.util.concurrent.*;



public class LeaderboardServerGUI {

    private static final int PORT = 12345; // Choose a port number
    private static final String LEADERBOARD_FILE;
    private ConcurrentHashMap<String, ClientHandler> activeClientHandlers = new ConcurrentHashMap<>();
    static {
        String userHome = System.getProperty("user.home");
        // It's good practice to put application data in a subfolder.
        // This will create a folder like C:\Users\<username>\QuizAppServerData
        // (Windows)
        // or /home/<username>/QuizAppServerData (Linux/macOS)
        String appDataDir = userHome + File.separator + "QuizAppServerData";
        File directory = new File(appDataDir);
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }
        LEADERBOARD_FILE = appDataDir + File.separator + "leaderboard.txt";
    }

    private TextArea serverLogArea;
    private ListView<String> connectedClientsList;
    private ListView<String> leaderboardDisplayList;
    private Button startButton;
    private Button stopButton;
    private Label statusLabel;

    private ServerSocket serverSocket; // To hold the server socket instance for closing
    private ExecutorService clientThreadPool; // Thread pool for handling clients
    private List<LeaderboardEntry> leaderboard; // In-memory representation of the leaderboard
    private final Object fileLock = new Object(); // Lock for thread-safe file access
    private AtomicBoolean isServerRunning = new AtomicBoolean(false); // Thread-safe flag for server status
    private List<String> activeClients = Collections.synchronizedList(new ArrayList<>()); // Thread-safe list of
                                                                                          // connected clients

    public  Parent getRoot() {
        // Same exact code from your start(...) method, minus primaryStage and Scene setup

        serverLogArea = new TextArea("Server log:\n");
        serverLogArea.setEditable(false);
        serverLogArea.setWrapText(true);

        connectedClientsList = new ListView<>();
        connectedClientsList.setPlaceholder(new Label("No connected clients"));

        leaderboardDisplayList = new ListView<>();
        leaderboardDisplayList.setPlaceholder(new Label("Leaderboard is empty"));
        leaderboardDisplayList.setPrefHeight(150);

        startButton = new Button("Start Server");
        stopButton = new Button("Stop Server");
        stopButton.setDisable(true);

        statusLabel = new Label("Status: Server Stopped");

        VBox controlsBox = new VBox(10, startButton, stopButton);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setAlignment(Pos.CENTER);

        VBox statusBox = new VBox(10, statusLabel, controlsBox);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(10));

        VBox clientsBox = new VBox(5, new Label("Connected Clients:"), connectedClientsList);
        clientsBox.setPadding(new Insets(0, 10, 10, 10));

        VBox leaderboardBox = new VBox(5, new Label("Leaderboard Top Entries:"), leaderboardDisplayList);
        leaderboardBox.setPadding(new Insets(0, 10, 10, 10));

        HBox infoBoxes = new HBox(10, clientsBox, leaderboardBox);
        infoBoxes.setPadding(new Insets(0, 10, 10, 10));
        infoBoxes.setFillHeight(true);

        clientsBox.setPrefWidth(300);
        leaderboardBox.setPrefWidth(300);
        connectedClientsList.setPrefWidth(300);
        leaderboardDisplayList.setPrefWidth(300);

        BorderPane root = new BorderPane();
        root.setTop(statusBox);
        root.setCenter(new SplitPane(infoBoxes, new ScrollPane(serverLogArea)));

        leaderboard = loadLeaderboard();
        updateLeaderboardDisplay();

        startButton.setOnAction(_ -> startServer());
        stopButton.setOnAction(_ -> stopServer());

        clientThreadPool = Executors.newCachedThreadPool();

        return root;
    }

    // Method to append messages to the server log TextArea safely from any thread
    public void log(String message) {
        Platform.runLater(() -> {
            serverLogArea.appendText(message + "\n");
            // Optional: auto-scroll to bottom
            serverLogArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    // Method to update the connected clients list safely from any thread
    public void updateConnectedClients() {
        Platform.runLater(() -> {
            // Create a new list from the synchronized list to avoid
            // ConcurrentModificationException
            connectedClientsList.getItems().setAll(new ArrayList<>(activeClients));
        });
    }

    // Method to update the leaderboard display safely from any thread
    public void updateLeaderboardDisplay() { // Assuming LeaderboardEntry class is accessible
        Platform.runLater(() -> {
            leaderboardDisplayList.getItems().clear();
            // Get top entries from the current leaderboard state
            List<LeaderboardEntry> topEntries = getTopLeaderboardEntries(10); // Get top 10 for display

            if (topEntries != null) {
                for (LeaderboardEntry entry : topEntries) {
                    // Format the entry for display in the ListView
                    leaderboardDisplayList.getItems().add(
                            entry.getUsername() + " - Score: " + entry.getScore() + " - Time: " + entry.getTimeTaken()
                                    + "s");
                }
            }
        });
    }

    private void startServer() {
        if (isServerRunning.get()) {
            log("Server is already running.");
            return;
        }

        log("Attempting to start server...");
        statusLabel.setText("Status: Starting...");
        startButton.setDisable(true);
        stopButton.setDisable(false);
        isServerRunning.set(true);

        // Task to run the server's main loop in a background thread
        Task<Void> serverTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    serverSocket = new ServerSocket(PORT);
                    log("Server started successfully on Port " + PORT);
                    Platform.runLater(() -> statusLabel.setText("Status: Server Running on Port " + PORT)); // Update
                                                                                                            // status on
                                                                                                            // FX thread

                    while (isServerRunning.get() && !isCancelled()) {
                        log("Waiting for client connection...");
                        Socket clientSocket = serverSocket.accept();
                        String clientAddress = clientSocket.getInetAddress().getHostAddress();
                        log("Client connected: " + clientAddress);

                        // Handle the client connection in a separate thread from the pool
                        clientThreadPool.submit(new ClientHandler(clientSocket, LeaderboardServerGUI.this));
                    }

                } catch (IOException e) {
                    if (isServerRunning.get()) { // Only log as error if server was expected to be running
                        log("Server socket error: " + e.getMessage());
                        Platform.runLater(() -> statusLabel.setText("Status: Server Error"));
                    } else {
                        log("Server stopped."); // Expected exception when closing socket
                    }
                } finally {
                    // Clean up resources
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            log("Error closing server socket in finally block: " + e.getMessage());
                        }
                    }
                    clientThreadPool.shutdown(); // Shutdown the client handler pool
                    log("Server task finished.");
                    Platform.runLater(() -> statusLabel.setText("Status: Server Stopped"));
                    Platform.runLater(() -> {
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                    });
                    isServerRunning.set(false); // Ensure flag is false
                    activeClients.clear(); // Clear active clients list
                    updateConnectedClients(); // Update GUI
                }
                return null;
            }
        };

        // Start the server task in a new daemon thread
        Thread serverThread = new Thread(serverTask);
        serverThread.setDaemon(true); // Daemon threads don't prevent application exit
        serverThread.start();
    }

    private void stopServer() {
        if (!isServerRunning.get()) {
            log("Server is not running.");
            return;
        }

        log("Attempting to stop server...");
        Platform.runLater(() -> statusLabel.setText("Status: Stopping..."));
        startButton.setDisable(true);
        stopButton.setDisable(true);

        isServerRunning.set(false); // Signal the server task to stop

        // Close the server socket to break the accept() loop in the server task
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log("Error closing server socket: " + e.getMessage());
            }
        }

        // clientThreadPool.shutdownNow(); // Consider this for a more abrupt shutdown
        // if needed
        // The finally block in the serverTask will handle the shutdown of the thread
        // pool
    }

    // Load leaderboard data from the file (thread-safe)
    private List<LeaderboardEntry> loadLeaderboard() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            // Create the file if it doesn't exist
            try {
                file.createNewFile();
                log("Created new leaderboard file: " + LEADERBOARD_FILE);
            } catch (IOException e) {
                log("Error creating leaderboard file: " + e.getMessage());
            }
            return entries; // Return empty list if file was just created or error occurred
        }

        synchronized (fileLock) { // Synchronize access to the file
            try (BufferedReader reader = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LeaderboardEntry entry = LeaderboardEntry.fromString(line);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            } catch (IOException e) {
                log("Error loading leaderboard from file: " + e.getMessage());
            }
        }
        // Sort loaded entries
        Collections.sort(entries, Comparator.comparing(LeaderboardEntry::getScore).reversed()
                .thenComparing(LeaderboardEntry::getTimeTaken)); // Sort by score DESC, then time ASC
        return entries;
    }

    // Save leaderboard data to the file (thread-safe)
    private void saveLeaderboard() {
        synchronized (fileLock) { // Synchronize access to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
                for (LeaderboardEntry entry : leaderboard) {
                    writer.write(entry.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                log("Error saving leaderboard to file: " + e.getMessage());
            }
        }
    }

    // Process a new score and update the leaderboard (thread-safe)
    public String processScore(String username, int score, int timeTaken, int correctAnswers) {
        String response;
        synchronized (fileLock) { // Synchronize access to the shared leaderboard list and file
            // Load the latest state of the leaderboard before processing
            leaderboard = loadLeaderboard();

            // Remove existing entry for this user if any
            leaderboard.removeIf(entry -> entry.getUsername().equalsIgnoreCase(username));

            // Add the new entry
            LeaderboardEntry newEntry = new LeaderboardEntry(username, score, timeTaken, correctAnswers);
            leaderboard.add(newEntry);

            // Re-sort the leaderboard
            Collections.sort(leaderboard, Comparator.comparing(LeaderboardEntry::getScore).reversed()
                    .thenComparing(LeaderboardEntry::getTimeTaken));

            // Save the updated leaderboard back to the file
            saveLeaderboard();

            // Find the user's rank
            int userRank = -1;
            for (int i = 0; i < leaderboard.size(); i++) {
                if (leaderboard.get(i).getUsername().equalsIgnoreCase(username)) {
                    userRank = i + 1; // Rank is 1-based index + 1
                    break;
                }
            }

            // Prepare the response
            StringBuilder responseBuilder = new StringBuilder();
            if (userRank != -1) {
                responseBuilder.append("Your Rank: ").append(userRank).append(" / ").append(leaderboard.size())
                        .append("\n");
            } else {
                responseBuilder.append("Could not determine your rank.\n");
            }

            responseBuilder.append("--- Top 3 Players ---\n");
            List<LeaderboardEntry> top3 = getTopLeaderboardEntries(3); // Get top 3 for response
            if (top3.isEmpty()) {
                responseBuilder.append("Leaderboard is empty.\n");
            } else {
                for (int i = 0; i < top3.size(); i++) {
                    LeaderboardEntry entry = top3.get(i);
                    responseBuilder.append(i + 1).append(". ")
                            .append(entry.getUsername()).append(" - Score: ").append(entry.getScore())
                            .append(" - Time: ").append(entry.getTimeTaken()).append("s\n");
                }
            }
            response = responseBuilder.toString();
        } // End synchronized block

        return response;
    }

    // Get the top N leaderboard entries (thread-safe)
    private List<LeaderboardEntry> getTopLeaderboardEntries(int n) {
        synchronized (fileLock) {
            // Ensure the leaderboard is loaded before getting entries
            leaderboard = loadLeaderboard();
            int count = Math.min(leaderboard.size(), n);
            return new ArrayList<>(leaderboard.subList(0, count));
        }
    }

    // Inner class to handle individual client connections
    // Inner class to handle individual client connections
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final LeaderboardServerGUI gui;
        private String username = null;
        private volatile long lastHeartbeat = System.currentTimeMillis();
        private volatile boolean running = true;

        public ClientHandler(Socket socket, LeaderboardServerGUI gui) {
            this.clientSocket = socket;
            this.gui = gui;
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                 DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                while (running) {
                    if (in.available() > 0) {
                        String command = in.readUTF();
                        switch (command) {
                            case "CONNECT":
                                username = in.readUTF();
                                gui.activeClients.add(username);
                                gui.activeClientHandlers.put(username, this);
                                gui.updateConnectedClients();
                                out.writeUTF("CONNECTED");
                                out.flush();
                                gui.log("User connected: " + username);
                                break;
                            case "HEARTBEAT":
                                lastHeartbeat = System.currentTimeMillis();
                                break;
                            case "START_QUIZ":
                                username = in.readUTF();
                                gui.log("User started quiz: " + username);
                                break;
                            case "SUBMIT_SCORE":
                                username = in.readUTF();
                                int score = in.readInt();
                                int timeTaken = in.readInt();
                                int correctAnswers = in.readInt();
                                String response = gui.processScore(username, score, timeTaken, correctAnswers);
                                gui.updateLeaderboardDisplay();
                                out.writeUTF(response);
                                out.flush();
                                gui.log("Score submitted: " + username);
                                break;
                            case "DISCONNECT":
                                running = false;
                                break;
                            default:
                                gui.log("Unknown command: " + command);
                        }
                    } else {
                        // Heartbeat timeout check
                        if (System.currentTimeMillis() - lastHeartbeat > 15000) {
                            gui.log("Heartbeat timeout: " + username);
                            running = false;
                        }
                        Thread.sleep(500);
                    }
                }
            } catch (IOException | InterruptedException e) {
                gui.log("Client error: " + (username != null ? username : clientSocket.getInetAddress()) + " - " + e.getMessage());
            } finally {
                if (username != null) {
                    gui.activeClients.remove(username);
                    gui.activeClientHandlers.remove(username);
                    gui.updateConnectedClients();
                    gui.log("User disconnected: " + username);
                }
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
        }
    }

}
