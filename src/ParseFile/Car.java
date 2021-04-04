package ParseFile;
import Utility.Functions;
import Utility.Constants;
import java.util.Arrays;

public class Car {
    private Point coords;  // x and y coords, first instance of car => this will be the top most, left most instance
    private boolean[] size; // values 0 - 5 stores the size of the car (3 bits)
    private boolean[] name; // values A - Z (26 letters, 5 bits) 
    private boolean isHorizontal; // the direction the car can move

    public Car(char name) {
        this.size = new boolean[Constants.COORD_BIN_LEN]; 
        this.name = new boolean[Constants.LETTERS_BIN]; 
        this.coords = null;
    }

    /** 
     * @param name The name of the car
     * @param start The starting (first instance) coordinates of the car of as the class Point 
     * @param end The last (last instance) coordinates of the car of as the class Point
     */
    public Car(char name, Point start, Point end) {
        this.size = new boolean[Constants.COORD_BIN_LEN]; 
        this.name = new boolean[Constants.LETTERS_BIN];
        Functions.decimalToBinary(this.name, Constants.LETTERS_BIN, name - Constants.ASCII_CAPITAL);
        this.coords = start; 
        calcDirection(end);
        // calc size requires the end coordinate
        calcSize(end);
    }

    // setters and getters
    /** 
     * @param start Coordinates of the first instance of the car as class Point.
     */
    public void setStart(Point start) {
        this.coords = start;
    }

    /**
     * @param end: Coordinate of the last instance of the car as class Point.
     */
    public void setEnd(Point end) {
        // re calculate the direction and calculate size
        calcDirection(end);
        calcSize(end);
    }

    /**
     * @return name of the car
     */
    public char getName() {
        return (char) (Functions.binaryToDecimal(this.name, Constants.LETTERS_BIN) + Constants.ASCII_CAPITAL);
    }


    /**
     * @return Coordinates of the first (top most/ left most) instance of the car of class Point
     */
    public Point getStart() {
        return this.coords;
    }

    /**
     * @return Cooridnates of the last (bottom most / right most) instance of the car of class Point
     */
    public Point getEnd() {
        int x = this.coords.getX();
        int y = this.coords.getY();

        if (this.isHorizontal) {
            x += Functions.binaryToDecimal(this.size, Constants.COORD_BIN_LEN);
        } else {
            y += Functions.binaryToDecimal(this.size, Constants.COORD_BIN_LEN);
        }

        return new Point(x, y);
    }

    /**
     * Gets the direction of the car.
     * @return Returns true if the car is horizontal, false if vertical.
     */
    public boolean isHorizontal() {
        return this.isHorizontal;
    }

    /**
     * Calculates the direction and changes the value of this.isHorizontal. 
     * Uses it's starting coordinates and ending coordinates to calculate
     * @param end Cooridnates of the last instance of the car of class Point
     */
    private void calcDirection(Point end) {
        if (this.coords == null && this.size == null) {
            return; 
        }

        if (this.coords.getX() != end.getX()) {
            this.isHorizontal = true;
        } else {
            this.isHorizontal = false;
        }
    }

    /**
     * Calculates the size of the car, requires the direction of the car to be calculated first.
     * This can be done using {@link #calcDirection(Point)}
     * @param end Cooridnates of the last instance of the car of class Point
     */
    private void calcSize(Point end) {
        int size;

        if (this.isHorizontal) {
            size = end.getX() - this.coords.getX();
        } else {
            size = end.getY() - this.coords.getY();
        }

        Functions.decimalToBinary(this.size, Constants.COORD_BIN_LEN, size);
    }

    @Override
    /**
     * @return String with the name, start and end coordinates, and direction of the car (on seperate lines).
     */
    public String toString() {
        return new String("Name: " + this.getName() + "\nStart: " + coords + "\nEnd: " + this.getEnd() + "\nDir: " + (isHorizontal ? "Horizontal" : "Vertical"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;

        if (this.hashCode() != o.hashCode()) 
            return false;
    
        if (o == null || this.getClass() != o.getClass()) return false;
        return isHorizontal == ((Car) o).isHorizontal() && coords.equals(((Car) o).coords) && Arrays.equals(size, ((Car) o).size) && Arrays.equals(name, ((Car) o).name);
    }

    @Override
    public int hashCode() {
        int result = coords.hashCode();
        result = 31 * result + Functions.binaryToDecimal(this.size, 3);
        result = 31 * result + Functions.binaryToDecimal(this.name, 3);
        return result;
    }
}
