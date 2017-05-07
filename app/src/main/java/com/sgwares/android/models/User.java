package com.sgwares.android.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String key;
    private String name;
    private int points;

    public User() {

    }

    public User(String name, int points) {
        this.name = name;
        this.points = points;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return name + " [" + String.valueOf(points) + "]";
    }

}
