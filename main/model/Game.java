package main.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import main.exceptions.IllegalMoveException;
import main.exceptions.InvalidMoveException;

public class Game {
    private Board current;
    private List<Board> history;

    public static void main(String[] args) {
        new Game();
    }

    public Game() {
        current = new Board();
        history = new LinkedList<>();
        loop();
    }

    public void loop() {
        Scanner kb = new Scanner(System.in);
        while (!current.equals(Board.FINISHED)) {
            System.out.println(current);
            System.out.print("Move: ");
            move(kb.nextLine());
            System.out.println("");
        }
        kb.close();
        System.out.println("Congratulations!");
        return;
    }

    public void move(String move) {
        Board nova = current.duplicate();
        if (move.equals("undo")) {
            if(history.isEmpty()) {
                System.out.println("No move to undo.");
            } else {
                current = history.remove(0);
            }
        }

        try {
            nova.move(move);
            history.add(0, current);
            current = nova;
        } catch (InvalidMoveException e) {
            System.out.println("Invalid move.");
        } catch (IllegalMoveException e) {
            System.out.println("Illegal move.");
        }
    }
}
