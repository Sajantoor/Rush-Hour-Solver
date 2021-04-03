package Utility;

public class Functions {
    // converts the binNumber to an integer
    public static int binaryToDecimal(boolean[] bin, int length) {
        int res = 0;
        for (int i = 0; i < length; i++) {
            if (bin[i] == true) {
                res += Math.pow(2, i);
            }
        }

        return res;
    }

    // converts the input an integer (val), to binary using the coordArray
    public static void decimalToBinary(boolean[] bin, int length, int decimal) {
        int i = 0;
        while (decimal > 0 && i < length) {
            // if decimal % 2 == 0 then false (0), else true (1)
            bin[i] = ((decimal % 2) == 0) ? false : true;
            decimal /= 2;
            i++;
        }

        // clears the rest of the array if nessessary 
        while (i < length) {
            bin[i] = false;
            i++;
        }
    }
}
