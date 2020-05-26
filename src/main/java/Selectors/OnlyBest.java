package Selectors;

import Interfaces.Selector;
import MainClasses.Candidate;

import java.util.Random;

public class OnlyBest implements Selector {

    int[] isHydrophobic;

    public OnlyBest(int[] isHydrophobic) {
        this.isHydrophobic = isHydrophobic;
    }

    @Override
    public Candidate[] selectNewPopulation(Candidate[] population, double[] fitness, double totalFitness) {
        int populationSize = population.length;

        int bestIndex = 0;
        double bestFitness = 0;

        for (int i = 0; i < populationSize; i++) {
            if (fitness[i] >= bestFitness) {
                bestFitness = fitness[i];
                bestIndex = i;
            }
        }

        Candidate[] newPopulation = new Candidate[populationSize];
        int[] bestFolding = population[bestIndex].getOutgoing();

        for (int i = 0; i < populationSize; i++) {
            newPopulation[i] = new Candidate(this.isHydrophobic, bestFolding);
        }

        return newPopulation;
    }
}
