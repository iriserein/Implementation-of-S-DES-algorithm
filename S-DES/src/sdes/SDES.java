package sdes;

import java.util.Arrays;

public class SDES {
    private static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    private static final int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    private static final int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    private static final int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
    private static final int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};
    private static final int[][] SBOX1 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 0, 2}
    };
    private static final int[][] SBOX2 = {
            {0, 1, 2, 3},
            {2, 3, 1, 0},
            {3, 0, 1, 2},
            {2, 1, 0, 3}
    };
    private static final int[] SP = {2, 4, 3, 1};


    public static String encrypt(String plaintext, String key) {
        String k1 = generateKey(key, 1);
        String k2 = generateKey(key, 2);
        String permutedInput = permute(plaintext, IP);
        String afterFk1 = fk(permutedInput, k1);
        String swapped = swap(afterFk1);
        return permute(fk(swapped, k2), IP_INV);
    }

    public static String decrypt(String ciphertext, String key) {
        String k1 = generateKey(key, 1);
        String k2 = generateKey(key, 2);
        String permutedInput = permute(ciphertext, IP);
        String afterFk2 = fk(permutedInput, k2);
        String swapped = swap(afterFk2);
        return permute(fk(swapped, k1), IP_INV);
    }

    private static String generateKey(String key, int round) {
        String permutedKey = permute(key, P10);
        String leftShifted = leftShift(permutedKey, round);
        return permute(leftShifted, P8);
    }

    private static String fk(String input, String key) {
        String left = input.substring(0, 4);
        String right = input.substring(4);
        String fResult = f(right, key);
        return leftXOR(left, fResult) + right;
    }

    private static String f(String right, String key) {
        String expanded = permute(right, EP);
        String xorResult = leftXOR(expanded, key);
        String sboxOutput = SBoxes(xorResult);
        return permute(sboxOutput, SP);
    }

    private static String SBoxes(String input) {
        String left = input.substring(0, 4);
        String right = input.substring(4);
        int row1 = Integer.parseInt("" + left.charAt(0) + left.charAt(3), 2);
        int col1 = Integer.parseInt("" + left.charAt(1) + left.charAt(2), 2);
        int row2 = Integer.parseInt("" + right.charAt(0) + right.charAt(3), 2);
        int col2 = Integer.parseInt("" + right.charAt(1) + right.charAt(2), 2);
        return String.format("%02d%02d", SBOX1[row1][col1], SBOX2[row2][col2]);
    }

    private static String swap(String input) {
        return input.substring(4) + input.substring(0, 4);
    }

    private static String permute(String input, int[] permutation) {
        StringBuilder output = new StringBuilder();
        for (int index : permutation) {
            output.append(input.charAt(index - 1));
        }
        return output.toString();
    }

    private static String leftXOR(String left, String right) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < left.length(); i++) {
            result.append(left.charAt(i) == right.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    private static String leftShift(String input, int round) {
        String left = input.substring(0, 5);
        String right = input.substring(5);
        if (round == 1) {
            return left.substring(1) + left.charAt(0) + right.substring(1) + right.charAt(0);
        } else {
            return left.substring(2) + left.substring(0, 2) + right.substring(2) + right.substring(0, 2);
        }
    }
}