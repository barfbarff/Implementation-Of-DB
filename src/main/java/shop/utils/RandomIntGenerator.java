package shop.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomIntGenerator implements RandomGenerator<Integer> {
    Set<Integer> existingEntries = new HashSet<>();
    private final static Random random = new Random();

    public Integer generate() {
        int randomId = random.nextInt(Integer.MAX_VALUE) + 1;

        while (existingEntries.contains(randomId)) {
            randomId = random.nextInt(Integer.MAX_VALUE) + 1;
        }

        existingEntries.add(randomId);

        return randomId;
    }
}
