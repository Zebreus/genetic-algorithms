package InitialGenerationCreators;

import Interfaces.InitialGenerationCreator;
import MainClasses.Candidate;

import java.util.Random;

public class  RandomDirection<T extends Enum<?>> implements InitialGenerationCreator {

    Random rand;
    Class<T> possibleDirections;

    public RandomDirection(Class<T> possibleDirections, Random rand) {
        this.possibleDirections = possibleDirections;
        this.rand = rand;
    }

    @Override
    public Candidate[] initializeDirections(int populationSize, int sequenceLength) {
        Candidate[] population = new Candidate[populationSize];

        for (int i = 0; i < populationSize; i++) {
            int[] candidateDirections = new int[sequenceLength];
            candidateDirections[0] = this.rand.nextInt(4); // Can start in any direction
            for (int j = 1; j < sequenceLength; j++) {
                // Make sure there can never be a backtracking overlap while initializing
                candidateDirections[j] = ((candidateDirections[j-1] - 1 + this.rand.nextInt(3)) + 4 ) % 4;
            }
            population[i] = new Candidate(candidateDirections);
        }

        return population;
    }
}
