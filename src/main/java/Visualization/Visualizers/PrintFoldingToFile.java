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

public class VisualizerNESWtoFile implements Visualizer {

    String folder;
    String filename;

    int maxHeight;
    int maxWidth;

    // For image sizing
    int pixelsPerCell = 40;
    int margin = pixelsPerCell * 2;
    int outline = 2;
    final int[] isHydrophobic;
    Config config;

    public VisualizerNESWtoFile(int[] isHydrophobic, Config config) {
        this.folder = config.getImageSequencePath();
        this.filename = "image.png"; // Default

        this.maxHeight = 0;
        this.maxWidth = 0;
        this.isHydrophobic = isHydrophobic;
        this.config = config;
    }

    public void drawProtein(ArrayList<Vertex> vertexListOriginal, double fit, int bond, int over, int gen) {
        // Copy VertexList to be able to manipulate it
        ArrayList<Vertex> vertexList = Visualizer.deepCopyVertexList(vertexListOriginal);

        Cell[][] cellArray = Visualizer.convertProteinTo2DArray(vertexList, this.isHydrophobic);

        int height = (cellArray.length * pixelsPerCell) + margin * 2;
        int width = (cellArray[0].length * pixelsPerCell) + margin * 2;

        if (height > maxHeight) {
            maxHeight = height;
        }
        if (width > maxWidth) {
            maxWidth = width;
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(config.getFont());
        FontMetrics metrics = g2.getFontMetrics();
        int ascent = metrics.getAscent();

        // Background
        g2.setColor(config.getImageBackground());
        g2.fillRect(0, 0, width, height);

        for (int yIndex = cellArray.length-1; yIndex >= 0; yIndex--) {
            int yIndexPosition = cellArray.length-1 - yIndex;

            for (int xIndex = 0; xIndex < cellArray[0].length; xIndex++) {
                Color aminoColor = null;

                State cellState = cellArray[yIndex][xIndex].getRelevantDrawState();

                switch (cellState) {
                    case Empty:
                        break;

                    case Hydrophobic:
                    case HydrophobicMulti:
                        aminoColor = config.getImageHydrophobic();
                        break;

                    case Hydrophilic:
                    case HydrophilicMulti:
                        aminoColor = config.getImageHydrophilic();
                        break;

                    case Mixed:
                        aminoColor = config.getImageMixed();
                        break;

                    case ConnectionVertical:
                        g2.setColor(config.getImageConnection());
                        g2.fillRect((xIndex * pixelsPerCell) + margin + (pixelsPerCell/2)-outline,
                                (yIndexPosition * pixelsPerCell) + margin,
                                outline * 2, pixelsPerCell);
                        break;

                    case ConnectionHorizontal:
                        g2.setColor(config.getImageConnection());
                        g2.fillRect((xIndex * pixelsPerCell) + margin,
                                (yIndexPosition * pixelsPerCell) + margin + (pixelsPerCell/2)-outline,
                                pixelsPerCell, outline * 2);
                        break;
                }

                if (aminoColor != null) {
                    g2.setColor(config.getImageOutline());
                    g2.fillRect((xIndex * pixelsPerCell) + margin, (yIndexPosition * pixelsPerCell) + margin,
                            pixelsPerCell, pixelsPerCell);
                    g2.setColor(aminoColor);
                    g2.fillRect((xIndex * pixelsPerCell) + outline + margin, (yIndexPosition * pixelsPerCell) + outline + margin,
                            pixelsPerCell - (outline * 2), pixelsPerCell - (outline * 2));

                    g2.setColor(config.getImageAminoText());
                    String label = "";
                    for (int aminoIndex : cellArray[yIndex][xIndex].aminoIndexes) {
                        label += aminoIndex + " ";
                    }
                    int labelWidth = metrics.stringWidth(label);
                    int x = (xIndex * pixelsPerCell) + margin + (pixelsPerCell/2) - (labelWidth/2) + outline;
                    int y = (yIndexPosition * pixelsPerCell) + margin + (pixelsPerCell/2) + (ascent/2) - outline;
                    g2.drawString(label, x, y);
                }
            }
        }

        g2.setColor(config.getImageText());
        String label = "Gen: " + gen
                + "     Fitness: " + String.format("%.4f", fit)
                + "     H/H Bonds: " + bond
                + "     Overlaps: " + over;
        int labelWidth = metrics.stringWidth(label);
        int x = margin / 4;
        int y = margin / 4;
        g2.drawString(label, x, y);

        if (!new File(folder).exists()) new File(folder).mkdirs();

        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
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
