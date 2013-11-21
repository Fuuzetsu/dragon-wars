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

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.group7.dragonwars.engine.Database.Database;

public class StatisticsActivity extends Activity implements OnClickListener {

    private Button btnMenu;
    private DecimalFormat decformat = new DecimalFormat("#.##");

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);
    }

    @Override
    protected final void onStart() {
        super.onStart();
        Database db = new Database(getApplicationContext());

        /* TODO do the Database.Entry class properly to avoid all this */
        String[] statsEntries = new String[6];

        Database.Entry entry = db.GetSummedEntries();
        db.Close();

        statsEntries[0] =
            String.format("Damage dealt: %s",
                          decformat.format(entry.DAMAGEDEALT));
        statsEntries[1] =
            String.format("Damage received: %s",
                          decformat.format(entry.DAMAGERECEIVED));
        statsEntries[2] =
            String.format("Distance travelled: %s",
                          decformat.format(entry.DISTANCETRAVELLED));
        statsEntries[3] =
            String.format("Gold collected: %s",
                          decformat.format(entry.GOLDCOLLECTED));
        statsEntries[4] =
            String.format("Units killed: %s",
                          decformat.format(entry.UNITSKILLED));
        statsEntries[5] =
            String.format("Units produced: %s",
                          decformat.format(entry.UNITSMADE));

        ArrayAdapter<String> adapter =
            new ArrayAdapter<String>(getBaseContext(),
                                     android.R.layout.simple_list_item_1,
                                     statsEntries);

        ListView statsList = (ListView) findViewById(R.id.statsList);
        statsList.setAdapter(adapter);
    }

    @Override
    public final void onClick(final View v) {
        if (v == this.btnMenu) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
