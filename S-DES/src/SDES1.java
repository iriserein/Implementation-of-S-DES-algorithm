import java.util.ArrayList;
import java.util.List;

public class SDES1 {
    private static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    private static final int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    private static final int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    private static final int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
    private static final int[] EP_BOX = {4, 1, 2, 3, 2, 3, 4, 1};
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

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // 输入明文和密文
        String plaintext = "10101010"; // 示例明文
        String ciphertext = "11010100"; // 示例密文

        List<String> possibleKeys = findPossibleKeys(plaintext, ciphertext);

        long endTime = System.currentTimeMillis();

        // 输出结果
        System.out.println("Possible keys (10 bits):");
        for (String key : possibleKeys) {
            System.out.println(key);
        }
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    private static List<String> findPossibleKeys(String plaintext, String ciphertext) {
        List<String> keys = new ArrayList<>();

        // 遍历所有可能的10位密钥（这里假设密钥是10位二进制数）
        for (int i = 0; i < 1024; i++) { // 2^10 = 1024
            String key = String.format("%10s", Integer.toBinaryString(i)).replace(' ', '0');
            if (testKey(key, plaintext, ciphertext)) {
                keys.add(key); // 直接将10位密钥加入列表
            }
        }

        return keys;
    }

    private static boolean testKey(String key, String plaintext, String ciphertext) {
        // 密钥扩展
        String k1 = generateK1(key);
        String k2 = generateK2(key);

        // 加密过程
        String encrypted = encrypt(plaintext, k1, k2);

        // 检查加密结果是否匹配
        return encrypted.equals(ciphertext);
    }

    private static String generateK1(String key) {
        String permutedKey = permute(key, P10);
        String left = permutedKey.substring(0, 5);
        String right = permutedKey.substring(5, 10);

        left = leftShift(left);
        right = leftShift(right);
        String k1 = permute(left + right, P8);
        return k1;
    }

    private static String generateK2(String key) {
        String permutedKey = permute(key, P10);
        String left = permutedKey.substring(0, 5);
        String right = permutedKey.substring(5, 10);

        left = leftShift(left);
        right = leftShift(right);
        left = leftShift(left);
        right = leftShift(right);
        String k2 = permute(left + right, P8);
        return k2;
    }

    private static String permute(String input, int[] permutation) {
        StringBuilder output = new StringBuilder();
        for (int index : permutation) {
            output.append(input.charAt(index - 1));
        }
        return output.toString();
    }

    private static String leftShift(String half) {
        return half.substring(1) + half.charAt(0);
    }

    private static String encrypt(String plaintext, String k1, String k2) {
        String permutedInput = permute(plaintext, IP);
        String left = permutedInput.substring(0, 4);
        String right = permutedInput.substring(4, 8);

        // 第一次轮的加密
        String afterFirstRound = roundFunction(left, right, k1);
        left = afterFirstRound.substring(0, 4);
        right = afterFirstRound.substring(4, 8);

        // 第二次轮的加密
        String afterSecondRound = roundFunction(right, left, k2);
        String finalOutput = permute(afterSecondRound, IP_INV);
        return finalOutput;
    }

    private static String roundFunction(String left, String right, String key) {
        String expandedRight = permute(right, EP_BOX);
        String xorResult = xor(expandedRight, key);

        String sBoxOutput = sBoxSubstitution(xorResult);
        String p4Result = permute(sBoxOutput, new int[]{2, 4, 3, 1});

        return xor(left, p4Result) + right;
    }

    private static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    private static String sBoxSubstitution(String input) {
        String left = input.substring(0, 4);
        String right = input.substring(4, 8);

        int row1 = Integer.parseInt("" + left.charAt(0) + left.charAt(3), 2);
        int col1 = Integer.parseInt("" + left.charAt(1) + left.charAt(2), 2);
        int sBox1Value = SBOX1[row1][col1];

        int row2 = Integer.parseInt("" + right.charAt(0) + right.charAt(3), 2);
        int col2 = Integer.parseInt("" + right.charAt(1) + right.charAt(2), 2);
        int sBox2Value = SBOX2[row2][col2];

        return String.format("%2s%2s", Integer.toBinaryString(sBox1Value), Integer.toBinaryString(sBox2Value))
                .replace(' ', '0');
    }
}
