package com.group7.dragonwars;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.group7.dragonwars.engine.GameField;
import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Unit;
import com.group7.dragonwars.engine.Map;
import com.group7.dragonwars.engine.MapReader;
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

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_FULLSCREEN;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	private ScrollView map_scroller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game);

		final View contentView = findViewById(R.id.game_view);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.hide();
		GameView game_view = (GameView) this.findViewById(R.id.game_view);
		Map map = MapReader.readMap(readFile(R.raw.map2)); // ugh
		game_view.setMap(map);
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
}

class GameView extends SurfaceView implements SurfaceHolder.Callback {
	Bitmap bm;
	Map gm;
	DrawingThread dt;
	Context context;
	List<Bitmap> tiles;

	//
	public GameView(Context ctx, AttributeSet attrset) {
		super(ctx, attrset);
		context = ctx;
		bm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		tiles = new ArrayList<Bitmap>();
		tiles.add(BitmapFactory.decodeResource(context.getResources(),
				R.raw.grass));
		tiles.add(BitmapFactory.decodeResource(context.getResources(),
				R.raw.sea));
		tiles.add(BitmapFactory.decodeResource(context.getResources(),
				R.raw.small_dragon));
		tiles.add(BitmapFactory.decodeResource(context.getResources(),
				R.raw.soldier));
	}

	public void setMap(Map newmap) {
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
		int size = 90;
		for (int i = 0; i < gm.getWidth(); ++i) {
			for (int j = 0; j < gm.getHeight(); j++) {
				canvas.drawBitmap(tiles.get(0), size * j, size * i, null); // Draw
																		// grass
																		// underneith.
				GameField gf = gm.getField(i, j);
				String gfn = gf.toString();
				if (gfn.equals("Water")) { // No switch for you, String!
					canvas.drawBitmap(tiles.get(1), size * j, size * i, null);
				} else if (gfn.equals("Grass")) { // No == for you, String!
					// Surprise, grass has already been drawn.
				}
				if (gf.hostsBuilding()) {
					// Draw the building, however the current engine does not
					// currently do this
					// Well techinically the classes and string IDs of the
					// buildings currently do not do exist, and the engine does
				}
				if (gf.hostsUnit()) {
					Unit unit = gf.getUnit();
					if (unit != null) {
						String un = unit.toString();
						if (un.equals("Dragon")) {
							canvas.drawBitmap(tiles.get(2), size * j, size * i,
									null);
						} else if (un.equals("Soldier")) {
							canvas.drawBitmap(tiles.get(3), size * j, size * i,
									null);
						}
						//Log.v(null, i + ", " + j + " has a " + un);
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