@echo off
set FX_PATH=%~dp0..\javafx-sdk-23.0.2\lib
java --module-path "%FX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.media -jar "%~dp0target\quiz_app-1.0-SNAPSHOT.jar"
pause
