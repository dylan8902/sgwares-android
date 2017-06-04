package com.sgwares.android.generators;

import java.util.Random;

public class ColourGenerator {

    private static final int MAX_HEX = 256*256*256;

    public static String generate() {
        Random rand = new Random();
        return String.format("#%06x", rand.nextInt(MAX_HEX));
    }

}
