package com.sgwares.android.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leaderboard {

    public static final List<Score> SCORES = new ArrayList<>();
    public static final Map<String, Score> ITEM_MAP = new HashMap<>();
    private static final int COUNT = 25;

    static {
        for (int i = 1; i <= COUNT; i++) {
            addScore(createDummyItem(i));
        }
    }

    private static void addScore(Score item) {
        SCORES.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static Score createDummyItem(int position) {
        return new Score(String.valueOf(position), "Score " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Score: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static class Score {
        public final String id;
        public final String content;
        public final String details;

        public Score(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }

}
