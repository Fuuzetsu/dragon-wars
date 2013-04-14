package com.group7.dragonwars;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MapSelectActivity extends Activity implements OnItemClickListener {
    private String[]  mapFileNames = {};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);
        ListView mapList = (ListView)this.findViewById(R.id.mapList);
        mapList.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AssetManager assman = getAssets();
        ListView mapList = (ListView)this.findViewById(R.id.mapList);
        try {
            mapFileNames = assman.list("maps");
            //String[] mapNames = new String[mapFileNames.length];
            for (int i = 0; i < mapFileNames.length; ++i) {
                mapFileNames[i] = new File("maps", mapFileNames[i]).toString();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, mapFileNames);
            mapList.setAdapter(adapter);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // TODO Auto-generated method stub
        if (position < mapFileNames.length) {
            setContentView(R.layout.loading_screen);
            Intent intent = new Intent(this, GameActivity.class);
            Bundle b = new Bundle();
            b.putString("mapFileName", mapFileNames[position]);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }
    }



}
