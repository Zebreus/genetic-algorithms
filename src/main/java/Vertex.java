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
        else if (x == vertex.x + 1 && y == vertex.y) {
            return true; // West
        }
        else if (x == vertex.x - 1 && y == vertex.y) {
            return true; // East
        }
        else {
            return false;
        }
    }
}