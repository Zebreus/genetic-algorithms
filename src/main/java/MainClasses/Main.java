package MainClasses;

import Enums.VisualizerMethods;
import Visualization.VideoCreator;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        String propertyPath = "./src/main/resources/genetic.properties";
        Config config = new Config(propertyPath);

        int[] protein = Examples.convertStringToIntArray(Examples.SEQ36);
        GeneticAlgorithm ga = new GeneticAlgorithm(protein, config);
        ga.simulateGenerations();

        // Create a new video if possible and desired
        boolean imagesRefreshed = Arrays.asList(config.getVisualizers()).contains(VisualizerMethods.Image);
        boolean videoEnabled = Arrays.asList(config.getVisualizers()).contains(VisualizerMethods.Video);
        if (imagesRefreshed && videoEnabled){
            VideoCreator.createVideo(config.getImageSequencePath(), config.getVideoPathAndFile(),
                config.getImageFps(), config.getImagesToFpsIncrease(), config.getImageFpsMax(),
                    ga.getMaxH(), ga.getMaxW(), config.isZoom());
        }
    }
}
