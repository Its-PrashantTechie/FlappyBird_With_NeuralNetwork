package game;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Basic genetic algorithm population for evolving neural networks.
 * This is not yet wired into the visible game loop, but can be used
 * in a separate trainer to evolve better AIController brains.
 */
public class Population {

    private Genome[] genomes;
    private final int size;
    private final Random rand = new Random();

    public Population(int size) {
        this.size = size;
        genomes = new Genome[size];
        for (int i = 0; i < size; i++) {
            genomes[i] = new Genome();
        }
    }

    public Genome[] getGenomes() {
        return genomes;
    }

    public Genome getBest() {
        return Arrays.stream(genomes)
                .max(Comparator.comparingDouble(g -> g.fitness))
                .orElse(genomes[0]);
    }

    public void evolve() {
        Arrays.sort(genomes, Comparator.comparingDouble(g -> -g.fitness));
        int elite = Math.max(1, size / 10); // keep top 10% for more aggressive selection

        Genome[] next = new Genome[size];
        for (int i = 0; i < elite; i++) {
            next[i] = genomes[i].copy();
        }

        for (int i = elite; i < size; i++) {
            Genome parent = genomes[rand.nextInt(elite)].copy();
            parent.brain.mutate(0.2, 0.3); // Higher mutation rate and magnitude
            parent.fitness = 0.0;
            next[i] = parent;
        }

        genomes = next;
    }
}

