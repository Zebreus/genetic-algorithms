package Selectors;

import Interfaces.Selector;
import MainClasses.Candidate;

import java.util.Random;

public class OnlyBest implements Selector {

    public OnlyBest() {
    }

    @Override
    public Candidate[] selectNewPopulation(Candidate[] generation) {
        Candidate bestCandidate = generation[0];
        for (Candidate candidate: generation) {
            if (candidate.getFitness() >= bestCandidate.getFitness()) {
                bestCandidate = candidate;
            }
        }

        Candidate[] newPopulation = new Candidate[generation.length];
        for (int i = 0; i < generation.length; i++) {
            newPopulation[i] = new Candidate(bestCandidate.getFolding());
        }

        return newPopulation;
    }
}
