package com.sgwares.android.models;


public class LeaderboardScore {

    public int position;
    public String name;
    public int points;

    public LeaderboardScore() {

    }

    public LeaderboardScore(int position, String name, int points) {
        this.position = position;
        this.name = name;
        this.points = points;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return String.valueOf(position) + ") " + name + " [" + String.valueOf(points) + "]";
    }

}
