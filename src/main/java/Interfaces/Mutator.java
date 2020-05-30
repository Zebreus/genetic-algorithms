package Interfaces;

import Enums.DirectionFRL;
import MainClasses.Candidate;

public interface Mutator {

    Candidate[] generateMutatedPopulation(Candidate[] population);

    //TODO Remove, when decided on FRL vs NESW
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
