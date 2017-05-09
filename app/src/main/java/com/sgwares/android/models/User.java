package com.sgwares.android.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String key;
    private String name;
    private int points;
    private String colour;

    public User() {

    }

    public User(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.points = 0;
        this.colour = "#ff0000";
        this.key = user.getUid();
    }

    public User(String name, int points, String colour) {
        this.name = name;
        this.points = points;
        this.colour = colour;
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

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public String toString() {
        return name + " [" + String.valueOf(points) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return getKey() != null ? getKey().equals(user.getKey()) : user.getKey() == null;
    }

    @Override
    public int hashCode() {
        return getKey() != null ? getKey().hashCode() : 0;
    }

}
