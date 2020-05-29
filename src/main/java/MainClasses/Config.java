package MainClasses;

import Enums.InitializationMethods;
import Enums.MutatorMethods;
import Enums.SelectionMethods;
import Enums.VisualizerMethods;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    String propertyPath;
    Properties properties;

    static String ENCODING_VARIANT;
    static int SEED;


    static int POPULATION_SIZE;
    static int TOTAL_GENERATIONS;
    static InitializationMethods INITIALIZATION_METHOD;
    static SelectionMethods SELECTION_METHOD;
    static int K; // Number of selected Candidates to face off in a tournament selection
    static MutatorMethods[] MUTATOR_METHODS;
    static int POINTS_PER_BOND; // Points per hydrophobic bond, default Evaluator will work the same with any value

    static int MUTATION_ATTEMPTS_PER_CANDIDATE;
    static double MUTATION_CHANCE;
    static double MUTATION_MULTIPLIER;
    static int CROSSOVER_ATTEMPTS_PER_CANDIDATE;
    static double CROSSOVER_CHANCE;
    static double CROSSOVER_MULTIPLIER;

    static String LOGFILE;
    static VisualizerMethods[] VISUALIZERS;
    static String IMAGE_SEQUENCE_PATH;
    static String VIDEO_PATH_AND_FILE;
    static int IMAGE_FPS;
    static int IMAGES_TO_FPS_INCREASE;
    static int IMAGE_FPS_MAX;
    static boolean ZOOM;

    // For images
    public static final Font font = new Font("Sans-Serif", Font.PLAIN, 15);
    public static final Color imageBackground = new Color(255, 255, 255);
    public static final Color imageConnection = new Color(0, 0, 0);
    public static final Color imageOutline = new Color(0, 0, 0);
    public static final Color imageHydrophobic = new Color(205, 0, 0);
    public static final Color imageHydrophilic = new Color(0, 0, 255);
    public static final Color imageMixed = new Color(205, 0, 205);
    public static final Color imageAminoText = new Color(0, 190, 190);
    public static final Color imageText = new Color(0,0,0);

    // For console output
    public static final String consoleEmpty = "   ";
    public static final String consoleHydrophobic = "(o)";
    public static final String consoleHydrophilic = "(i)";
    public static final String consoleHydrophobicMulti = "{o}";
    public static final String consoleHydrophilicMulti = "{i}";
    public static final String consoleMixed = "{z}";
    public static final String consoleConnectionVertical = " | ";
    public static final String consoleConnectionHorizontal = "---";

    public Config(String propertyPath) {
        this.propertyPath = propertyPath;
        this.properties = this.readProperties();
        this.initializeProperties();
    }

    private Properties readProperties() {
        Properties properties = new Properties();

        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propertyPath));
            properties.load(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private void initializeProperties() {

        // Basic Initialization settings
        ENCODING_VARIANT = this.properties.getProperty("encodingVariant");
        SEED = Integer.parseInt(this.properties.getProperty("seed"));


        // Algorithm settings
        POPULATION_SIZE = Integer.parseInt(this.properties.getProperty("populationSize"));
        TOTAL_GENERATIONS = Integer.parseInt(this.properties.getProperty("noGenerations"));

        switch (this.properties.getProperty("initializationMethod")) {
            case "curl":
                INITIALIZATION_METHOD = InitializationMethods.Curl;
                break;
            case "straight":
                INITIALIZATION_METHOD = InitializationMethods.Straight;
                break;
            case "random":
                INITIALIZATION_METHOD = InitializationMethods.Random;
                break;
        }

        switch (this.properties.getProperty("selectionMethod")) {
            case "proportional":
                SELECTION_METHOD = SelectionMethods.Proportional;
                break;
            case "tournament":
                SELECTION_METHOD = SelectionMethods.Tournament;
                break;
            case "onlybest":
                SELECTION_METHOD = SelectionMethods.OnlyBest;
                break;
        }

        K = Integer.parseInt(this.properties.getProperty("k"));

        String[] mutatorsToUse = this.properties.getProperty("mutatorMethods").split(",");
        MUTATOR_METHODS = new MutatorMethods[mutatorsToUse.length];
        for (int i = 0; i < mutatorsToUse.length; i++) {
            if (mutatorsToUse[i].equals("singlePoint")) {
                MUTATOR_METHODS[i] = MutatorMethods.SinglePoint;
            } else if (mutatorsToUse[i].equals("crossover")) {
                MUTATOR_METHODS[i] = MutatorMethods.Crossover;
            }
        }

        POINTS_PER_BOND = Integer.parseInt(this.properties.getProperty("pointsPerBond"));

        // Mutation settings
        MUTATION_ATTEMPTS_PER_CANDIDATE = Integer.parseInt(this.properties.getProperty("mutationAttemptsPerCandidate"));
        MUTATION_CHANCE = Double.parseDouble(this.properties.getProperty("mutationChance"));
        MUTATION_MULTIPLIER = Double.parseDouble(this.properties.getProperty("mutationMultiplier"));
        CROSSOVER_ATTEMPTS_PER_CANDIDATE = Integer.parseInt(this.properties.getProperty("crossoverAttemptsPerCandidate"));
        CROSSOVER_CHANCE = Double.parseDouble(this.properties.getProperty("crossoverChance"));
        CROSSOVER_MULTIPLIER = Double.parseDouble(this.properties.getProperty("crossoverMultiplier"));


        // Output settings
        LOGFILE = this.properties.getProperty("logfilePath");

        String[] visualizersToUse = this.properties.getProperty("visualizerType").split(",");
        VISUALIZERS = new VisualizerMethods[visualizersToUse.length];
        for (int i = 0; i < visualizersToUse.length; i++) {
            switch (visualizersToUse[i]) {
                case "console":
                    VISUALIZERS[i] = VisualizerMethods.Console;
                    break;
                case "image":
                    VISUALIZERS[i] = VisualizerMethods.Image;
                    break;
                case "video":
                    VISUALIZERS[i] = VisualizerMethods.Video;
                    break;
            }
        }

        IMAGE_SEQUENCE_PATH = this.properties.getProperty("imageSequencePath");
        VIDEO_PATH_AND_FILE = this.properties.getProperty("videoPathAndFile");
        IMAGE_FPS = Integer.parseInt(this.properties.getProperty("imgFps"));
        IMAGES_TO_FPS_INCREASE = Integer.parseInt(this.properties.getProperty("imagesToFpsIncrease"));
        IMAGE_FPS_MAX = Integer.parseInt(this.properties.getProperty("imgFpsMax"));
        ZOOM = this.properties.getProperty("zoom").equals("true");
    }

    public Properties getProperties() {
        return this.properties;
    }

}
