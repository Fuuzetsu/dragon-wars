package com.group7.dragonwars;

import android.app.Activity;

import android.content.Context;
import android.content.res.Configuration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import android.os.Bundle;

import android.util.AttributeSet;
import android.util.Log;

import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.group7.dragonwars.engine.Building;
import com.group7.dragonwars.engine.GameField;
import com.group7.dragonwars.engine.GameMap;
import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Logic;
import com.group7.dragonwars.engine.MapReader;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Pair;
import com.group7.dragonwars.engine.Unit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;


public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";
    private Integer orientation;
    private Boolean orientationChanged = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // remove the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_game);
    }

}

class GameView extends SurfaceView implements SurfaceHolder.Callback,
                                              OnGestureListener,
                                              OnDoubleTapListener {
    final private String TAG = "GameView";
    private Bitmap bm;
    private GameState state;
    private Logic logic;
    private GameMap map;
    private Position selected; // Currently selected position

    private FloatPair scroll_offset; // offset caused by scrolling, in pixels
    private GestureDetector gesture_detector;

    private DrawingThread dt;
    private Bitmap highlighter;
    private Bitmap selector;
    private boolean unit_selected;

    private Bitmap fullMap;

    private Context context;
    private HashMap<String, HashMap<String, Bitmap>> graphics;
    private private Integer orientation;
    private int tilesize = 64;
    private GameField lastField;
    private Unit lastUnit;
    private List<Position> lastDestinations;


    public GameView(Context ctx, AttributeSet attrset) {
        super(ctx, attrset);
        Log.d(TAG, "GameView ctor");

        GameView game_view = (GameView) this.findViewById(R.id.game_view);
        GameMap gm = null;

        Log.d(TAG, "nulling GameMap");

        try {
            gm = MapReader.readMap(readFile(R.raw.cornermap)); // ugh
        } catch (JSONException e) {
            Log.d(TAG, "Failed to load the map: " + e.getMessage());
        }

        if (gm == null) {
            Log.d(TAG, "gm is null");
            System.exit(1);
        }

        Log.d(TAG, "before setMap");
        game_view.setMap(gm);
        this.logic = new Logic();
        this.state = new GameState(map, logic, map.getPlayers());

        context = ctx;
        bm = BitmapFactory.decodeResource(context.getResources(),
                                          R.drawable.ic_launcher);
        SurfaceHolder holder = getHolder();
        this.graphics = new HashMap<String, HashMap<String, Bitmap>>();
        Log.d(TAG, "this.graphics new");
        /* Register game fields */
        this.graphics.put("Fields", new HashMap<String, Bitmap>());
        Log.d(TAG, "after putting empty Fields");
        Boolean b = map == null;
        Log.d(TAG, "map is current null?: " + b.toString());

        for (Map.Entry<Character, GameField> ent : this.map.getGameFieldMap().entrySet()) {
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
        Integer borderID = getResources().getIdentifier("water_grass_edge1",
                                                        "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Top grass->water border",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));
        borderID = getResources().getIdentifier("water_grass_edge3",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Bottom grass->water border",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));
        borderID = getResources().getIdentifier("water_grass_edge4",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Left grass->water border",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));
        borderID = getResources().getIdentifier("water_grass_edge2",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Right grass->water border",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));
        borderID = getResources().getIdentifier("grass_water_corner3",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Water->grass corner SW",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));
        borderID = getResources().getIdentifier("grass_water_corner2",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Water->grass corner SE",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));

        borderID = getResources().getIdentifier("grass_water_corner1",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Water->grass corner NE",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));
        borderID = getResources().getIdentifier("grass_water_corner4",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Water->grass corner NW",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));

        borderID = getResources().getIdentifier("water_grass_corner1",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Grass->water corner SW",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));

        borderID = getResources().getIdentifier("water_grass_corner4",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Grass->water corner SE",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));

        borderID = getResources().getIdentifier("water_grass_corner3",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Grass->water corner NE",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));

        borderID = getResources().getIdentifier("water_grass_corner2",
                                                "drawable", "com.group7.dragonwars");
        this.graphics.get("Fields").put("Grass->water corner NW",
                                        BitmapFactory.decodeResource(context.getResources(),
                                                                     borderID));

        Integer selID = getResources().getIdentifier("selector",
                                                     "drawable",
                                                     "com.group7.dragonwars");
        selector = BitmapFactory.decodeResource(context.getResources(), selID);

        Log.d(TAG, "after fields");
        /* Register units */

        this.graphics.put("Units", new HashMap<String, Bitmap>());

        for (Map.Entry<Character, Unit> ent : this.map.getUnitMap().entrySet()) {
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

        for (Map.Entry<Character, Building> ent : this.map.getBuildingMap().entrySet()) {
            Building f = ent.getValue();
            Log.d(TAG, "about to getResources() for " + f.getBuildingName());
            Integer resourceID = getResources().getIdentifier(f.getSpriteLocation(),
                                 f.getSpriteDir(),
                                 f.getSpritePack());
            this.graphics.get("Buildings").put(f.getBuildingName(),
                                               BitmapFactory.decodeResource(context.getResources(),
                                                       resourceID));
        }


        Integer highID = getResources().getIdentifier("highlight",
                                                      "drawable",
                                                      "com.group7.dragonwars");
        highlighter = BitmapFactory.decodeResource(context.getResources(),
                                                   highID);
        Log.d(TAG, "after buildings");
        holder.addCallback(this);

        selected = new Position(0, 0); // I really hope that it's ok to assume that the map is at least 1*1

        unit_selected = map.getField(selected).hostsUnit();

        gesture_detector = new GestureDetector(this.getContext(), this);
        scroll_offset = new FloatPair(0f, 0f);

        /* Prerender combined map */
        fullMap = combineMap();

    }

    /*
     * This method will combine the static part of the map into
     * a single bitmap. This should make it far faster to render
     * and prevent any tearing while scrolling the map
     */
    private Bitmap combineMap() {
        Bitmap result = null;
        Integer width = map.getWidth() * tilesize;
        Integer height = map.getHeight() * tilesize;

        result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas combined = new Canvas(result);

        for (int i = 0; i < map.getWidth(); ++i) {
            for (int j = 0; j < map.getHeight(); j++) {
            	Position pos = new Position(i, j);
                GameField gf = map.getField(i, j);
                String gfn = gf.getFieldName();
                RectF dest = getSquare(tilesize * i, tilesize * j, tilesize);
                combined.drawBitmap(graphics.get("Fields").get(gfn), null, dest, null);

                drawBorder(combined, pos, dest);

                if (gf.hostsBuilding()) {
                    Building b = gf.getBuilding();
                    String n = b.getBuildingName();
                    combined.drawBitmap(graphics.get("Buildings").get(n), null, dest, null);
                }
            }
        }

        return result;
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
        this.map = newmap;
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
    	// all of the functionality that was previously here is now in onSingleTapConfirmed()
    	gesture_detector.onTouchEvent(event);
        return true;
    }

    public RectF getSquare(float x, float y, float length) {
    	return new RectF(x, y, x + length, y + length);
    }

    public List<Position> getUnitDestinations(GameField selected_field) {
        List<Position> unit_destinations = new ArrayList<Position>(0);

        /* First time clicking */
        if (lastUnit == null || lastField == null || lastDestinations == null) {
            lastField = selected_field;
            if (selected_field.hostsUnit()) {
                Unit unit = selected_field.getUnit();
                lastUnit = unit;
                unit_destinations = logic.destinations(map, unit);
                lastDestinations = unit_destinations;
            }
            else {
                unit_destinations = new ArrayList<Position>(0);
                lastDestinations = unit_destinations;
            }
        }

        if (selected_field.equals(lastField) && selected_field.hostsUnit()) {
            if (selected_field.getUnit().equals(lastUnit)) { /* Same unit, same place */
                unit_destinations = lastDestinations;
            }
        }
        else {
            if (selected_field.hostsUnit())
                unit_destinations = logic.destinations(map, selected_field.getUnit());

            lastDestinations = unit_destinations;
            lastField = selected_field;
            lastUnit = selected_field.getUnit(); /* Fine if null */
        }

        return unit_destinations;

    }

    public void drawBorder(Canvas canvas, Position currentField, RectF dest) {
        /* TODO not hard-code this */
        Integer i = currentField.getX();
        Integer j = currentField.getY();

        final String FAKE = "NoTaReAlTiL3";

        String gfn = map.getField(currentField).getFieldName();

        Position nep, np, nwp, ep, cp, wp, sep, sp, swp;
        String ne, n, nw, e, c, w, se, s, sw;

        nep = new Position(i - 1, j - 1);
        np = new Position(i, j - 1);
        nwp = new Position(i + 1, j - 1);

        ep = new Position(i - 1, j);
        cp = new Position(i, j);
        wp = new Position(i + 1, j);

        sep = new Position(i - 1, j + 1);
        sp = new Position(i, j + 1);
        swp = new Position(i + 1, j + 1);

        ne = map.isValidField(nep) ? map.getField(nep).toString() : FAKE;
        n = map.isValidField(np) ? map.getField(np).toString() : FAKE;
        nw = map.isValidField(nwp) ? map.getField(nwp).toString() : FAKE;

        e = map.isValidField(ep) ? map.getField(ep).toString() : FAKE;
        c = map.isValidField(cp) ? map.getField(cp).toString() : FAKE;
        w = map.isValidField(wp) ? map.getField(wp).toString() : FAKE;

        se = map.isValidField(sep) ? map.getField(sep).toString() : FAKE;
        s = map.isValidField(sp) ? map.getField(sp).toString() : FAKE;
        sw = map.isValidField(swp) ? map.getField(swp).toString() : FAKE;

        if (gfn.equals("Water")) {
            if (w.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Right grass->water border"), //2
                                  null, dest, null);

        }

        if (gfn.equals("Water")) {
            if (e.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Left grass->water border"),
                                  null, dest, null);
        }


        if (gfn.equals("Water")) {
            if (s.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Top grass->water border"),
                                  null, dest, null);
        }

        if(gfn.equals("Water")) {
            if(n.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Bottom grass->water border"),
                                  null, dest, null);
        }

        if (gfn.equals("Water")) {
            if (s.equals("Water") && w.equals("Water") && sw.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Grass->water corner SW"),
                                  null, dest, null);
        }

        if (gfn.equals("Water")) {
            if (s.equals("Water") && e.equals("Water") && se.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Grass->water corner SE"),
                                  null, dest, null);
        }

        if (gfn.equals("Water")) {
            if (n.equals("Water") && w.equals("Water") && nw.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Grass->water corner NW"),
                                  null, dest, null);
        }

        if (gfn.equals("Water")) {
            if (n.equals("Water") && e.equals("Water") && ne.equals("Grass"))
                canvas.drawBitmap(graphics.get("Fields").get("Grass->water corner NE"),
                                  null, dest, null);
        }

        if (gfn.equals("Water")) {
            if (w.equals("Grass") && s.equals("Grass") && sw.equals("Grass")) {
                canvas.drawBitmap(graphics.get("Fields").get("Water->grass corner SW"),
                                  null, dest, null);
            }
        }

        if (gfn.equals("Water")) {
            if (e.equals("Grass") && s.equals("Grass") && se.equals("Grass")) {
                canvas.drawBitmap(graphics.get("Fields").get("Water->grass corner SE"),
                                  null, dest, null);
            }
        }

        if (gfn.equals("Water")) {
            if (w.equals("Grass") && n.equals("Grass") && nw.equals("Grass")) {
                canvas.drawBitmap(graphics.get("Fields").get("Water->grass corner NW"),
                                  null, dest, null);
            }
        }

        if (gfn.equals("Water")) {
            if (e.equals("Grass") && n.equals("Grass") && ne.equals("Grass")) {
                canvas.drawBitmap(graphics.get("Fields").get("Water->grass corner NE"),
                                  null, dest, null);
            }
        }

    }

    private List<String> getFieldSquare(Position p) {
        List<String> r = new ArrayList<String>();
        GameField  ne, n, nw, e, c, w, se, s, sw;
        int i, j;
        i = p.getX();
        j = p.getY();
        if (i < 1 || j < 1 || i > 6 || j > 6)
            return new ArrayList<String>();

        ne = map.getField(new Position(i - 1, j - 1));
        n = map.getField(new Position(i, j - 1));
        nw = map.getField(new Position(i + 1, j - 1));

        e = map.getField(new Position(i - 1, j));
        c = map.getField(new Position(i, j));
        w = map.getField(new Position(i + 1, j));

        se = map.getField(new Position(i - 1, j + 1));
        s = map.getField(new Position(i, j + 1));
        sw = map.getField(new Position(i + 1, j + 1));
        r.add("" + ne.toString().charAt(0) + n.toString().charAt(0) + nw.toString().charAt(0));
        r.add("" + e.toString().charAt(0) + c.toString().charAt(0) + w.toString().charAt(0));
        r.add("" + se.toString().charAt(0) + s.toString().charAt(0) + sw.toString().charAt(0));

        return r;
    }

    public void doDraw(Canvas canvas) {
        Configuration c = getResources().getConfiguration();

        canvas.drawColor(Color.BLACK); // Draw black anyway, in order to ensure that there are no leftover graphics

        GameField selected_field = map.getField(selected);
        List<Position> unit_destinations = getUnitDestinations(selected_field);

        canvas.drawBitmap(fullMap, scroll_offset.getX(), scroll_offset.getY(), null);

        for (int i = 0; i < map.getWidth(); ++i) {
            for (int j = 0; j < map.getHeight(); j++) {
                GameField gf = map.getField(i, j);
                RectF dest = getSquare(
                		tilesize * i + scroll_offset.getX(),
                		tilesize * j + scroll_offset.getY(),
                		tilesize);

                if (gf.hostsUnit()) {
                    Unit unit = gf.getUnit();

                    if (unit != null) {
                        String un = unit.toString();
                        canvas.drawBitmap(graphics.get("Units").get(un), null, dest, null);
                    }
                }

                // Paint p = new Paint();
                // p.setColor(Color.RED);
                // canvas.drawText(pos.toString() + gfn.charAt(0), tilesize * i + scroll_offset.getX(),
                //                 tilesize * j + scroll_offset.getY() + 64, p);
                // List<String> aoe = getFieldSquare(pos);
                // for (int x = 0; x < aoe.size(); ++x)
                //     canvas.drawText(aoe.get(x), tilesize * i + scroll_offset.getX(),
                //                     tilesize * j + scroll_offset.getY() + 20 + (x * 10), p);

                // Paint r = new Paint();
                // r.setStyle(Paint.Style.STROKE);
                // r.setColor(Color.RED);
                // canvas.drawRect(dest, r);
            }
        }


        // perform highlighting
        for (Position pos : unit_destinations) {
        	RectF dest = getSquare(
            		tilesize * pos.getX() + scroll_offset.getX(),
            		tilesize * pos.getY() + scroll_offset.getY(),
            		tilesize);
        	canvas.drawBitmap(highlighter, null, dest, null);
        }

        RectF dest = getSquare(
            tilesize * selected.getX() + scroll_offset.getX(),
            tilesize * selected.getY() + scroll_offset.getY(),
            tilesize);
        canvas.drawBitmap(selector, null, dest, null);


	// draw the information box
	drawInfoBox(canvas, selected_field.hostsUnit() ? selected_field.getUnit() : null, selected_field, true);
    }

    public float getMapDrawWidth() {
    	// returns the width of map in pixels
    	return map.getWidth() * tilesize;
    }

    public float getMapDrawHeight() {
    	// returns the width of map in pixels
    	return map.getHeight() * tilesize;
    }

    public void drawInfoBox(Canvas canvas, Unit unit, GameField field, boolean left) {
    	LinkedList<String> info = new LinkedList<String>();
        info.add(field.getFieldName()); /* Always print for debug */
    	// unit info
        if (unit != null) {
        	info.add(unit.getUnitName());// + " - Player: " + unit.getOwner().getName()); // causes problems when there is no owner
        	info.add("Health: " + unit.getHealth() + "/" + unit.getMaxHealth());
        	info.add("Attack: " + unit.getAttack());
        	info.add("Defense: " + unit.getMeleeDefense() + " (Melee) " + unit.getRangeDefense() + " (Ranged)");
        	info.add("");
        }

    	// field names
        if (field.hostsBuilding()) {
            Building building = field.getBuilding();
            String bLine = building.getBuildingName();
            bLine += building.hasOwner() ? " ~ " + building.getOwner().getName() : "";
            info.add(bLine);

            if (building.canProduceUnits())
                for (Unit u : building.getProducableUnits())
                    info.add("I can produce " + u + " - " + u.getProductionCost() + "g");
            else
                info.add("I can't produce anything.");
        }

        // field stats
        info.add("Attack: " + field.getAttackModifier() +
        		" Defense: " + field.getDefenseModifier() +
        		" Move: " + field.getMovementModifier());

        Paint text_paint = new Paint();
        text_paint.setColor(Color.WHITE);

        Rect text_bounds = new Rect(0, 0, 0, 0); // contains the bounds of the entire text in the info box
        LinkedList<Rect> info_bounds = new LinkedList<Rect>();
        for (int i = 0; i < info.size(); ++i) {
        	String text = info.get(i);
        	info_bounds.add(i, new Rect());
        	text_paint.getTextBounds(text, 0, text.length(), info_bounds.get(i));
        	if (info_bounds.get(i).right > text_bounds.right) {
        		text_bounds.right = info_bounds.get(i).right;
        	}
        	text_bounds.bottom += (info_bounds.get(i).bottom - info_bounds.get(i).top);
        }

        Paint back_paint = new Paint();
        back_paint.setColor(Color.BLACK); // FIXME make it black

        Rect back_rect = new Rect(0, canvas.getHeight() - text_bounds.bottom, text_bounds.right, canvas.getHeight());
        canvas.drawRect(back_rect, back_paint);

        float text_height = 0f;
        for (int i = info.size() - 1; i >= 0; --i) {
        	canvas.drawText(info.get(i), 0, info.get(i).length(), 0, (canvas.getHeight() - text_height) - info_bounds.get(i).bottom, text_paint);
        	text_height += (info_bounds.get(i).bottom - info_bounds.get(i).top);
        }
        //canvas.drawText(info, 0, info.length(), (float) back_rect.left, (float) back_rect.bottom, text_paint);
    }

    /* Please excuse all the auto-generated method stubs
     * as we're implementing an interface or two (GestureDetector.OnGestureListener etc), we need them all
     */

	@Override
	public boolean onDown(MotionEvent e) {return false;}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {return false;}

	@Override
	public boolean onDoubleTap(MotionEvent e) {return false;}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {return false;}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
    	int touchX = (int)((event.getX() - scroll_offset.getX()) / tilesize); // the coordinates of the pressed tile
    	int touchY = (int)((event.getY() - scroll_offset.getY()) / tilesize); // taking into account scrolling

		//Log.v(null, "Touch ended at: (" + touchX + ", " + touchY + ") (dimensions are: (" + this.map.getWidth() + "x" + this.map.getHeight() + "))");
        Position newselected = new Position(touchX, touchY);
        if (this.map.isValidField(touchX, touchY)) {
        	//Log.v(null, "Setting selection");
        	this.selected = newselected;
        }
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		float new_x = scroll_offset.getX() - distanceX;
		if (this.getWidth() >= this.getMapDrawWidth()) {
			new_x = 0;
		} else if ((-new_x) > (getMapDrawWidth() - getWidth())) {
			new_x = -(getMapDrawWidth() - getWidth());
		} else if (new_x > 0) {
			new_x = 0;
		}
		float new_y = scroll_offset.getY() - distanceY;
		if (this.getHeight() >= this.getMapDrawHeight()) {
			new_y = 0;
		} else if ((-new_y) > (getMapDrawHeight() - getHeight())) {
			new_y = -(getMapDrawHeight() - getHeight());
		} else if (new_y > 0) {
			new_y = 0;
		}
		scroll_offset = new FloatPair(new_x, new_y);
		return true;
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

class FloatPair { // the Position code, but with Floats
    private Pair<Float, Float> pair;

    public FloatPair(Float x, Float y) {
        this.pair = new Pair<Float, Float>(x, y);
    }

    public Float getX() {
        return this.pair.getLeft();
    }

    public Float getY() {
        return this.pair.getRight();
    }

    public Boolean equals(FloatPair other) {
        return this.getX() == other.getX() && this.getY() == other.getY();
    }

    public String toString() {
        return String.format("(%d, %d)", this.pair.getLeft(),
                             this.pair.getRight());
    }

}
