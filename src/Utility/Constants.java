package Utility;

// I've made the constants static, so that we don't waste memory by storing them in each state of the board
/**
    A set of constants used in the solution
 */
public class Constants{
    public static final int SIZE = 6;
    public static final char EMPTY_FIELD = '.';
    /** Length of the Point.java coordinate binary array */
    public static final int COORD_BIN_LEN = 3; 
    /** Used to convert char to binary array */
    public static final int LETTERS_BIN = 5; // 5 bits to store 0 - 26
    /** Integer value of A in ASCII */
    public static final int ASCII_CAPITAL = 65;
    /** The number of the row where player car is located.
     *  It is always 3 in the tests, so maybe that's a rule
     */
    public static final int PLAYER_CAR_Y_COORD = 3;
}
