package MainClasses;

import Enums.*;
import Evaluators.EvaluatorNESW;
import InitialGenerationCreators.Curl;
import InitialGenerationCreators.RandomDirection;
import InitialGenerationCreators.StraightLine;
import Interfaces.*;
import Mutators.Crossover;
import Mutators.SinglePoint;
import Selectors.FitnessProportional;
import Selectors.OnlyBest;
import Selectors.Tournament;
import Visualization.Visualizers.VisualizerNESWtoConsole;
import Visualization.Visualizers.VisualizerNESWtoFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class GeneticAlgorithm {
    Random rand;

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
    Visualizer[] visualizers;

    // Initialize with protein
    public GeneticAlgorithm (int[] protein) {
        this.isHydrophobic =  protein;

        this.initializeSettings();
        this.clearLog();

        this.population = this.initialGenCreator.initializeDirections(Config.POPULATION_SIZE, this.isHydrophobic.length);
        this.totalFitness = 0;
        this.fitness = new double[Config.POPULATION_SIZE];
        this.overallBestFitness = 0;
    }

    private void initializeSettings() {
        if (Config.SEED != -1) {
            this.rand = new Random(Config.SEED);
        } else {
            this.rand = new Random();
        }

        // Settings that are dependant on encoding
        if (Config.ENCODING_VARIANT.equals("NESW")) {
            int nullCount = 0;
            for (int i = 0; i < Config.VISUALIZERS.length; i++) {
                if (!Config.VISUALIZERS[i].equals(VisualizerMethods.Console)
                        && !Config.VISUALIZERS[i].equals(VisualizerMethods.Image)) {
                    nullCount++;
                }
            }
            this.visualizers = new Visualizer[Config.VISUALIZERS.length - nullCount];
            int j = 0;
            for (VisualizerMethods vm : Config.VISUALIZERS) {
                if (vm.equals(VisualizerMethods.Console)) {
                    this.visualizers[j] = new VisualizerNESWtoConsole(isHydrophobic);
                    j++;
                } else if (vm.equals(VisualizerMethods.Image)) {
                    this.visualizers[j] = new VisualizerNESWtoFile(Config.IMAGE_SEQUENCE_PATH,isHydrophobic);
                    j++;
                }
            }

            if (Config.INITIALIZATION_METHOD.equals(InitializationMethods.Curl)) {
                this.initialGenCreator = new Curl<>(DirectionNESW.class);
            } else if (Config.INITIALIZATION_METHOD.equals(InitializationMethods.Straight)) {
                this.initialGenCreator = new StraightLine();
            } else if (Config.INITIALIZATION_METHOD.equals(InitializationMethods.Random)) {
                this.initialGenCreator = new RandomDirection<>(DirectionNESW.class, this.rand);
            }

            this.mutators = new Mutator[Config.MUTATOR_METHODS.length];
            for (int i = 0; i < Config.MUTATOR_METHODS.length; i++) {
                if (Config.MUTATOR_METHODS[i].equals(MutatorMethods.SinglePoint)) {
                    this.mutators[i] = new SinglePoint<>(DirectionNESW.class, this.rand,
                            Config.MUTATION_ATTEMPTS_PER_CANDIDATE, Config.MUTATION_CHANCE, Config.MUTATION_MULTIPLIER);

                } else if (Config.MUTATOR_METHODS[i].equals(MutatorMethods.Crossover)) {
                    this.mutators[i] = new Crossover<>(DirectionNESW.class, this.rand,
                            Config.CROSSOVER_ATTEMPTS_PER_CANDIDATE, Config.CROSSOVER_CHANCE, Config.CROSSOVER_MULTIPLIER);
                }
            }

            this.evaluator = new EvaluatorNESW(Config.POINTS_PER_BOND, isHydrophobic);

        } else {
            // TODO: initialization for FRL settings
        }

        if (Config.SELECTION_METHOD.equals(SelectionMethods.Proportional)) {
            this.selector = new FitnessProportional(this.rand, this.isHydrophobic);
        } else if (Config.SELECTION_METHOD.equals(SelectionMethods.Tournament)) {
            this.selector = new Tournament(this.rand, this.isHydrophobic, Config.K);
        } else if (Config.SELECTION_METHOD.equals(SelectionMethods.OnlyBest)) {
            this.selector = new OnlyBest(this.isHydrophobic);
        }
    }

    private void clearLog() {
        String content = "Generation\tAverage Fitness\tBest Fitness\tOverall Best Fitness\tBonds\tOverlaps\n";
        try {
            Files.write(Paths.get(Config.LOGFILE), content.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        for (Visualizer v : this.visualizers) {
            v.setFilename(String.format("gen_%d.png", gen));
            //TODO Print real bond and overlap amount
            v.drawProtein(this.population[bestIndex].getVertices(), bestFitness, -1, -1, gen);
        }

        //TODO Print real bond and overlap amount
        System.out.println("The fitness is: " + bestFitness
                    + " [hydrophobicBonds = " + -1 + " | overlaps = " + -1 + "]");

        // Save the overall best
        if (bestFitness >= this.overallBestFitness) {
            this.overallBestFitness = bestFitness;
            this.overallBest = new Candidate(this.population[bestIndex].getFolding());
        }

        double averageFitness = this.totalFitness / Config.POPULATION_SIZE;
        String log = String.format("%d\t%.4f\t%.4f\t%.4f\t %d\t%d\n",
                gen, averageFitness, bestFitness,
                this.evaluator.evaluateFitness(overallBest),
                -1,
                -1);

        try {
            Files.write(Paths.get(Config.LOGFILE), log.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bestIndex;
    }

    public int getMaxH() {
        int maxHAcrossVisualiszators = 0;
        for (Visualizer v : visualizers) {
            if (maxHAcrossVisualiszators < v.getMaxH()) {
                maxHAcrossVisualiszators = v.getMaxH();
            }
        }
        return maxHAcrossVisualiszators;
    }

    public int getMaxW() {
        int maxWAcrossVisualiszators = 0;
        for (Visualizer v : visualizers) {
            if (maxWAcrossVisualiszators < v.getMaxW()) {
                maxWAcrossVisualiszators = v.getMaxW();
            }
        }
        return maxWAcrossVisualiszators;
    }
}
