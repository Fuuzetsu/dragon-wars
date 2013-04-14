package com.group7.dragonwars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Results extends Activity implements OnClickListener {
    
    private Button btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        btnMenu = (Button)findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Bundle b = getIntent().getExtras();
        TextView winnerNameText = (TextView) findViewById(R.id.winnerNameText);
        winnerNameText.setText(b.getString("winnerName"));
        TextView turnsText = (TextView) findViewById(R.id.turnsText);
        turnsText.setText(Integer.toString(b.getInt("turns")) + " turns");
        
        String[] statsKeys = {"Damage dealt", "Damage received", "Distance travelled", "Gold received", "Units killed", "Units produced"};
        String[] statsEntries = new String[statsKeys.length];
        for (int i = 0; i < statsKeys.length; ++i) {
            statsEntries[i] = statsKeys[i] + ": " + b.getDouble(statsKeys[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, statsEntries);
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
