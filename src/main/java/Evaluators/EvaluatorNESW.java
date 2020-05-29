package Evaluators;

import Interfaces.Evaluator;
import MainClasses.Candidate;
import MainClasses.Config;
import MainClasses.Vertex;

public class EvaluatorNESW implements Evaluator {

    final int POINTS_PER_BOND;

    public EvaluatorNESW(int pointsPerBond) {
        this.POINTS_PER_BOND = pointsPerBond;
    }

    @Override
    public double evaluateFitness(Candidate candidate) {
        if (candidate.fitness <= -1) { // Not calculated before
            double fitness;

            int hydrophobicBonds = evaluateBonds(candidate);
            int overlaps = evaluateOverlaps(candidate);

            fitness =(double) (hydrophobicBonds * this.POINTS_PER_BOND) / (double) (overlaps + 1);

            candidate.fitness = fitness;
        }
        // Return cached value if this is not the first time the value is needed
        return candidate.fitness;
    }

    public int evaluateBonds(Candidate candidate) {

        int bonds = 0;

        for (int i = 0; i < candidate.vertexList.size() - 2; i++) {
            Vertex toCompare = candidate.vertexList.get(i);

            if (toCompare.isHydrophobic) {
                for (int j = i + 2; j < candidate.vertexList.size(); j++) {
                    Vertex vertex = candidate.vertexList.get(j);
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

    public int evaluateOverlaps(Candidate candidate) {

        int overlaps = 0;
        for (int i = 0; i < candidate.vertexList.size(); i++) {
            Vertex toCompare = candidate.vertexList.get(i);
            for (Vertex vertex : candidate.vertexList) {
                if (toCompare.equalsPosition(vertex) && toCompare != vertex) {
                    overlaps++;
                }
            }
        }
        overlaps /= 2;
        overlaps = overlaps;

        return overlaps;
    }
}
