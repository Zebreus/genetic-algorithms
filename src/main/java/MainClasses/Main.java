package MainClasses;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        String propertyPath = "./src/main/resources/genetic.properties";


        try {
            Properties properties = new Properties();
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propertyPath));
            properties.load(stream);
            stream.close();

//          int[] protein = new int[]{1,0,1,0,0,1,1,0,1,0,0,1,0,1,1,0,0,1,0,1};
            int[] protein = new int[]{1,1,0,1,0,1,0,1,0,1,1,1,1,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,1,1,1,1,0,1,0,1,0,1,0,1,1};
            GeneticAlgorithm ga = new GeneticAlgorithm(properties, protein);
            ga.simulateGenerations();

            VideoCreator.createVideo(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        MainClasses.ProteinDrawer pdraw = new MainClasses.ProteinDrawer("./visualization/individual/", "image.jpg");
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
