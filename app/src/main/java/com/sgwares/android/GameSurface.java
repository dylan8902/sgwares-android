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
            Move move = pen.completeMove();
            if ((move != null) && (game.whosMove().equals(player))) {
                game.addMove(move);
            } else {
                Log.d(TAG, "Not your turn, it's: " + game.whosMove());
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
                return new Move(startX, startY, 0, player);
            } else if (endY > startY) {
                return new Move(startX, startY, 1, player);
            } else if  (endY < startY) {
                return new Move(startX, startY - 1, 1, player);
            } else if  (endX < startX) {
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