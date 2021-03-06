package com.sgwares.android.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Move {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private String key;
    // The start co-ordinates of the move
    private int x;
    private int y;
    // The direction: 1 for vertical down, 0 for horizontal left
    private int direction;
    // The user that made the move
    private User user;

    public Move() {

    }

    public Move(int x, int y, int direction, User user) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.user = user;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Exclude
    public void draw(Canvas canvas, int gameWidth) {
        int spacing = canvas.getWidth() / gameWidth;

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStrokeWidth(4);

        int startX = x * spacing;
        int startY = y * spacing;
        int endX = x * spacing;
        int endY = y * spacing;

        if (direction == HORIZONTAL) {
            endX = endX + spacing;
        } else {
            endY = endY + spacing;
        }

        canvas.drawLine(startX, startY, endX, endY, whitePaint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (getX() != move.getX()) return false;
        if (getY() != move.getY()) return false;
        return getDirection() == move.getDirection();

    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        result = 31 * result + getDirection();
        return result;
    }
}
