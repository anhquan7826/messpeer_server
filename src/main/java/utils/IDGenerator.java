package utils;

import java.util.Random;

public class IDGenerator {
    private static char[] values = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static Random random = new Random();

    public static String generate(int length) {
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append(values[random.nextInt(values.length)]);
        }
        return randomString.toString();
    }
}
