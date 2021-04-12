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

    // Largest value would be 5 * 7 + 5 => 40 => 6 bits in binary which could be
    // stored instead of x and y (same memory)
    // if accessing hashcode is more important then x and y values => Can easily
    // compute x and y from hashcode.
    @Override
    public int hashCode() {
        // 7 is the smallest prime bigger than 5 (largest possible values, that's I
        // choose it as the prime).
        int result = x;
        result += 7 * y;
        return result;
    }

    // to string for printing
    @Override
    public String toString() {
        return new String("X: " + x 
                        + " Y: " + y);
    }
}
