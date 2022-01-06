package main.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import main.exceptions.DuplicateCardException;
import main.exceptions.IllegalMoveException;
import main.exceptions.InvalidMoveException;
import main.exceptions.MissingCardException;
import main.model.enums.Suit;

public class Board {
    private Card[] cells;
    private int[] foundations;
    private List<List<Card>> cascades;

    private static final int cellLimit = 4;
    private static final int cascadeLimit = 8;

    public static final Board FINISHED = finishedBoard();

    public static final String allowedFrom = "ABCD12345678";
    public static final String allowedTo = "ABCD12345678F";

    /**
     * Constructs a new FreeCell board with shuffled deck.
     */
    public Board() {
        cells = new Card[cellLimit];
        foundations = new int[4];
        cascades = new LinkedList<>();
        for (int i = 0; i < cascadeLimit; i++) { // initializes 8 empty cascades
            List<Card> cascade = new LinkedList<>();
            cascades.add(cascade);
        }
        deal(shuffle());
    }

    // TODO: constructor for user input

    /**
     * Default constructor.
     * @param cells
     * @param foundations
     * @param cascades
     */
    private Board(Card[] cells, int[] foundations, List<List<Card>> cascades) {
        this.cells = cells;
        this.foundations = foundations;
        this.cascades = cascades;
    }

    /**
     * Makes a finished board.
     * @return all 52 cards in foundation
     */
    private static Board finishedBoard() {
        int[] found = {13, 13, 13, 13};
        List<List<Card>> cs = new LinkedList<>();
        for (int i = 0; i < cascadeLimit; i++) { // initializes 8 empty cascades
            List<Card> cascade = new LinkedList<>();
            cs.add(cascade);
        }
        return new Board(new Card[cellLimit], found, cs);
    }

    public Card[] getCells() {
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
        int counter = 0;
        while (!deck.isEmpty()) {
            cascades.get(counter).add(deck.remove(0));
            counter++;
            counter %= cascadeLimit;
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
        board.addAll(Arrays.asList(cells));
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

    /**
     * Moves a card on the board.
     * @param move
     * @throws InvalidMoveException
     * @throws IllegalMoveException
     */
    public void move(String move) throws InvalidMoveException, IllegalMoveException {
        String[] moves = decodeMove(move.toUpperCase());
        Card toMove;
        if (isAlpha(moves[0])) {
            Card temp = cells[alphanumeric(moves[0])];
            if (temp == null) {
                throw new IllegalMoveException();
            } else {
                toMove = temp;
            }
        } else {
            List<Card> list = cascades.get(Integer.parseInt(moves[0]) - 1);
            if (list.isEmpty()) {
                throw new IllegalMoveException();
            } else {
                toMove = list.get(list.size() - 1);
            }
        }

        if (isAlpha(moves[1])) {
            if (cells[alphanumeric(moves[1])] == null) {
                remove(moves[0]);
                add(moves[1], toMove);
            } else {
                throw new IllegalMoveException();
            }
        } else if (moves[1].equals("F")) {
            int index = toMove.getSuit().getValue();
            if (foundations[index] == toMove.getNumber() - 1) {
                foundations[index]++;
                remove(moves[0]);
            } else {
                throw new IllegalMoveException();
            }
        } else {
            List<Card> list = cascades.get(Integer.parseInt(moves[1]) - 1);
            if (list.isEmpty()) {
                remove(moves[0]);
                add(moves[1], toMove);
            } else {
                Card temp = list.get(list.size() - 1);
                if (temp.getNumber() == toMove.getNumber() + 1 && temp.getSuit().getColour() != toMove.getSuit().getColour()) {
                    remove(moves[0]);
                    add(moves[1], toMove);
                } else {
                    throw new IllegalMoveException();
                }
            }
        }
    }

    private void remove(String loci) throws InvalidMoveException {
        if (isAlpha(loci)) {
            cells[alphanumeric(loci)] = null;
        } else {
            List<Card> list = cascades.get(Integer.parseInt(loci) - 1);
            list.remove(list.size() - 1);
        }
    }

    private void add(String loci, Card card) throws InvalidMoveException {
        if (isAlpha(loci)) {
            cells[alphanumeric(loci)] = card;
        } else {
            cascades.get(Integer.parseInt(loci) - 1).add(card);
        }
    }

    private String[] decodeMove(String move) throws InvalidMoveException {
        if (move.length() != 2) {
            throw new InvalidMoveException();
        }

        String from = move.substring(0, 1);
        String to = move.substring(1);
        if (allowedFrom.contains(from) && allowedTo.contains(to)) {
            return new String[]{from, to};
        } else {
            throw new InvalidMoveException();
        }
    }

    private static boolean isAlpha(String position) {
        return position.equals("A") || position.equals("B") ||
            position.equals("C") || position.equals("D");
    }

    private static int alphanumeric(String position) throws InvalidMoveException {
        if (position.length() != 1) {
            throw new InvalidMoveException();
        }
        char pos = position.toCharArray()[0];
        return (int) pos - 65;
    }

    public Board duplicate() {
        return new Board(cells, foundations, cascades);
    }

    @Override
    public String toString() {
        List<String> cellsPrint = new ArrayList<>();
        for (Card c : cells) {
            if (c == null) {
                cellsPrint.add("--");
            } else {
                cellsPrint.add(c.toString());
            }
        }

        List<String> foundationsPrint = new ArrayList<>();
        Suit[] suits = Suit.values();
        assert suits.length == foundations.length;
        for (int i = 0; i < suits.length; i++) {
            if (foundations[i] == 0) {
                foundationsPrint.add("--");
            } else {
                String card = foundations[i] + suits[i].toString();
                foundationsPrint.add(card);
            }
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
            output += "\n" + (i+1) + " " + String.join(" ", cascadesPrint.get(i));
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
        return new HashSet<>(Arrays.asList(cells)).equals(new HashSet<>(Arrays.asList(b.getCells()))) &&
            foundations.equals(b.getFoundations()) &&
            new HashSet<>(cascades).equals(new HashSet<>(b.getCascades()));
    }

    // TODO: override hashcode
}