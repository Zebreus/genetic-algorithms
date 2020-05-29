package MainClasses;

import java.util.ArrayList;
import java.util.Arrays;

public class Candidate {

    int[] isHydrophobic;       // 0 = no | 1 = yes
    public int[] outgoingDirection;   // 0 = North | 1 = East | 2 = South | 3 = West
    public ArrayList<Vertex> vertexList;
    public double fitness;

    public Candidate(int[] isH, int[] oD) {
        this.isHydrophobic = isH;
        this.outgoingDirection = oD;
        this.vertexList = constructVertexes();

        this.fitness = -1d; // Not calculated yet
    }

    public ArrayList<Vertex> constructVertexes() {
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
