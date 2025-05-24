@echo off
set FX_PATH=%~dp0..\server_app\javafx-sdk-23.0.2\lib
java --module-path "%FX_PATH%" --add-modules javafx.controls,javafx.fxml -jar "..\\server_app\\target\\server_app-1.0-SNAPSHOT.jar"
pause
