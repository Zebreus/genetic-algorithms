package Visualization.Visualizers;

import Enums.State;
import Interfaces.Visualizer;
import MainClasses.Config;
import MainClasses.Vertex;
import Visualization.Cell;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VisualizerNESWtoConsole implements Visualizer {

    int maxHeight;
    int maxWidth;
    final int[] isHydrophobic;

    public VisualizerNESWtoConsole(int[] isHydrophobic) {
        this.maxHeight = 0;
        this.maxWidth = 0;
        this.isHydrophobic = isHydrophobic;
    }

    public void drawProtein(ArrayList<Vertex> vertexListOriginal, double fit, int bond, int over, int gen) {
        // Copy VertexList to be able to manipulate it
        ArrayList<Vertex> vertexList = Visualizer.deepCopyVertexList(vertexListOriginal);

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
                return Config.consoleEmpty;

            case Hydrophobic:
                return Config.consoleHydrophobic;

            case Hydrophilic:
                return Config.consoleHydrophilic;

            case HydrophobicMulti:
                return Config.consoleHydrophobicMulti;

            case HydrophilicMulti:
                return Config.consoleHydrophilicMulti;

            case Mixed:
                return Config.consoleMixed;

            case ConnectionVertical:
                return Config.consoleConnectionVertical;

            case ConnectionHorizontal:
                return Config.consoleConnectionHorizontal;

        }
        // Fallback
        return Config.consoleEmpty;
    }


    @Override
    public void setFilename(String format) {
        // Only here so file based visualizers work
    }

    @Override
    public int getMaxH() {
        return this.maxHeight;
    }

    @Override
    public int getMaxW() {
        return  this.maxWidth;
    }
}
