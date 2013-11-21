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

import java.util.Map;

import org.json.JSONException;

import com.group7.dragonwars.engine.GameMap;
import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Logic;
import com.group7.dragonwars.engine.MapReader;
import com.group7.dragonwars.engine.Statistics;
import com.group7.dragonwars.engine.Database.Database;

import android.app.Activity;
import android.content.Intent;
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
    protected final void onStart() {
        super.onStart();
        Bundle b = getIntent().getExtras();
        String mapFileName = b.getString("mapFileName");
        boolean[] isAi = b.getBooleanArray("isAi");
        GameMap map = null;

        try {
            map = MapReader.readMapFromFile(mapFileName, this, isAi);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to load the map: " + e.getMessage());
        }

        if (map == null) {
            Log.d(TAG, "map is null");
            System.exit(1);
        }

        setContentView(R.layout.activity_game);
        GameView gameView = (GameView) this.findViewById(R.id.gameView);
        state = new GameState(map, new Logic(), map.getPlayers(), gameView);
        Button menuButton = (Button) this.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(gameView);
        gameView.setState(state, this);
    }

    @Override
    public final boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            GameView gameView = (GameView) this.findViewById(R.id.gameView);
            gameView.showMenu();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public final void endGame() {
        setContentView(R.layout.loading_screen);
        Intent intent = new Intent(this, Results.class);
        Bundle b = new Bundle();
        b.putString("winnerName", state.getWinner().getName());
        b.putInt("turns", state.getTurns());
        Statistics stats = state.getStatistics();
        Double damageDealt = stats.getStatistic("Damage dealt");
        Double damageReceived = stats.getStatistic("Damage received");
        Double distanceTravelled = stats.getStatistic("Distance travelled");
        Integer goldCollected = stats.getStatistic("Gold received").intValue();
        Integer unitsKilled = stats.getStatistic("Units killed").intValue();
        Integer unitsMade = stats.getStatistic("Units produced").intValue();

        Database db = new Database(getApplicationContext());
        db.AddEntry(damageDealt, damageReceived, distanceTravelled,
                    goldCollected, unitsKilled, unitsMade);
        db.Close();

        for (Map.Entry<String, Double> ent : stats.getEntrySet()) {
            b.putDouble(ent.getKey(), ent.getValue().doubleValue());
        }

        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

}
