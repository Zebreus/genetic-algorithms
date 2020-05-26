package Interfaces;

import Enums.DirectionFRL;
import MainClasses.Candidate;

public interface Mutator {

    double MINIMUM_CHANCE = 0.0001; // -> 0.01% is not worth mutating for

    Candidate[] mutatePopulation(Candidate[] population);

    static <T extends Enum<?>> boolean isFRLEncoding(Class<T> possibleDirections) {
        T[] possibleDirectionsEnum = possibleDirections.getEnumConstants();
        for (int i = 0; i < possibleDirectionsEnum.length; i++) {
            if (possibleDirectionsEnum[i].equals(DirectionFRL.Forward)) {
                return true;
            }
        }
        return false;
    }
}
