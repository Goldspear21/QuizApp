package bhargav.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LeaderboardClient {

    /**
     * Sends the user's quiz results to the leaderboard server and receives the
     * updated leaderboard information.
     *
     * @param username       The username of the player.
     * @param score          The calculated score for the quiz.
     * @param timeTaken      The time taken to complete the quiz in seconds.
     * @param correctAnswers The number of correct answers.
     * @param serverIp       The IP address or hostname of the leaderboard server.
     * @param serverPort     The port number the leaderboard server is listening on.
     * @return A String containing the server's response (user's rank and top 3), or
     *         an error message if connection fails.
     */
    public String sendScoreToServer(String username, int score, int timeTaken, int correctAnswers, String serverIp,
            int serverPort) {
        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        String response = "Error: Could not connect to leaderboard server."; // Default error message

        try {
            // 1. Establish a connection to the server
            System.out.println("Attempting to connect to server at " + serverIp + ":" + serverPort);
            socket = new Socket(serverIp, serverPort);
            System.out.println("Connected to server.");

            // 2. Get input and output streams
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // 3. Send data to the server (command first, then data)
            out.writeUTF("SUBMIT_SCORE"); // Send the command
            out.writeUTF(username); // Then send the username
            out.writeInt(score);
            out.writeInt(timeTaken);
            out.writeInt(correctAnswers); // Send correct answers as well
            out.flush();
            System.out.println("Sent SUBMIT_SCORE command and data to server: Username=" + username + ", Score=" + score
                    + ", Time=" + timeTaken + "s, Correct=" + correctAnswers);
                    

            // 4. Receive the response from the server
            response = in.readUTF(); // Read the response string

            System.out.println("Received response from server:\n" + response);

        } catch (UnknownHostException e) {
            System.err.println("Error: Server IP address not found: " + serverIp);
            response = "Error: Server address not found (" + serverIp + ").";
            // e.printStackTrace(); // Uncomment for debugging
        } catch (IOException e) {
            System.err.println("Error connecting to or communicating with server: " + e.getMessage());
            response = "Error: Could not connect to leaderboard server. Is it running at " + serverIp + ":" + serverPort
                    + "?\n" + e.getMessage();
            // e.printStackTrace(); // Uncomment for debugging
        } finally {
            // 5. Close the streams and the socket
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("Connection to server closed.");
                }
            } catch (IOException e) {
                System.err.println("Error closing socket resources: " + e.getMessage());
                // e.printStackTrace(); // Uncomment for debugging
            }
        }
        return response; // Return the server's response or an error message
    }

    /**
     * Attempts to connect to the server and sends a "CONNECT" notification.
     *
     * @param username   The username of the player.
     * @param serverIp   The IP address or hostname of the leaderboard server.
     * @param serverPort The port number the leaderboard server is listening on.
     * @return A response string from the server (e.g., "CONNECTED" or an error
     *         message).
     * @throws IOException If a connection or communication error occurs.
     */
    public String connectAndNotify(String username, String serverIp, int serverPort) throws IOException {
        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        String response = "CONNECTION_FAILED"; // Default error response

        try {
            System.out.println("Client: Attempting initial connection to " + serverIp + ":" + serverPort);
            socket = new Socket(serverIp, serverPort);
            System.out.println("Client: Connected.");

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // --- Send the "CONNECT" command and username ---
            out.writeUTF("CONNECT"); // Send command first
            out.writeUTF(username); // Then send username
            out.flush();
            System.out.println("Client: Sent CONNECT message for user: " + username);

            // --- Receive acknowledgment from server ---
            response = in.readUTF(); // Server is expected to send back a confirmation (e.g., "CONNECTED")
            System.out.println("Client: Received initial response: " + response);

        } finally {
            // Close resources
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("Client: Connection for CONNECT message closed.");
                }
            } catch (IOException e) {
                System.err.println("Client: Error closing resources after CONNECT: " + e.getMessage());
            }
        }
        return response; // Return the received response
    }

    /**
     * Attempts to connect to the server and sends a "START_QUIZ" notification.
     *
     * @param username   The username of the player.
     * @param serverIp   The IP address or hostname of the leaderboard server.
     * @param serverPort The port number the leaderboard server is listening on.
     * @throws IOException If a connection or communication error occurs.
     */
    public void notifyQuizStarting(String username, String serverIp, int serverPort) throws IOException {
        Socket socket = null;
        DataOutputStream out = null;

        try {
            System.out.println("Client: Attempting to send START_QUIZ notification to " + serverIp + ":" + serverPort);
            socket = new Socket(serverIp, serverPort);
            System.out.println("Client: Connected for START_QUIZ notification.");

            out = new DataOutputStream(socket.getOutputStream());

            // --- Send the "START_QUIZ" command and username ---
            out.writeUTF("START_QUIZ"); // Send command first
            out.writeUTF(username); // Then send username
            out.flush();
            System.out.println("Client: Sent START_QUIZ message for user: " + username);

            // Note: This method doesn't expect a response back, just sends the
            // notification.
            // If you needed confirmation, you would add DataInputStream and readUTF().

        } finally {
            // Close resources
            try {
                if (out != null)
                    out.close();
                // No need to close 'in' as we don't read a response
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("Client: Connection for START_QUIZ notification closed.");
                }
            } catch (IOException e) {
                System.err.println("Client: Error closing resources after START_QUIZ: " + e.getMessage());
            }
        }
    }
}