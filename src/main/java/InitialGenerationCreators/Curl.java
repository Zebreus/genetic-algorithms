package InitialGenerationCreators;

import Enums.DirectionFRL;
import Enums.DirectionNESW;
import Interfaces.InitialGenerationCreator;
import MainClasses.Candidate;

public class Curl<T extends Enum<?>> implements InitialGenerationCreator {

    Class<T> possibleDirections;

    public Curl(Class<T> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    @Override
    public Candidate[] initializeDirections(int populationSize, int[] isHydrophobic) {
        Candidate[] population = new Candidate[populationSize];
        int proteinLength = isHydrophobic.length;

        int[] candidateDirections;
        if (isFRLEncoding(possibleDirections) && populationSize > 0) {
            candidateDirections = curlFRL(proteinLength);
        } else {
            candidateDirections = curlNESW(proteinLength);
        }

        for (int i = 0; i < populationSize; i++) {
            population[i] = new Candidate(isHydrophobic, candidateDirections);
        }

        return population;
    }

    private int[] curlNESW(int proteinLength) {
        int[] candidateDirections = new int[proteinLength];
        int stepSize = 1;
        int stepsLeft = 1;
        boolean stepSizeChange = false; // To increase stepSize every second time
        DirectionNESW dir = DirectionNESW.North;

        for (int j = 0; j < proteinLength; j++) {
            candidateDirections[j] = dir.getValue();
            stepsLeft--;

            if (stepsLeft == 0) {
                if (stepSizeChange) {
                    stepSize++;
                    stepsLeft = stepSize;
                    stepSizeChange = false;
                } else {
                    stepsLeft = stepSize;
                    stepSizeChange = true;
                }

                // Rotate direction
                if (dir == DirectionNESW.North) {
                    dir = DirectionNESW.East;
                } else if (dir == DirectionNESW.East) {
                    dir = DirectionNESW.South;
                } else if (dir == DirectionNESW.South) {
                    dir = DirectionNESW.West;
                } else {
                    dir = DirectionNESW.North;
                }
            }
        }
        return candidateDirections;
    }

    private int[] curlFRL(int proteinLength) {
        int[] candidateDirections = new int[proteinLength];
        candidateDirections[0] = DirectionFRL.Forward.getValue();

        int stepSize = 0;
        int stepsLeft = 0;
        boolean stepSizeChange = false; // To increase stepSize every second time

        for (int j = 1; j < proteinLength; j++) {
            if (stepsLeft == 0) { // Turn
                candidateDirections[j] = DirectionFRL.Right.getValue();

                if (stepSizeChange) {
                    stepSize++;
                    stepSizeChange = false;
                } else {
                    stepSizeChange = true;
                }
                stepsLeft = stepSize;

            } else { // Straight
                candidateDirections[j] = DirectionFRL.Forward.getValue();
                stepsLeft--;
            }
        }
        return candidateDirections;
    }

    private <T extends Enum<?>> boolean isFRLEncoding(Class<T> possibleDirections) {
        T[] possibleDirectionsEnum = possibleDirections.getEnumConstants();
        for (int i = 0; i < possibleDirectionsEnum.length; i++) {
            if (possibleDirectionsEnum[i].equals(DirectionFRL.Forward)) {
                return true;
            }
        }
        return false;
    }
}
