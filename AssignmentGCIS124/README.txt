                                                          @@@@@@@@@                                 
                                                          @@#@@@@@@@@@                              
                                                         @@@@@@@@@@@@@@                             
                                                         @@%@@@@@@@@@@@                             
                                    *: ..==*         @@@@@@@@@@@@@@@@@                              
                                   %=:    ..-%      @@@@@@%@@@@@@@@@@                               
                                  %=+       .=  %*::#@@@@@@@@@@@@@@@                                
                                  %-:      =. %#=.  -*#@@@@@@@@@@@@@@@                              
                                  %::.    =-.%%+::-:.. .-=%@@@@@@@@@@@                              
                                    @=-++.#@@%####=       .    .:.-#=                               
                                          @@%+**#%%=.               --.                             
                                          @@#+###+-=@@.              -+:                            
                                         @%%#=+#@@@@%.      @@@@%:  .:+*-                           
                                        @%%%##%@@@@@@@%.    :-=**##: :=#%%                          
                                        @%##*+%@@@@@@@@-.-@@@@@@@%:::=%  #                          
                                        @#*%#++%@@@@@@= -@@@@@@@@@ :+%@@                            
                                        @%%##%%****%@@@@@@@@@@@@@#.-=%@ .%                          
                                        @%#%%+       +@@*@##%@@@::=:=%@**+                          
                                        %@%@#                 .+=-:-#%@%%                           
                                       #%%@@%. ..              .***#@@                              
                                      @##@@@@%**+.   -*--:     :#%%%%                               
                                      @##%@@@@@@@*+#@@- .    . %%@%                                 
                                      @%##@@@@@@@@@@@@@%#%*++#: .*#                                 
                                   %+++%##%@@@@@@@@@@@@@@@@@@#- -%% %%                              
                            %*===*####%@#+%#@@@@@@@@@@@@@@@@+  =%++=-=+++#                          
                            *-.=+#@%##%%*=-*#@@@@@@@@@@@@@@%:.=+  =%+=-..=%                         
                          @#=-..-*@%##%%%#- .:@@@@@@@@@@@@+---+  .:*+  .-#%%                        
                         @%*++=*==%%##%%%%%%: :. :%@%@@#=*+-==:.  :=:. .--+%%                       
                       @#%#+=+-::-%#+#++##%@@@@+:::...    -=   :::-=:.   .:+%%                      
                      %*%#++=. . .%*:= :    :=##%@@@@@@@@#=     .-+-       .=%@                     
                     @%%%%#*-..  --      .-=     @@  :.          -#*- .=. ..-@@%                    
                    @%@@@#+==-+%%+   ....+%#=.   .%*            %%%@%*-===::-#%%@@                  
                    : @@@%####%@#+-..::.:::#=-.              #%##**=+: -:.+-+#%  @                  
                      %@@%####%%+=.   ++-=+=::::           .@@%%%%%*++=-:.:.-%@ @@                  
                    +  %#+%@@@@%%:  :==-..::-=+*.     :   @@@%%@#+-=##-.....-#% @@=                 
                     @#-:-*@@@%#*===-.  :  -*-##. :+.    @@@@%%%*+: .--.  :-*@@  @                  
                   @#:  *@@@@@%%#+-:**-=@..=--##=.=    .%@@@%%%==**-::-: .=+*#=% +                  
                  %+-  #@@@@@@%##==#- :@@+-++#+.      *%%%@%###::  -:  ..-+%%::-*@                  
        =----=+**%@@@:.@@@@@@@%#+--:  @--:=+*%+-     *%%%%@%++--.   .  :==*%:...:=%                 
       %:    :==-:=#@@@%@@@@@@%#*%@@@@*  .:=%**%%@@@@@@@@@@%%#@@@#    .++#%: %%-. :# :-*=::         
