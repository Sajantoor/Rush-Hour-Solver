package Utility;

public class Functions {
    /**
     * Converts a binary number array to an integer.
     * @param bin A boolean array representing a binary array.
     * @param length Length of bin.
     * @return The integer value of the binary number array.
     */
    public static int binaryToDecimal(boolean[] bin, int length) {
        int res = 0;
        for (int i = 0; i < length; i++) {
            if (bin[i] == true) {
                res += Math.pow(2, i);
            }
        }

        return res;
    }

    /**
     * Converts a decimal number to a binary array
     * @param bin A boolean array representing a binary array.
     * @param length Length of bin
     * @param decimal Decimal number that is to be converted
     */
    public static void decimalToBinary(boolean[] bin, int length, int decimal) {
        int i = 0;
        while (decimal > 0 && i < length) {
            // if decimal % 2 == 0 then false (0), else true (1)
            bin[i] = ((decimal % 2) == 0) ? false : true;
            decimal /= 2;
            i++;
        }

        // clears the rest of the array if necessary 
        while (i < length) {
            bin[i] = false;
            i++;
        }
    }
}
