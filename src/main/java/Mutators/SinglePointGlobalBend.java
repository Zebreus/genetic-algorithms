package Mutators;

import Interfaces.Mutator;
import MainClasses.Candidate;

import java.util.Random;

public class SinglePointGlobalBend<T extends Enum<?>> implements Mutator { // TODO: Maybe this should just extend SinglePoint

    boolean isFRL;
    Class<T> possibleDirections;
    Random rand;
    int mutationAttemptsPerCandidate;
    double mutationChance;
    double mutationMinimalChance;
    double mutationMultiplier;

    public SinglePointGlobalBend(Class<T> possibleDirections, Random rand, int mutationAttemptsPerCandidate,
                                 double mutationChance, double mutationMinimalChance, double mutationMultiplier) {
        this.possibleDirections = possibleDirections;
        this.isFRL = Mutator.isFRLEncoding(possibleDirections);
        this.rand = rand;
        this.mutationAttemptsPerCandidate = mutationAttemptsPerCandidate;
        this.mutationChance = mutationChance;
        this.mutationMinimalChance = mutationMinimalChance;
        this.mutationMultiplier = mutationMultiplier;
    }

    @Override
    public Candidate[] generateMutatedPopulation(Candidate[] population) {
        Candidate[] mutatedPopulation = new Candidate[population.length];
        if (this.mutationChance > mutationMinimalChance) {

            int proteinLength = population[0].getFolding().length;


            for (int candidateId = 0; candidateId<population.length ; candidateId++) {
                int[] mutatedFolding = population[candidateId].getFolding();
                for (int j = 0; j < this.mutationAttemptsPerCandidate; j++) {
                    if (this.mutationChance > this.rand.nextDouble()) {
                        int mutationPlace = this.rand.nextInt(proteinLength);
                        if (this.isFRL) {
                            mutatedFolding[mutationPlace] = this.rand.nextInt(3);
                        } else {
                            int oldDirection = mutatedFolding[mutationPlace];
                            if (mutationPlace == 0) {
                                // Allow any direction in the first position
                                mutatedFolding[mutationPlace] = this.rand.nextInt(4);
                            } else {
                                // Make sure there can never be a backtracking overlap while mutating
                                mutatedFolding[mutationPlace] =
                                        ((mutatedFolding[mutationPlace-1] - 1 + this.rand.nextInt(3)) + 4 ) % 4;
                            }
                            // Also bend the following amino acids in the right way to achieve a "global" bend
                            int offset = (mutatedFolding[mutationPlace] - oldDirection + 4) % 4;
                            for (int r = mutationPlace + 1; r < mutatedFolding.length; r++) {
                                mutatedFolding[r] = (mutatedFolding[r] + offset) % 4;
                            }
                        }
                    }
                }
                mutatedPopulation[candidateId] = new Candidate(mutatedFolding);
            }
            System.out.printf("MutationChance: %.4f\n", this.mutationChance);

            this.mutationChance *= (1 - this.mutationMultiplier); // Lower mutation rate with generation
        }
        return mutatedPopulation;
    }
}
