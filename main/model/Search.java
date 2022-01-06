package main.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import main.exceptions.IllegalMoveException;
import main.exceptions.InvalidMoveException;
import main.persistence.TextReader;

public class Search {
    private Board current;
    private List<String> path;
    private Set<Board> seen;

    private List<Board> boardWL;
    private List<List<String>> pathWL;

    public static void main(String[] args) {
        TextReader tr = new TextReader("data/data.txt");
        new Search(tr.read());
    }

    public Search(Board board) { // trampoline
        current = board;
        path = new ArrayList<>();
        seen = new HashSet<>();
        boardWL = new LinkedList<>();
        pathWL = new LinkedList<>();
        List<String> result = searchBoard();
        System.out.println(result.toString());
    }

    private List<String> searchBoard() { // fn-for-board
        if (current.equals(Board.FINISHED)) {
            return path;
        } else if (seen.contains(current)) {
            return searchWorklist();
        } else {
            genrec();
            seen.add(current);
            return searchWorklist();
        }
    }

    private List<String> searchWorklist() { // fn-for-lob
        if (boardWL.isEmpty()) {
            List<String> output = new ArrayList<>();
            output.add("false");
            return output;
        } else {
            current = boardWL.remove(0);
            path = pathWL.remove(0);
            return searchBoard();
        }
    }

    private void genrec() {
        List<String> possibleMoves = new ArrayList<>();
        for (int i = 0; i < Board.allowedFrom.length(); i++) {
            for (int j = 0; j < Board.allowedTo.length(); j++) {
                String move = Board.allowedFrom.substring(i, i+1) + Board.allowedTo.substring(j, j+1);
                possibleMoves.add(move);
            }
        }

        List<Board> boardList = new LinkedList<>();
        List<List<String>> pathList = new LinkedList<>();
        for (String m : possibleMoves) {
            Board nova = current.duplicate();
            try {
                nova.move(m);
                boardList.add(nova);
                List<String> newPath = new LinkedList<>();
                newPath.addAll(path);
                newPath.add(m);
                pathList.add(newPath);
            } catch (InvalidMoveException | IllegalMoveException e) {
                
            }
        }
        boardWL.addAll(0, boardList);
        pathWL.addAll(0, pathList);
        assert boardWL.size() == pathWL.size();
    }
}
