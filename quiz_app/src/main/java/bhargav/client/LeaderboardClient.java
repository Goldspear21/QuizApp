package bhargav.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LeaderboardClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private HeartbeatThread heartbeatThread;

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
            int serverPort) throws IOException {
        if (socket == null || socket.isClosed())
            throw new IOException("Not connected");
        out.writeUTF("SUBMIT_SCORE");
        out.writeUTF(username);
        out.writeInt(score);
        out.writeInt(timeTaken);
        out.writeInt(correctAnswers);
        out.flush();
        return in.readUTF();
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
        if (socket != null && !socket.isClosed())
            return "ALREADY_CONNECTED";
        socket = new Socket(serverIp, serverPort);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        out.writeUTF("CONNECT");
        out.writeUTF(username);
        out.flush();
        String response = in.readUTF();
        // Start heartbeat
        heartbeatThread = new HeartbeatThread(out);
        heartbeatThread.start();
        return response;
    }

    class HeartbeatThread extends Thread {
        private final DataOutputStream out;
        private volatile boolean running = true;

        public HeartbeatThread(DataOutputStream out) {
            this.out = out;
        }

        public void stopHeartbeat() {
            running = false;
        }

        public void run() {
            try {
                while (running) {
                    out.writeUTF("HEARTBEAT");
                    out.flush();
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                System.out.println("Heartbeat stopped: " + e.getMessage());
            }
        }
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
        if (socket == null || socket.isClosed())
            throw new IOException("Not connected");
        out.writeUTF("START_QUIZ");
        out.writeUTF(username);
        out.flush();
    }

    /*
     * Disconnect from server, stop heartbeat, close resources. If the server is
     * not connected, this method does nothing.
     */
    public void disconnectFromServer() {
        try {
            if (out != null) {
                out.writeUTF("DISCONNECT");
                out.flush();
            }
        } catch (IOException ignored) {
        }
        if (heartbeatThread != null)
            heartbeatThread.stopHeartbeat();
        try {
            if (in != null)
                in.close();
        } catch (IOException ignored) {
        }
        try {
            if (out != null)
                out.close();
        } catch (IOException ignored) {
        }
        try {
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException ignored) {
        }
        socket = null;
        in = null;
        out = null;
        heartbeatThread = null;
    }
}