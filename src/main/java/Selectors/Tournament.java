package Selectors;

import Interfaces.Selector;
import MainClasses.Candidate;

import MainClasses.Config;
import java.util.Random;

public class Tournament implements Selector {

    Random rand;
    Config config;

    public Tournament(Config config, Random rand) {
        this.rand = rand;
        this.config = config;
    }

    @Override
    public Candidate[] selectNewPopulation(Candidate[] generation) {

        Candidate[] newPopulation = new Candidate[generation.length];
        for (int i = 0; i < generation.length; i++) {
            Candidate bestParticipant = generation[rand.nextInt(generation.length)];
            for (int participant = 0; participant < config.getK()-1; participant++) {
                Candidate nextParticipant = generation[rand.nextInt(generation.length)];
                if (nextParticipant.getFitness() > bestParticipant.getFitness()){
                    bestParticipant = nextParticipant;
                }
            }
            newPopulation[i] = bestParticipant;
        }

        return newPopulation;
    }
}
