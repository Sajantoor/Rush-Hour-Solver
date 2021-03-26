package ParseFile;

import Utility.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;

// class with methods to parse a board. 
public class Board {
    private char[][] board; 
    private ArrayList<Car> carArray; // array of car objects

    // char representation of the board
    public Board(String fileName) throws Exception {
        if (fileName == null) {
            throw new FileNotFoundException("File is null in the file constructor.");
        }        
        this.board = new char[Constants.SIZE][Constants.SIZE]; // char array representation of the board (temporary)
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

        createObjectsFromBoard();
    }

    private void createObjectsFromBoard() {
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
                        Point end = findLastInstance(car, i, j);
                        Car carObject = new Car(car, start, end);
                        carArray.add(carObject);   
                    }
                }
            }
        }
    }

    // look down, look right to find the last instance of the car
    private Point findLastInstance(char car, int x, int y) {
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
            while (lastX < (Constants.SIZE - 1) && board[lastX + 1][y] == car ) {
                lastX++;
            }
        }

        return new Point(lastX, lastY);
    }

    public ArrayList<Car> getCars() {
        return carArray;
    }

    public void setCars(ArrayList<Car> carArray) {
        this.carArray = carArray;
    }

    // computes the heuristic distance needed for a player car to reach exit
    // currently adds 1 to heuristic whenever there's car on the way
    // TODO: do something smarter than this, i.e. add number of moves needed to get the car out of the way
    public int getHeuristicDistance(){
        // get the playerCar from the board
        var playerCar = getCars()
                .stream() // some java magic
                .filter(c -> c.getName() == 'X') // player car's name is 'X'
                .findFirst() // filter will return a *list* with 1 element in it, and this will return the element
                .get(); // findFirst actually returns a wrapper class that is nullable, to get the Car instance do .get()

        var playerCarXEndCoord = playerCar.getEnd().getX();
        var playerCarYCoord = playerCar.getEnd().getY();

        // compute the heuristic as a distance till exit + 1 per each car in the way
        var heuristic = Constants.SIZE - playerCarXEndCoord - 1;

        // list of cars blocking the way
        var carsOnTheWay =  getCars() // get all cars
                .stream() // **java magic**
                .filter(c -> { // filter out the ones not blocking the way
                    var yStart = c.getStart().getY();
                    var yEnd = c.getEnd().getY();
                    var xEnd = c.getEnd().getX();
                    // car blocks the way if it crosses the player car row
                    // and is to the right of the car
                    var isOnTheWay = xEnd > playerCarXEndCoord  // check that it is to the right
                            && yStart <= playerCarYCoord && playerCarYCoord <= yEnd; // check that it intersects the row
                    return isOnTheWay;
        });

        heuristic += carsOnTheWay.count();

        return heuristic;
    }

    // for debugging
    public void printBoard() {
        for (int i = 0; i < Constants.SIZE; i++) {
            for (int j = 0; j < Constants.SIZE; j++) {
                System.out.print(board[j][i] + " ");
            }
            System.out.print("\n");
        }
    }
    
    // also for debugging
    public void printCarObjects() {
        for (int i = 0; i < carArray.size(); i++) {
            System.out.println("--------------------");
            System.out.println(carArray.get(i));
            System.out.println("--------------------");
        } 
    }
}
