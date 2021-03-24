package ParseFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;

// class with methods to parse a board. 
public class Board {
    private final int SIZE = 6; 
    private char[][] board; 
    private ArrayList<Car> carArray; // array of car objects

    // char representation of the board
    public Board(String fileName) throws Exception {
        if (fileName == null) {
            throw new FileNotFoundException("File is null in the file constructor.");
        }        
        this.board = new char[SIZE][SIZE]; // char array representation of the board (temporary)
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
        char lastInstance = car;

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

        System.out.println(lastX +" " + lastY);

        return new Point(lastX, lastY);
    }


    // for debugging
    public void printBoard() {
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
}
