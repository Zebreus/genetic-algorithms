import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int[] protein = new int[]{1,0,1,0,0,1,1,0,1,0,0,1,0,1,1,0,0,1,0,1};
        GeneticAlgorithm ga = new GeneticAlgorithm("log.txt", protein, 100, 1_000);
        ga.simulateGenerations();
        try {
            VideoCreator.createVideo("./visualization/video.mp4", GeneticAlgorithm.imageSeriesPath, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ProteinDrawer pdraw = new ProteinDrawer("./visualization/individual/", "image.jpg");
//
//
//        int[] isHydrophobic =       {0, 0, 0, 1, 0, 1, 1, 0, 0}; // 0 = no | 1 = yes
//        // Last outgoing direction has no influence on the protein
//        int[] outgoingDirection =   {0, 1, 2, 1, 2, 3, 3, 1, 0}; // 0 = North | 1 = East | 2 = South | 3 = West
//
//        Canidate c = new Canidate(isHydrophobic, outgoingDirection);
//        double[] fitnessBondOverlap = c.calculateFitness(true);
//        pdraw.printProtein(c.getVertexList());
//        pdraw.drawProteinToFile(c.getVertexList(), fitnessBondOverlap);

//        Random rd = new Random();
//        int proteinSize = 20;
//        int[] isHydrophobicRandom = new int[proteinSize];
//        int[] outgoingDirectionRandom = new int[proteinSize];
//
//        for (int i = 0; i < proteinSize; i++) {
//            isHydrophobicRandom[i] = rd.nextInt(2);
//            outgoingDirectionRandom[i] = rd.nextInt(4);
//        }
//
//        System.out.print("Hydrophobic:        ");
//        for (int a : isHydrophobicRandom) {
//            System.out.print(a + " ");
//        }
//        System.out.println();
//
//        System.out.print("Outgoing Direction: ");
//        for (int a : outgoingDirectionRandom) {
//            System.out.print(a + " ");
//        }
//
//        System.out.println();
//        Canidate cRandom = new Canidate(isHydrophobicRandom, outgoingDirectionRandom);
//        cRandom.calculateFitness(true);
//        pdraw.printProtein(cRandom.getVertexList());
    }
}
