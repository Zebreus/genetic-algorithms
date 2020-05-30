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
    double mutationMultiplier;

    public SinglePoint(Class<T> possibleDirections, Random rand, int mutationAttemptsPerCandidate,
                       double mutationChance, double mutationMultiplier) {
        this.possibleDirections = possibleDirections;
        this.isFRL = Mutator.isFRLEncoding(possibleDirections);
        this.rand = rand;
        this.mutationAttemptsPerCandidate = mutationAttemptsPerCandidate;
        this.mutationChance = mutationChance;
        this.mutationMultiplier = mutationMultiplier;
    }

    @Override
    public Candidate[] mutatePopulation(Candidate[] population) {
        Candidate[] mutatedPopulation = new Candidate[population.length];
        if (this.mutationChance > MINIMUM_CHANCE) {

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
                            mutatedFolding[mutationPlace] = this.rand.nextInt(4);
                        }
                    }
                }
                mutatedPopulation[candidateId] = new Candidate(mutatedFolding);
            }
            System.out.printf("MutationChance: %.4f\n", this.mutationChance);

            this.mutationChance *= (1 - this.mutationMultiplier); // Lower mutation rate with generation
        }
        return  population;
    }
}
