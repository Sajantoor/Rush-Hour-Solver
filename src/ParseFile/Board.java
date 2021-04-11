package ParseFile;

import Utility.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;

// class with methods to parse a board. 
public class Board {
    private ArrayList<Car> carArray; // array of car objects

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

        char[][] board = new char[Constants.SIZE][Constants.SIZE]; // char array representation of the board
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
    }

    public Board(ArrayList<Car> carArray) {
        this.carArray = carArray;
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
    // TODO: do something smarter than this, i.e. add number of moves needed to get
    // the car out of the way
    public int getHeuristicDistance() {
        // get the playerCar from the board
        var playerCar = getCars().stream() // some java magic
                .filter(c -> c.getName() == 'X') // player car's name is 'X'
                .findFirst() // filter will return a *list* with 1 element in it, and this will return the
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
                    var yStart = c.getStart().getY();
                    var yEnd = c.getEnd().getY();
                    var xEnd = c.getEnd().getX();
                    // car blocks the way if it crosses the player car row
                    // and is to the right of the car
                    var isOnTheWay = xEnd > playerCarXEndCoord // check that it is to the right
                            && yStart <= playerCarYCoord && playerCarYCoord <= yEnd; // check that it intersects the row
                    return isOnTheWay;
                });

        var wrapper = new Object() {
            int carValues = 0;
            int carCount = 0;
        };

        // basic implemenation of freedom of movement for each car blocking the way and
        // then freedom of movement of that car
        // TODO: Do something with the number of moves each car has and just something
        // smarter in general lol
        carsInTheWay.forEach(car -> {
            var forwardProjection = car.getMoveForwardProjection();
            var backwardsProjection = car.getMoveForwardProjection();

            // if going forward is blocked, increase value
            if (forwardProjection == null || forwardProjection.isWreckedIntoAnyOf(getCars())) {
                wrapper.carValues++;
            }

            if (backwardsProjection == null || backwardsProjection.isWreckedIntoAnyOf(getCars())) {
                wrapper.carValues++;
            }

            wrapper.carCount++;
        });

        return heuristic + wrapper.carCount + wrapper.carValues;
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

        printBoard(board);
    }

    // also for debugging
    public void printCarObjects() {
        for (int i = 0; i < carArray.size(); i++) {
            System.out.println("--------------------");
            System.out.println(carArray.get(i));
            System.out.println("--------------------");
        }
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
            result = result * 11 + carArray.get(i).hashCode();
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
