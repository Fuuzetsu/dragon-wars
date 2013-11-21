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

package com.group7.dragonwars.engine;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

public final class GameMap implements Iterable<GameField> {

    private List<List<GameField>> fields;
    private List<Player> players;
    private Map<Character, Unit> units;
    private Map<Character, Building> buildings;
    private Map<Character, GameField> gameFields;
    private Bitmap ownImage;

    public GameMap(final List<List<GameField>> fields,
                   final Map<Character, Unit> units,
                   final Map<Character, Building> buildings,
                   final Map<Character, GameField> gameFields,
                   final List<Player> players) {
        this.fields = fields;
        this.units = units;
        this.buildings = buildings;
        this.gameFields = gameFields;
        this.players = players;

    }

    public Iterator<GameField> iterator() {
        /* This really isn't ideal as we will iterate twice. */
        List<GameField> flat = new ArrayList<GameField>();

        for (List<GameField> row : this.fields) {
            for (GameField gf : row) {
                flat.add(gf);
            }
        }

        return flat.iterator();
    }

    public Bitmap getImage() {
        return ownImage;
    }

    public void setImage(final Bitmap img) {
        ownImage = img;
    }

    public Boolean isInstantiated() {
        return fields != null;
    }

    public Integer getWidth() {
        return fields.get(0).size();
    }

    public Integer getHeight() {
        return fields.size();
    }

    public GameField getField(final Position position) {
        return getField(position.getX(), position.getY());
    }

    public GameField getField(final Integer x, final Integer y) {
        return fields.get(y).get(x);
    }

    public Boolean isValidField(final Position position) {
        return isValidField(position.getX(), position.getY());
    }

    public Boolean isValidField(final Integer x, final Integer y) {
        if (this.fields == null || this.fields.get(0) == null) {
            return false;
        }

        if (x < 0 || y < 0) {
            return false;
        }

        if (x >= this.getWidth() || y >= this.getHeight()) {
            return false;
        }

        return true;
    }

    public String toString() {
        String m = "";

        for (List<GameField> agf : this.fields) {
            for (GameField gf : agf) {
                m += gf.toString().charAt(0);
            }

            m += '\n';
        }

        return m;
    }

    public Map<Character, Unit> getUnitMap() {
        return this.units;
    }

    public Map<Character, Building> getBuildingMap() {
        return this.buildings;
    }

    public Map<Character, GameField> getGameFieldMap() {
        return this.gameFields;
    }

    public List<Player> getPlayers() {
        return this.players;
    }
}
