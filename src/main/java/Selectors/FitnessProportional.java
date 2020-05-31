package Selectors;

import Interfaces.Selector;
import MainClasses.Candidate;

import MainClasses.Config;
import java.util.Random;

public class FitnessProportional implements Selector {

    Random rand;
    Config config;

    public FitnessProportional(Config config, Random rand) {
        this.rand = rand;
        this.config = config;
    }

    @Override
    public Candidate[] selectNewPopulation(Candidate[] generation) {
        int populationSize = generation.length;

        double totalFitness = 0.0;
        for (Candidate candidate: generation) {
            totalFitness += candidate.getFitness();
        }

        Candidate[] newPopulation = new Candidate[populationSize];
        for (int i = 0; i < populationSize; i++) {
            //Select a number between 0 and the maximum fitness
            double requiredFitness = rand.nextDouble() * totalFitness;
            double currentFitness = 0.0;
            for (Candidate candidate : generation) {
                currentFitness += candidate.getFitness();
                if(currentFitness >= requiredFitness) {
                    newPopulation[i] = candidate;
                    break;
                }
            }
        }

        return newPopulation;
    }
}
