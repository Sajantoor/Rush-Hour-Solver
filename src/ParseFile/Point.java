package ParseFile;
import Utility.Functions;
import Utility.Constants;

public class Point {
    private boolean[] x; // only need 3 bits to store values from 0 - 5
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


    // to string for printing
    @Override
    public String toString() {
        return new String("X: " + Functions.binaryToDecimal(x, Constants.COORD_BIN_LEN)  + " Y: " + Functions.binaryToDecimal(y, Constants.COORD_BIN_LEN));
    }
}  

