@echo off
set FX_PATH=%~dp0..\javafx-sdk-23.0.2\lib
java --module-path "%FX_PATH%" --add-modules javafx.controls,javafx.fxml -jar "%~dp0target\server_app-1.0-SNAPSHOT.jar"
pause
