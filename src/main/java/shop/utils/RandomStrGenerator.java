package shop.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomStrGenerator implements RandomGenerator<String> {
    private final static String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private final static int LENGTH_UPPER_BOUND = 16;
    private final static int LENGTH_LOWER_BOUND = 10;

    private final static Random random = new Random();

    private final Set<String> existingEntries = new HashSet<>();

    public String generate() {
        StringBuilder stringBuilder = new StringBuilder();

        int bound = LENGTH_UPPER_BOUND - LENGTH_LOWER_BOUND + 1;
        int length = LENGTH_LOWER_BOUND + random.nextInt(bound);

        for (int i = 0; i < length; i++) {
            int randomCharIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomCharIndex));
        }

        while (existingEntries.contains(stringBuilder.toString())) {
            stringBuilder.setLength(0);
            for (int i = 0; i < length; i++) {
                int randomCharIndex = random.nextInt(CHARACTERS.length());
                stringBuilder.append(CHARACTERS.charAt(randomCharIndex));
            }
        }

        existingEntries.add(stringBuilder.toString());

        return stringBuilder.toString();
    }
}
