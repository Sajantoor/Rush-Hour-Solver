package ParseFile;

public class Car {
    private Point start; // x and y coords, first instance of car => this will be the top most, left most instance
    private Point end;  // x and y coords, last instance of car => this will be the bottom most, right most instance 
    private char name; // name of the car in the board
    private boolean isHorizontal; // the direction the car can move 

    public Car(char name) {
        this.name = name; 
        this.end = null; 
        this.start = null;
    }

    public Car(char name, Point start, Point end) {
        this.name = name;
        this.start = start; 
        this.end = end;
        calcDirection();
    }

    // setters and getters
    public void setStart(Point start) {
        this.start = start;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public char getName() {
        return this.name;
    }

    /** coords of top most / left most instance */
    public Point getStart() {
        return this.start;
    }
    /** coords of bottom most / right most instance */
    public Point getEnd() {
        return this.end;
    }

    public boolean isHorizontal() {
        return this.isHorizontal;
    }

    // gets the direction of the car
    private void calcDirection() {
        if (this.start == null && this.end == null) {
            return; 
        }

        if (this.start.getX() != this.end.getX()) {
            this.isHorizontal = true;
        } else {
            this.isHorizontal = false;
        }
    }

    // override the to string method
    public String toString() {
        return new String("Name: " + this.name + "\nStart: " + start + "\nEnd: " + end + "\nDir: " + (isHorizontal ? "Horizontal" : "Vertical"));
    }
}
