package main.model.enums;

public enum Suit {
    HEARTS (0, "H"),
    CLUBS (1, "C"),
    DIAMONDS (2, "D"),
    SPADES (3, "S");

    private final int value; // numbering the suits
    private final String shorthand;

    Suit(int value, String shorthand) {
        this.value = value;
        this.shorthand = shorthand;
    }

    public boolean getColour() {
        return value % 2 == 0; // true if red, false if black
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return shorthand;
    }
}
