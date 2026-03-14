@echo off
cd /d "%~dp0src"
javac game\*.java
java game.Game
pause

