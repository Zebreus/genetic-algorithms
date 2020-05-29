package InitialGenerationCreators;

import Enums.DirectionFRL;
import Interfaces.InitialGenerationCreator;
import MainClasses.Candidate;

import java.util.Random;

public class StraightLine implements InitialGenerationCreator {

    public StraightLine() {
    }

    @Override
    public Candidate[] initializeDirections(int populationSize, int sequenceLength) {
        Candidate[] population = new Candidate[populationSize];

        int[] candidateDirections = new int[sequenceLength];
        for (int j = 0; j < sequenceLength; j++) {
            candidateDirections[j] = 0; // Default starting direction is set by Enum
        }

        for (int i = 0; i < populationSize; i++) {
            population[i] = new Candidate(candidateDirections);
        }

        return population;
    }
}
