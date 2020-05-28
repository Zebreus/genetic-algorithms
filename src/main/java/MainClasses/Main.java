package MainClasses;

import Visualization.VideoCreator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        String propertyPath = "./src/main/resources/genetic.properties";
        Config config = new Config (propertyPath);

        int[] protein = Examples.convertStringToIntArray(Examples.SEQ20);
        GeneticAlgorithm ga = new GeneticAlgorithm(protein);
        ga.simulateGenerations();

//        if (Config.isVidoable && Config.doVideo)
        try {
            VideoCreator.createVideo(config.getProperties(), ga.getMaxH(), ga.getMaxW());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
