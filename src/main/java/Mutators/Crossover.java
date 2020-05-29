package Mutators;

import Interfaces.Mutator;
import MainClasses.Candidate;

import java.util.Random;

public class Crossover<T extends Enum<?>> implements Mutator {

    boolean isFRL;
    Class<T> possibleDirections;
    Random rand;
    int crossoverAttemptsPerCandidate;
    double crossoverChance;
    double crossoverMultiplier;

    public Crossover(Class<T> possibleDirections, Random rand, int crossoverAttemptsPerCandidate,
                     double crossoverChance, double crossoverMultiplier) {
        this.possibleDirections = possibleDirections;
        this.isFRL = Mutator.isFRLEncoding(possibleDirections);
        this.rand = rand;
        this.crossoverAttemptsPerCandidate = crossoverAttemptsPerCandidate;
        this.crossoverChance = crossoverChance;
        this.crossoverMultiplier = crossoverMultiplier;
    }

    @Override
    public Candidate[] mutatePopulation(Candidate[] population) {
        if (this.crossoverChance > MINIMUM_CHANCE) {
            int populationSize = population.length;
            int proteinLength = population[0].getOutgoing().length;

            for (Candidate candidate : population) {
                for (int j = 0; j < this.crossoverAttemptsPerCandidate; j++) {
                    if (this.crossoverChance > this.rand.nextDouble()) {
                        int crossoverPartner = this.rand.nextInt(populationSize);
                        int crossoverPlace = this.rand.nextInt(proteinLength);

                        int[] originalDirections = new int[candidate.outgoingDirection.length];
                        for (int i = crossoverPlace; i < candidate.outgoingDirection.length; i++) {
                            originalDirections[i] = candidate.outgoingDirection[i];
                        }

                        // Edit these directions
                        for (int i = crossoverPlace; i < candidate.outgoingDirection.length; i++) {
                            candidate.outgoingDirection[i] = population[crossoverPartner].outgoingDirection[i];
                        }

                        // Edit partners directions
                        for (int i = crossoverPlace; i < candidate.outgoingDirection.length; i++) {
                            population[crossoverPartner].outgoingDirection[i] = originalDirections[i];
                        }
                    }
                }
            }

            System.out.printf("CrossoverChance: %.4f\n", this.crossoverChance);

            this.crossoverChance *= (1 - this.crossoverMultiplier); // Lower mutation rate with generation
        }
        return  population;
    }
}
