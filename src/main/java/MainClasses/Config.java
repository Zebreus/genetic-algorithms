package MainClasses;

import Enums.Selection;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    String propertyPath;
    Properties properties;

    static String LOGFILE;
    static int POPULATION_SIZE;
    static int TOTAL_GENERATIONS;
    static int MUTATION_ATTEMPTS_PER_CANDIDATE;
    static double MUTATION_CHANCE;
    static double MUTATION_MULTIPLIER;
    static int CROSSOVER_ATTEMPTS_PER_CANDIDATE;
    static double CROSSOVER_CHANCE;
    static double CROSSOVER_MULTIPLIER;
    static Selection SELECTION_VARIANT;
    static int K; // Number of selected Candidates to face off in a tournament selection
    static String IMAGE_SEQUENCE_PATH;

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

    // Points per hydrophobic bond
    static int POINTS_PER_BOND;

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
        LOGFILE = this.properties.getProperty("logfilePath");
        POPULATION_SIZE = Integer.parseInt(this.properties.getProperty("populationSize"));
        TOTAL_GENERATIONS = Integer.parseInt(this.properties.getProperty("noGenerations"));

        MUTATION_ATTEMPTS_PER_CANDIDATE = Integer.parseInt(this.properties.getProperty("mutationAttemptsPerCandidate"));
        MUTATION_CHANCE = Double.parseDouble(this.properties.getProperty("mutationChance"));
        MUTATION_MULTIPLIER = Double.parseDouble(this.properties.getProperty("mutationDecline"));
        CROSSOVER_ATTEMPTS_PER_CANDIDATE = Integer.parseInt(this.properties.getProperty("crossoverAttemptsPerCandidate"));
        CROSSOVER_CHANCE = Double.parseDouble(this.properties.getProperty("crossoverChance"));
        CROSSOVER_MULTIPLIER = Double.parseDouble(this.properties.getProperty("crossoverDecline"));

        K = Integer.parseInt(this.properties.getProperty("k"));

        try {
            if (this.properties.getProperty("selection").equals("proportional")) {
                SELECTION_VARIANT = Selection.Proportional;
            } else if (this.properties.getProperty("selection").equals("tournament")) {
                SELECTION_VARIANT = Selection.Tournament;
            } else if (this.properties.getProperty("selection").equals("onlybest")) {
                SELECTION_VARIANT = Selection.Tournament;
            } else {
                throw new Exception("Selection variant not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        IMAGE_SEQUENCE_PATH = properties.getProperty("imageSequencePath");
        POINTS_PER_BOND = Integer.parseInt(this.properties.getProperty("pointsPerBond"));
    }

    public Properties getProperties() {
        return this.properties;
    }

}
