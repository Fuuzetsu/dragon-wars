package com.group7.dragonwars;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.json.*;

import android.app.Activity;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.PorterDuff.*;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.ScrollView;
import android.widget.Toast;

import com.group7.dragonwars.engine.*;
import com.group7.dragonwars.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 *
 *      I removed almost all of this, a lot of it was to make the navigation ui
 *      (top bar + any software buttons) appear and disappear and animate,
 *      however now we only want it gone. it was also incredibly boring.
 */
public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";
    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_FULLSCREEN;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private ScrollView map_scroller;
    private Integer orientation;
    private Boolean orientationChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_game);

        final View contentView = findViewById(R.id.game_view);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView,
                         HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.hide();

    }

}

class GameView extends SurfaceView implements SurfaceHolder.Callback {
    final private String TAG = "GameView";
    Bitmap bm;
    GameMap gm;
    DrawingThread dt;
    Context context;
    HashMap<String, HashMap<String, Bitmap>> graphics;
    private Integer orientation;

    //
    public GameView(Context ctx, AttributeSet attrset) {
        super(ctx, attrset);
        Log.d(TAG, "GameView ctor");

        GameView game_view = (GameView) this.findViewById(R.id.game_view);
        GameMap map = null;
        Log.d(TAG, "nulling GameMap");

        try {
            map = MapReader.readMap(readFile(R.raw.testmap)); // ugh
        } catch (JSONException e) {
            Log.d(TAG, "Failed to load the map: " + e.getMessage());
        }

        if (map == null) {
            Log.d(TAG, "map is null");
            System.exit(1);
        }

        Log.d(TAG, "before setMap");
        game_view.setMap(map);

        context = ctx;
        bm = BitmapFactory.decodeResource(context.getResources(),
                                          R.drawable.ic_launcher);
        SurfaceHolder holder = getHolder();
        this.graphics = new HashMap<String, HashMap<String, Bitmap>>();
        Log.d(TAG, "this.graphics new");
        /* Register game fields */
        this.graphics.put("Fields", new HashMap<String, Bitmap>());
        Log.d(TAG, "after putting empty Fields");
        Boolean b = gm == null;
        Log.d(TAG, "gm is current null?: " + b.toString());

        for (Map.Entry<Character, GameField> ent : this.gm.getGameFieldMap().entrySet()) {
            Log.d(TAG, "inside for loop, about to ent.getValue()");
            GameField f = ent.getValue();
            Log.d(TAG, "about to getResources() for " + f.getFieldName());
            Integer resourceID = getResources().getIdentifier(f.getSpriteLocation(),
                                 f.getSpriteDir(),
                                 f.getSpritePack());
            Log.d(TAG, "after getResources()");
            this.graphics.get("Fields").put(f.getFieldName(),
                                            BitmapFactory.decodeResource(context.getResources(),
                                                    resourceID));
            Log.d(TAG, "after putting decoded resource into Fields");
        }

        Log.d(TAG, "after fields");
        /* Register units */
        this.graphics.put("Units", new HashMap<String, Bitmap>());

        for (Map.Entry<Character, Unit> ent : this.gm.getUnitMap().entrySet()) {
            Unit f = ent.getValue();
            Log.d(TAG, "about to getResources() for " + f.getUnitName());
            Integer resourceID = getResources().getIdentifier(f.getSpriteLocation(),
                                 f.getSpriteDir(),
                                 f.getSpritePack());
            this.graphics.get("Units").put(f.getUnitName(),
                                           BitmapFactory.decodeResource(context.getResources(),
                                                   resourceID));
        }

        Log.d(TAG, "after units");
        /* Register buildings */
        this.graphics.put("Buildings", new HashMap<String, Bitmap>());

        for (Map.Entry<Character, Building> ent : this.gm.getBuildingMap().entrySet()) {
            Building f = ent.getValue();
            Log.d(TAG, "about to getResources() for " + f.getBuildingName());
            Integer resourceID = getResources().getIdentifier(f.getSpriteLocation(),
                                 f.getSpriteDir(),
                                 f.getSpritePack());
            this.graphics.get("Buildings").put(f.getBuildingName(),
                                               BitmapFactory.decodeResource(context.getResources(),
                                                       resourceID));
        }

        Log.d(TAG, "after buildings");
        holder.addCallback(this);
    }

    private List<String> readFile(int resourceid) {
        List<String> text = new ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this
                                                   .getResources().openRawResource(resourceid)));
            String line;

            while ((line = in.readLine()) != null)
                text.add(line);

            in.close();
        } catch (FileNotFoundException fnf) {
            System.err.println("Couldn't find " + fnf.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Couldn't read " + ioe.getMessage());
            System.exit(1);
        }

        return text;
    }

    public void setMap(GameMap newmap) {
        this.gm = newmap;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        dt = new DrawingThread(arg0, context, this);
        dt.setRunning(true);
        dt.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        dt.setRunning(false);

        try {
            dt.join();
        } catch (InterruptedException e) {
            // TODO something, perhaps, but what?
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(null, "Touched at: (" + event.getX() + ", " + event.getY()
              + ")");
        return true;
    }


    public void doDraw(Canvas canvas) {
        int size = 64;
        Configuration c = getResources().getConfiguration();

        if (orientation == null || orientation != c.orientation) {
            orientation = c.orientation;
            canvas.drawColor(Color.BLACK);
            Log.d(TAG, "Painted canvas black due to orientation change.");
        }


        for (int i = 0; i < gm.getWidth(); ++i) {
            for (int j = 0; j < gm.getHeight(); j++) {
                // canvas.drawBitmap(tiles.get(0), size * j, size * i, null); // Draw

                GameField gf = gm.getField(i, j);
                String gfn = gf.getFieldName();

                canvas.drawBitmap(graphics.get("Fields").get(gfn), size * j, size * i, null);

                if (gf.hostsBuilding()) {
                    Building b = gf.getBuilding();
                    String n = b.getBuildingName();

                    canvas.drawBitmap(graphics.get("Buildings").get(n), size * j, size * i, null);
                }

                if (gf.hostsUnit()) {
                    Unit unit = gf.getUnit();

                    if (unit != null) {
                        String un = unit.toString();
                        canvas.drawBitmap(graphics.get("Units").get(un), size * j, size * i, null);

                    }
                }
            }
        }
    }
}

class DrawingThread extends Thread {
    boolean run;
    Canvas canvas;
    SurfaceHolder surfaceholder;
    Context context;
    GameView gview;

    public DrawingThread(SurfaceHolder sholder, Context ctx, GameView gv) {
        surfaceholder = sholder;
        context = ctx;
        run = false;
        gview = gv;
    }

    void setRunning(boolean newrun) {
        run = newrun;
    }

    @Override
    public void run() {
        super.run();

        while (run) {
            canvas = surfaceholder.lockCanvas();

            if (canvas != null) {
                gview.doDraw(canvas);
                surfaceholder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
