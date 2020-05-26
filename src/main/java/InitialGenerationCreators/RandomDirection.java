package InitialGenerationCreators;

import Enums.DirectionNESW;
import Interfaces.InitialGenerationCreator;
import MainClasses.Candidate;

import java.util.Random;

public class  RandomDirection<T extends Enum<?>> implements InitialGenerationCreator {

    Random rand;
    Class<T> possibleDirections;

    public RandomDirection(Random rand, Class<T> possibleDirections) {
        this.rand = rand;
        this.possibleDirections = possibleDirections;
    }

    @Override
    public Candidate[] initializeDirections(int populationSize, int[] isHydrophobic) {
        Candidate[] population = new Candidate[populationSize];
        int proteinLength = isHydrophobic.length;

        for (int i = 0; i < populationSize; i++) {
            int[] candidateDirections = new int[proteinLength];
            for (int j = 0; j < proteinLength; j++) {
                candidateDirections[j] = this.randomDirection(this.possibleDirections);
            }
            population[i] = new Candidate(isHydrophobic, candidateDirections);
        }

        return population;
    }

    private int randomDirection(Class<T> dirEnum) {
        return rand.nextInt(dirEnum.getEnumConstants().length);
    }
}
