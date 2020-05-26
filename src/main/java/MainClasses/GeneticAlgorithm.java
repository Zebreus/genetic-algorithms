package MainClasses;

import Enums.DirectionNESW;
import InitialGenerationCreators.Curl;
import Interfaces.InitialGenerationCreator;
import Interfaces.Mutator;
import Interfaces.Selector;
import Mutators.Crossover;
import Mutators.SinglePoint;
import Selectors.FitnessProportional;
import Selectors.OnlyBest;
import Selectors.Tournament;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.Random;

public class GeneticAlgorithm {

    Properties properties;

    Random rand = new Random();
    ProteinDrawer pdraw;
    String logfile;

    int[] isHydrophobic;
    Candidate[] population;

    double totalFitness;
    double[] fitness;

    double overallBestFitness;
    Candidate overallBest;

    // TODO: Move to config object
    int populationSize;
    int totalGenerations;
    int mutationAttemptsPerCandidate;
    double mutationChance;
    double mutationMultiplier;
    int crossoverAttemptsPerCandidate;
    double crossoverChance;
    double crossoverMultiplier;
    Selection selectionVariant;
    int k; // Number of selected Candidates to face off in a tournament selection

    enum Selection {
        proportional,
        tournament
    }

    InitialGenerationCreator initialGenCreator;
    Mutator[] mutators;
    Selector selector;

    // Initialize with protein
    public GeneticAlgorithm (Properties properties, int[] protein) {
        this.properties = properties;
        this.isHydrophobic =  protein;
        initializeProperties();

//        this.initialGenCreator = new RandomDirection<>(this.rand, DirectionNESW.class);
//        this.initialGenCreator = new StraightLine();
        this.initialGenCreator = new Curl<>(DirectionNESW.class);

        this.mutators = new Mutator[2];
        this.mutators[0] = new SinglePoint<>(DirectionNESW.class, this.rand, this.mutationAttemptsPerCandidate,
                this.mutationChance, this.mutationMultiplier);
        this.mutators[1] = new Crossover<>(DirectionNESW.class, this.rand, this.crossoverAttemptsPerCandidate,
                this.crossoverChance, this.crossoverMultiplier);

//        this.selector = new FitnessProportional(this.rand, this.isHydrophobic);
//        this.selector = new Tournament(this.rand, this.isHydrophobic, this.k);
        this.selector = new OnlyBest(this.isHydrophobic);

        // Clear log file
        String content = "Generation\tAverage Fitness\tBest Fitness\tOverall Best Fitness\tBonds\tOverlaps\n";
        try {
            Files.write(Paths.get(logfile), content.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.population = this.initialGenCreator.initializeDirections(this.populationSize, this.isHydrophobic);
        this.totalFitness = 0;
        this.fitness = new double[populationSize];
        this.overallBestFitness = 0;
    }

    // TODO: Move to config object
    private void initializeProperties() {
        this.logfile = this.properties.getProperty("logfilePath");
        this.populationSize = Integer.parseInt(this.properties.getProperty("populationSize"));
        this.totalGenerations = Integer.parseInt(this.properties.getProperty("noGenerations"));

        this.mutationAttemptsPerCandidate = Integer.parseInt(this.properties.getProperty("mutationAttemptsPerCandidate"));
        this.mutationChance = Double.parseDouble(this.properties.getProperty("mutationChance"));
        this.mutationMultiplier = Double.parseDouble(this.properties.getProperty("mutationDecline"));
        this.crossoverAttemptsPerCandidate = Integer.parseInt(this.properties.getProperty("crossoverAttemptsPerCandidate"));
        this.crossoverChance = Double.parseDouble(this.properties.getProperty("crossoverChance"));
        this.crossoverMultiplier = Double.parseDouble(this.properties.getProperty("crossoverDecline"));

        this.k = Integer.parseInt(this.properties.getProperty("k"));

        try {
            if (this.properties.getProperty("selection").equals("proportional")) {
                this.selectionVariant = Selection.proportional;
            } else if (this.properties.getProperty("selection").equals("tournament")) {
                this.selectionVariant = Selection.tournament;
            } else {
                    throw new Exception("Selection variant not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.pdraw = new ProteinDrawer(properties.getProperty("imageSequencePath"));
    }

    public void simulateGenerations() {
        for (int gen = 0; gen < totalGenerations-1; gen++) {
            this.evaluateGeneration(gen);
            this.population = this.selector.selectNewPopulation(this.population, this.fitness, this.totalFitness);

            for (Mutator m : mutators) { // SinglePoint and Crossover
                this.population = m.mutatePopulation(this.population);
            }

            System.out.println();
        }
        evaluateGeneration(totalGenerations-1);
    }

    private int evaluateGeneration(int gen) {
        // Evaluate current generation
        System.out.println("Generation " + gen + ":");

        double bestFitness = 0;
        int bestIndex = 0;
        this.totalFitness = 0;
        for (int i = 0; i < this.populationSize; i++) {
            this.fitness[i] = this.population[i].calculateFitness(false)[0];
            this.totalFitness += this.fitness[i];

            if (this.fitness[i] > bestFitness) {
                bestFitness = this.fitness[i];
                bestIndex = i;
            }
        }
        this.pdraw.setFilename(String.format("gen_%07d.jpg",gen));
        this.pdraw.drawProteinToFile(this.population[bestIndex].getVertexList(),
                this.population[bestIndex].calculateFitness(true), gen);

        // Save the overall best
        if (bestFitness >= this.overallBestFitness) {
            this.overallBestFitness = bestFitness;
            this.overallBest = new Candidate(this.isHydrophobic, this.population[bestIndex].getOutgoing());
        }

        double averageFitness = this.totalFitness / this.populationSize;
        double[] fitBondOverBest = this.overallBest.calculateFitness(false);
        String log = String.format("%d\t%.4f\t%.4f\t%.4f\t %d\t%d\n",
                gen, averageFitness, bestFitness, fitBondOverBest[0], (int)fitBondOverBest[1], (int)fitBondOverBest[2]);

        try {
            Files.write(Paths.get(this.logfile), log.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bestIndex;
    }
}
