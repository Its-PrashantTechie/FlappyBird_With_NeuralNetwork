# Flappy Bird in Java (with AI)

A classic Flappy Bird clone written entirely in Java using AWT and Swing, featuring both a playable **Human Mode** and a smart **AI Mode** that learns how to play the game using a Neural Network and a Genetic Algorithm!

## Features

- **Classic Gameplay Engine**: Smooth physics, scrolling pipes, and moving clouds built from scratch using Java's Swing/AWT Graphics `paintComponent`.
- **Human Mode**: Prove your skills! Play the game manually and track both your current score and all-time best score.
- **AI Mode (Neuroevolution)**: Watch an AI learn to master Flappy Bird! 
  - A population of 40 birds spawns in every generation.
  - Guided by a Neural Network (6 inputs -> 8 hidden units -> 1 output) deciding whether a bird should jump or let gravity take over.
  - The fittest birds pass on their "genes" to the next generation using a Genetic Algorithm, progressively learning out to flawlessly fly through pipes infinitely.
- **Dynamic HUD**: Tracks generation cycles, current best scores, all-time highest scores, and fitness variables.

## Controls
- On the **Menu Screen**:
  - Press `1` or `P`, or **Click** "Human Mode" to play manually.
  - Press `2` or `A`, or **Click** "AI Mode" to start neuroevolution.
- **In-Game (Human)**:
  - Press `SPACE` to jump.
  - Press `ESC` (if game over) to return to the menu.
- **In-Game (AI)**:
  - Press `SPACE` to completely restart the evolution cycle from Generation 1.
  - Press `ESC` (when game over) to return to menu mode.

## How to Run

### Using the Batch Script (Windows)
Just double-click the `run.bat` file from the main directory or run it in your terminal:
```bash
.\run.bat
```

### Manually via JDK
You can compile and run it straight from `src`:
```bash
javac -d out src/game/*.java
java -cp out game.Game
```
