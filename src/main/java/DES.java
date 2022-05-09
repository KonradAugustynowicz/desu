import java.util.Arrays;

public class DES {

    public static boolean[] encrypt(boolean[] input, boolean[][] keys) {
        boolean[] output = initialPermutation(input);
        boolean[] temp;
        boolean[] tempLeft;

        boolean[] Left = new boolean[32];
        boolean[] Right = new boolean[32];

        for (int i = 0; i < 32; i++) {
            Left[i] = output[i];
            Right[i] = output[i + 32];
        }

        for (int i = 0; i < 15; i++) {
            tempLeft = Arrays.copyOf(Left, Left.length);

            Left = Arrays.copyOf(Right, Right.length);

            temp = feistelFunction(Right, keys[i]);
            for (int j = 0; j < 32; j++) {

                Right[j] = tempLeft[j] ^ temp[j];
            }
        }

        tempLeft = Left;
        temp = feistelFunction(Right, keys[15]);
        for (int j = 0; j < 32; j++) {

            Left[j] = tempLeft[j] ^ temp[j];
        }
        for (int i = 0; i < 32; i++) {

            output[i] = Left[i];
            output[i + 32] = Right[i];
        }
        output = finalPermutation(output);
        return output;
    }

    public static boolean[] decrypt(boolean[] input, boolean[][] keys) {

        boolean[] output = initialPermutation(input);
        boolean[] temp;
        boolean[] tempLeft;

        boolean[] Left = new boolean[32];
        boolean[] Right = new boolean[32];
        for (int i = 0; i < 32; i++) {
            Left[i] = output[i];
            Right[i] = output[i + 32];
        }

        for (int i = 15; i > 0; i--) {
            tempLeft = Arrays.copyOf(Left, Left.length);
            Left = Arrays.copyOf(Right, Right.length);
            temp = feistelFunction(Right, keys[i]);
            for (int j = 0; j < 32; j++) {
                Right[j] = tempLeft[j] ^ temp[j];
            }
        }
        tempLeft = Left;
        temp = feistelFunction(Right, keys[0]);
        for (int j = 0; j < 32; j++) {
            Left[j] = tempLeft[j] ^ temp[j];
        }
        for (int i = 0; i < 32; i++) {
            output[i] = Left[i];
            output[i + 32] = Right[i];
        }
        output = finalPermutation(output);
        return output;
    }

    public static boolean[] initialPermutation(boolean[] input) {
        boolean[] output = new boolean[64];
        for (int i = 0; i < 64; i++) {
            output[i] = input[Permutations.initial[i] - 1];
        }
        return output;
    }

    public static boolean[] finalPermutation(boolean[] input) {
        boolean[] output = new boolean[64];
        for (int i = 0; i < 64; i++) {
            output[i] = input[Permutations.ending[i] - 1];
        }
        return output;
    }

    public static boolean[] feistelFunction(boolean[] Right, boolean[] key) {
        boolean[] extendedTable = new boolean[48];
        for (int i = 0; i < 48; i++) {

            extendedTable[i] = Right[Permutations.selectionTable[i] - 1];
        }
        int x2;
        int[] xored = new int[48];
        for (int i = 0; i < 48; i++) {

            xored[i] = (extendedTable[i] ^ key[i]) ? 1 : 0;
        }
        int j = 0;
        String temp2;
        boolean[] temp3 = new boolean[32];
        SBoxes sBox = new SBoxes();

        for (int i = 0; i < 8; i++) {

            int outer = xored[i * 6] * 2 + xored[i * 6 + 5];
            int inner = xored[i * 6 + 1] * 8 + xored[i * 6 + 2] * 4 + xored[i * 6 + 3] * 2 + xored[i * 6 + 4];

            x2 = SBoxes.sBoxes[i][outer][inner];

            temp2 = Integer.toBinaryString(x2);
            String t2 = "";

            if (temp2.length() == 4) {
                t2 = temp2;
            } else if (temp2.length() == 3) {
                t2 = "0" + temp2.charAt(0) + temp2.charAt(1) + temp2.charAt(2);
            } else if (temp2.length() == 2) {
                t2 = "00" + temp2.charAt(0) + temp2.charAt(1);
            } else if (temp2.length() == 1) {
                t2 = "000" + temp2.charAt(0);
            } else if (temp2.length() == 0) {
                t2 = "0000";
            }

            for (int l = 0; l < 4; l++)
                if (t2.charAt(l) == '1') {
                    temp3[j++] = true;
                } else if (t2.charAt(l) == '0') {
                    temp3[j++] = false;
                }
        }

        boolean[] output = new boolean[32];
        for (int i = 0; i < 32; i++) {
            output[i] = temp3[Permutations.functionPermutation[i] - 1];
        }
        return output;
    }
}
