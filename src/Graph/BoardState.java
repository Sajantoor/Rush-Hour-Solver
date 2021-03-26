package Graph;

import ParseFile.Board;

import java.util.ArrayList;

public class BoardState {
    private final Board board;
    private int approximateDistance;
    private int currentDistance;
    private int hash;

    private BoardState parent;

    public BoardState(Board board){
        this.board = board;
        approximateDistance = board.getHeuristicDistance();
        computeHash();
    }

    // getters
    public Board getBoard(){return board;}
    public int getApproximateDistance(){return approximateDistance;}
    public int getCurrentDistance(){return currentDistance;}
    public int getTotalDistance(){return currentDistance + approximateDistance;}
    public BoardState getParent() {return parent; }

    // setters
    public void setCurrentDistance(int distance){currentDistance = distance;}
    public void setParent(BoardState parent) { this.parent = parent; }


    /** returns true if player car is touching the right wall of the board*/
    public boolean isTarget(){return approximateDistance == 0;}

    /**  in O(1) returns the pre-computed hash for the given state */
    public int hashCode(){return hash;}

    public boolean equals(BoardState other){
        if(hash != other.hashCode()) return false;
        // TODO: optimize this
        return this.board.equals(other.board);
    }
    /** returns an ArrayList of all board states that can be reached from this one*/
    public ArrayList<BoardState> getReachableStates(){
        var states = new ArrayList<BoardState>();

        //TODO implement me
        var cars = board.getCars();


        return states;
    }

    // computes the hash for the given state of the board
    private void computeHash(){
        //TODO need to implement this method
        hash = 0;
    }
}
