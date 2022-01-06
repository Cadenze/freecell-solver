package main.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import main.exceptions.DuplicateCardException;
import main.exceptions.MissingCardException;
import main.exceptions.NotACardException;
import main.model.Board;
import main.model.Card;

public class TextReader {
    private final String source;

    public TextReader(String source) {
        this.source = source;
    }

    public Board read() {
        File file = new File(source);
        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            return null;
        }
        List<List<Card>> cascades = new LinkedList<>();
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            String[] list = data.split(" ");
            List<Card> cascade = new LinkedList<>();
            for (String s : list) {
                Card c;
                try {
                    c = new Card(s);
                } catch (NotACardException e) {
                    System.out.println("Not a card!");
                    return null;
                }
                cascade.add(c);
            }
            cascades.add(cascade);
        }
        reader.close();
        try {
            return new Board(new Card[4], new int[4], cascades);
        } catch (MissingCardException e) {
            System.out.println("Missing card!");
            return null;
        } catch (DuplicateCardException e) {
            System.out.println("Duplicate card!");
            return null;
        }
    }
}
