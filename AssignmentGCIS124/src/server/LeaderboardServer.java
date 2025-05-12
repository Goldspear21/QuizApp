package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class LeaderboardEntry {
    String username;
    int score;
    int timeTaken; // in seconds
    int correctAnswers;

    // Constructor
    public LeaderboardEntry(String username, int score, int timeTaken, int correctAnswers) {
        this.username = username;
        this.score = score;
        this.timeTaken = timeTaken;
        this.correctAnswers = correctAnswers;
    }

    // Getters
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getTimeTaken() { return timeTaken; }
    public int getCorrectAnswers() { return correctAnswers; }

    // For saving to file and displaying (adjust format as needed)
    @Override
    public String toString() {
        return username + " - Score: " + score + " - Correct: " + correctAnswers + " - Time: " + timeTaken + "s";
    }

    // Method to parse a line from the file into an entry (will need robust parsing)
    public static LeaderboardEntry fromString(String line) {
        // Implement parsing logic here
        // This needs to handle the format "username - Score: X - Correct: Y - Time: Zs"
        try {
            String[] parts = line.split(" - ");
            String username = parts[0].trim();
            // Extract score, correct, and time using regex or careful splitting
            int score = extractInt(parts[1], "Score: ");
            int correct = extractInt(parts[2], "Correct: ");
            int timeTaken = extractInt(parts[3], "Time: ", "s");

            return new LeaderboardEntry(username, score, timeTaken, correct);
        } catch (Exception e) {
            System.err.println("Error parsing leaderboard line: " + line + " - " + e.getMessage());
            return null; // Or throw an exception
        }
    }

    // Helper for extracting integers from a string with a prefix and optional suffix
    private static int extractInt(String source, String prefix) {
        return extractInt(source, prefix, "");
    }

    private static int extractInt(String source, String prefix, String suffix) {
        String valueString = source.replace(prefix, "").replace(suffix, "").trim();
        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing integer from: " + source);
            return 0; // Default or handle error
        }
    }

}

public class LeaderboardServer {

    private static final int PORT = 12345; // Choose a port number
    private static final String LEADERBOARD_FILE = "..//AssignmentGCIS124//src//server//resources//leaderboard.txt"; // File to store leaderboard
    private List<LeaderboardEntry> leaderboard; // In-memory representation of the leaderboard
    // Use a lock or synchronized block for thread-safe file access
    private final Object fileLock = new Object();

    // ExecutorService for managing client threads
    private ExecutorService clientThreadPool;

    public LeaderboardServer() {
        leaderboard = loadLeaderboard();
        // Create a thread pool to handle incoming client connections
        clientThreadPool = Executors.newCachedThreadPool(); // Or fixed size: Executors.newFixedThreadPool(10);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Leaderboard Server started on port " + PORT);

            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Handle the client connection in a separate thread
                clientThreadPool.submit(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Shutdown the thread pool when the server stops
            clientThreadPool.shutdown();
        }
    }

    // Load leaderboard data from the file
    private List<LeaderboardEntry> loadLeaderboard() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            // Create the file if it doesn't exist
            try {
                file.createNewFile();
                System.out.println("Created new leaderboard file: " + LEADERBOARD_FILE);
            } catch (IOException e) {
                System.err.println("Error creating leaderboard file: " + e.getMessage());
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
                System.err.println("Error loading leaderboard from file: " + e.getMessage());
            }
        }
        // Sort loaded entries
        Collections.sort(entries, Comparator.comparing(LeaderboardEntry::getScore).reversed()
                .thenComparing(LeaderboardEntry::getTimeTaken)); // Sort by score DESC, then time ASC
        return entries;
    }

    // Save leaderboard data to the file
    private void saveLeaderboard() {
        synchronized (fileLock) { // Synchronize access to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
                for (LeaderboardEntry entry : leaderboard) {
                    writer.write(entry.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error saving leaderboard to file: " + e.getMessage());
            }
        }
    }

    // Inner class to handle individual client connections
    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    // Use Data streams for simplicity if just sending primitives and strings
                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                // 1. Receive data from the client
                String username = in.readUTF(); // Read username
                int score = in.readInt(); // Read score
                int timeTaken = in.readInt(); // Read time taken
                 int correctAnswers = in.readInt(); // Read correct answers


                System.out.println("Received score from " + username + ": " + score + " in " + timeTaken + "s");

                // 2. Process the score and update the leaderboard
                int userRank;
                List<LeaderboardEntry> top3 = new ArrayList<>();

                // Perform operations on the shared leaderboard list and file thread-safely
                synchronized (fileLock) {
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
                    userRank = -1; // Default if not found (shouldn't happen if added)
                    for (int i = 0; i < leaderboard.size(); i++) {
                        if (leaderboard.get(i).getUsername().equalsIgnoreCase(username)) {
                            userRank = i + 1; // Rank is 1-based index + 1
                            break;
                        }
                    }

                    // Get the top 3 players
                    for (int i = 0; i < Math.min(leaderboard.size(), 3); i++) {
                        top3.add(leaderboard.get(i));
                    }
                } // End synchronized block

                // 3. Prepare and send the response back to the client
                StringBuilder response = new StringBuilder();
                if (userRank != -1) {
                    response.append("Your Rank: ").append(userRank).append(" / ").append(leaderboard.size()).append("\n");
                } else {
                    response.append("Could not determine your rank.\n");
                }

                response.append("--- Top 3 Players ---\n");
                if (top3.isEmpty()) {
                    response.append("Leaderboard is empty.\n");
                } else {
                    for (int i = 0; i < top3.size(); i++) {
                        LeaderboardEntry entry = top3.get(i);
                         response.append(i + 1).append(". ")
                                 .append(entry.getUsername()).append(" - Score: ").append(entry.getScore())
                                 .append(" - Time: ").append(entry.getTimeTaken()).append("s\n"); // Simplified format for display
                    }
                }

                out.writeUTF(response.toString()); // Send the response string back to the client
                out.flush();

                System.out.println("Sent response to " + username);

            } catch (IOException e) {
                System.err.println("Error handling client " + clientSocket.getInetAddress().getHostAddress() + ": " + e.getMessage());
                // e.printStackTrace(); // Uncomment for more detailed error
            } finally {
                try {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                        System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
                    }
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        LeaderboardServer server = new LeaderboardServer();
        server.start();
    }
}