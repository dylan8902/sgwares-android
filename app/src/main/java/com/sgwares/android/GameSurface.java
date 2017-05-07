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

            pen.setX(event.getX());
            pen.setY(event.getY());
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // motion complete, what move??
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
        private float x;
        private float y;

        public Pen() {
            this.x = 200;
            this.y = 200;
        }
        public void setX(float x) {
            this.x = x;
        }
        public void setY(float y) {
            this.y = y;
        }
        public void draw(Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, SIZE, paint);
        }
    }

}
