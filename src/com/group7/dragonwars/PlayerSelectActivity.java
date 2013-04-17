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
    boolean[] isAi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_select);
        backButton = (Button) findViewById(R.id.backButton);
        beginButton = (Button) findViewById(R.id.beginButton);
    }
    
    @Override
    protected void onStart() {
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
        
        IsAiAdapter adapter = new IsAiAdapter(getBaseContext(),
                    R.layout.listview_row_player_select, players);
        
        ListView playerList = (ListView) findViewById(R.id.playerList);
        playerList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == this.backButton) {
            Intent intent = new Intent(this, MapSelectActivity.class);
            startActivity(intent);
        } else if (v == this.beginButton) {
            Intent intent = new Intent(this, GameActivity.class);
            for (int i = 0; i < numPlayers; ++i) {
                isAi[i] = players[i].isAi();
            }
            bundle.putBooleanArray("isAi", isAi);
            startActivity(intent);
            finish();
        }
    }
}
