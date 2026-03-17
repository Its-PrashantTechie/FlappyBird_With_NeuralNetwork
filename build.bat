@echo off
echo Compiling source files...
cd /d "%~dp0"
if not exist "out" mkdir "out"
javac -d out src/game/*.java

echo Creating FlappyBirdAI.jar...
cd out
jar cfe ../FlappyBirdAI.jar game.Game game/*.class

echo.
echo =========================================
echo Build Successful! 
echo You can run the game by double-clicking FlappyBirdAI.jar
echo or running: java -jar FlappyBirdAI.jar
echo =========================================
echo.
pause
