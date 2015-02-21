package it.polimi.hegira.generator;

import java.util.Random;
import java.util.UUID;

public class RandomUtils {

    private static Random random = null;

    private static Random random() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static Long randomLong() {
        return random().nextLong();
    }

    public static int randomInt() {
        return random().nextInt();
    }

    public static int randomInt(int range) {
        return random().nextInt(range);
    }
}
