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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@IgnoreExtraProperties
public class Game {

    public static final int SPACING = 200;
    private static final String TAG = Game.class.getSimpleName();
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference movesRef = database.getReference("moves");
        DatabaseReference moveRef = movesRef.child(getKey()).push();
        move.setKey(moveRef.getKey());
        if (isWinningMove(move)) {
            User user = move.getUser();
            int points = user.getPoints() + 1;
            Log.d(TAG, "Winning move - awarding " + points + " points to " + user);
            // Update /moves
            user.setPoints(points);
            // Update /users
            DatabaseReference userRef = database.getReference("users").child(user.getKey());
            userRef.setValue(user);
            // Update /participants
            DatabaseReference participantsRef = database.getReference("games").child(getKey()).child("participants");
            getParticipants().set(getParticipants().indexOf(user), user);
            participantsRef.setValue(getParticipants());

        }
        moveRef.setValue(move);
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
        Paint winningPaint = new Paint();
        winningPaint.setStyle(Paint.Style.FILL);
        for (Move move : getMoves()) {
            move.draw(canvas);
            // Draw the users box if this is a winning move
            if (isWinningMove(move)) {
                winningPaint.setColor(Color.parseColor(move.getUser().getColour()));
                Rect rect = new Rect((move.getX() * SPACING),
                        (move.getY() * SPACING),
                        (move.getX() * SPACING) + SPACING,
                        (move.getY() * SPACING) + SPACING);
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

}
