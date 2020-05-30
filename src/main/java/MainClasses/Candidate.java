package MainClasses;

import java.util.ArrayList;
import java.util.Arrays;

public class Candidate {

    private final int[] folding;   // 0 = North | 1 = East | 2 = South | 3 = West
    private ArrayList<Vertex> vertices;
    private double fitness;

    public Candidate(int[] folding) {
        this.folding = folding;
        this.vertices = constructVertices();
        this.fitness = -1d; // Not calculated yet
    }

    public ArrayList<Vertex> constructVertices() {
        ArrayList<Vertex> vertexList = new ArrayList<>();
        int currentX = 0;
        int currentY = 0;

        for (int currentVertex = 0; currentVertex < folding.length; currentVertex++) {
            vertexList.add(new Vertex(currentX, currentY, folding[currentVertex]));

            // Update position
            if (folding[currentVertex] == 0) {
                currentY++;
            } else if (folding[currentVertex] == 1) {
                currentX++;
            } else if (folding[currentVertex] == 2) {
                currentY--;
            } else if (folding[currentVertex] == 3) {
                currentX--;
            }
        }

        return vertexList;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    @Override
    public String toString() {
        return "Canidate{" +
                "outgoingDirection=" + Arrays.toString(folding) +
                '}';
    }

    //TODO What if fitness is not set yet?
    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    //TODO Try out return folding without copy;
    public int[] getFolding() {
        int[] newOut = new int[this.folding.length];

        for(int i = 0; i < this.folding.length; i++) {
            newOut[i] = this.folding[i];
        }

        return newOut;
    }
}
