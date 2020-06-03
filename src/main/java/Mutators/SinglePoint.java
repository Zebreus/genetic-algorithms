package Mutators;

import Interfaces.Mutator;
import MainClasses.Candidate;

import java.util.Random;

public class SinglePoint<T extends Enum<?>> implements Mutator {

    boolean isFRL;
    Class<T> possibleDirections;
    Random rand;
    int mutationAttemptsPerCandidate;
    double mutationChance;
    double mutationMinimalChance;
    double mutationMultiplier;

    public SinglePoint(Class<T> possibleDirections, Random rand, int mutationAttemptsPerCandidate,
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
                        // TODO: Use the enums or get rid of this hard coded 3 and 4...
                        if (this.isFRL) {
                            mutatedFolding[mutationPlace] = this.rand.nextInt(3);
                        } else {
                            if (mutationPlace == 0) {
                                // Allow any direction in the first position
                                mutatedFolding[mutationPlace] = this.rand.nextInt(4);
                            } else {
                                // Make sure there can never be a backtracking overlap while mutating
                                mutatedFolding[mutationPlace] =
                                        ((mutatedFolding[mutationPlace-1] - 1 + this.rand.nextInt(3)) + 4 ) % 4;
                            }
                        }
                    }
                }
                mutatedPopulation[candidateId] = new Candidate(mutatedFolding);
            }
            System.out.printf("MutationChance: %.4f\n", this.mutationChance);

            this.mutationChance *= (1 - this.mutationMultiplier); // Lower mutation rate with generation
        }else{
            return population;
        }
        return mutatedPopulation;
    }
}
