import java.util.ArrayList;
import java.util.Arrays;

public class Candidate {

    // Points per hydrophobic bond
    static int POINTS_PER_BOND = 1;

    int[] isHydrophobic;       // 0 = no | 1 = yes
    int[] outgoingDirection;   // 0 = North | 1 = East | 2 = South | 3 = West
    ArrayList<Vertex> vertexList;
    double fitness;
    int bonds;
    int overlaps;

    public Candidate(int[] isH, int[] oD) {
        this.isHydrophobic = isH;
        this.outgoingDirection = oD;
        this.vertexList = constructVertexes();

        this.fitness = -1d; // Not calculated yet
        this.bonds = 0;
        this.overlaps = 0;
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

    public double[] calculateFitness(boolean doOutput) {
        if (this.fitness <= -1) { // Not calculated before
            double fitness = 0d;

            int hydrophobicBonds = calculateBonds();
            int overlaps = calculateOverlaps();

            fitness =(double) (hydrophobicBonds * POINTS_PER_BOND) / (double) (overlaps + 1);

            this.fitness = fitness;
            this.bonds = hydrophobicBonds;
            this.overlaps = overlaps;
        }

        if (doOutput) {
            System.out.println("The fitness is: " + this.fitness
                    + " [hydrophobicBonds = " + this.bonds + " | overlaps = " + this.overlaps + "]");
        }

        // Return cached values if this is not the first time the values are needed
        return new double[]{this.fitness, this.bonds, this.overlaps};
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

    public void mutateDir(int mutationPlace, int mutation) {
        outgoingDirection[mutationPlace] = mutation;
        this.vertexList = constructVertexes();
    }

    public void crossover(Candidate partner, int crossoverPlace) {
        // Save this gene for a moment while skipping the part that stays
        int[] originalDirections = new int[outgoingDirection.length];
        for (int i = crossoverPlace; i < outgoingDirection.length; i++) {
            originalDirections[i] = outgoingDirection[i];
        }

        // Edit these directions
        for (int i = crossoverPlace; i < outgoingDirection.length; i++) {
            outgoingDirection[i] = partner.outgoingDirection[i];
        }

        // Edit partners directions
        for (int i = crossoverPlace; i < outgoingDirection.length; i++) {
            partner.outgoingDirection[i] = originalDirections[i];
        }

    }

    @Override
    public String toString() {
        return "Canidate{" +
                "outgoingDirection=" + Arrays.toString(outgoingDirection) +
                '}';
    }

    public int[] getOutgoing() {
        int[] newOut = new int[this.outgoingDirection.length];

        for(int i = 0; i < this.outgoingDirection.length; i++) {
            newOut[i] = this.outgoingDirection[i];
        }

        return newOut;
    }
}
