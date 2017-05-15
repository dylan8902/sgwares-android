package com.sgwares.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sgwares.android.models.Game;
import com.sgwares.android.models.Move;
import com.sgwares.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = GameSurface.class.getSimpleName();
    private Game game;
    private Pen pen;
    private User player;

    public GameSurface(Context context, Game game, User player) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setWillNotDraw(false);
        this.game = game;
        this.player = player;
        this.pen = new Pen();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_DOWN) ||
            (event.getAction() == MotionEvent.ACTION_MOVE)) {
            pen.addMovement(new Movement(event.getX(), event.getY()));
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // TODO Check it is the players' turn, will be in game object
            Move move = pen.completeMove();
            if (move != null) {
                game.addMove(move);
            }
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        game.draw(canvas);
        pen.draw(canvas);
    }

    private class Pen {
        private static final int SIZE = 50;
        private List<Movement> movements;
        public Pen() {
            this.movements = new ArrayList<>();
        }
        public void addMovement(Movement movement) {
            movements.add(movement);
        }
        public Move completeMove() {
            Movement start = movements.get(0);
            Movement end = movements.get(movements.size() - 1);
            movements.clear();
            int spacing = getWidth() / game.getSize();
            int startX = Math.round(start.getX() / spacing);
            int startY = Math.round(start.getY() / spacing);
            int endX = Math.round(end.getX() / spacing);
            int endY = Math.round(end.getY() / spacing);
            if ((endX > startX) && (startY < game.getSize())) {
                return new Move(startX, startY, 0, player);
            } else if ((endY > startY) && (endY < game.getSize())) {
                return new Move(startX, startY, 1, player);
            } else if  ((endY < startY) && (startY < game.getSize())) {
                return new Move(startX, startY - 1, 1, player);
            } else if  ((endX < startX) && (startY < game.getSize())) {
                return new Move(startX - 1, startY, 0, player);
            } else {
                return null;
            }
        }
        public void draw(Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            if (movements.size() > 0) {
                Movement movement = movements.get(movements.size() - 1);
                canvas.drawCircle(movement.getX(), movement.getY(), SIZE, paint);
            }
        }
    }

    private class Movement {
        private float x;
        private float y;
        public Movement(float x, float y) {
            this.x = x;
            this.y = y;
        }
        public float getX() {
            return x;
        }
        public float getY() {
            return y;
        }
    }

}
