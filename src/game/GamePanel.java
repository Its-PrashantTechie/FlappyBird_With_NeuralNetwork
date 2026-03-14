package game;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    private Thread gameThread;
    private boolean running = true;
    private boolean gameOver = false;

    // Selected mode (null means we're still on the menu screen)
    private GameMode mode = null;

    // Single player bird (used in human mode)
    private Bird playerBird;

    // GA mode: many AI birds and a population
    private static final int POPULATION_SIZE = 40;
    private Population population;
    private java.util.List<AIBird> aiBirds;
    private int generation = 1;
    private double bestEverFitness = 0.0;

    // Pipes
    private PipeManager pipeManager;

    // Clouds
    private int cloudX1 = 0;
    private int cloudX2;
    private int cloudSpeed = 1;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        setBackground(Color.cyan);
        setFocusable(true);

        pipeManager = new PipeManager();

        cloudX2 = Constants.WIDTH;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (mode == null) {
                    // Title screen – choose mode
                    switch (code) {
                        case KeyEvent.VK_1:
                        case KeyEvent.VK_H:
                            startHumanMode();
                            break;
                        case KeyEvent.VK_2:
                        case KeyEvent.VK_A:
                            startGAMode();
                            break;
                        default:
                            break;
                    }
                } else {
                    // In-game controls depend on mode
                    switch (mode) {
                        case HUMAN:
                            if (code == KeyEvent.VK_SPACE) {
                                if (gameOver) {
                                    resetGameHuman();
                                } else {
                                    playerBird.jump();
                                }
                            }
                            break;
                        case AI:
                            if (code == KeyEvent.VK_SPACE) {
                                // restart evolution from scratch
                                startGAMode();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        startGame();
    }

    private void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void startHumanMode() {
        mode = GameMode.HUMAN;
        gameOver = false;
        pipeManager.reset();
        playerBird = new Bird(100, 250);
    }

    private void startGAMode() {
        mode = GameMode.AI;
        gameOver = false;
        initGAMode();
    }

    private void resetGameHuman() {
        gameOver = false;
        playerBird.reset(100, 250);
        pipeManager.reset();
    }

    @Override
    public void run() {
        while (running) {
            updateGame();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                // Stop the loop cleanly if interrupted
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void updateGame() {
        if (mode == null) {
            // Menu screen: just move clouds for some life
            moveClouds();
        } else if (mode == GameMode.HUMAN) {
            updateHuman();
        } else {
            updateGA();
        }
    }

    private void updateHuman() {
        if (!gameOver) {
            playerBird.update();
            pipeManager.update();
            moveClouds();
            checkCollisionHuman();
        }
    }

    private void updateGA() {
        pipeManager.update();
        moveClouds();

        boolean allDead = true;
        for (AIBird ai : aiBirds) {
            if (ai.alive) {
                ai.update(pipeManager);
                checkCollisionForAIBird(ai);
            }
            if (ai.alive) {
                allDead = false;
            }
        }

        if (allDead) {
            nextGeneration();
        }
    }

    private void moveClouds() {
        cloudX1 -= cloudSpeed;
        cloudX2 -= cloudSpeed;
        if (cloudX1 + Constants.WIDTH < 0) cloudX1 = Constants.WIDTH;
        if (cloudX2 + Constants.WIDTH < 0) cloudX2 = Constants.WIDTH;
    }

    private void checkCollisionHuman() {
        if (playerBird.y + playerBird.size > Constants.HEIGHT - 50) gameOver = true;

        Rectangle birdRect = new Rectangle(playerBird.x, playerBird.y, playerBird.size, playerBird.size);

        for (Pipe p : pipeManager.getPipes()) {
            Rectangle topPipe = new Rectangle(p.x, 0, p.pipeWidth, p.topHeight);
            Rectangle bottomPipe = new Rectangle(p.x, p.topHeight + p.gap, p.pipeWidth,
                    Constants.HEIGHT - (p.topHeight + p.gap));

            if (birdRect.intersects(topPipe) || birdRect.intersects(bottomPipe)) {
                gameOver = true;
                break;
            }
        }
    }

    private void checkCollisionForAIBird(AIBird ai) {
        Bird b = ai.bird;
        if (!ai.alive) return;

        if (b.y + b.size > Constants.HEIGHT - 50) {
            ai.kill();
        } else {
            Rectangle birdRect = new Rectangle(b.x, b.y, b.size, b.size);

            for (Pipe p : pipeManager.getPipes()) {
                Rectangle topPipe = new Rectangle(p.x, 0, p.pipeWidth, p.topHeight);
                Rectangle bottomPipe = new Rectangle(p.x, p.topHeight + p.gap, p.pipeWidth,
                        Constants.HEIGHT - (p.topHeight + p.gap));

                if (birdRect.intersects(topPipe) || birdRect.intersects(bottomPipe)) {
                    ai.kill();
                    break;
                }
            }
        }
    }

    private void initGAMode() {
        pipeManager.reset();
        population = new Population(POPULATION_SIZE);
        aiBirds = new java.util.ArrayList<>();
        generation = 1;
        bestEverFitness = 0.0;

        for (Genome g : population.getGenomes()) {
            aiBirds.add(new AIBird(g.brain.copy(), 100, 250));
        }
    }

    private void nextGeneration() {
        // copy fitness back into genomes
        Genome[] genomes = population.getGenomes();
        for (int i = 0; i < genomes.length && i < aiBirds.size(); i++) {
            genomes[i].fitness = aiBirds.get(i).fitness;
            if (genomes[i].fitness > bestEverFitness) {
                bestEverFitness = genomes[i].fitness;
            }
        }

        population.evolve();
        generation++;
        pipeManager.reset();

        aiBirds.clear();
        for (Genome g : population.getGenomes()) {
            aiBirds.add(new AIBird(g.brain.copy(), 100, 250));
        }
    }

    private AIBird getBestAIBird() {
        if (aiBirds == null || aiBirds.isEmpty()) return null;
        AIBird best = aiBirds.get(0);
        for (AIBird ai : aiBirds) {
            if (ai.fitness > best.fitness) {
                best = ai;
            }
        }
        return best;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.cyan);
        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        // Clouds
        drawCloud(g, cloudX1, 60);
        drawCloud(g, cloudX2, 100);

        // Ground
        g.setColor(Color.orange);
        g.fillRect(0, Constants.HEIGHT - 50, Constants.WIDTH, 50);

        // If mode not yet chosen, show a simple menu and return
        if (mode == null) {
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Flappy Bird", 90, 200);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press 1 or H for Human mode", 50, 250);
            g.drawString("Press 2 or A for AI (Genetic)", 50, 280);
            return;
        }

        // Pipes
        for (Pipe p : pipeManager.getPipes()) {
            p.draw(g);
        }

        if (mode == GameMode.HUMAN) {
            // Single bird
            playerBird.draw(g, true);

            // Game Over Text
            if (gameOver) {
                g.setColor(Color.red);
                g.setFont(new Font("Arial", Font.BOLD, 48));
                g.drawString("GAME OVER", 100, Constants.HEIGHT / 2);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Press SPACE to restart", 130, Constants.HEIGHT / 2 + 40);
            }
        } else {
            // GA mode: draw all AI birds, highlight the best
            AIBird best = getBestAIBird();
            for (AIBird ai : aiBirds) {
                if (!ai.alive) continue;
                boolean highlight = (ai == best);
                ai.bird.draw(g, highlight);
            }

            // HUD: generation and fitness
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Generation: " + generation, 10, 20);
            g.drawString("Best ever: " + (int) bestEverFitness, 10, 40);
        }
    }

    private void drawCloud(Graphics g, int x, int y) {
        g.setColor(Color.white);
        g.fillOval(x + 30, y, 60, 40);
        g.fillOval(x, y + 10, 60, 40);
        g.fillOval(x + 60, y + 10, 60, 40);
        g.fillOval(x + 30, y + 20, 60, 40);
    }
}
