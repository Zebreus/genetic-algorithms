import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int[] isHydrophobic =       {0, 0, 0, 1, 0, 1, 1, 0, 1}; // 0 = no | 1 = yes
        // Last outgoing direction has no influence on the protein
        int[] outgoingDirection =   {0, 1, 2, 1, 2, 3, 3, 1, 0}; // 0 = North | 1 = East | 2 = South | 3 = West

        Canidate c = new Canidate(isHydrophobic, outgoingDirection);
        c.calculateFitness(true);
        c.printProtein();



        Random rd = new Random();
        int proteinSize = 20;
        int[] isHydrophobicRandom = new int[proteinSize];
        int[] outgoingDirectionRandom = new int[proteinSize];

        for (int i = 0; i < proteinSize; i++) {
            isHydrophobicRandom[i] = rd.nextInt(2);
            outgoingDirectionRandom[i] = rd.nextInt(4);
        }

        System.out.print("Hydrophobic:        ");
        for (int a : isHydrophobicRandom) {
            System.out.print(a + " ");
        }
        System.out.println();

        System.out.print("Outgoing Direction: ");
        for (int a : outgoingDirectionRandom) {
            System.out.print(a + " ");
        }

        System.out.println();
        Canidate cRandom = new Canidate(isHydrophobicRandom, outgoingDirectionRandom);
        cRandom.calculateFitness(true);
        cRandom.printProtein();
    }
}
