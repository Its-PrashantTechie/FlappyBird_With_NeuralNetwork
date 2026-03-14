package game;

/**
 * Wraps a neural network with a fitness value for use in a genetic algorithm.
 */
public class Genome {
    public NeuralNetwork brain;
    public double fitness;

    public Genome() {
        this.brain = new NeuralNetwork();
        this.fitness = 0.0;
    }

    public Genome copy() {
        Genome g = new Genome();
        g.brain = this.brain.copy();
        g.fitness = this.fitness;
        return g;
    }
}

