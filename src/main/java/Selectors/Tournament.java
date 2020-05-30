package Selectors;

import Interfaces.Selector;
import MainClasses.Candidate;

import java.util.Random;

public class Tournament implements Selector {

    Random rand;
    int[] isHydrophobic;
    int k;

    public Tournament(Random rand, int[] isHydrophobic, int k) {
        this.rand = rand;
        this.isHydrophobic = isHydrophobic;
        this.k = k;
    }

    @Override
    public Candidate[] selectNewPopulation(Candidate[] population, double[] fitness, double totalFitness) {
        int populationSize = population.length;

        Candidate[] newPopulation = new Candidate[populationSize];
        double tournamentScoreMax = 0;
        int tournamentChoosenIndex = 0;

        for (int i = 0; i < populationSize; i++) {
            tournamentScoreMax = 0;
            for (int ik = 0; ik < this.k; ik++) {
                int nextIndex = rand.nextInt(populationSize);
                double nextScore = fitness[nextIndex];
                if (tournamentScoreMax < nextScore){
                    tournamentScoreMax = nextScore;
                    tournamentChoosenIndex = nextIndex;
                }
            }
            newPopulation[i] = new Candidate(population[tournamentChoosenIndex].getFolding());
        }

        return newPopulation;
    }
}
