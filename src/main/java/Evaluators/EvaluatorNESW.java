package Evaluators;

import Interfaces.Evaluator;
import MainClasses.Candidate;
import MainClasses.Config;
import MainClasses.Vertex;

public class EvaluatorNESW implements Evaluator {

    final int POINTS_PER_BOND;
    final int[] isHydrophobic;

    public EvaluatorNESW(int pointsPerBond, int[] isHydrophobic) {
        this.POINTS_PER_BOND = pointsPerBond;
        this.isHydrophobic = isHydrophobic;
    }

    @Override
    public Candidate evaluateFitness(Candidate candidate) {
        //TODO Is it a good idea to "cache" fitness this way? Maybe
        //If fitness was not calculated yet, calculate it
        if (candidate.getFitness() <= -1) { // Not calculated before
            double fitness;

            int hydrophobicBonds = evaluateBonds(candidate);
            int overlaps = evaluateOverlaps(candidate);

            fitness =(double) (hydrophobicBonds * this.POINTS_PER_BOND) / (double) (overlaps + 1);

            candidate.setFitness(fitness);
        }

        return candidate;
    }

    public int evaluateBonds(Candidate candidate) {

        int bonds = 0;

        for (int i = 0; i < candidate.getVertices().size() - 2; i++) {
            Vertex toCompare = candidate.getVertices().get(i);

            if (isHydrophobic[i]==1) {
                for (int j = i + 2; j < candidate.getVertices().size(); j++) {
                    Vertex vertex = candidate.getVertices().get(j);
                    if (isHydrophobic[j]==1) {
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
        for (int i = 0; i < candidate.getVertices().size(); i++) {
            Vertex toCompare = candidate.getVertices().get(i);
            for (Vertex vertex : candidate.getVertices()) {
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
