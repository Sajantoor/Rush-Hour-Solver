package ParseFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;

// class with methods to parse a board. 
public class Board {
    private final int SIZE = 6; 
    private ArrayList<Car> carArray; // array of car objects

    /**
     * Constructor of the board, used to parse a board file.
     * @param fileName Filename of the board
     * @throws Exception Throws an exception if the filename is null or file is not found
     */
    public Board(String fileName) throws Exception {
        if (fileName == null) {
            throw new FileNotFoundException("File is null in the file constructor.");
        }  

        char[][] board = new char[SIZE][SIZE]; // char array representation of the board
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

    /**
     * Creates Car objects from the char representation of the board array
     * @param board Char representation of the board
     */
    private void createObjectsFromBoard(char[][] board) {
        // parse board
        for (int i = 0; i < SIZE; i++) { // x
            for (int j = 0; j < SIZE; j++) { // y
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
     * @param board Char representation of the board
     * @param car Name of the car
     * @param x x coordinate of the first instance of the car found
     * @param y y coordinate of the first instance of the car found
     * @return Returns a new point with the coordinates of the last instance of the car
     */
    private Point findLastInstance(char[][] board, char car, int x, int y) {
        int lastX = x; 
        int lastY = y;

        // if the immediate down is the car, look there
        if ((SIZE - 1 > y) && board[x][y + 1] == car) {
            // look down 
            while (lastY < (SIZE - 1) && board[x][lastY + 1] == car) {
                lastY++;
            }
        // if the immediate right is the car, look there
        } else if ((SIZE - 1 > x) && board[x + 1][y] == car) {
            // look right 
            while (lastX < (SIZE - 1) && board[lastX + 1][y] == car ) {
                lastX++;
            }
        }

        return new Point(lastX, lastY);
    }

    /**
     * Gets the board's car array
     * @return The car array as a array list
     */
    public ArrayList<Car> getCars() {
        return carArray;
    }

    /**
     * Sets the board's car array
     * @param carArray Array list of the new car array
     */
    public void setCars(ArrayList<Car> carArray) {
        this.carArray = carArray;
    }

    // for debugging
    private void printBoard(char[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
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

    /**
     * Computes the hashcode for the car array.
     * @return Returns the hashcode of all the cars's hashcode summed up.
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
}
