package MainClasses;

import Enums.*;
import Evaluators.EvaluatorNESW;
import InitialGenerationCreators.Curl;
import InitialGenerationCreators.RandomDirection;
import InitialGenerationCreators.StraightLine;
import Interfaces.*;
import Mutators.Crossover;
import Mutators.SinglePoint;
import Mutators.SinglePointGlobalBend;
import Selectors.FitnessProportional;
import Selectors.OnlyBest;
import Selectors.Tournament;
import Visualizers.*;

import java.util.Random;

public class GeneticAlgorithm {
    Random rand;

    Config config;

    //TODO Make private again when the Generation class is created
    public int[] isHydrophobic;
    public Candidate[] population;

    public double totalFitness;
    public double[] fitness;

    public double overallBestFitness;
    public Candidate overallBest;

    public InitialGenerationCreator initialGenCreator;
    public Mutator[] mutators;
    public Selector selector;
    public Evaluator evaluator;
    public Visualizer[] visualizers;

    //TODO Remove again with the new Generation class
    public int generation;

    // Initialize with protein
    public GeneticAlgorithm (int[] protein, Config config) {
        this.isHydrophobic =  protein;
        this.config = config;

        this.initializeSettings();

        this.population = this.initialGenCreator.initializeDirections(config.getPopulationSize(), this.isHydrophobic.length);
        this.totalFitness = 0;
        this.fitness = new double[config.getPopulationSize()];
        this.overallBestFitness = 0;
        this.generation = 0;

    }

    private void initializeSettings() {
        if (config.getSeed() != -1) {
            this.rand = new Random(config.getSeed());
        } else {
            this.rand = new Random();
        }

        // Settings that are dependant on encoding
        if (config.getEncodingVariant().equals("NESW")) {
            this.visualizers = new Visualizer[config.getVisualizers().length];
            int j = 0;
            for (VisualizerMethods vm : config.getVisualizers()) {
                if (vm.equals(VisualizerMethods.Console)) {
                    this.visualizers[j] = new BestFoldingToConsole(isHydrophobic, config);
                    j++;
                } else if (vm.equals(VisualizerMethods.Image)) {
                    this.visualizers[j] = new BestFoldingToImage(isHydrophobic, config);
                    j++;
                }else if (vm.equals(VisualizerMethods.Log)) {
                    this.visualizers[j] = new GenerationProgressToLog(isHydrophobic, config);
                    j++;
                }else if (vm.equals(VisualizerMethods.Generation)) {
                    this.visualizers[j] = new GenerationOverviewToConsole(isHydrophobic, config);
                    j++;
                }else if (vm.equals(VisualizerMethods.Video)) {
                    this.visualizers[j] = new BestFoldingsToVideo(config);
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

                } else if (config.getMutatorMethods()[i].equals(MutatorMethods.SinglePointGlobal)) {
                    this.mutators[i] = new SinglePointGlobalBend<>(DirectionNESW.class, this.rand,
                            config.getMutationAttemptsPerCandidate(), config.getMutationChance(), config.getMutationMinimalChance(), config.getMutationMultiplier());
                }
            }

            this.evaluator = new EvaluatorNESW(config.getPointsPerBond(), isHydrophobic);

        } else {
            // TODO: initialization for FRL settings
        }

        if (config.getSelectionMethod().equals(SelectionMethods.Proportional)) {
            this.selector = new FitnessProportional(this.config, this.rand);
        } else if (config.getSelectionMethod().equals(SelectionMethods.Tournament)) {
            this.selector = new Tournament(this.config, this.rand);
        } else if (config.getSelectionMethod().equals(SelectionMethods.OnlyBest)) {
            this.selector = new OnlyBest();
        }
    }

    public void simulateGenerations() {
        evaluateGeneration();
        for (int gen = 0; gen < config.getTotalGenerations(); gen++) {
            //TODO Remove with the new Generation class

            visualizeGeneration();
            generation = gen+1;
            filterGeneration();
            mutateGeneration();
            evaluateGeneration();

        }
        visualizeGeneration();
    }

    //TODO These should all be in the new Generation class with definitions like
    //     mutateGeneration(Mutator e);
    private void evaluateGeneration() {
        for (int i = 0; i < population.length; i++) {

            this.population[i] = this.evaluator.evaluateFitness(this.population[i]);
        }
    }

    private void visualizeGeneration(){
        for (Visualizer v : this.visualizers) {
            v.drawProtein(this.population, this);
        }
    }

    private void mutateGeneration(){
        for (Mutator m : mutators) { // SinglePoint and Crossover at the moment
            this.population = m.generateMutatedPopulation(this.population);
        }
    }

    private void filterGeneration(){
        this.population = this.selector.selectNewPopulation(this.population);
    }

    public int getMaxH() {
        for (Visualizer v : visualizers) {
            if(v instanceof BestFoldingToImage){
                return ((BestFoldingToImage)v).getMaxH();
            }
        }
        return 0;
    }

    public int getMaxW() {
        for (Visualizer v : visualizers) {
            if(v instanceof BestFoldingToImage){
                return ((BestFoldingToImage)v).getMaxW();
            }
        }
        return 0;
    }
}
