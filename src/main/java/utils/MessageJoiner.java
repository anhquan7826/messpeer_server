package utils;

public class MessageJoiner {
    public static String join(String[] stringArr, int start, int end) {
        StringBuilder sb = new StringBuilder();
        if (start < 0 || end > stringArr.length) {
            return null;
        }
        for (int i = start; i < end; i++) {
            sb.append(stringArr[i]).append(" ");
        }
        return sb.toString().trim();
    }
}
