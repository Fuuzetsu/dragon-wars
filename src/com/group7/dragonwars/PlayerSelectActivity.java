package com.group7.dragonwars;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PlayerSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_select);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player_select, menu);
        return true;
    }

}
