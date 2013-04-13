package com.group7.dragonwars;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.group7.dragonwars.engine.GameMap;
import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Logic;
import com.group7.dragonwars.engine.MapReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";
    private Integer orientation;
    private Boolean orientationChanged = false;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // remove the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_game);
        Log.v(null, "on inCreate");

        Button menuButton = (Button) this.findViewById(R.id.menuButton);
        GameView gameView = (GameView) this.findViewById(R.id.gameView);
        menuButton.setOnClickListener(gameView);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Bundle b = getIntent().getExtras();
        String mapFileName = b.getString("mapFileName");
        GameMap map = null;
        try {
            map = MapReader.readMap(readFile(mapFileName));
        } catch (JSONException e) {
            Log.d(TAG, "Failed to load the map: " + e.getMessage());
        }
        if (map == null) {
            Log.d(TAG, "map is null");
            System.exit(1);
        }

        GameState state = new GameState(map, new Logic(), map.getPlayers());
        GameView gameView = (GameView) this.findViewById(R.id.gameView);
        gameView.setState(state);
    }
    
    private List<String> readFile(final String fileName) {
        AssetManager am = this.getAssets();
        List<String> text = new ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(am.open(fileName)));
            String line;

            while ((line = in.readLine()) != null) {
                text.add(line);
            }

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
