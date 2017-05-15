package com.sgwares.android.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@IgnoreExtraProperties
public class Game {

    private static final String TAG = Game.class.getSimpleName();
    private static final int DOT_SIZE = 6;

    private String key;
    private List<Move> moves;
    private boolean finished;
    private boolean open;
    private String background;
    private Object createdAt;
    private int size;

    public Game() {
        this.moves = new CopyOnWriteArrayList<>();
        this.createdAt = ServerValue.TIMESTAMP;
        this.size = 8;
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

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Exclude
    public String getDateTimeCreatedAt() {
        Date date = new Date((long) getCreatedAt());
        return new SimpleDateFormat("dd/MMM/yyyy").format(date);
    }

    @Exclude
    public void addMove(Move move) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference movesRef = database.getReference("moves").child(getKey());
        DatabaseReference moveRef = movesRef.push();
        move.setKey(moveRef.getKey());
        if (isWinningMove(move)) {
            //TODO Move to awardPoints and only keep game points on /participants and global points on /users
            User user = move.getUser();
            int points = user.getPoints() + 1;
            user.setPoints(points);
            Log.d(TAG, "Winning move - awarding " + points + " points to " + user);
            // Update /users
            DatabaseReference userRef = database.getReference("users").child(user.getKey());
            userRef.setValue(user);
            // Update /participants
            DatabaseReference ref = database.getReference("participants").child(getKey()).child(user.getKey()).child("points");
            ref.setValue(points);
        }
        moveRef.setValue(move);
    }

    @Exclude
    public void draw(Canvas canvas) {
        int spacing = canvas.getWidth() / getSize();

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        // Draw Background
        canvas.drawColor(Color.parseColor(getBackground()));

        // Draw dots
        for (int x = 0; x < getSize(); x++) {
            for (int y = 0; y < getSize(); y++) {
                canvas.drawCircle(x * spacing, y * spacing, DOT_SIZE, paint);
            }
        }

        // Draw moves
        Paint winningPaint = new Paint();
        winningPaint.setStyle(Paint.Style.FILL);
        for (Move move : getMoves()) {
            move.draw(canvas, getSize());
            // Draw the users box if this is a winning move
            if (isWinningMove(move)) {
                winningPaint.setColor(Color.parseColor(move.getUser().getColour()));
                Rect rect = new Rect((move.getX() * spacing),
                        (move.getY() * spacing),
                        (move.getX() * spacing) + spacing,
                        (move.getY() * spacing) + spacing);
                canvas.drawRect(rect, winningPaint);
            }
        }

    }

    private boolean isWinningMove(Move move) {
        if (move.getDirection() == Move.HORIZONTAL) {
            List<Move> requiredMoves = new ArrayList<>();
            requiredMoves.add(new Move(move.getX(), move.getY() + 1, Move.HORIZONTAL, null));
            requiredMoves.add(new Move(move.getX(), move.getY(), Move.VERTICAL, null));
            requiredMoves.add(new Move(move.getX() + 1, move.getY(), Move.VERTICAL, null));
            for (Move m : moves) {
                requiredMoves.remove(m);
                if (requiredMoves.isEmpty()) {
                    return true;
                }
            }
        } else if (move.getDirection() == Move.VERTICAL) {
            List<Move> requiredMoves = new ArrayList<>();
            requiredMoves.add(new Move(move.getX() + 1, move.getY(), Move.VERTICAL, null));
            requiredMoves.add(new Move(move.getX(), move.getY(), Move.HORIZONTAL, null));
            requiredMoves.add(new Move(move.getX(), move.getY() + 1, Move.HORIZONTAL, null));
            for (Move m : getMoves()) {
                requiredMoves.remove(m);
                if (requiredMoves.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Game{" +
                "key='" + key + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}
