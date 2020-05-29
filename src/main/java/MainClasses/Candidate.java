package MainClasses;

import java.util.ArrayList;
import java.util.Arrays;

public class Candidate {

    int[] isHydrophobic;       // 0 = no | 1 = yes
    int[] outgoingDirection;   // 0 = North | 1 = East | 2 = South | 3 = West
    public ArrayList<Vertex> vertexList;
    public double fitness;

    public Candidate(int[] isH, int[] oD) {
        this.isHydrophobic = isH;
        this.outgoingDirection = oD;
        this.vertexList = constructVertexes();

        this.fitness = -1d; // Not calculated yet
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

    public ArrayList<Vertex> getVertexList() {
        return vertexList;
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
