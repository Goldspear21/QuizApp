# QuizApp

![project-image](https://socialify.git.ci/Goldspear21/QuizApp/image?language=1&name=1&owner=1&pattern=Plus&stargazers=1&theme=Dark)

**Interactive quiz application using JavaFX incorporating multimedia elements (images, GIFs, sounds) and visual transitions to enhance user engagement and theme immersion (FNAF-inspired).**

---

## 📸 Project Screenshots

![screenshot1](https://res.cloudinary.com/dfio7wdjh/image/upload/v1747044092/Screenshot_2025-05-12_135938_mamo2t.png)  
![screenshot2](https://res.cloudinary.com/dfio7wdjh/image/upload/v1747044092/Screenshot_2025-05-12_140006_ncil4q.png)  
![screenshot3](https://res.cloudinary.com/dfio7wdjh/image/upload/v1747044086/Screenshot_2025-05-12_140017_la0pa9.png)  
![screenshot4](https://res.cloudinary.com/dfio7wdjh/image/upload/v1747044086/Screenshot_2025-05-12_140039_hwr2vb.png)  
![screenshot5](https://res.cloudinary.com/dfio7wdjh/image/upload/v1747044085/Screenshot_2025-05-12_140051_fftdqn.png)  

---

## 🧐 Features

- 🌐 Online/Offline mode using TCP/IP server and client  
- ⏸️ Pause menu with themed buttons  
- ⏱️ Countdown timer with animated color-changing progress bar  
- 🧠 10 randomized quiz questions  
- ✅ Correct answers highlighted in green; incorrect in red  
- 🎶 Background music with loop support  
- 🎨 Custom UI: fonts, outlines, responsive layout  
- 📜 Quiz layout adjusts for long text  
- 📊 Leaderboard system  
- 🔁 Replay button  
- 🔍 View full leaderboard  

---

## 🎮 FNaF-Themed JavaFX Quiz App

This is an interactive quiz app inspired by *Five Nights at Freddy’s*, featuring JavaFX animations, music, GIFs, and transitions. Experience an eerie, immersive quiz environment as you race against time to prove your trivia skills.

---

## 🚀 Getting Started

### 1. Open the Project

- Use **Visual Studio Code**  
- Go to `File > Open Folder...` to avoid duplicate JavaFX JAR issues

### 2. Navigate to `src` Folder

---

### 🖥️ Server Setup

1. Open `LeaderboardServerGUI.java`  
2. Run the file — it starts the leaderboard server on port `12345`  
3. The GUI allows you to:
   - Monitor client connections  
   - View leaderboard entries  

---

### 🎯 Client (Quiz App)

1. Run `QuizGUI.java`  
2. Enter your **username** and select a mode:
   - `Start Quiz (Offline)` — runs locally and saves score to a text file  
   - `Play Online` — connects to the server and submits score in real-time  

---

## 🌐 Online Mode Details

- Connects to server on port `12345`  
- Sends a `CONNECT` command to register user  
- At quiz end:
  - Submits score, completion time, and correct answers  
  - Receives ranked leaderboard from server  

---

## 💻 How to Run

1. Install **Java 21** (OpenJDK 23+ recommended)  
2. Install **JavaFX 21 SDK**  
3. Add all JavaFX `.jar` files to the `lib/` directory  
4. Set up `launch.json` and `tasks.json` inside `.vscode` folder to reference `lib`  
5. Press the green **Run** button or `Ctrl + F5` to launch  

---

## 📁 Project Structure

