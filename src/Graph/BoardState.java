package Graph;

import ParseFile.Board;
import ParseFile.Car;

import java.util.ArrayList;

public class BoardState {
    private final Board board;
    private int approximateDistance;
    private int currentDistance;
    private int hash;

    private BoardState parent;

    /**
     * 
     * @param board creates a new board state from a board
     */
    public BoardState(Board board) {
        this.board = board;
        approximateDistance = board.getHeuristicDistance();
        computeHash();
    }

    /**
     * 
     * @param carArray Creates a new board state from a car array
     */
    public BoardState(ArrayList<Car> carArray) {
        board = new Board(carArray);
        approximateDistance = board.getHeuristicDistance();
        computeHash();
    }

    /**
     * 
     * @param carArray Creates a new board state from a car array
     * @param shouldComputeDecentHeuristic flag to set if it should compute decent heurstic or not
     */
    public BoardState(ArrayList<Car> carArray, boolean shouldComputeDecentHeuristic){
        board = new Board(carArray, shouldComputeDecentHeuristic);
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

    public void setApproximateDistance(int approximateDistance) {
        this.board.setHeuristicDistance(approximateDistance);
        this.approximateDistance = approximateDistance;
    }

    public void computeApproximateDistance() {
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

    /**
     * Checks if two boards are equal
     * 
     * @param other board
     * 
     */
    public boolean equals(BoardState other) {
        if (hash != other.hashCode())
            return false;

        // compare hashcode of each car of the cars themselves, would be very very
        // unlikely to get a colision here
        int carArraySize = this.getBoard().getCars().size();
        if (carArraySize != other.getBoard().getCars().size())
            return false;

        return true;
    }

    /**
     * 
     * @param original what you want to clone
     * @return Clone of the car array list
     */
    private static ArrayList<Car> carListClone(ArrayList<Car> original) {
        ArrayList<Car> clone = new ArrayList<Car>();

        for (Car car : original) {
            clone.add(car);
        }

        return clone;
    }

    /**
     * @return an ArrayList of all board states that can be reached from this one
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

            // for each forward projection create new state
            for (Car forwardProjection : forwardList) {
                var nextList = carListClone(carList);
                nextList.set(index, forwardProjection);
                // generate a new board state with this projection instead of the car
                var newState = new BoardState(nextList);
                newState.setParent(this);
                newState.setCurrentDistance(currentDistance + 1);
                // add it to the reachable states list
                states.add(newState);
            }

            // for each backward projection create new state
            for (Car backwardsProjection : backwardsList) {
                var nextList = carListClone(carList);
                nextList.set(index, backwardsProjection);
                // generate a new board state with this projection instead of the car
                var newState = new BoardState(nextList);
                newState.setParent(this);
                newState.setCurrentDistance(this.currentDistance + 1);
                // add it to the reachable states list
                states.add(newState);
            }

        }

        return states;
    }

    // computes the hash for the given state of the board
    private void computeHash() {
        hash = board.hashCode();
    }
}
