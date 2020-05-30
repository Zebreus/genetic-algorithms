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

    private String propertyPath;
    private Properties properties;

    private String encodingVariant;
    private int seed;


    private int populationSize;
    private int totalGenerations;
    private InitializationMethods initializationMethod;
    private SelectionMethods selectionMethod;
    private int k; // Number of selected Candidates to face off in a tournament selection
    private MutatorMethods[] mutatorMethods;
    private int pointsPerBond; // Points per hydrophobic bond, default Evaluator will work the same with any value

    private int mutationAttemptsPerCandidate;
    private double mutationChance;
    private double mutationMultiplier;
    private double mutationMinimalChance; // -> 0.01% is not worth mutating for
    private int crossoverAttemptsPerCandidate;
    private double crossoverChance;
    private double crossoverMinimalChance; // -> 0.01% is not worth mutating for
    private double crossoverMultiplier;

    private String logfile;
    private VisualizerMethods[] visualizers;
    private String imageSequencePath;
    private String videoPathAndFile;
    private int imageFps;
    private int imagesToFpsIncrease;
    private int imageFpsMax;
    private boolean zoom;

    // For images
    private final Font font = new Font("Sans-Serif", Font.PLAIN, 15);
    private final Color imageBackground = new Color(255, 255, 255);
    private final Color imageConnection = new Color(0, 0, 0);
    private final Color imageOutline = new Color(0, 0, 0);
    private final Color imageHydrophobic = new Color(205, 0, 0);
    private final Color imageHydrophilic = new Color(0, 0, 255);
    private final Color imageMixed = new Color(205, 0, 205);
    private final Color imageAminoText = new Color(0, 190, 190);
    private final Color imageText = new Color(0,0,0);

    // For console output
    private final String consoleEmpty = "   ";
    private final String consoleHydrophobic = "(o)";
    private final String consoleHydrophilic = "(i)";
    private final String consoleHydrophobicMulti = "{o}";
    private final String consoleHydrophilicMulti = "{i}";
    private final String consoleMixed = "{z}";
    private final String consoleConnectionVertical = " | ";
    private final String consoleConnectionHorizontal = "---";

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
        encodingVariant = this.properties.getProperty("encodingVariant");
        seed = Integer.parseInt(this.properties.getProperty("seed"));


        // Algorithm settings
        populationSize = Integer.parseInt(this.properties.getProperty("populationSize"));
        totalGenerations = Integer.parseInt(this.properties.getProperty("noGenerations"));

        switch (this.properties.getProperty("initializationMethod")) {
            case "curl":
                initializationMethod = InitializationMethods.Curl;
                break;
            case "straight":
                initializationMethod = InitializationMethods.Straight;
                break;
            case "random":
                initializationMethod = InitializationMethods.Random;
                break;
        }

        switch (this.properties.getProperty("selectionMethod")) {
            case "proportional":
                selectionMethod = SelectionMethods.Proportional;
                break;
            case "tournament":
                selectionMethod = SelectionMethods.Tournament;
                break;
            case "onlybest":
                selectionMethod = SelectionMethods.OnlyBest;
                break;
        }

        k = Integer.parseInt(this.properties.getProperty("k"));

        String[] mutatorsToUse = this.properties.getProperty("mutatorMethods").split(",");
        mutatorMethods = new MutatorMethods[mutatorsToUse.length];
        for (int i = 0; i < mutatorsToUse.length; i++) {
            if (mutatorsToUse[i].equals("singlePoint")) {
                mutatorMethods[i] = MutatorMethods.SinglePoint;
            } else if (mutatorsToUse[i].equals("crossover")) {
                mutatorMethods[i] = MutatorMethods.Crossover;
            }
        }

        pointsPerBond = Integer.parseInt(this.properties.getProperty("pointsPerBond"));

        // Mutation settings
        mutationAttemptsPerCandidate = Integer.parseInt(this.properties.getProperty("mutationAttemptsPerCandidate"));
        mutationChance = Double.parseDouble(this.properties.getProperty("mutationChance"));
        mutationMinimalChance = Double.parseDouble(this.properties.getProperty("mutationMinimalChance"));
        mutationMultiplier = Double.parseDouble(this.properties.getProperty("mutationMultiplier"));
        crossoverAttemptsPerCandidate = Integer.parseInt(this.properties.getProperty("crossoverAttemptsPerCandidate"));
        crossoverChance = Double.parseDouble(this.properties.getProperty("crossoverChance"));
        crossoverMinimalChance = Double.parseDouble(this.properties.getProperty("crossoverMinimalChance"));
        crossoverMultiplier = Double.parseDouble(this.properties.getProperty("crossoverMultiplier"));

        // Output settings
        logfile = this.properties.getProperty("logfilePath");

        String[] visualizersToUse = this.properties.getProperty("visualizerType").split(",");
        visualizers = new VisualizerMethods[visualizersToUse.length];
        for (int i = 0; i < visualizersToUse.length; i++) {
            switch (visualizersToUse[i]) {
                case "console":
                    visualizers[i] = VisualizerMethods.Console;
                    break;
                case "image":
                    visualizers[i] = VisualizerMethods.Image;
                    break;
                case "video":
                    visualizers[i] = VisualizerMethods.Video;
                    break;
            }
        }

        imageSequencePath = this.properties.getProperty("imageSequencePath");
        videoPathAndFile = this.properties.getProperty("videoPathAndFile");
        imageFps = Integer.parseInt(this.properties.getProperty("imgFps"));
        imagesToFpsIncrease = Integer.parseInt(this.properties.getProperty("imagesToFpsIncrease"));
        imageFpsMax = Integer.parseInt(this.properties.getProperty("imgFpsMax"));
        zoom = this.properties.getProperty("zoom").equals("true");
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getEncodingVariant() {
        return encodingVariant;
    }

    public int getSeed() {
        return seed;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getTotalGenerations() {
        return totalGenerations;
    }

    public InitializationMethods getInitializationMethod() {
        return initializationMethod;
    }

    public SelectionMethods getSelectionMethod() {
        return selectionMethod;
    }

    public int getK() {
        return k;
    }

    public MutatorMethods[] getMutatorMethods() {
        return mutatorMethods;
    }

    public int getPointsPerBond() {
        return pointsPerBond;
    }

    public int getMutationAttemptsPerCandidate() {
        return mutationAttemptsPerCandidate;
    }

    public double getMutationChance() {
        return mutationChance;
    }

    public double getMutationMultiplier() {
        return mutationMultiplier;
    }

    public double getMutationMinimalChance() {
        return mutationMinimalChance;
    }

    public int getCrossoverAttemptsPerCandidate() {
        return crossoverAttemptsPerCandidate;
    }

    public double getCrossoverChance() {
        return crossoverChance;
    }

    public double getCrossoverMinimalChance() {
        return crossoverMinimalChance;
    }

    public double getCrossoverMultiplier() {
        return crossoverMultiplier;
    }

    public String getLogfile() {
        return logfile;
    }

    public VisualizerMethods[] getVisualizers() {
        return visualizers;
    }

    public String getImageSequencePath() {
        return imageSequencePath;
    }

    public String getVideoPathAndFile() {
        return videoPathAndFile;
    }

    public int getImageFps() {
        return imageFps;
    }

    public int getImagesToFpsIncrease() {
        return imagesToFpsIncrease;
    }

    public int getImageFpsMax() {
        return imageFpsMax;
    }

    public boolean isZoom() {
        return zoom;
    }

    public Font getFont() {
        return font;
    }

    public Color getImageBackground() {
        return imageBackground;
    }

    public Color getImageConnection() {
        return imageConnection;
    }

    public Color getImageOutline() {
        return imageOutline;
    }

    public Color getImageHydrophobic() {
        return imageHydrophobic;
    }

    public Color getImageHydrophilic() {
        return imageHydrophilic;
    }

    public Color getImageMixed() {
        return imageMixed;
    }

    public Color getImageAminoText() {
        return imageAminoText;
    }

    public Color getImageText() {
        return imageText;
    }

    public String getConsoleEmpty() {
        return consoleEmpty;
    }

    public String getConsoleHydrophobic() {
        return consoleHydrophobic;
    }

    public String getConsoleHydrophilic() {
        return consoleHydrophilic;
    }

    public String getConsoleHydrophobicMulti() {
        return consoleHydrophobicMulti;
    }

    public String getConsoleHydrophilicMulti() {
        return consoleHydrophilicMulti;
    }

    public String getConsoleMixed() {
        return consoleMixed;
    }

    public String getConsoleConnectionVertical() {
        return consoleConnectionVertical;
    }

    public String getConsoleConnectionHorizontal() {
        return consoleConnectionHorizontal;
    }

}
