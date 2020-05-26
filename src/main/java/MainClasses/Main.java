package MainClasses;

import Visualization.VideoCreator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        String propertyPath = "./src/main/resources/genetic.properties";
        Config config = new Config (propertyPath);

//      int[] protein = new int[]{1,0,1,0,0,1,1,0,1,0,0,1,0,1,1,0,0,1,0,1};
        int[] protein = new int[]{1,1,0,1,0,1,0,1,0,1,1,1,1,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,1,1,1,1,0,1,0,1,0,1,0,1,1};
        GeneticAlgorithm ga = new GeneticAlgorithm(protein);
        ga.simulateGenerations();

        try {
            VideoCreator.createVideo(config.getProperties());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
