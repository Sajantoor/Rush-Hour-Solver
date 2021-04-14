package ParseFile;

import Utility.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.stream.Stream;

// class with methods to parse a board. 
public class Board {
    private ArrayList<Car> carArray; // array of car objects
    private final int heuristicDistance;
    /**
     * Constructor of the board, used to parse a board file.
     * 
     * @param fileName Filename of the board
     * @throws Exception Throws an exception if the filename is null or file is not
     *                   found
     */
    public Board(String fileName) throws Exception {
        if (fileName == null) {
            throw new FileNotFoundException("File is null in the file constructor.");
        }

        char[][] board = new char[Constants.SIZE][Constants.SIZE];
        this.carArray = new ArrayList<Car>();

        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            // file exists, scan and add to char array
            int y = 0;
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                // loop through string and add to char array
                for (int i = 0; i < data.length(); i++) {
                    board[i][y] = data.charAt(i);
                }

                y++;
            }

            scanner.close();
        } catch (Exception e) {
            throw new FileNotFoundException("File not found in the file constructor: " + e);
        }

        createObjectsFromBoard(board);

        heuristicDistance = computeHeuristicDistance(true);
    }

    public Board(ArrayList<Car> carArray) {
        this.carArray = carArray;
        heuristicDistance = computeHeuristicDistance(true);
    }

    public Board(ArrayList<Car> carArray, boolean shouldComputeDecentHeuristics){

        this.carArray = carArray;
        heuristicDistance = computeHeuristicDistance(shouldComputeDecentHeuristics);
    }

    /**
     * Creates Car objects from the char representation of the board array
     * 
     * @param board Char representation of the board
     */
    private void createObjectsFromBoard(char[][] board) {
        // parse board
        for (int i = 0; i < Constants.SIZE; i++) { // x
            for (int j = 0; j < Constants.SIZE; j++) { // y
                // locate car
                if (board[i][j] != '.') {
                    char car = board[i][j];
                    // check if car exists in carArray
                    boolean foundCar = false;

                    for (int k = 0; k < carArray.size(); k++) {
                        if (carArray.get(k).getName() == car) {
                            foundCar = true;
                            break;
                        }
                    }

                    // if not create new car array
                    if (!foundCar) {
                        Point start = new Point(i, j);
                        Point end = findLastInstance(board, car, i, j);
                        Car carObject = new Car(car, start, end);
                        carArray.add(carObject);
                    }
                }
            }
        }
    }

    /**
     * Finds the last instance of the car by looking right or down.
     * 
     * @param board Char representation of the board
     * @param car   Name of the car
     * @param x     x coordinate of the first instance of the car found
     * @param y     y coordinate of the first instance of the car found
     * @return Returns a new point with the coordinates of the last instance of the
     *         car
     */
    private Point findLastInstance(char[][] board, char car, int x, int y) {
        int lastX = x;
        int lastY = y;

        // if the immediate down is the car, look there
        if ((Constants.SIZE - 1 > y) && board[x][y + 1] == car) {
            // look down
            while (lastY < (Constants.SIZE - 1) && board[x][lastY + 1] == car) {
                lastY++;
            }
            // if the immediate right is the car, look there
        } else if ((Constants.SIZE - 1 > x) && board[x + 1][y] == car) {
            // look right
            while (lastX < (Constants.SIZE - 1) && board[lastX + 1][y] == car) {
                lastX++;
            }
        }

        return new Point(lastX, lastY);
    }

    /**
     * Gets the board's car array
     * 
     * @return The car array as a array list
     */
    public ArrayList<Car> getCars() {
        return carArray;
    }

    /**
     * Sets the board's car array
     * 
     * @param carArray Array list of the new car array
     */
    public void setCars(ArrayList<Car> carArray) {
        this.carArray = carArray;
    }

    // computes the heuristic distance needed for a player car to reach exit
    // currently adds 1 to heuristic whenever there's car on the way
    // TODO: do something smarter than this, i.e. add number of moves needed to get the car out of the way
    private int computeHeuristicDistance(boolean shouldComputeDecentHeuristics) {
        // get the playerCar from the board
        var playerCar = getCars().stream() // some java magic
                .filter(c -> c.getName() == 'X') // player car's name is 'X'
                .findAny() // filter will return a *list* with 1 element in it, and this will return the
                             // element
                .get(); // findFirst actually returns a wrapper class that is nullable, to get the Car
                        // instance do .get()

        var playerCarXEndCoord = playerCar.getEnd().getX();
        var playerCarYCoord = playerCar.getEnd().getY();

        // compute the heuristic as a distance till exit + 1 per each car in the way
        var heuristic = Constants.SIZE - playerCarXEndCoord - 1;

        // list of cars blocking the way
        var carsInTheWay = getCars() // get all cars
                .stream() // **java magic**
                .filter(c -> { // filter out the ones not blocking the way
                    if(c.getName() == 'X') return false;

                    var yStart = c.getStart().getY();
                    var yEnd = c.getEnd().getY();
                    var xEnd = c.getEnd().getX();
                    // car blocks the way if it crosses the player car row
                    // and is to the right of the car
                    var isOnTheWay = xEnd > playerCarXEndCoord // check that it is to the right
                            && yStart <= playerCarYCoord && playerCarYCoord <= yEnd; // check that it intersects the row
                    return isOnTheWay;
                });

        return heuristic + (shouldComputeDecentHeuristics? getHalfDecentHeuristics(carsInTheWay): getSimplestHeuristic());
    }

    /**
     * gets a heuristic approximation for the board in O(1)
     * @return pre-computed integer heuristic approximation
     */
    public int getHeuristicDistance(){
        return heuristicDistance;
    }
    /**
     *
     * @param carsInTheWay - list of cars blocking the way of the player car, not null
     * @return approximation on the amount of moves to clear the way
     */
    private int getCarsBlockedRecursiveHeuristic(Stream<Car> carsInTheWay){
        var cars = getCars();

        // phaseThroughMoves - number of moves to clear the way, if blocking cars were free to move
        // blockingCars - number of cars blocking the way to exit
        var wrapper = new Object(){int blockingCars = 0;};
        // represents each car by its letter
        boolean[] lookedAt = new boolean[26];

        var board = getBoard();
        var carBoard = getCarBoard();

        var blockingCarsQueue = new LinkedList<Car>();

        carsInTheWay.forEach(car -> {
            lookedAt[car.getName() - Constants.ASCII_CAPITAL] = true;
            var size = car.getSize() + 1;
            ArrayList<Car> blockingCars;

            // I tried to do different slightly heuristics depending on size of the car
            switch (size){
                case 3: {
                    // player car is always on row #2
                    // if a len-3 car is blocking its way, then
                    // it can be removed from the way only by going all the way down to the bottom of the board
                    blockingCars = car.getBlockingForwardCarList(carBoard);

                    break;
                }
                case 2:
                default:
                 {
                    // len-2 car is obstructing way
                    // to free the road it needs to either move 1 step in one way, or 2 steps in the other way
                    if(car.getStart().getY() == Constants.PLAYER_CAR_Y_COORD){
                        blockingCars = car.getBlockingNStepsBackwardsCarList(carBoard, 2);
                        var carBlockingForward = car.getBlockingForwardCar(carBoard);
                        if (carBlockingForward.isPresent()) blockingCars.add(carBlockingForward.get());
                    }
                    else {

                        blockingCars = car.getBlockingNStepsForwardCarList(carBoard, 2);
                        var carBlockingBackwards = car.getBlockingBackwardsCar(carBoard);
                        if (carBlockingBackwards.isPresent()) blockingCars.add(carBlockingBackwards.get());
                    }
                    break;
                }
            }

            // checking if the cars blocking main way are blocked:
            wrapper.blockingCars += blockingCars.size();

//            blockingCars.forEach(blockingCar -> {
//                // blocked, so will increase heuristic
//                // for bfs
//                if(!lookedAt[blockingCar.getName() - Constants.ASCII_CAPITAL]) {
//                    blockingCarsQueue.add(blockingCar);
//                    lookedAt[blockingCar.getName() - Constants.ASCII_CAPITAL] = true;
//                }
//            });

            wrapper.blockingCars++;
        });

        while(!blockingCarsQueue.isEmpty()){
            var car = blockingCarsQueue.poll();

            var blockingCars = car.getOneStepBlockingCars(carBoard);

            // almost same thing as the previous loop, but this is for the cars blocking
            // the cars that are blocking the main path, and so on
            // i did not try adding the free moves here
            blockingCars.forEach(blockingCar -> {
                wrapper.blockingCars++;
                if(!lookedAt[blockingCar.getName() - Constants.ASCII_CAPITAL]) {
                    blockingCarsQueue.add(blockingCar);
                    lookedAt[blockingCar.getName() - Constants.ASCII_CAPITAL] = true;
                }
            });
        }

        return wrapper.blockingCars;
    }
    private int getHalfDecentHeuristics(Stream<Car> carsInTheWay){
        var wrapper = new Object(){int count;};
        carsInTheWay.forEach(c -> {wrapper.count++;});
        return wrapper.count;
    }
    private int getSimplestHeuristic(){
        return 0;
    }

    // for debugging
    private void printBoard(char[][] board) {

        System.out.println("--------------------");
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.println("--------------------");
    }

    // for debugging
    public void printBoard() {
        printBoard(getBoard());
    }

    // also for debugging
    public void printCarObjects() {
        for (int i = 0; i < carArray.size(); i++) {
            System.out.println("--------------------");
            System.out.println(carArray.get(i));
            System.out.println("--------------------");
        }
    }
    // for debugging
    public char[][] getBoard() {
        char[][] board = new char[Constants.SIZE][Constants.SIZE];
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                board[i][j] = Constants.EMPTY_FIELD;
            }
        }

        carArray.forEach(car -> {
            var startX = car.getStart().getX();
            var startY = car.getStart().getY();
            var endX = car.getEnd().getX();
            var endY = car.getEnd().getY();
            var name = car.getName();
            for (int i = startY; i <= endY; ++i) {
                for (int j = startX; j <= endX; ++j) {
                    board[i][j] = name;
                }
            }
        });

        return board;
    }
    // this is either stupid or genius I am not sure yet
    // with this we can get wreck car by board[i][j]
    public Car[][] getCarBoard() {
        var board = new Car[Constants.SIZE][Constants.SIZE];

        carArray.forEach(car -> {
            var startX = car.getStart().getX();
            var startY = car.getStart().getY();
            var endX = car.getEnd().getX();
            var endY = car.getEnd().getY();
            for (int i = startY; i <= endY; ++i) {
                for (int j = startX; j <= endX; ++j) {
                    board[i][j] = car;
                }
            }
        });

        return board;
    }
    /**
     * Computes the hashcode for the car array.
     * 
     * @return Returns the hashcode of all the cars' hashcode summed up.
     */
    public int computeCarArrayHashCode() {
        int result = 0;
        // add up the hashcodes for all the cars in the array
        for (int i = 0; i < carArray.size(); i++) {
            result = result * 47 + carArray.get(i).hashCode();
        }

        return result;
    }

    /**
     * Returns the same thing as {@link #computeCarArrayHashCode()}
     */
    @Override
    public int hashCode() {
        return computeCarArrayHashCode();
    }

    /**
     * 
     * @param other A board, same board but different state, find difference between
     *              them. Other is a state in the future.
     * @return A string with the differences in the boards
     */
    public String findDifference(Board other) {
        // number of cars is the same
        ArrayList<Car> thisCarsList = this.getCars();
        ArrayList<Car> otherCarList = other.getCars();

        // the cars may be in different orders, no point sorting since n is small
        for (int i = 0; i < thisCarsList.size(); i++) {
            for (int j = 0; j < otherCarList.size(); j++) {

                Car car = thisCarsList.get(i);
                Car otherCar = otherCarList.get(j);
                // if they have different names ignore.
                if (car.getName() != otherCar.getName()) {
                    continue;
                }

                // if they aren't equal => find what's difference and output that
                if (!car.equals(otherCar)) {
                    int x = car.getEnd().getX();
                    int otherX = otherCar.getEnd().getX();
                    // compare x values
                    if (x > otherX) {
                        return new String(car.getName() + "R" + (x - otherX)) + "\n";
                    } else if (x < otherX) {
                        return new String(car.getName() + "L" + (otherX - x) + "\n");
                    }

                    int y = car.getEnd().getY();
                    int otherY = otherCar.getEnd().getY();
                    // compare y values
                    if (y > otherY) {
                        return new String(car.getName() + "D" + (y - otherY) + "\n");
                    } else if (y < otherY) {
                        return new String(car.getName() + "U" + (otherY - y) + "\n");
                    }
                }
            }
        }

        // they are the same
        return null;
    }
}
