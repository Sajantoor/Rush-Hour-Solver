package Graph;

import ParseFile.Board;
import ParseFile.Car;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
    public BoardState(ArrayList<Car> carArray){
        board = new Board(carArray);
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
        var carList = board.getCars();

        var iterator = carList.iterator();
        while(iterator.hasNext()){
            var car = iterator.next();

            // try to move the car forward and backwards
            var forwardProjection = car.getMoveForwardProjection();
            var backwardsProjection = car.getMoveBackwardsProjection();

            // all the cars on the board except for the one we are trying to move
            var restOfTheCarsStream = carList.stream().filter(c -> !c.equals(car));
            var restOfTheCars = restOfTheCarsStream.collect(Collectors.toList());

            // TODO: a better way instead of comparison with null?
            if(forwardProjection != null
                    && forwardProjection.isWithinBounds() // if forward projection does not go overboard
                    && !forwardProjection.isWreckedIntoAnyOf(restOfTheCars)) // and does not hit any of the other cars
            {
                // generate a new board state with this projection instead of the car
                var newCarList = new ArrayList<>(restOfTheCars);
                newCarList.add(forwardProjection);

                var newState = new BoardState(newCarList);
                newState.setParent(this);
                // add it to the reachable states list
                states.add(newState);
            }

            // TODO: a better way instead of comparison with null?
            if(backwardsProjection != null
                    && backwardsProjection.isWithinBounds() // if backwards projection does not go overboard
                    && !backwardsProjection.isWreckedIntoAnyOf(restOfTheCars)) // and does not hit any of the other cars
            {
                // generate a new board state with this projection instead of the car
                var newCarList = new ArrayList<>(restOfTheCars);
                newCarList.add(backwardsProjection);

                var newState = new BoardState(newCarList);
                newState.setParent(this);
                // add it to the reachable states list
                states.add(newState);
            }
        }

        return states;
    }

    // computes the hash for the given state of the board
    private void computeHash(){
        hash = board.hashCode();
    }
}
