import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class GeneticAlgorithm {

    Random rand = new Random();
    ProteinDrawer pdraw = new ProteinDrawer("./visualization/series", "image.png");
    String logfile;
    int populationSize;
    int totalGenerations;

    int[] isHydrophobic;
    Canidate[] population;

    double totalFitness;
    double[] fitness;

    double overallBestFitness;
    Canidate overallBest;

    double mutationChance;
    double mutationDecline;

    // Initialize with protein
    public GeneticAlgorithm (String logfile, int[] protein, int pop, int gens) {
        this.logfile = logfile;
        this.isHydrophobic =  protein;
        this.populationSize = pop;
        this.totalGenerations = gens;

        this.population = generateInitalPopulation();
        this.totalFitness = 0;
        this.fitness = new double[populationSize];

        this.overallBestFitness = 0;
        this.mutationChance = 1.0; // Guaranteed mutation in the first generation
        this.mutationDecline = 0.001; // Decline in mutation probablility with generations -> ex with 0.05: 2nd 0.95, 3rd 0.9025, 4th 0.857

        // Clear log file
        String content = "";
        try {
            Files.write(Paths.get(logfile), content.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generate initial population
    private Canidate[] generateInitalPopulation() {
        Canidate[] population = new Canidate[populationSize];

        for (int i = 0; i < populationSize; i++) {
            int[] canidateDirections = new int[isHydrophobic.length];
            for (int j = 0; j < isHydrophobic.length; j++) {
                canidateDirections[j] = rand.nextInt(4);
            }
            population[i] = new Canidate(isHydrophobic, canidateDirections);
        }

        return population;
    }

    public void simulateGenerations() {
        for (int gen = 0; gen < totalGenerations-1; gen++) {
            int bestIndex = evaluateGeneration(gen);
            population = pickSurvivors(bestIndex);
            mutateGeneration(mutationChance, 1);
            mutationChance *= (1 - mutationDecline);
            System.out.println();
        }
        evaluateGeneration(totalGenerations-1);
    }

    private int evaluateGeneration(int gen) {
        // Evaluate current generation
        System.out.println("Generation " + gen + ":");

        double bestFitness = 0;
        int bestIndex = 0;
        totalFitness = 0;
        for (int i = 0; i < populationSize; i++) {
            fitness[i] = population[i].calculateFitness(false)[0];
            totalFitness += fitness[i];

            if (fitness[i] > bestFitness) {
                bestFitness = fitness[i];
                bestIndex = i;
            }
        }
        pdraw.setFilename("gen_" + gen + ".png");
        pdraw.drawProteinToFile(population[bestIndex].getVertexList(), population[bestIndex].calculateFitness(true));

        // Save the overall best
        if (bestFitness > overallBestFitness) {
            overallBestFitness = bestFitness;
            overallBest = new Canidate(this.isHydrophobic, population[bestIndex].getOutgoing());
        }

        double averageFitness = totalFitness / populationSize;
        double[] fitBondOverBest = overallBest.calculateFitness(false);
        String log = String.format("%d \t %.4f \t %.4f \t %.4f \t %d \t %d \n",
                gen, averageFitness, bestFitness, fitBondOverBest[0], (int)fitBondOverBest[1], (int)fitBondOverBest[2]);

        try {
            Files.write(Paths.get(logfile), log.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bestIndex;
    }

    private Canidate[] pickSurvivors(int bestIndex) {
        // Pick set for next generation
        double[] proportionalFitness = new double[populationSize];
        for (int i = 0; i < populationSize; i++) {
            proportionalFitness[i] = fitness[i] / totalFitness;
        }

        Canidate[] newPopulation = new Canidate[populationSize];
        for (int i = 0; i < populationSize; i++) {
            double picked = rand.nextDouble();
            int j = -1;
            while (picked > 0) {
                j++;
                picked -= proportionalFitness[j];
            }
            newPopulation[i] = new Canidate(this.isHydrophobic, population[j].getOutgoing());
        }

        return newPopulation;
    }

    private void mutateGeneration(double mutationChance, int mutationAttemptsPerCanidate) {
        // Mutate
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < mutationAttemptsPerCanidate; j++) {
                if (mutationChance > rand.nextDouble()) {
                    int mutationPlace = rand.nextInt(isHydrophobic.length);
                    population[i].mutateDir(mutationPlace, rand.nextInt(4));
                }
            }
        }
    }
}
