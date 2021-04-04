package ParseFile;
import Utility.Functions;
import Utility.Constants;

public class Point {
    // only need 3 bits to store values from 0 - 5
    private boolean[] x; 
    private boolean[] y; 

    // create new point with coords as params
    public Point(int x, int y) {
        // 3 bits required for each coord
        this.x = new boolean[Constants.COORD_BIN_LEN]; 
        this.y = new boolean[Constants.COORD_BIN_LEN];

        Functions.decimalToBinary(this.x, Constants.COORD_BIN_LEN, x);
        Functions.decimalToBinary(this.y, Constants.COORD_BIN_LEN, y);
    }

    // setters and getters 
    public int getX() {
        return Functions.binaryToDecimal(this.x, Constants.COORD_BIN_LEN);
    }

    public int getY() {
        return Functions.binaryToDecimal(this.y, Constants.COORD_BIN_LEN);
    }

    public void setX(int x) {
        Functions.decimalToBinary(this.x, Constants.COORD_BIN_LEN, x);
    }

    public void setY(int y) {
        Functions.decimalToBinary(this.y, Constants.COORD_BIN_LEN, y);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) 
            return true;

        // check hashcode 
        if (o.hashCode() != this.hashCode()) 
            return false;
        
        if (o.getClass() != this.getClass()) 
            return false;
        
        // i don't think the next 2 ifs are needed
        if (((Point) o).getX() != this.getX()) 
            return false;
        
        if (((Point) o).getY() != this.getY()) 
            return false;

        return true;
    }

    // Largest value would be 5 * 7 + 5 => 40 => 6 bits in binary which could be stored instead of x and y (same memory) 
    // if accessing hashcode is more important then x and y values => Can easily compute x and y from hashcode. 
    @Override
    public int hashCode() {
        // 7 is the smallest prime bigger than 5 (largest possible values, that's I choose it as the prime).  
        int result = Functions.binaryToDecimal(this.x, Constants.COORD_BIN_LEN);
        result = 7 * result + Functions.binaryToDecimal(this.y, Constants.COORD_BIN_LEN); 
        return result;
    }

    // to string for printing
    @Override
    public String toString() {
        return new String("X: " + Functions.binaryToDecimal(x, Constants.COORD_BIN_LEN)  + " Y: " + Functions.binaryToDecimal(y, Constants.COORD_BIN_LEN));
    }
}

