package com.group7.dragonwars;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class DrawingThread extends Thread {
    private boolean run;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private GameView gView;

    public DrawingThread(final SurfaceHolder surfaceHolder,
                         final Context ctx, final GameView gv) {
        this.surfaceHolder = surfaceHolder;
        run = false;
        gView = gv;
    }

    public void setRunning(final boolean newrun) {
        run = newrun;
    }

    @Override
    public void run() {
        super.run();

        while (run) {
            canvas = surfaceHolder.lockCanvas();

            if (canvas != null) {
                gView.doDraw(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}
