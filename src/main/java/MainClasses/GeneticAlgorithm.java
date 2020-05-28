package MainClasses;

import Enums.DirectionNESW;
import Evaluators.EvaluatorNESW;
import InitialGenerationCreators.Curl;
import Interfaces.*;
import Mutators.Crossover;
import Mutators.SinglePoint;
import Selectors.OnlyBest;
import Visualization.Visualizers.VisualizerNESWtoConsole;
import Visualization.Visualizers.VisualizerNESWtoFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class GeneticAlgorithm {
    Random rand = new Random();
    Visualizer visualizer;

    int[] isHydrophobic;
    Candidate[] population;

    double totalFitness;
    double[] fitness;

    double overallBestFitness;
    Candidate overallBest;

    InitialGenerationCreator initialGenCreator;
    Mutator[] mutators;
    Selector selector;

    Evaluator evaluator;

    // Initialize with protein
    public GeneticAlgorithm (int[] protein) {
        this.isHydrophobic =  protein;
//        this.visualizer = new VisualizerNESWtoConsole();
        this.visualizer = new VisualizerNESWtoFile(Config.IMAGE_SEQUENCE_PATH);

//        this.initialGenCreator = new RandomDirection<>(this.rand, DirectionNESW.class);
//        this.initialGenCreator = new StraightLine();
        this.initialGenCreator = new Curl<>(DirectionNESW.class);

        this.mutators = new Mutator[2];
        this.mutators[0] = new SinglePoint<>(DirectionNESW.class, this.rand, Config.MUTATION_ATTEMPTS_PER_CANDIDATE,
                Config.MUTATION_CHANCE, Config.MUTATION_MULTIPLIER);
        this.mutators[1] = new Crossover<>(DirectionNESW.class, this.rand, Config.CROSSOVER_ATTEMPTS_PER_CANDIDATE,
                Config.CROSSOVER_CHANCE, Config.CROSSOVER_MULTIPLIER);

//        this.selector = new FitnessProportional(this.rand, this.isHydrophobic);
//        this.selector = new Tournament(this.rand, this.isHydrophobic, this.k);
        this.selector = new OnlyBest(this.isHydrophobic);

        this.evaluator = new EvaluatorNESW(Config.POINTS_PER_BOND);

        // Clear log file
        String content = "Generation\tAverage Fitness\tBest Fitness\tOverall Best Fitness\tBonds\tOverlaps\n";
        try {
            Files.write(Paths.get(Config.LOGFILE), content.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.population = this.initialGenCreator.initializeDirections(Config.POPULATION_SIZE, this.isHydrophobic);
        this.totalFitness = 0;
        this.fitness = new double[Config.POPULATION_SIZE];
        this.overallBestFitness = 0;
    }

    public void simulateGenerations() {
        for (int gen = 0; gen < Config.TOTAL_GENERATIONS-1; gen++) {
            this.evaluateGeneration(gen);
            this.population = this.selector.selectNewPopulation(this.population, this.fitness, this.totalFitness);

            for (Mutator m : mutators) { // SinglePoint and Crossover at the moment
                this.population = m.mutatePopulation(this.population);
            }

            System.out.println();
        }
        evaluateGeneration(Config.TOTAL_GENERATIONS-1);
    }

    private int evaluateGeneration(int gen) {
        // Evaluate current generation
        System.out.println("Generation " + gen + ":");

        double bestFitness = 0;
        int bestIndex = 0;
        this.totalFitness = 0;
        for (int i = 0; i < Config.POPULATION_SIZE; i++) {
            this.fitness[i] = this.evaluator.evaluateFitness(this.population[i]);
            this.totalFitness += this.fitness[i];

            if (this.fitness[i] > bestFitness) {
                bestFitness = this.fitness[i];
                bestIndex = i;
            }
        }
        int bonds = this.evaluator.evaluateBonds(this.population[bestIndex]);
        int overlaps = this.evaluator.evaluateOverlaps(this.population[bestIndex]);

        this.visualizer.setFilename(String.format("gen_%07d.jpg",gen));
        this.visualizer.drawProtein(this.population[bestIndex].getVertexList(), bestFitness, bonds, overlaps, gen);

        System.out.println("The fitness is: " + bestFitness
                    + " [hydrophobicBonds = " + bonds + " | overlaps = " + overlaps + "]");

        // Save the overall best
        if (bestFitness >= this.overallBestFitness) {
            this.overallBestFitness = bestFitness;
            this.overallBest = new Candidate(this.isHydrophobic, this.population[bestIndex].getOutgoing());
        }

        double averageFitness = this.totalFitness / Config.POPULATION_SIZE;
        String log = String.format("%d\t%.4f\t%.4f\t%.4f\t %d\t%d\n",
                gen, averageFitness, bestFitness,
                this.evaluator.evaluateFitness(overallBest),
                this.evaluator.evaluateBonds(overallBest),
                this.evaluator.evaluateOverlaps(overallBest));

        try {
            Files.write(Paths.get(Config.LOGFILE), log.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bestIndex;
    }

    public int getMaxH() {
        return this.visualizer.getMaxH();
    }

    public int getMaxW() {
        return  this.visualizer.getMaxH();
    }
}
