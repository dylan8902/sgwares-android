package com.sgwares.android;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private static final String TAG = GameThread.class.getSimpleName();
    private final SurfaceHolder surfaceHolder;
    private GameSurface surface;

    public GameThread(SurfaceHolder surfaceHolder, GameSurface surface) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.surface = surface;
    }

    private boolean running;
    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");
        while (running) {
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.surface.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

}