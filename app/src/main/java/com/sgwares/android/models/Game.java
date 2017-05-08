package com.sgwares.android.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@IgnoreExtraProperties
public class Game {

    public static final int SPACING = 200;
    private static final int DOT_SIZE = 6;

    private String key;
    private List<Move> moves;
    private List<User> participants;
    private boolean finished;
    private boolean open;
    private String background;

    public Game() {
        this.moves = new CopyOnWriteArrayList<>();
        this.participants = new CopyOnWriteArrayList<>();
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public List<Move> getMoves() {
        return moves;
    }

    @Exclude
    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Exclude
    public void addMove(Move move) {
        this.moves.add(move);
    }

    @Exclude
    public User whosMove() {
        //TODO calculate which participant's move is next based on number of moves
        return getParticipants().get(0);
    }

    @Exclude
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        // Draw Background
        canvas.drawColor(Color.parseColor(getBackground()));

        // Draw dots
        for (int x = 0; x < canvas.getWidth(); x = x + SPACING) {
            for (int y = 0; y < canvas.getHeight(); y = y + SPACING) {
                canvas.drawCircle(x, y, DOT_SIZE, paint);
            }
        }

        // Draw moves
        for (Move move : getMoves()) {
            move.draw(canvas);
        }
    }
}
