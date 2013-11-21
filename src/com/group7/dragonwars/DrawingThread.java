/* This file is part of Dragon Wars.
 *
 * Dragon Wars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragon Wars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dragon Wars.  If not, see <http://www.gnu.org/licenses/>.
 */

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
