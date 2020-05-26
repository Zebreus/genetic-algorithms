package Selectors;

import Interfaces.Selector;
import MainClasses.Candidate;

import java.util.Random;

public class FitnessProportional implements Selector {

    Random rand;
    int[] isHydrophobic;

    public FitnessProportional(Random rand, int[] isHydrophobic) {
        this.rand = rand;
        this.isHydrophobic = isHydrophobic;
    }

    @Override
    public Candidate[] selectNewPopulation(Candidate[] population, double[] fitness, double totalFitness) {
        int populationSize = population.length;

        // Pick set for next generation
        double[] proportionalFitness = new double[populationSize];
        for (int i = 0; i < populationSize; i++) {
            proportionalFitness[i] = fitness[i] / totalFitness;
        }

        Candidate[] newPopulation = new Candidate[populationSize];
        for (int i = 0; i < populationSize; i++) {
            double picked = rand.nextDouble();
            int j = -1;
            while (picked > 0) {
                j++;
                picked -= proportionalFitness[j];
            }
            newPopulation[i] = new Candidate(this.isHydrophobic, population[j].getOutgoing());
        }

        return newPopulation;
    }
}
