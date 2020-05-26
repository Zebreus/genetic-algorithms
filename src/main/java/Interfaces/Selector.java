package Interfaces;

import MainClasses.Candidate;

public interface Selector {

    Candidate[] selectNewPopulation(Candidate[] population, double[] fitness, double totalFitness);
}
