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

    public boolean equals(BoardState other) { 
        // OPTIMIZE:
        // if you know you won't use the equals method with a null boardstate, delete this: 
        if (other == null)
            return false;
        
        // same with this if you know you won't the equals method with itself, delete this: 
        if (other == this) 
            return true;

        if (hash != other.hashCode()) 
            return false;
        
        // compare hashcode of each car of the cars themselves, would be very very unlikely to get a colision here
        int carArraySize = this.getBoard().getCars().size();
        if (carArraySize != other.getBoard().getCars().size()) 
            return false;

        // go through each car and compare their hashcodes
        for (int i = 0; i < carArraySize; i++) {
            if (this.getBoard().getCars().get(i).hashCode() != other.getBoard().getCars().get(i).hashCode()) 
                return false;
        }

        // OPTIMIZE: 
        // i think the above would be enough, this should be fast enough to determine whether or not they are equal but in the extreme case, i've written a bit more. 
        // Could probably comment this out and test and see if it improves anything or breaks everything lol
        for (int i = 0; i < carArraySize; i++) {
            // unreadbale code because i didn't want to import ParseFile.Cars lol 
            // if the cars are not equal return false
            if (!this.getBoard().getCars().get(i).equals(other.getBoard().getCars().get(i)))
                return false;
        }

        return true;
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
        hash = board.hashCode();
    }
}
