package Enums;

// TODO: Maybe actually replace these with a boolean and hard code...

public enum DirectionNESW {
    North(0),
    East(1),
    South(2),
    West(3);

    private final int value;
    private DirectionNESW(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
