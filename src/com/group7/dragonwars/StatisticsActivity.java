package com.group7.dragonwars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.group7.dragonwars.engine.Database.Database;

import java.sql.SQLException;

public class StatisticsActivity extends Activity implements OnClickListener {

    private Button btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        btnMenu = (Button)findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Database db = new Database(getApplicationContext());

        String[] statsEntries = new String[statsKeys.length];

        Database.Entry entry = db.GetSummedEntries();

        statsEntries[0] = String.format("Damage dealt: %f", entry.DAMAGEDEALT);
        statsEntries[1] = String.format("Damage received: %f", entry.DAMAGERECEIVED);
        statsEntries[2] = String.format("Distance travelled: %f", entry.DISTANCETRAVELLED);
        statsEntries[3] = String.format("Gold collected: %d", entry.GOLDCOLLECTED);
        statsEntries[4] = String.format("Units killed: %d", entry.UNITSKILLED);
        statsEntries[5] = String.format("Units produced: %d", entry.UNITSMADE);

        ArrayAdapter<String> adapter =
            new ArrayAdapter<String>(getBaseContext(),
                                     android.R.layout.simple_list_item_1, statsEntries);

        ListView statsList = (ListView) findViewById(R.id.statsList);
        statsList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == this.btnMenu) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
