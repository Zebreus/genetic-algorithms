package MainClasses;

// Helper class representing a single amino acid
public class Vertex {
    public int x;
    public int y;
    public int outgoingDirection;
    public boolean isStretched; // Meaning it has room for connections

    public Vertex(int x, int y, int outgoingDirection) {
        this.x = x;
        this.y = y;
        this.outgoingDirection = outgoingDirection;
        this.isStretched = false;
    }

    public boolean equalsPosition(Vertex vertex) {
        return x == vertex.x && y == vertex.y;
    }

    public boolean neighbouringPosition(Vertex vertex) {
        int neighbourDistance = 1;
        if (isStretched) {
            neighbourDistance *= 2;
        }

        if (x == vertex.x && y == vertex.y + neighbourDistance) {
            return true; // South
        }
        else if (x == vertex.x && y == vertex.y -neighbourDistance) {
            return true; // North
        }
        else if (x == vertex.x + neighbourDistance && y == vertex.y) {
            return true; // West
        }
        else if (x == vertex.x - neighbourDistance && y == vertex.y) {
            return true; // East
        }
        else {
            return false;
        }
    }
}