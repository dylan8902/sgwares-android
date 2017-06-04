package com.sgwares.android.generators;

import java.util.Random;

public class NameGenerator {

    private static final String[] ADJECTIVES = {
            "Smelly"
    };

    private static final String[] NOUNS = {
            "Cheese"
    };

    public static String generate() {
        Random rand = new Random();
        int adjective = rand.nextInt(ADJECTIVES.length);
        int noun = rand.nextInt(NOUNS.length);
        return ADJECTIVES[adjective] + NOUNS[noun];
    }

}
