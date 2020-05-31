package Visualizers;

import Enums.State;
import Interfaces.Visualizer;
import MainClasses.Candidate;
import MainClasses.Config;
import MainClasses.GeneticAlgorithm;
import MainClasses.Vertex;
import MainClasses.Cell;

import java.util.ArrayList;

public class BestFoldingToConsole implements Visualizer {

    final int[] isHydrophobic;
    Config config;

    public BestFoldingToConsole(int[] isHydrophobic, Config config) {
        this.isHydrophobic = isHydrophobic;
        this.config = config;
    }

    public void drawProtein(Candidate[] generation, GeneticAlgorithm geneticAlgorithm) {
        Candidate bestCandidateOfGeneration = generation[0];
        for (Candidate evaluatedCandidate : generation) {
            if(bestCandidateOfGeneration.getFitness() < evaluatedCandidate.getFitness()){
                bestCandidateOfGeneration=evaluatedCandidate;
            }
        }

        ArrayList<Vertex> vertexList = bestCandidateOfGeneration.getVertices();

        Cell[][] cellArray = Visualizer.convertProteinTo2DArray(vertexList, isHydrophobic);

        for (int yIndex = cellArray.length-1; yIndex >= 0; yIndex--) {
            for (int xIndex = 0; xIndex < cellArray[0].length; xIndex++) {
                System.out.print(convertCellToString(cellArray[yIndex][xIndex]));
            }
            System.out.println();
        }
        System.out.println();
    }

    private String convertCellToString(Cell cell) {

        State cellState = cell.getRelevantDrawState ();

        switch (cellState) {
            case Empty:
                return config.getConsoleEmpty();

            case Hydrophobic:
                return config.getConsoleHydrophobic();

            case Hydrophilic:
                return config.getConsoleHydrophilic();

            case HydrophobicMulti:
                return config.getConsoleHydrophobicMulti();

            case HydrophilicMulti:
                return config.getConsoleHydrophilicMulti();

            case Mixed:
                return config.getConsoleMixed();

            case ConnectionVertical:
                return config.getConsoleConnectionVertical();

            case ConnectionHorizontal:
                return config.getConsoleConnectionHorizontal();

        }
        // Fallback
        return config.getConsoleEmpty();
    }
}
