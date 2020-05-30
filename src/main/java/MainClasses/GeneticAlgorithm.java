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
import Visualization.Visualizers.PrintFoldingToConsole;
import Visualization.Visualizers.PrintFoldingToFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class GeneticAlgorithm {
    Random rand;

    Config config;

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
    public GeneticAlgorithm (int[] protein, Config config) {
        this.isHydrophobic =  protein;
        this.config = config;

        this.initializeSettings();
        this.clearLog();

        this.population = this.initialGenCreator.initializeDirections(config.getPopulationSize(), this.isHydrophobic.length);
        this.totalFitness = 0;
        this.fitness = new double[config.getPopulationSize()];
        this.overallBestFitness = 0;

    }

    private void initializeSettings() {
        if (config.getSeed() != -1) {
            this.rand = new Random(config.getSeed());
        } else {
            this.rand = new Random();
        }

        // Settings that are dependant on encoding
        if (config.getEncodingVariant().equals("NESW")) {
            int nullCount = 0;
            for (int i = 0; i < config.getVisualizers().length; i++) {
                if (!config.getVisualizers()[i].equals(VisualizerMethods.Console)
                        && !config.getVisualizers()[i].equals(VisualizerMethods.Image)) {
                    nullCount++;
                }
            }
            this.visualizers = new Visualizer[config.getVisualizers().length - nullCount];
            int j = 0;
            for (VisualizerMethods vm : config.getVisualizers()) {
                if (vm.equals(VisualizerMethods.Console)) {
                    this.visualizers[j] = new PrintFoldingToConsole(isHydrophobic, config);
                    j++;
                } else if (vm.equals(VisualizerMethods.Image)) {
                    this.visualizers[j] = new PrintFoldingToFile(isHydrophobic, config);
                    j++;
                }
            }

            if (config.getInitializationMethod().equals(InitializationMethods.Curl)) {
                this.initialGenCreator = new Curl<>(DirectionNESW.class);
            } else if (config.getInitializationMethod().equals(InitializationMethods.Straight)) {
                this.initialGenCreator = new StraightLine();
            } else if (config.getInitializationMethod().equals(InitializationMethods.Random)) {
                this.initialGenCreator = new RandomDirection<>(DirectionNESW.class, this.rand);
            }

            this.mutators = new Mutator[config.getMutatorMethods().length];
            for (int i = 0; i < config.getMutatorMethods().length; i++) {
                if (config.getMutatorMethods()[i].equals(MutatorMethods.SinglePoint)) {
                    this.mutators[i] = new SinglePoint<>(DirectionNESW.class, this.rand,
                            config.getMutationAttemptsPerCandidate(), config.getMutationChance(), config.getMutationMinimalChance(), config.getMutationMultiplier());

                } else if (config.getMutatorMethods()[i].equals(MutatorMethods.Crossover)) {
                    this.mutators[i] = new Crossover<>(DirectionNESW.class, this.rand,
                            config.getCrossoverAttemptsPerCandidate(), config.getCrossoverChance(), config.getCrossoverMinimalChance(), config.getCrossoverMultiplier());
                }
            }

            this.evaluator = new EvaluatorNESW(config.getPointsPerBond(), isHydrophobic);

        } else {
            // TODO: initialization for FRL settings
        }

        if (config.getSelectionMethod().equals(SelectionMethods.Proportional)) {
            this.selector = new FitnessProportional(this.rand, this.isHydrophobic);
        } else if (config.getSelectionMethod().equals(SelectionMethods.Tournament)) {
            this.selector = new Tournament(this.rand, this.isHydrophobic, config.getK());
        } else if (config.getSelectionMethod().equals(SelectionMethods.OnlyBest)) {
            this.selector = new OnlyBest(this.isHydrophobic);
        }
    }

    private void clearLog() {
        String content = "Generation\tAverage Fitness\tBest Fitness\tOverall Best Fitness\tBonds\tOverlaps\n";
        try {
            //TODO This does not belong here
            Files.createDirectories(Paths.get(config.getLogfileDirectory()));
            String logfilePath = config.getLogfileDirectory() + "/" + config.getJobName() + ".txt";
            Files.write(Paths.get(logfilePath), content.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simulateGenerations() {
        for (int gen = 0; gen < config.getTotalGenerations()-1; gen++) {
            this.evaluateGeneration(gen);
            this.population = this.selector.selectNewPopulation(this.population, this.fitness, this.totalFitness);

            for (Mutator m : mutators) { // SinglePoint and Crossover at the moment
                this.population = m.generateMutatedPopulation(this.population);
            }

            System.out.println();
        }
        evaluateGeneration(config.getTotalGenerations()-1);
    }

    private int evaluateGeneration(int gen) {
        // Evaluate current generation
        System.out.println("Generation " + gen + ":");

        double bestFitness = 0;
        int bestIndex = 0;
        this.totalFitness = 0;
        for (int i = 0; i < config.getPopulationSize(); i++) {
            this.population[i] = this.evaluator.evaluateFitness(this.population[i]);
            this.fitness[i] = this.population[i].getFitness();
            this.totalFitness += this.fitness[i];

            if (this.fitness[i] > bestFitness) {
                bestFitness = this.fitness[i];
                bestIndex = i;
            }
        }

        for (Visualizer v : this.visualizers) {
            String imagePath = config.getImageSequenceDirectory() + "/" + config.getJobName() + "_" + gen + ".png";
            v.setFilename(imagePath);
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

        double averageFitness = this.totalFitness / config.getPopulationSize();
        String log = String.format("%d\t%.4f\t%.4f\t%.4f\t %d\t%d\n",
                gen, averageFitness, bestFitness,
                this.overallBest.getFitness(),
                -1,
                -1);

        try {
            String logfilePath = config.getLogfileDirectory() + "/" + config.getJobName() + ".txt";
            Files.write(Paths.get(logfilePath), log.getBytes(), StandardOpenOption.APPEND);

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
