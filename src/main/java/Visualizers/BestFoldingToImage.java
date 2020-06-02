package Visualizers;

import Enums.State;
import Evaluators.EvaluatorNESW;
import Interfaces.Visualizer;
import MainClasses.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BestFoldingToImage implements Visualizer {

    int maxHeight;
    int maxWidth;

    // For image sizing
    int pixelsPerCell = 40;
    int margin = pixelsPerCell * 2;
    int outline = 2;
    final int[] isHydrophobic;
    Config config;

    public BestFoldingToImage(int[] isHydrophobic, Config config) {
        this.maxHeight = 0;
        this.maxWidth = 0;
        this.isHydrophobic = isHydrophobic;
        this.config = config;
        try {
            Files.createDirectories(Paths.get(config.getImageSequenceDirectory()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void drawProtein(Candidate[] generation, GeneticAlgorithm geneticAlgorithm) {
        String filename = config.getImageSequenceDirectory() + "/" + config.getJobName() + "_" + geneticAlgorithm.generation + ".png";

        //TODO This should probably be in the new Generation Class
        Candidate bestCandidateOfGeneration = generation[0];
        for (Candidate evaluatedCandidate : generation) {
            if(bestCandidateOfGeneration.getFitness() < evaluatedCandidate.getFitness()){
                bestCandidateOfGeneration=evaluatedCandidate;
            }
        }

        ArrayList<Vertex> vertexList = bestCandidateOfGeneration.getVertices();

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
        //TODO Get the labels from the new Generation class?
        EvaluatorNESW evaluator = new EvaluatorNESW(1,isHydrophobic);
        int bonds = evaluator.evaluateBonds(bestCandidateOfGeneration);
        int overlaps = evaluator.evaluateOverlaps(bestCandidateOfGeneration);
        String label = "Gen: " + geneticAlgorithm.generation
                + "     Fitness: " + String.format("%.4f", bestCandidateOfGeneration.getFitness())
                + "     H/H Bonds: " + bonds
                + "     Overlaps: " + overlaps;
        int labelWidth = metrics.stringWidth(label);
        int x = margin / 4;
        int y = margin / 4;
        g2.drawString(label, x, y);

        try {
            //ImageIO.write(image, "png", new File(folder + File.separator + filename));
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public int getMaxH() {
        return this.maxHeight;
    }

    public int getMaxW() {
        return  this.maxWidth;
    }
}
