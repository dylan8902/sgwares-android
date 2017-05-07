package com.sgwares.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sgwares.android.models.Game;
import com.sgwares.android.models.Move;

import java.util.ArrayList;
import java.util.List;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = GameSurface.class.getSimpleName();
    private GameThread thread;
    private Game game;
    private Pen pen;

    public GameSurface(Context context, Game game) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setWillNotDraw(false);
        thread = new GameThread(getHolder(), this);
        this.game = game;
        this.pen = new Pen();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_DOWN) ||
            (event.getAction() == MotionEvent.ACTION_MOVE)) {

            // check if in the lower part of the screen we exit
            if (event.getY() > getHeight() - 50) {
                thread.setRunning(false);
                ((Activity)getContext()).finish();
            }

            pen.addMovement(new Movement(event.getX(), event.getY()));
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
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

            int startX = Math.round(start.getX() / Game.SPACING);
            int startY = Math.round(start.getY() / Game.SPACING);
            int endX = Math.round(end.getX() / Game.SPACING);
            int endY = Math.round(end.getY() / Game.SPACING);
            if (endX > startX) {
                return new Move(startX, startY, 0);
            } else if (endY > startY) {
                return new Move(startX, startY, 1);
            } else if  (endY < startY) {
                return new Move(startX, startY - 1, 1);
            } else if  (endX < startX) {
                return new Move(startX - 1, startY, 0);
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
