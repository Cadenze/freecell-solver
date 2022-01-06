package main.model;

import main.exceptions.NotACardException;
import main.model.enums.Suit;

public class Card {
    private int number;
    private Suit suit;

    /**
     * Creates a playing card with a number and a suit
     * @param number between 1 and 13, 1 for A, 11 for J, 12 for Q, 13 for K
     * @param suit the four suits, Hearts, Clubs, Diamonds, & Spades
     */
    public Card(int number, Suit suit) {
        this.number = number;
        this.suit = suit;
    }

    public Card(String card) throws NotACardException {
        if (card.length() != 2) {
            throw new NotACardException();
        }

        int number;
        Suit suit;
        
        switch(card.charAt(0)) {
            case 'A': number = 1; break;
            case 'T': number = 10; break;
            case 'J': number = 11; break;
            case 'Q': number = 12; break;
            case 'K': number = 13; break;
            default: number = Integer.parseInt(card.substring(0, 1));
        }
        
        switch(card.charAt(1)) {
            case 'H': suit = Suit.HEARTS; break;
            case 'C': suit = Suit.CLUBS; break;
            case 'D': suit = Suit.DIAMONDS; break;
            case 'S': suit = Suit.SPADES; break;
            default: throw new NotACardException();
        }

        this.number = number;
        this.suit = suit;
    }

    public int getNumber() {
        return number;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Card)) {
            return false;
        }

        Card c = (Card) o;
        return number == c.getNumber() && suit == c.getSuit();
    }

    @Override
    public int hashCode() {
        return number * 4 + suit.getValue();
    }

    @Override
    public String toString() {
        String output;
        switch (number) {
            case 1: output = "A"; break;
            case 10: output = "T"; break;
            case 11: output = "J"; break;
            case 12: output = "Q"; break;
            case 13: output = "K"; break;
            default: output = Integer.toString(number);
        }
        return output + suit.toString();
    }
}
