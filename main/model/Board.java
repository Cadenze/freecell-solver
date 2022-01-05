package main.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import main.exceptions.DuplicateCardException;
import main.exceptions.MissingCardException;
import main.model.enums.Suit;

public class Board {
    private List<Card> cells;
    private int[] foundations;
    private List<List<Card>> cascades;

    private final int cellLimit = 4;
    private final int cascadeLimit = 8;

    /**
     * Constructs a new FreeCell board with shuffled deck.
     */
    public Board() {
        cells = new LinkedList<>();
        foundations = new int[4];
        cascades = new LinkedList<>();
        for (int i = 0; i < cascadeLimit; i++) { // initializes 8 empty cascades
            List<Card> cascade = new LinkedList<>();
            cascades.add(cascade);
        }
        deal(shuffle());
    }

    // TODO: constructor for user input

    public List<Card> getCells() {
        return cells;
    }

    public int[] getFoundations() {
        return foundations;
    }

    public List<List<Card>> getCascades() {
        return cascades;
    }

    /**
     * Makes a deck in order that is fresh off the printing press.
     * @return a deck of 52 cards, in a list
     */
    private List<Card> makeDeck() {
        List<Card> deck = new ArrayList<>();

        for (int i = 1; i <= 13; i++) {
            for (Suit s : Suit.values()) {
                Card c = new Card(i, s);
                deck.add(c);
            }
        }
        
        return deck;
    }

    /**
     * Shuffles a deck of cards.
     * @return a list of 52 cards, shuffled
     */
    public List<Card> shuffle() {
        List<Card> deck = makeDeck();
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Deals a deck of cards onto 8 cascades.
     * @param deck shuffled deck of 52 cards
     */
    public void deal(List<Card> deck) {
        while (!deck.isEmpty()) {
            for (int i = 0; i < cascadeLimit; i++) {
                cascades.get(i).add(deck.get(0));
                deck.remove(0);
            }
        }
    }

    /**
     * Verifies that the current gamestate is valid.
     * Checks that all 52 cards are present, and there are no duplicates.
     * @throws MissingCardException when card is missing from board
     * @throws DuplicateCardException when two of same card on board
     */
    public void verify() throws MissingCardException, DuplicateCardException {
        List<Card> board = new LinkedList<>(); // list of cards on board
        board.addAll(cells);
        board.addAll(cardsInFoundation());
        for (int i = 0; i < cascadeLimit; i++) {
            board.addAll(cascades.get(i));
        }

        List<Card> deck = makeDeck();
        
        while (!(board.isEmpty() && deck.isEmpty())) {
            if (deck.isEmpty()) {
                throw new DuplicateCardException(board.get(0).toString());
            }

            if (board.remove(deck.get(0))) {
                deck.remove(0);
            } else {
                throw new MissingCardException(deck.get(0).toString());
            }
        }
    }

    /**
     * Returns all the cards in foundation as a list
     * @return list of cards in foundation
     */
    private List<Card> cardsInFoundation() {
        List<Card> foundation = new LinkedList<>();
        Suit[] suits = Suit.values();
        assert suits.length == foundations.length;

        for (int i = 0; i < suits.length; i++) {
            for (int j = 1; j <= foundations[i]; j++) {
                Card c = new Card(j, suits[i]);
                foundation.add(c);
            }
        }

        return foundation;
    }

    @Override
    public String toString() {
        List<String> cellsPrint = new ArrayList<>();
        for (Card c : cells) {
            cellsPrint.add(c.toString());
        }

        List<String> foundationsPrint = new ArrayList<>();
        Suit[] suits = Suit.values();
        assert suits.length == foundations.length;
        for (int i = 0; i < suits.length; i++) {
            String card = foundations[i] + suits[i].toString();
            foundationsPrint.add(card);
        }

        List<List<String>> cascadesPrint = new LinkedList<>();
        for (int i = 0; i < cascadeLimit; i++) {
            List<String> cascadePrint = new LinkedList<>();
            for (Card c : cascades.get(i)) {
                cascadePrint.add(c.toString());
            }
            cascadesPrint.add(cascadePrint);
        }

        String output = "";
        output += "Cells:       " + String.join(" ", cellsPrint) + "\n";
        output += "Foundations: " + String.join(" ", foundationsPrint);
        for (int i = 0; i < cascadeLimit; i++) {
            output += "\n" + i + " " + String.join(" ", cascadesPrint.get(i));
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Board)) {
            return false;
        }

        Board b = (Board) o;
        return new HashSet<>(cells).equals(new HashSet<>(b.getCells())) &&
            foundations.equals(b.getFoundations()) &&
            new HashSet<>(cascades).equals(new HashSet<>(b.getCascades()));
    }

    // TODO: override hashcode
}