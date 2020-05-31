package Interfaces;

import MainClasses.Candidate;

public interface Selector {

    Candidate[] selectNewPopulation(Candidate[] generation);
}
