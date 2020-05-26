package Interfaces;

import MainClasses.Candidate;

public interface InitialGenerationCreator {

    Candidate[] initializeDirections(int populationSize, int[] isHydrophobic);
}
