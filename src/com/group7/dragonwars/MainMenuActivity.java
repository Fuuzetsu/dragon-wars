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

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity implements OnClickListener {

    private Button btnBattle, btnStats, btnQuit, btnHelp;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        this.btnBattle = (Button) this.findViewById(R.id.btnBattle);
        this.btnBattle.setOnClickListener(this);

        this.btnStats = (Button) this.findViewById(R.id.btnStats);
        this.btnStats.setOnClickListener(this);

        this.btnHelp = (Button) this.findViewById(R.id.btnHelp);
        this.btnHelp.setOnClickListener(this);

        this.btnQuit = (Button) this.findViewById(R.id.btnQuit);
        this.btnQuit.setOnClickListener(this);

    }

    @Override
    protected final void onStart() {
        super.onStart();

        AssetManager ass = getAssets();

        try {
            String[] files = ass.list("maps");

            for (int i = 0; i < files.length; ++i) {
                Log.v(null, "File " + i + " " + files[i]);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public final void onClick(final View v) {
        if (v == this.btnBattle) {
            Intent intent = new Intent(this, MapSelectActivity.class);
            startActivity(intent);
            finish();
        }

        if (v == this.btnStats) {
            setContentView(R.layout.loading_screen);
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        }

        if (v == this.btnHelp) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }

        if (v == this.btnQuit) {
            System.exit(0);
        }
    }
}
