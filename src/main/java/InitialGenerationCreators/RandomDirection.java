package InitialGenerationCreators;

import Enums.DirectionNESW;
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
            for (int j = 0; j < sequenceLength; j++) {
                candidateDirections[j] = this.randomDirection(this.possibleDirections);
            }
            population[i] = new Candidate(candidateDirections);
        }

        return population;
    }

    private int randomDirection(Class<T> dirEnum) {
        return rand.nextInt(dirEnum.getEnumConstants().length);
    }
}
