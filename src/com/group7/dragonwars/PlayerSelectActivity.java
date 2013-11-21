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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.group7.dragonwars.engine.Player;

public class PlayerSelectActivity extends Activity implements OnClickListener {
    private int numPlayers;
    private Button backButton;
    private Button beginButton;
    private Bundle bundle;
    private Player[] players;
    private String[] playerNames;
    private boolean[] isAi;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_select);
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        beginButton = (Button) findViewById(R.id.beginButton);
        beginButton.setOnClickListener(this);
    }

    @Override
    protected final void onStart() {
        super.onStart();
        bundle = getIntent().getExtras();

        numPlayers = bundle.getInt("numPlayers");
        players = new Player[numPlayers];
        playerNames = bundle.getStringArray("playerNames");
        isAi = bundle.getBooleanArray("isAi");

        for (int i = 0; i < numPlayers; ++i) {
            players[i] = new Player(playerNames[i], 0);
            players[i].setIsAi(isAi[i]);
        }

        IsAiAdapter adapter = new IsAiAdapter(
            getBaseContext(), R.layout.listview_row_player_select, players);

        ListView playerList = (ListView) findViewById(R.id.playerList);
        playerList.setAdapter(adapter);
    }

    @Override
    public final void onClick(final View v) {
        if (v == this.backButton) {
            Intent intent = new Intent(this, MapSelectActivity.class);
            startActivity(intent);
            finish();
        } else if (v == this.beginButton) {
            setContentView(R.layout.loading_screen);
            Intent intent = new Intent(this, GameActivity.class);

            for (int i = 0; i < numPlayers; ++i) {
                isAi[i] = players[i].isAi();
            }

            bundle.putBooleanArray("isAi", isAi);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }
}
