package Interfaces;

import Enums.State;
import MainClasses.Candidate;
import MainClasses.Cell;
import MainClasses.GeneticAlgorithm;
import MainClasses.Vertex;

import java.util.ArrayList;
import java.util.List;

public interface Visualizer {

    //TODO There should probably be a seperate datastructure for generation information/ga information
    void drawProtein(Candidate[] generation, GeneticAlgorithm geneticAlgorithm);

    static ArrayList<Vertex> deepCopyVertexList (List<Vertex> vertexListOriginal) {
        ArrayList<Vertex> vertexList = new ArrayList<>();
        for (Vertex v : vertexListOriginal) {
            Vertex vNew = new Vertex(v.x, v.y, v.outgoingDirection);
            vertexList.add(vNew);
        }
        return vertexList;
    }

    static Cell[][] convertProteinTo2DArray(ArrayList<Vertex> vertexList,  int[] isHydrophobic) {
        // Determine size
        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;
        for (Vertex vertex : vertexList) {
            if (vertex.x < minX) {
                minX = vertex.x;
            }
            if (vertex.x > maxX) {
                maxX = vertex.x;
            }
            if (vertex.y < minY) {
                minY = vertex.y;
            }
            if (vertex.y > maxY) {
                maxY = vertex.y;
            }
        }

        // Fix min values to 0 and double vertex coordinates to make room for connections
        maxX += Math.abs(minX);
        maxY += Math.abs(minY);
        for (Vertex vertex : vertexList) {
            vertex.x += Math.abs(minX);
            vertex.x *= 2;
            vertex.y += Math.abs(minY);
            vertex.y *= 2;
            vertex.isStretched = true;
        }

        // Add vertexes and connections to 2d array
        Cell[][] cellArray = new Cell[maxY * 2 + 1][maxX * 2 + 1];
        for (int yIndex = 0; yIndex < maxY * 2 + 1; yIndex++) {
            for (int xIndex = 0; xIndex < maxX * 2 + 1; xIndex++) {
                cellArray[yIndex][xIndex] = new Cell();
            }
        }

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex vertex = vertexList.get(i);
            if (isHydrophobic[i]==1) {
                cellArray[vertex.y][vertex.x].addState(State.Hydrophobic);
            } else {
                cellArray[vertex.y][vertex.x].addState(State.Hydrophilic);
            }
            cellArray[vertex.y][vertex.x].addAminoIndex(i);

            // Add connection, except on the last one
            if (i + 1 != vertexList.size()) {
                if (vertex.outgoingDirection == 0) {
                    cellArray[vertex.y + 1][vertex.x].addState(State.ConnectionVertical);
                } else if (vertex.outgoingDirection == 1) {
                    cellArray[vertex.y][vertex.x + 1].addState(State.ConnectionHorizontal);
                } else if (vertex.outgoingDirection == 2) {
                    cellArray[vertex.y - 1][vertex.x].addState(State.ConnectionVertical);
                } else if (vertex.outgoingDirection == 3) {
                    cellArray[vertex.y][vertex.x - 1].addState(State.ConnectionHorizontal);
                }
            }
        }

        return cellArray;
    }
}
