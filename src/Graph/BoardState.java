package Graph;

import ParseFile.Board;
import ParseFile.Car;
import Utility.Statistics;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BoardState {
    private final Board board;
    private int approximateDistance;
    private int currentDistance;
    private int hash;

    private BoardState parent;

    public BoardState(Board board) {
        Statistics.numberOfBoardStatesCreated++;
        this.board = board;
        approximateDistance = board.getHeuristicDistance();
        computeHash();
    }

    public BoardState(ArrayList<Car> carArray) {
        Statistics.numberOfBoardStatesCreated++;
        board = new Board(carArray);
        approximateDistance = board.getHeuristicDistance();
        computeHash();
    }

    public BoardState(ArrayList<Car> carArray, boolean shouldComputeDecentHeuristic){

        Statistics.numberOfBoardStatesCreated++;
        board = new Board(carArray,shouldComputeDecentHeuristic);
        approximateDistance = board.getHeuristicDistance();
        computeHash();
    }

    // getters
    public Board getBoard() {
        return board;
    }

    public int getApproximateDistance() {
        return approximateDistance;
    }
    public void setApproximateDistance(int approximateDistance){
        this.board.setHeuristicDistance(approximateDistance);
        this.approximateDistance = approximateDistance;
    }
    public void computeDecentApproximateDistance(){
        this.board.setHeuristicDistance(this.board.computeHeuristicDistance(true));
        this.approximateDistance = this.board.getHeuristicDistance();
    }

    public int getCurrentDistance() {
        return currentDistance;
    }

    public int getTotalDistance() {
        return currentDistance + approximateDistance;
    }

    public BoardState getParent() {
        return parent;
    }

    // setters
    public void setCurrentDistance(int distance) {
        currentDistance = distance;
    }

    public void setParent(BoardState parent) {
        this.parent = parent;
    }

    /** returns true if player car is touching the right wall of the board */
    public boolean isTarget() {
        return approximateDistance == 0;
    }

    /** in O(1) returns the pre-computed hash for the given state */
    public int hashCode() {
        return hash;
    }

    public boolean equals(BoardState other) {
        if (hash != other.hashCode())
            return false;

        // compare hashcode of each car of the cars themselves, would be very very
        // unlikely to get a colision here
        int carArraySize = this.getBoard().getCars().size();
        if (carArraySize != other.getBoard().getCars().size())
            return false;

        // // go through each car and compare their hashcodes
        // for (int i = 0; i < carArraySize; i++) {
        //     if (this.getBoard().getCars().get(i).hashCode() != other.getBoard().getCars().get(i).hashCode())
        //         return false;
        // }

        return true;
    }

    private static ArrayList<Car> carListClone(ArrayList<Car> original) {
        ArrayList<Car> clone = new ArrayList<Car>();

        for (Car car : original) {
            clone.add(car);
        }

        return clone;
    }

    /**
     * returns an ArrayList of all board states that can be reached from this one
     */
    public ArrayList<BoardState> getReachableStates(boolean shouldCreateDecentHeuristics) {
        var states = new ArrayList<BoardState>();
        var carList = board.getCars();
        var iterator = carList.iterator();
        var charBoard = board.getBoard();

        while (iterator.hasNext()) {
            var car = iterator.next();
            var name = car.getName();
            var size = carList.size();
            int index = 0;

            // get index of car, linear search
            for (int i = 0; i < size; i++) {
                if (carList.get(i).getName() == name) {
                    index = i;
                    break;
                }
            }

            // try to move the car forward and backwards
            var forwardList = car.getMoveForwardsList(charBoard);
            var backwardsList = car.getMoveBackwardsList(charBoard);

            // for each forward projection check validaty and create new state
            for (Car forwardProjection : forwardList) {
                if (forwardProjection != null) {
                    var nextList = carListClone(carList);
                    // generate a new board state with this projection instead of the car
                    nextList.set(index, forwardProjection);

                    var newState = new BoardState(nextList, shouldCreateDecentHeuristics);
                    newState.setParent(this);
                    newState.setCurrentDistance(currentDistance + 1);
                    // add it to the reachable states list
                    states.add(newState);
                } 
            }

            for (Car backwardsProjection : backwardsList) {
                if (backwardsProjection != null) {
                    // generate a new board state with this projection instead of the car
                    var nextList = carListClone(carList);
                    // generate a new board state with this projection instead of the car
                    nextList.set(index, backwardsProjection);

                    var newState = new BoardState(nextList, shouldCreateDecentHeuristics);
                    newState.setParent(this);
                    newState.setCurrentDistance(this.currentDistance + 1);
                    // add it to the reachable states list
                    states.add(newState);
                } 
            }

        }

        return states;
    }

    // computes the hash for the given state of the board
    private void computeHash() {
        hash = board.hashCode();
    }
}
