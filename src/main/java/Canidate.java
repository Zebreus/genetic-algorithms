import java.util.ArrayList;
import java.util.List;

public class Canidate {

    // Points per hydrophobic bond
    static int POINTS_PER_BOND = 1;

    int[] isHydrophobic;       // 0 = no | 1 = yes
    int[] outgoingDirection;   // 0 = North | 1 = East | 2 = South | 3 = West
    List<Vertex> vertexList;

    public Canidate(int[] isH, int[] oD) {
        this.isHydrophobic = isH;
        this.outgoingDirection = oD;
        this.vertexList = constructVertexes();
    }

    public double calculateFitness(boolean doOutput) {
        double fitness = 0d;

        int hydrophobicBonds = calculateBonds();
        int overlaps = calculateOverlaps();

        fitness =(double) (hydrophobicBonds * POINTS_PER_BOND) / (double) (overlaps + 1);

        if (doOutput) {
            System.out.println("The fitness is: " + fitness
                    + " [hydrophobicBonds = " + hydrophobicBonds + " | overlaps = " + overlaps + "]\n");
        }

        return fitness;
    }

    private List<Vertex> constructVertexes() {
        List<Vertex> vertexList = new ArrayList<Vertex>();
        int currentX = 0;
        int currentY = 0;

        for (int currentVertex = 0; currentVertex < isHydrophobic.length; currentVertex++) {
            vertexList.add(new Vertex(currentX, currentY,
                    isHydrophobic[currentVertex] == 1, outgoingDirection[currentVertex]));

            // Update position
            if (outgoingDirection[currentVertex] == 0) {
                currentY++;
            } else if (outgoingDirection[currentVertex] == 1) {
                currentX++;
            } else if (outgoingDirection[currentVertex] == 2) {
                currentY--;
            } else if (outgoingDirection[currentVertex] == 3) {
                currentX--;
            }
        }

        return vertexList;
    }

    private int calculateBonds() {
        int bonds = 0;

        for (int i = 0; i < vertexList.size() - 2; i++) {
            Vertex toCompare = vertexList.get(i);

            if (toCompare.isHydrophobic) {
                for (int j = i+2; j < vertexList.size(); j++) {
                    Vertex vertex = vertexList.get(j);
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

    private int calculateOverlaps() {
        int overlaps = 0;

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex toCompare = vertexList.get(i);
            for (Vertex vertex : vertexList) {
                if (toCompare.equalsPosition(vertex) && toCompare != vertex) {
                    overlaps++;
                }
            }
        }

        return overlaps / 2;
    }

    public void printProtein() {
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
        String[][] proteinArray = new String[maxY*2+1][maxX*2+1];
        for (int yIndex = 0; yIndex < maxY*2+1; yIndex++) {
            for (int xIndex = 0; xIndex < maxX*2+1; xIndex++) {
                proteinArray[yIndex][xIndex] = "   ";
            }
        }

        for (int i = 0; i < vertexList.size(); i++) {
            Vertex vertex = vertexList.get(i);
            if (proteinArray[vertex.y][vertex.x] == "   ") {
                if (vertex.isHydrophobic) {
                    proteinArray[vertex.y][vertex.x] = " x ";
                } else {
                    proteinArray[vertex.y][vertex.x] = " o ";
                }
            } else {
                proteinArray[vertex.y][vertex.x] = " z ";
            }

            // Add connection, except on the last one
            if (i+1 != vertexList.size()) {
                if (vertex.outgoingDirection == 0) {
                    proteinArray[vertex.y+1][vertex.x] = " | ";
                }
                else if (vertex.outgoingDirection == 1) {
                    proteinArray[vertex.y][vertex.x+1] = "---";
                }
                else if (vertex.outgoingDirection == 2) {
                    proteinArray[vertex.y-1][vertex.x] = " | ";
                }
                else if (vertex.outgoingDirection == 3) {
                    proteinArray[vertex.y][vertex.x-1] = "---";
                }
            }
        }

        for (int yIndex = maxY*2; yIndex >= 0; yIndex--) {
            for (int xIndex = 0; xIndex < maxX*2+1; xIndex++) {
                System.out.print(proteinArray[yIndex][xIndex]);
            }
            System.out.println();
        }
        System.out.println();
    }
}

// Helper class representing a single amino acid
class Vertex {
    int x;
    int y;
    boolean isHydrophobic;
    int outgoingDirection;

    public Vertex(int x, int y, boolean isHydrophobic, int outgoingDirection) {
        this.x = x;
        this.y = y;
        this.isHydrophobic = isHydrophobic;
        this.outgoingDirection = outgoingDirection;
    }

    public boolean equalsPosition(Vertex vertex) {
        return x == vertex.x && y == vertex.y;
    }

    public boolean neighbouringPosition(Vertex vertex) {
        if (x == vertex.x && y == vertex.y + 1) {
            return true; // South
        }
        else if (x == vertex.x && y == vertex.y -1) {
            return true; // North
        }
        else if (x == vertex.x + 1 && y == vertex.y + 1) {
            return true; // West
        }
        else if (x == vertex.x - 1 && y == vertex.y + 1) {
            return true; // East
        }
        else {
            return false;
        }
    }
}
