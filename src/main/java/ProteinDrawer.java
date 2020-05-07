import java.util.ArrayList;
import java.util.List;

public class ProteinDrawer {

    String folder;
    String filename;

    // Used only for the console output
    String consoleEmpty = "   ";
    String consoleHydrophobic = " o ";
    String consoleHydrophilic = " i ";
    String consoleMultiple = " z ";
    String consoleConnectionVertical = " | ";
    String consoleConnectionHorizontal = "---";

    public ProteinDrawer(String folder, String filename) {
        this.folder = folder;
        this.filename = filename;
    }

    public void drawProteinToFile(ArrayList<Vertex> vertexListOriginal) {
        // Copy VertexList to be able to manipulate it
        ArrayList<Vertex> vertexList = deepCopyVertexList(vertexListOriginal);

        Block[][] proteinArray = convertProteinTo2DArray(vertexList);

        for (int yIndex = proteinArray.length-1; yIndex >= 0; yIndex--) {
            for (int xIndex = 0; xIndex < proteinArray[0].length; xIndex++) {
                System.out.print(convertBlockToString(proteinArray[yIndex][xIndex]));
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printProtein(ArrayList<Vertex> vertexListOriginal) {
        // Copy VertexList to be able to manipulate it
        ArrayList<Vertex> vertexList = deepCopyVertexList(vertexListOriginal);

        Block[][] proteinArray = convertProteinTo2DArray(vertexList);

        for (int yIndex = proteinArray.length-1; yIndex >= 0; yIndex--) {
            for (int xIndex = 0; xIndex < proteinArray[0].length; xIndex++) {
                System.out.print(convertBlockToString(proteinArray[yIndex][xIndex]));
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

    private Block[][] convertProteinTo2DArray(ArrayList<Vertex> vertexList) {
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
        Block[][] proteinArray = new Block[maxY * 2 + 1][maxX * 2 + 1];
        for (int yIndex = 0; yIndex < maxY * 2 + 1; yIndex++) {
            for (int xIndex = 0; xIndex < maxX * 2 + 1; xIndex++) {
                proteinArray[yIndex][xIndex] = new Block();
            }
        }

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex vertex = vertexList.get(i);
            if (vertex.isHydrophobic) {
                proteinArray[vertex.y][vertex.x].addState(State.Hydrophobic);
            } else {
                proteinArray[vertex.y][vertex.x].addState(State.Hydrophilic);
            }

            // Add connection, except on the last one
            if (i + 1 != vertexList.size()) {
                if (vertex.outgoingDirection == 0) {
                    proteinArray[vertex.y + 1][vertex.x].addState(State.ConnectionVertical);
                } else if (vertex.outgoingDirection == 1) {
                    proteinArray[vertex.y][vertex.x + 1].addState(State.ConnectionHorizontal);
                } else if (vertex.outgoingDirection == 2) {
                    proteinArray[vertex.y - 1][vertex.x].addState(State.ConnectionVertical);
                } else if (vertex.outgoingDirection == 3) {
                    proteinArray[vertex.y][vertex.x - 1].addState(State.ConnectionHorizontal);
                }
            }
        }

        return proteinArray;
    }

    private String convertBlockToString (Block block) {
        boolean multiple = false;
        if (block.states.size() > 1) {
            multiple = true;
        }

        State blockState = block.states.get(0);

        switch (blockState) {
            case Empty:
                return consoleEmpty;

            case Hydrophobic:
                if (multiple) {
                    return consoleMultiple;
                } else {
                    return consoleHydrophobic;
                }

            case Hydrophilic:
                if (multiple) {
                    return consoleMultiple;
                } else {
                    return consoleHydrophilic;
                }

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

class Block {
    ArrayList<State> states = new ArrayList<>();

    public Block () {
        states.add(State.Empty);
    }

    public void addState (State state) {
        if (states.get(0).equals(State.Empty)) {
            states.clear();
        }
        states.add(state);
    }
}

enum State {
    Empty,
    Hydrophobic,
    Hydrophilic,
    ConnectionVertical,
    ConnectionHorizontal
}