@#:@@@@%-*%@@%*=. .=%%@@@@@@@@@@@@@@@@@@@==#%+=:..%@@@@@@@@%% .* -@@-.-=*%+.  ++   -:.-%%+=+   ++:. 
@-@@@@@@%%%@@@%*-+=.=+%@@@@@@@@@%*@@@@%%@@#*+--:.=@@@@@@@@@@@ @*   @@#+*%%=:.      -=**#@%*=.    . -
@#@@@@@#=-##-=#-.++  :%@@  @%*=  . .:: +=++*%#:..+%%@@@@%%@%@#%%%###***%@@@*-.. %%###*#@@@@=:  :.:.=
 %@@@@@+:+#-.*+:.-+ *%@ %=-=+**=-=..:-++++++#%++*%@@@@%%#@%@@%%###*#%%%@@@@@@*%@@%#=+*#%#*=:  ::::+%
  @@@@@%#%#:*#+-.-#*@@@+     .*%=    .:@+-.:   +@@@@=  :::=@%+  .:-*@%+=:  :#@@@@#%+*+=-::..:=+++*% 
      @@@@%#%%#=.-*@@@%-...   *#=:..   =%##.    - @*-+:. .:@*.     .*%#-    :+ @@@@%*+*++++*#%%%%   
        @@@@@@@%#%@@@@%-::.  :%%=::-.  +@%=...  =@@*----:::@=..     *@@#:   .+    @@@@@@@@@@        
               @@@@  @%+==:.-=#%+=*=   +@%===-  : @%++==++*@*=-.   :%@%#=...-*                      



               
🎮 FNaF-Themed JavaFX Quiz App

Welcome to the FNaF Quiz App, an interactive, animated quiz application inspired by Five Nights at Freddy's. Built with JavaFX, this project includes rich UI elements, music, GIFs, and a themed pause menu — all designed to create a polished, spooky quiz experience!




🚀 Getting Started:

-> Open the project in Visual Studio Code via File > Open Folder... — this avoids duplicate JavaFX JAR issues.

-> Navigate to the src folder.

-> 🖥️ Server
    1. Open the `LeaderboardServerGUI.java`.
    2. Run it to start the leaderboard server on port `12345`.
    3. It displays a GUI to monitor:
        - Server status
        - Connected clients
        - Leaderboard entries

-> 🎯 Client (Quiz App)
  1. Run `QuizGUI.java`.
  2. Enter your username and choose between:
      - `Start Quiz (Offline)` — plays locally and stores scores in a text file.
      - `Play Online` — connects to the server and submits scores in real-time.

---

-> 🌐 Online Mode Details

        When in online mode:
            - The app connects to the server on port `12345`.
            - The app connects to the server and sends a `CONNECT` command.
            - Upon quiz completion, the app submits score, time, and correct answers to the server.
            - The server processes the data, ranks the user, and returns top leaderboard entries.


-> Press the Run button or use Ctrl + F5 to launch the quiz.

Note: Ensure you have Java 21 and JavaFX 21 properly set up. Place all JavaFX JARs in the lib directory and reference them in your launch.json and tasks.json.

## 🚀 How to Run





📁 Project Structure:
├── .vscode           # VSCode config files
├── audio             # Background music files
├── bin/Assignment2Quiz  # Compiled .class files
├── gifs              # Animated GIFs (pause menu, etc.)
├── images            # UI images (pause icon, backgrounds, etc.)
├── lib               # JavaFX JAR libraries
├── src/Assignment2Quiz  # Main package and quiz logic
├── src/App.java      # Entry point of the application
├── txt               # Quiz questions and leaderboard text files
├── videos            # Optional video files
└── README.md         # This file





💡 Features

-> 🧠 10-question quiz with randomized selection

-> ⏱️ Countdown timer with animated progress bar (changes color based on time)

-> 🎶 Background music that loops during the quiz

-> ⏸️ Pause menu triggered by ESC or pause button (bottom-right)

-> GIF background (pause.gif)

-> Themed buttons to Resume, Restart, or Quit

-> ✅ Correct answer highlights in green, incorrect in red — with styled button transitions

-> 🎨 Beautiful UI with themed fonts, outlines, and background effects

-> 📜 Question layout adapts responsively for long text





✅ Requirements

1. Java 21 (Preferably OpenJDK 23 or higher)

2. JavaFX 21 SDK (added in lib folder)

3. VSCode with Java Extension Pack



✍️ Customization Tips

Add your own questions to the txt files, make sure you follow the format of the quiz

Change the background music by replacing the .m4a file in audio/

Update GIFs or images in the respective folders for a different theme.



📬 Feedback

Have suggestions, or want to fork this for another theme? Go ahead and remix it! Freddy’s always watching 👀Send us feedback on this Google form below: (especially if you want to report any bugs)

https://docs.google.com/forms/d/e/1FAIpQLScuwZ5qb5oEPw5Cla8N2WJ3LoLqTyW3o6K9kJdIoWH_GzzztA/viewform




Made with 💀, 🎃, and JavaFX.