package com.group7.dragonwars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.group7.dragonwars.engine.BasicMapInfo;
import com.group7.dragonwars.engine.MapReader;

public class MapSelectActivity extends Activity implements OnItemClickListener {
    private List<BasicMapInfo> mapInfo = new ArrayList<BasicMapInfo>();

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
        if (mapInfo.size() == 0) {
            AssetManager assman = getAssets();
            ListView mapList = (ListView)this.findViewById(R.id.mapList);
            try {
                String[] files = assman.list("maps");
    
                for (int i = 0; i < files.length; ++i) {
                    String path = "maps/" + files[i];
                    BasicMapInfo info = MapReader.getBasicMapInformation(path, this);
                    mapInfo.add(info);
                }
                String[] displayInfo = new String[mapInfo.size()];
                for (Integer i = 0; i < mapInfo.size(); i++) {
                    displayInfo[i] = mapInfo.get(i).getDesc();
                }
                ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this,
                                             android.R.layout.simple_list_item_1, displayInfo);
                mapList.setAdapter(adapter);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (position < mapInfo.size()) {
            Intent intent = new Intent(this, PlayerSelectActivity.class);
            Bundle b = new Bundle();
            b.putString("mapFileName", mapInfo.get(position).getPath());
            b.putString("mapName", mapInfo.get(position).getName());
            
            int numPlayers = mapInfo.get(position).getPlayers();
            b.putBooleanArray("isAi", new boolean[numPlayers]);
            // boolean defaults to false, what could possibly go wrong?
            b.putInt("numPlayers", numPlayers);
            
            /* Make a fake player list for now, again */
            String[] playerNames = new String[numPlayers];
            for (Integer i = 0; i < numPlayers; ++i)
                playerNames[i] = ("Player " + (i + 1));
            b.putStringArray("playerNames", playerNames);
            
            intent.putExtras(b);
            startActivity(intent);
            //finish();
        }
    }



}
