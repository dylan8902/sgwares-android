package com.sgwares.android.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by dylan8902 on 07/05/2017.
 */

@IgnoreExtraProperties
public class Move {
    private String key;
    private int startX;
    private int endX;
    private int startY;
    private int endY;
    private User user;

    public Move() {

    }

    public Move(int startX, int endX, int startY, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Exclude
    public void draw(Canvas canvas) {
        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStrokeWidth(2);
        canvas.drawLine(startX, startY, endX, endY, whitePaint);
    }

}
