package ParseFile;
import Utility.Functions;
import Utility.Constants;

public class Car {
    private Point coords; // x and y coords, first instance of car => this will be the top most, left most instance
    private boolean[] size; // values 0 - 5 stores the size of the car (3 bits)
    private boolean[] name; // values A - Z (26 letters, 5 bits) 
    private boolean isHorizontal; // the direction the car can move

    public Car(char name) {
        this.size = new boolean[Constants.COORD_BIN_LEN]; 
        this.name = new boolean[Constants.LETTERS_BIN]; 
        this.coords = null;
    }

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
    public void setStart(Point start) {
        this.coords = start;
    }

    public void setEnd(Point end) {
        // re calculate the direction and calculate size
        calcDirection(end);
        calcSize(end);
    }

    public char getName() {
        return (char) (Functions.binaryToDecimal(this.name, Constants.LETTERS_BIN) + Constants.ASCII_CAPITAL);
    }

    public Point getStart() {
        return this.coords;
    }

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

    public boolean isHorizontal() {
        return this.isHorizontal;
    }

    // gets the direction of the car
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

    private void calcSize(Point end) {
        int size;

        if (this.isHorizontal) {
            size = end.getX() - this.coords.getX();
        } else {
            size = end.getY() - this.coords.getY();
        }

        Functions.decimalToBinary(this.size, Constants.COORD_BIN_LEN, size);
    }

    // override the to string method
    public String toString() {
        return new String("Name: " + this.getName() + "\nStart: " + coords + "\nEnd: " + this.getEnd() + "\nDir: " + (isHorizontal ? "Horizontal" : "Vertical"));
    }
}
