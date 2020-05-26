package Enums;

// TODO: Maybe actually replace these with a boolean and hard code...

public enum DirectionFRL {
    Forward(0),
    Right(1),
    Left(2);

    private final int value;
    private DirectionFRL(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
