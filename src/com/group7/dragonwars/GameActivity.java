package com.group7.dragonwars;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.group7.dragonwars.engine.GameMap;
import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Logic;
import com.group7.dragonwars.engine.MapReader;
import com.group7.dragonwars.engine.Statistics;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";

    private GameState state = null;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // remove the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d(TAG, "in onCreate");
        //setContentView(R.layout.loading_screen);
        Log.d(TAG, "on inCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle b = getIntent().getExtras();
        String mapFileName = b.getString("mapFileName");
        GameMap map = null;
        try {
            map = MapReader.readMapFromFile(mapFileName, this);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to load the map: " + e.getMessage());
        }
        if (map == null) {
            Log.d(TAG, "map is null");
            System.exit(1);
        }

        state = new GameState(map, new Logic(), map.getPlayers());
        setContentView(R.layout.activity_game);
        GameView gameView = (GameView) this.findViewById(R.id.gameView);
        Button menuButton = (Button) this.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(gameView);
        gameView.setState(state, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            GameView gameView = (GameView) this.findViewById(R.id.gameView);
            gameView.showMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void endGame() {
        setContentView(R.layout.loading_screen);
        Intent intent = new Intent(this, Results.class);
        Bundle b = new Bundle();
        b.putString("winnerName", state.getWinner().getName());
        b.putInt("turns", state.getTurns());
        Statistics stats = state.getStatistics();
        for (Map.Entry<String, Double> ent : stats.getEntrySet()) {
            b.putDouble(ent.getKey(), ent.getValue().doubleValue());
        }
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

}
