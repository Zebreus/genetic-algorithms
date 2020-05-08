import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProteinDrawer {

    String folder;
    String filename;

    // Used only for the console output
    String consoleEmpty = "   ";
    String consoleHydrophobic = "(o)";
    String consoleHydrophilic = "(i)";
    String consoleHydrophobicMulti = "{o}";
    String consoleHydrophilicMulti = "{i}";
    String consoleMixed = "{z}";
    String consoleConnectionVertical = " | ";
    String consoleConnectionHorizontal = "---";

    // For image
    int pixelsPerCell = 40;
    int margin = pixelsPerCell * 2;
    int outline = 2;
    public static final Font font = new Font("Sans-Serif", Font.PLAIN, 15);
    public static final Color imageBackground = new Color(255, 255, 255);
    public static final Color imageConnection = new Color(0, 0, 0);
    public static final Color imageOutline = new Color(0, 0, 0);
    public static final Color imageHydrophobic = new Color(205, 0, 0);
    public static final Color imageHydrophilic = new Color(0, 0, 255);
    public static final Color imageMixed = new Color(205, 0, 205);
    public static final Color imageAminoText = new Color(180, 180, 180);
    public static final Color imageText = new Color(0,0,0);

    public static int maxHeight;
    public static int maxWidth;

    public ProteinDrawer(String folder, String filename) {
        this.folder = folder;
        this.filename = filename;

        maxHeight = 0;
        maxWidth = 0;
    }

    public void drawProteinToFile(ArrayList<Vertex> vertexListOriginal, double[] fitBondOver, int gen) {
        // Copy VertexList to be able to manipulate it
        ArrayList<Vertex> vertexList = deepCopyVertexList(vertexListOriginal);

        Cell[][] cellArray = convertProteinTo2DArray(vertexList);

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
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int ascent = metrics.getAscent();

        // Background
        g2.setColor(imageBackground);
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
                        aminoColor = imageHydrophobic;
                        break;

                    case Hydrophilic:
                    case HydrophilicMulti:
                        aminoColor = imageHydrophilic;
                        break;

                    case Mixed:
                        aminoColor = imageMixed;
                        break;

                    case ConnectionVertical:
                        g2.setColor(imageConnection);
                        g2.fillRect((xIndex * pixelsPerCell) + margin + (pixelsPerCell/2)-outline,
                                (yIndexPosition * pixelsPerCell) + margin,
                                outline * 2, pixelsPerCell);
                        break;

                    case ConnectionHorizontal:
                        g2.setColor(imageConnection);
                        g2.fillRect((xIndex * pixelsPerCell) + margin,
                                (yIndexPosition * pixelsPerCell) + margin + (pixelsPerCell/2)-outline,
                                pixelsPerCell, outline * 2);
                        break;
                }

                if (aminoColor != null) {
                    g2.setColor(imageOutline);
                    g2.fillRect((xIndex * pixelsPerCell) + margin, (yIndexPosition * pixelsPerCell) + margin,
                            pixelsPerCell, pixelsPerCell);
                    g2.setColor(aminoColor);
                    g2.fillRect((xIndex * pixelsPerCell) + outline + margin, (yIndexPosition * pixelsPerCell) + outline + margin,
                            pixelsPerCell - (outline * 2), pixelsPerCell - (outline * 2));

                    g2.setColor(imageAminoText);
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

        g2.setColor(imageText);
        String label = "Gen: " + gen
                + "     Fitness: " + String.format("%.4f", fitBondOver[0])
                + "     H/H Bonds: " + (int)fitBondOver[1]
                + "     Overlaps: " + (int)fitBondOver[2];
        int labelWidth = metrics.stringWidth(label);
        int x = margin / 4;
        int y = margin / 4;
        g2.drawString(label, x, y);

        if (!new File(folder).exists()) new File(folder).mkdirs();

        try {
            ImageIO.write(image, "jpg", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void printProtein(ArrayList<Vertex> vertexListOriginal) {
        // Copy VertexList to be able to manipulate it
        ArrayList<Vertex> vertexList = deepCopyVertexList(vertexListOriginal);

        Cell[][] cellArray = convertProteinTo2DArray(vertexList);

        for (int yIndex = cellArray.length-1; yIndex >= 0; yIndex--) {
            for (int xIndex = 0; xIndex < cellArray[0].length; xIndex++) {
                System.out.print(convertCellToString(cellArray[yIndex][xIndex]));
            }
            System.out.println();
        }
        System.out.println();
    }

    private ArrayList<Vertex> deepCopyVertexList (List<Vertex> vertexListOriginal) {
        ArrayList<Vertex> vertexList = new ArrayList<>();
        for (Vertex v : vertexListOriginal) {
            Vertex vNew = new Vertex(v.x, v.y, v.isHydrophobic, v.outgoingDirection);
            vertexList.add(vNew);
        }
        return vertexList;
    }

    private Cell[][] convertProteinTo2DArray(ArrayList<Vertex> vertexList) {
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
            if (vertex.isHydrophobic) {
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

    private String convertCellToString(Cell cell) {

        State cellState = cell.getRelevantDrawState ();

        switch (cellState) {
            case Empty:
                return consoleEmpty;

            case Hydrophobic:
                    return consoleHydrophobic;

            case Hydrophilic:
                    return consoleHydrophilic;

            case HydrophobicMulti:
                return consoleHydrophobicMulti;

            case HydrophilicMulti:
                return consoleHydrophilicMulti;

            case Mixed:
                return consoleMixed;

            case ConnectionVertical:
                return consoleConnectionVertical;

            case ConnectionHorizontal:
                return consoleConnectionHorizontal;

        }
        // Fallback
        return consoleEmpty;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

class Cell {
    ArrayList<State> states = new ArrayList<>();
    ArrayList<Integer> aminoIndexes = new ArrayList<>();

    public Cell() {
        states.add(State.Empty);
    }

    public void addState (State state) {
        if (states.get(0).equals(State.Empty)) {
            states.clear();
        }
        states.add(state);
    }

    public void addAminoIndex (int aminoIndex) {
        aminoIndexes.add(aminoIndex);
    }

    public State getRelevantDrawState () {
        State rc = states.get(0);
        if (states.size() > 1) {
            if (rc.equals(State.Hydrophobic)) {
                for (State s : states) {
                    if (s.equals(State.Hydrophilic)) {
                        return State.Mixed;
                    }
                }
                return State.HydrophobicMulti;

            } else if (rc.equals(State.Hydrophilic)) {
                for (State s : states) {
                    if (s.equals(State.Hydrophobic)) {
                        return State.Mixed;
                    }
                }
                return State.HydrophilicMulti;
            }
        }
        return rc;
    }
}

enum State {
    Empty,
    Hydrophobic,
    Hydrophilic,
    HydrophobicMulti,
    HydrophilicMulti,
    Mixed,
    ConnectionVertical,
    ConnectionHorizontal
}