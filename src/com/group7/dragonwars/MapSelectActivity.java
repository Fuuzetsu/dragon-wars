package com.group7.dragonwars;

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

import java.util.ArrayList;
import java.util.List;

import com.group7.dragonwars.engine.MapReader;
import com.group7.dragonwars.engine.Pair;

import org.json.JSONException;

public class MapSelectActivity extends Activity implements OnItemClickListener {
    private List<Pair<String, String>> mapInfo = new ArrayList<Pair<String, String>>();

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
            String[] files = assman.list("maps");

            for (int i = 0; i < files.length; ++i) {
                String path = files[i];
                String info = MapReader.getBasicMapInformation("maps/" + path, this);
                mapInfo.add(new Pair<String, String>(path, info));
            }
            String[] displayInfo = new String[mapInfo.size()];
            for (Integer i = 0; i < mapInfo.size(); i++) {
                displayInfo[i] = mapInfo.get(i).getRight();
            }
            ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getBaseContext(),
                                         android.R.layout.simple_list_item_1, displayInfo);
            mapList.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // TODO Auto-generated method stub
        if (position < mapInfo.size()) {
            setContentView(R.layout.loading_screen);
            Intent intent = new Intent(this, GameActivity.class);
            Bundle b = new Bundle();
            b.putString("mapFileName", mapInfo.get(position).getLeft());
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }
    }



}
