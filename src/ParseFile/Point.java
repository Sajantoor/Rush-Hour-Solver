package ParseFile;

public class Point {
    private byte x;
    private byte y;

    /**
     * Creates a new point
     * 
     * @param x x coordinate
     * @param y y coordinate
     */
    public Point(int x, int y) {
        this.x = (byte) x;
        this.y = (byte) y;
    }

    // setters and getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = (byte) x;
    }

    public void setY(int y) {
        this.y = (byte) y;
    }

    /**
     * Compares two Points and determines if they are equal
     * 
     * @param o Other point
     * @return True if they are, false if not
     */
    public boolean equals(Point o) {
        if (o == null) {
            return false;
        }

        if (this == o)
            return true;

        // check hashcode
        if (o.hashCode() != this.hashCode())
            return false;

        return true;
    }

    /**
     * 
     * @return Hashcode for the Point object
     */
    @Override
    public int hashCode() {
        int result = x;
        result = result * 7 + y;
        return result;
    }

    /**
     * To string used for printing
     * 
     * @return string of the coordinates
     */
    @Override
    public String toString() {
        return new String("X: " + x + " Y: " + y);
    }
}
