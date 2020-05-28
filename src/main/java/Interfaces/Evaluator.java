package Interfaces;

import MainClasses.Candidate;

public interface Evaluator {

    double evaluateFitness(Candidate candidate);

    int evaluateBonds(Candidate candidate);

    int evaluateOverlaps(Candidate candidate);
}