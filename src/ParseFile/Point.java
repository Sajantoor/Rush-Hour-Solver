package ParseFile;

public class Point {
    private int x; 
    private int y; 

    // create new point with coords as params
    public Point(int x, int y) {
        this.x = x; 
        this.y = y;
    }

    // setters and getters 
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // to string for printing
    public String toString() {
        return new String("X: " + x + " Y: " + y);
    }
}  

