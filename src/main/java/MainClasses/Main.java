package MainClasses;

import Enums.VisualizerMethods;
import Visualization.VideoCreator;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        String propertyPath = "./src/main/resources/genetic.properties";
        Config config = new Config(propertyPath);

        int[] protein = Examples.convertStringToIntArray(Examples.SEQ20);
        GeneticAlgorithm ga = new GeneticAlgorithm(protein);
        ga.simulateGenerations();

        // Create a new video if possible and desired
        boolean imagesRefreshed = Arrays.stream(Config.VISUALIZERS).anyMatch(VisualizerMethods.Image::equals);
        boolean videoEnabled = Arrays.stream(Config.VISUALIZERS).anyMatch(VisualizerMethods.Video::equals);
        if (imagesRefreshed && videoEnabled){
            VideoCreator.createVideo(Config.IMAGE_SEQUENCE_PATH, Config.VIDEO_PATH_AND_FILE,
                    Config.IMAGE_FPS, ga.getMaxH(), ga.getMaxW());
        }
    }
}
