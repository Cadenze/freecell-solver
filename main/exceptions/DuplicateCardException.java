package main.exceptions;

public class DuplicateCardException extends Exception {
    // throws when there are two of the same cards on board when verified

    public DuplicateCardException(String string) {
        super(string);
    }
}
