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
    double crossoverMinimalChance;
    double crossoverMultiplier;

    public Crossover(Class<T> possibleDirections, Random rand, int crossoverAttemptsPerCandidate,
                     double crossoverChance, double crossoverMinimalChance, double crossoverMultiplier) {
        this.possibleDirections = possibleDirections;
        this.isFRL = Mutator.isFRLEncoding(possibleDirections);
        this.rand = rand;
        this.crossoverAttemptsPerCandidate = crossoverAttemptsPerCandidate;
        this.crossoverChance = crossoverChance;
        this.crossoverMinimalChance = crossoverMinimalChance;
        this.crossoverMultiplier = crossoverMultiplier;
    }

    @Override
    public Candidate[] generateMutatedPopulation(Candidate[] population) {
        Candidate[] mutatedPopulation = new Candidate[population.length];
        if (this.crossoverChance > crossoverMinimalChance) {
            int populationSize = population.length;
            int proteinLength = population[0].getFolding().length;

            for (int candidateId = 0; candidateId<population.length ; candidateId++) {
                //Mutates only the selected Candidate not the partner
                int[] mutatedFolding = population[candidateId].getFolding();
                for (int j = 0; j < this.crossoverAttemptsPerCandidate; j++) {
                    if (this.crossoverChance > this.rand.nextDouble()) {
                        int crossoverPartner = this.rand.nextInt(populationSize);
                        int crossoverPlace = this.rand.nextInt(proteinLength);
                        int[] partnerFolding = population[crossoverPartner].getFolding();

                        // Edit these directions
                        for (int i = crossoverPlace; i < mutatedFolding.length; i++) {
                            mutatedFolding[i] = partnerFolding[i];
                        }

                        // Removed partner crossover mutation, because this can lead to unexpected double mutations if the partner mutates again
                        // Edit partners directions
                        //for (int i = crossoverPlace; i < candidate.outgoingDirection.length; i++) {
                        //    population[crossoverPartner].outgoingDirection[i] = originalDirections[i];
                        //}
                    }
                }
                mutatedPopulation[candidateId] = new Candidate(mutatedFolding);
            }

            System.out.printf("CrossoverChance: %.4f\n", this.crossoverChance);

            this.crossoverChance *= (1 - this.crossoverMultiplier); // Lower mutation rate with generation
        }else{
            return population;
        }
        return mutatedPopulation;
    }
}
