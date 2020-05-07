import java.util.ArrayList;
import java.util.List;

public class Canidate {

    // Points per hydrophobic bond
    static int POINTS_PER_BOND = 1;

    int[] isHydrophobic;       // 0 = no | 1 = yes
    int[] outgoingDirection;   // 0 = North | 1 = East | 2 = South | 3 = West
    ArrayList<Vertex> vertexList;

    public Canidate(int[] isH, int[] oD) {
        this.isHydrophobic = isH;
        this.outgoingDirection = oD;
        this.vertexList = constructVertexes();
    }

    private ArrayList<Vertex> constructVertexes() {
        ArrayList<Vertex> vertexList = new ArrayList<>();
        int currentX = 0;
        int currentY = 0;

        for (int currentVertex = 0; currentVertex < isHydrophobic.length; currentVertex++) {
            vertexList.add(new Vertex(currentX, currentY,
                    isHydrophobic[currentVertex] == 1, outgoingDirection[currentVertex]));

            // Update position
            if (outgoingDirection[currentVertex] == 0) {
                currentY++;
            } else if (outgoingDirection[currentVertex] == 1) {
                currentX++;
            } else if (outgoingDirection[currentVertex] == 2) {
                currentY--;
            } else if (outgoingDirection[currentVertex] == 3) {
                currentX--;
            }
        }

        return vertexList;
    }

    public double calculateFitness(boolean doOutput) {
        double fitness = 0d;

        int hydrophobicBonds = calculateBonds();
        int overlaps = calculateOverlaps();

        fitness =(double) (hydrophobicBonds * POINTS_PER_BOND) / (double) (overlaps + 1);

        if (doOutput) {
            System.out.println("The fitness is: " + fitness
                    + " [hydrophobicBonds = " + hydrophobicBonds + " | overlaps = " + overlaps + "]\n");
        }

        return fitness;
    }

    public ArrayList<Vertex> getVertexList() {
        return vertexList;
    }

    private int calculateBonds() {
        int bonds = 0;

        for (int i = 0; i < vertexList.size() - 2; i++) {
            Vertex toCompare = vertexList.get(i);

            if (toCompare.isHydrophobic) {
                for (int j = i+2; j < vertexList.size(); j++) {
                    Vertex vertex = vertexList.get(j);
                    if (vertex.isHydrophobic) {
                        if (toCompare.neighbouringPosition(vertex)) {
                            bonds++;
                        }
                    }
                }
            }
        }

        return bonds;
    }

    private int calculateOverlaps() {
        int overlaps = 0;

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex toCompare = vertexList.get(i);
            for (Vertex vertex : vertexList) {
                if (toCompare.equalsPosition(vertex) && toCompare != vertex) {
                    overlaps++;
                }
            }
        }

        return overlaps / 2;
    }
}
