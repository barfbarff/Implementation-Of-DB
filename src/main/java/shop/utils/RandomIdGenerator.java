package shop.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomIdGenerator {
    Set<Integer> existingEntries = new HashSet<>();
    private final Random random = new Random();

    public int generate() {
        int randomId = random.nextInt(Integer.MAX_VALUE) + 1;

        while (existingEntries.contains(randomId)) {
            randomId = random.nextInt(Integer.MAX_VALUE) + 1;
        }
        existingEntries.add(randomId);

        return randomId;
    }
}
