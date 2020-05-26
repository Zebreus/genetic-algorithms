package InitialGenerationCreators;

import Enums.DirectionFRL;
import Interfaces.InitialGenerationCreator;
import MainClasses.Candidate;

import java.util.Random;

public class StraightLine implements InitialGenerationCreator {

    public StraightLine() {
    }

    @Override
    public Candidate[] initializeDirections(int populationSize, int[] isHydrophobic) {
        Candidate[] population = new Candidate[populationSize];
        int proteinLength = isHydrophobic.length;

        int[] candidateDirections = new int[proteinLength];
        for (int j = 0; j < proteinLength; j++) {
            candidateDirections[j] = 0; // Default starting direction is set by Enum
        }

        for (int i = 0; i < populationSize; i++) {
            population[i] = new Candidate(isHydrophobic, candidateDirections);
        }

        return population;
    }
}
