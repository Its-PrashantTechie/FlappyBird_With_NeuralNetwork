package game;

import java.awt.*;

/**
 * Represents a single vertical pipe pair (top and bottom) at a given x-position.
 * Instances are managed by {@link PipeManager}.
 */
public class Pipe {
    public int x;
    public int pipeWidth;
    public int gap;
    public int topHeight;

    public Pipe(int x, int pipeWidth, int gap, int topHeight) {
        this.x = x;
        this.pipeWidth = pipeWidth;
        this.gap = gap;
        this.topHeight = topHeight;
    }

    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(x, 0, pipeWidth, topHeight);
        g.fillRect(x, topHeight + gap, pipeWidth, Constants.HEIGHT - (topHeight + gap));
    }
}

