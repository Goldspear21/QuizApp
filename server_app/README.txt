# 🏆 Leaderboard Server Application

This project provides a Java-based server for managing a quiz leaderboard, complete with a graphical user interface (GUI) for monitoring and control. The server accepts connections from quiz clients, processes score submissions, and maintains a persistent leaderboard.

---

## 🚀 Getting Started

### 📦 Prerequisites
- Java 8 or higher must be installed on your system.
- JavaFX SDK is included in the repository (`javafx-sdk-23.0.2/`).

### ▶️ Running the Server
To start the server, simply use the provided batch script:

    run_server.bat

This script launches the server with the required JavaFX modules. Once started, a GUI window will appear, allowing you to:
- Start/Stop the server
- Monitor connected clients
- View leaderboard status in real-time

---

## ✨ Features

- 🥇 **Leaderboard Management:** Accepts score submissions from clients and maintains a ranked leaderboard.
- 🔒 **Thread-Safe:** Handles multiple client connections concurrently using a thread pool.
- 🖥️ **GUI Monitoring:** JavaFX-based interface to view logs, connected clients, and top scores.
- 💾 **Persistent Storage:** Saves leaderboard data to a local file for durability across restarts.

---

## 📁 Project Structure

- `src/main/java/bhargav/server/LeaderboardServer.java`  
  → Core server logic (handles client connections, processes scores, updates leaderboard)

- `src/main/java/bhargav/server/LeaderboardServerGUI.java`  
  → JavaFX GUI to control server and display logs, connections, and leaderboard

- `src/main/java/bhargav/Server.java`  
  → Entry point to launch the GUI server

- `run_server.bat`  
  → Batch script to run the server with JavaFX module path set

- `javafx-sdk-23.0.2/`  
  → JavaFX SDK required for the GUI to run properly

---

## ⚙️ How It Works

1. **Start the Server:**  
   Click "Start Server" in the GUI to begin listening for client connections (default port: 12345).

2. **Client Commands:**  
   Clients connect and send commands like:
   - `CONNECT`
   - `START_QUIZ`
   - `SUBMIT_SCORE`

3. **Leaderboard Handling:**  
   The server updates scores, calculates rankings, and sends back the top 3 leaderboard entries.

4. **Monitoring:**  
   The GUI provides real-time logs, displays connected clients, and shows top leaderboard entries.

5. **Shutdown:**  
   Click "Stop Server" to safely shut down and disconnect all clients.

---

## 🗂️ File Locations

- **Leaderboard Data:**  
  Stored in:  
  `C:\Users\<your-username>\QuizAppServerData\leaderboard.txt`

- **Logs:**  
  Displayed in the GUI (not persisted to disk by default).

---

## 📄 License

This project uses JavaFX, which is distributed under the GPL v2 with the Classpath Exception.  
See: `javafx-sdk-23.0.2/legal/` for third-party license details.

---

## 💬 Support

For issues, suggestions, or questions, please open an issue in this repository.
OR Send feedback or bug reports [here](https://docs.google.com/forms/d/e/1FAIpQLScuwZ5qb5oEPw5Cla8N2WJ3LoLqTyW3o6K9kJdIoWH_GzzztA/viewform).
