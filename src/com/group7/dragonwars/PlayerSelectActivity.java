package com.group7.dragonwars;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.group7.dragonwars.engine.Player;

public class PlayerSelectActivity extends Activity {
    private int numPlayers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_select);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Bundle b = getIntent().getExtras();
        
        numPlayers = b.getInt("numPlayers");
        Player[] players = new Player[numPlayers];
        String[] playerNames = b.getStringArray("playerNames");
        boolean[] isAi = b.getBooleanArray("isAi");

        for (int i = 0; i < numPlayers; ++i) {
            players[i] = new Player(playerNames[i], 0);
            players[i].setIsAi(isAi[i]);
        }
        
        IsAiAdapter adapter = new IsAiAdapter(getBaseContext(),
                    R.layout.listview_row_player_select, players);
        
        ListView playerList = (ListView) findViewById(R.id.playerList);
        playerList.setAdapter(adapter);
    }

}
