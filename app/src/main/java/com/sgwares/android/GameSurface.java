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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // delegating event handling to the pen
            pen.handleActionDown((int)event.getX(), (int)event.getY());

            // check if in the lower part of the screen we exit
            if (event.getY() > getHeight() - 50) {
                thread.setRunning(false);
                ((Activity)getContext()).finish();
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // the gestures
            if (pen.isTouched()) {
                pen.setX((int)event.getX());
                pen.setY((int)event.getY());
                invalidate();
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // touch was released
            if (pen.isTouched()) {
                pen.setTouched(false);
            }
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
        private int x;
        private int y;
        private boolean touched;

        public Pen() {
            this.x = 200;
            this.y = 200;
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
        public boolean isTouched() {
            return touched;
        }
        public void setTouched(boolean touched) {
            this.touched = touched;
        }
        public void draw(Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, SIZE, paint);
        }
        public void handleActionDown(int eventX, int eventY) {
            if ((eventX >= x-SIZE) && (eventX <= x+SIZE)) {
                if ((eventY >= y-SIZE) && (eventY <= y+SIZE)) {
                    setTouched(true);
                } else {
                    setTouched(false);
                }
            } else {
                setTouched(false);
            }
        }
        @Override
        public String toString() {
            return "Pen(" + String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(touched) + ")";
        }
    }

}
