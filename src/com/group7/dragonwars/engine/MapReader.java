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

/* Generates a GameField based on a flat text file. Test solution. */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class MapReader {
    private static final String TAG = "MapReader";

    private MapReader() {
    }

    public static GameMap
        readMap(final List<String> mapLines,
                final boolean[] isAi) throws JSONException {
        String jsonSource = "";

        for (String s : mapLines) {
            jsonSource += s + "\n";
        }

        JSONObject m = new JSONObject(jsonSource);
        String mapName = m.getString("mapName");
        Integer sizeX = m.getInt("sizeX");
        Integer sizeY = m.getInt("sizeY");
        Integer players = m.getInt("players");

        JSONObject fs = m.getJSONObject("fields");
        JSONObject bs = m.getJSONObject("buildings");
        JSONObject us = m.getJSONObject("units");
        JSONArray terrain = m.getJSONArray("terrain");
        JSONArray startingBuildingPos = m.getJSONArray("startingBuildingPos");
        JSONArray startingUnitPos = m.getJSONArray("startingUnitPos");
        JSONArray playerColours = m.getJSONArray("playerColours");


        /* Make a fake player list for now */
        List<Player> playerList = new ArrayList<Player>();

        for (Integer i = 0; i < players; ++i) {
            if (isAi[i]) {
                playerList.add(new PlayerAI("AIPlayer " + (i + 1),
                                            playerColours.getInt(i)));
            } else {
                playerList.add(new Player("Player " + (i + 1),
                                          playerColours.getInt(i)));
            }
        }

        /* Fill in a HashMap for look-up */
        Map<Character, JSONObject> fields =
            new HashMap<Character, JSONObject>();
        Iterator<?> iter = fs.keys();

        while (iter.hasNext()) {
            String key = (String) iter.next(); /* We have to cast ;_; */
            fields.put(key.charAt(0), fs.getJSONObject(key));
        }

        Map<Character, GameField> fieldsInfo
            = new HashMap<Character, GameField>();
        iter = fs.keys();

        while (iter.hasNext()) {
            String key = (String) iter.next();
            fieldsInfo.put(
                key.charAt(0),
                new MapReader.TerrainGetter(fields).apply(key.charAt(0)));
        }

        /* HashMap for buildings */
        Map<Character, JSONObject> buildings
            = new HashMap<Character, JSONObject>();
        Iterator<?> bIter = bs.keys();

        while (bIter.hasNext()) {
            String key = (String) bIter.next();
            buildings.put(key.charAt(0), bs.getJSONObject(key));
        }

        Map<Character, Building> buildingsInfo
            = new HashMap<Character, Building>();
        bIter = bs.keys();

        while (bIter.hasNext()) {
            String key = (String) bIter.next();
            buildingsInfo.put(
                key.charAt(0),
                new MapReader.BuildingGetter(buildings).apply(key.charAt(0)));
        }

        /* Map for units to be used for the current map throughout the game */
        Map<Character, Unit> units = new HashMap<Character, Unit>();
        Iterator<?> uIter = us.keys();

        while (uIter.hasNext()) {
            String key = (String) uIter.next();
            units.put(
                key.charAt(0),
                new MapReader.UnitGetter().apply(us.getJSONObject(key)));
        }


        List<List<GameField>> grid = MapReader.listifyJSONArray(
            new MapReader.TerrainGetter(fields), terrain);

        MapReader.setBuildings(grid, playerList, units,
                               buildingsInfo, startingBuildingPos);
        MapReader.spawnUnits(grid, playerList, units, startingUnitPos);

        return new GameMap(grid, units, buildingsInfo, fieldsInfo, playerList);

    }

    public static BasicMapInfo getBasicMapInformation(final String filename,
            final Activity activity) throws JSONException {
        String jsonSource = "";

        for (String s : MapReader.readFile(filename, activity)) {
            jsonSource += s + "\n";
        }

        JSONObject m = new JSONObject(jsonSource);
        String mapName = m.getString("mapName");
        Integer sizeX = m.getInt("sizeX");
        Integer sizeY = m.getInt("sizeY");
        Integer players = m.getInt("players");

        String desc = String.format("%s - %dx%d - %d Players", mapName,
                                    sizeX, sizeY, players);

        return new BasicMapInfo(mapName, desc, filename, players);
    }

    public static GameMap
        readMapFromFile(final String filename,
                        final Activity activity,
                        final boolean[] isAi) throws JSONException {
        return MapReader.readMap(MapReader.readFile(filename, activity), isAi);
    }

    private static List<String> readFile(final String filename,
                                         final Activity activity) {
        AssetManager am = activity.getAssets();
        List<String> text = new ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(am.open(filename)));
            String line;

            while ((line = in.readLine()) != null) {
                text.add(line);
            }

            in.close();
        } catch (FileNotFoundException fnf) {
            System.err.println("Couldn't find " + fnf.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Couldn't read " + ioe.getMessage());
            System.exit(1);
        }

        return text;
    }

    private static <O> List<List<O>> listifyJSONArray(
        final FuncEx<Character, O, JSONException> f,
        final JSONArray xs) throws JSONException {
        List<List<O>> v = new ArrayList<List<O>>();
        List<List<Character>> cs = new ArrayList<List<Character>>();

        for (Integer i = 0; i < xs.length(); i++) {
            String s = xs.getString(i);
            List<Character> t = new ArrayList<Character>();

            for (Integer j = 0; j < s.length(); j++) {
                t.add(s.charAt(j));
            }

            cs.add(t);
        }

        for (List<Character> ys : cs) {
            v.add(map(f, ys));
        }

        return v;
    }

    private static void
        setBuildings(final List<List<GameField>> grid,
                     final List<Player> players,
                     final Map<Character, Unit> units,
                     final Map<Character, Building> buildings,
                     final JSONArray posInfo) throws JSONException {
        Log.d(TAG, "Running setBuildings");
        Log.d(TAG, "The list of players contains " + players);

        for (int y = 0; y < grid.size(); ++y) {
            String row = "";

            for (int x = 0; x < grid.get(y).size(); ++x) {
                if (grid.get(y).get(x) != null) {
                    row += grid.get(y).get(x).getName().charAt(0);
                } else {
                    row += "_";
                }
            }

            Log.d(TAG, row);
        }

        for (Integer i = 0; i < posInfo.length(); ++i) {
            Log.d(TAG, "Grabbing info for building number " + i);
            JSONObject buildingInfo = posInfo.getJSONObject(i);
            Building buildingT
                = buildings.get(buildingInfo.getString("building").charAt(0));
            Building building = new Building(buildingT);
            Integer playerOwner = buildingInfo.getInt("owner");
            Integer posX = buildingInfo.getInt("posX");
            Integer posY = buildingInfo.getInt("posY");
            JSONArray prod = buildingInfo.getJSONArray("produces");

            for (Integer j = 0; j < prod.length(); ++j) {
                Unit unit = units.get(prod.getString(j).charAt(0));
                building.addProducibleUnit(unit);
            }

            Log.d(TAG, "Cast all the values into Java types for building " + i);

            if (playerOwner == 0) {
                building.setOwner(new Player("Gaia", 0));
            } else {
                Player p = players.get(playerOwner - 1);
                building.setOwner(p);
                p.addBuilding(building);
            }
            Log.d(TAG, String.format("Setting %s at %dx%d",
                                     building.getName(), posX, posY));
            Log.d(TAG, String.format("Grid size YxX: %dx%d", grid.size(),
                                     grid.get(0).size()));
            GameField gf = grid.get(posY).get(posX);
            Log.d(TAG, "gf == null: " + (gf == null));
            building.setPosition(new Position(posX, posY));
            gf.setBuilding(building);


        }

        Log.d(TAG, "Leaving setBuildings");

    }

    private static void
        spawnUnits(final List<List<GameField>> grid,
                   final List<Player> players,
                   final Map<Character, Unit> units,
                   final JSONArray posInfo) throws JSONException {
        Log.d(TAG, "Running spawnUnits");
        Log.d(TAG, "The list of players contains " + players);

        for (Integer i = 0; i < posInfo.length(); ++i) {
            Log.d(TAG, "Grabbing info for unit number " + i);
            JSONObject unitInfo = posInfo.getJSONObject(i);
            Unit unitT = units.get(unitInfo.getString("unit").charAt(0));
            Unit unit = new Unit(unitT);
            Integer playerOwner = unitInfo.getInt("owner");
            Integer posX = unitInfo.getInt("posX");
            Integer posY = unitInfo.getInt("posY");
            Log.d(TAG, "Cast all the values into Java types for unit " + i);

            /* TODO proper choice of player */
            if (playerOwner == 0) {
                unit.setOwner(new Player("Gaia", 0));
            } else {
                Log.d(TAG, "Getting player " + playerOwner);
                Player p = players.get(playerOwner - 1);
                Log.d(TAG, "That player has a name " + p);
                unit.setOwner(p);
                p.addUnit(unit);
            }

            Log.d(TAG, "Post setting owner.");
            Position pos = new Position(posX, posY);
            Log.d(TAG, "Grabbing GameField " + pos);
            GameField gf = grid.get(posY).get(posX);
            unit.setPosition(pos);
            gf.setUnit(unit);

        }

        Log.d(TAG, "Leaving spawnUnits");

    }

    private static class BuildingGetter
        implements FuncEx<Character, Building, JSONException> {

        private Map<Character, JSONObject> map;

        public BuildingGetter(Map<Character, JSONObject> m) {
            this.map = m;
        }

        public Building apply(final Character c) throws JSONException {
            if (!this.map.containsKey(c)) {
                return null;    /* TODO throw MapException */
            }

            JSONObject f = this.map.get(c);

            String name = f.getString("name");
            String file = f.getString("file");
            String pack = f.getString("package");
            String dir = f.getString("dir");
            Integer captureDifficulty = f.getInt("captureDifficulty");
            Double attackBonus = f.getDouble("attackBonus");
            Double defenseBonus = f.getDouble("defenseBonus");
            Boolean goalBuilding = f.getBoolean("goalBuilding");
            Integer captureWorth = f.getInt("captureWorth");

            return new Building(name, captureDifficulty, attackBonus,
                                defenseBonus, goalBuilding, captureWorth,
                                file, dir, pack);

        }
    }

    private static class UnitGetter
        implements FuncEx<JSONObject, Unit, JSONException> {

        public Unit apply(final JSONObject f) throws JSONException {

            String name = f.getString("name");
            String file = f.getString("file");
            String pack = f.getString("package");
            String dir = f.getString("dir");
            Boolean flying = f.getBoolean("flying");
            Double maxHealth = f.getDouble("maxHealth");
            Integer maxMovement = f.getInt("maxMovement");
            Double attack = f.getDouble("attack");
            Double meleeDefense = f.getDouble("meleeDefense");
            Double rangeDefense = f.getDouble("rangeDefense");
            Integer productionCost = f.getInt("productionCost");

            if (f.getBoolean("ranged")) {
                return new RangedUnit(name, maxHealth, maxMovement,
                                      attack, meleeDefense, rangeDefense,
                                      f.getDouble("minRange"),
                                      f.getDouble("maxRange"),
                                      flying, productionCost, file, dir, pack);
            }

            return new Unit(name, maxHealth, maxMovement,
                            attack, meleeDefense, rangeDefense, flying,
                            productionCost, file, dir, pack);

        }
    }


    private static <I, O, E extends Exception> List<O>
                                    map(final FuncEx<I, O, E> f,
                                        final List<I> ls) throws E {
        List<O> os = new ArrayList<O>();

        for (I l : ls) {
            os.add(f.apply(l));
        }

        return os;
    }

    private static class TerrainGetter
        implements FuncEx<Character, GameField, JSONException> {

        private Map<Character, JSONObject> map;

        public TerrainGetter(Map<Character, JSONObject> m) {
            this.map = m;
        }

        public GameField apply(final Character c) throws JSONException {
            if (!this.map.containsKey(c)) {
                return null;
            }

            JSONObject f = this.map.get(c);

            String name = f.getString("name");
            String file = f.getString("file");
            String pack = f.getString("package");
            String dir = f.getString("dir");
            Boolean accessible = f.getBoolean("accessible");
            Boolean flightOnly = f.getBoolean("flightOnly");
            Double movementModifier = f.getDouble("movementModifier");
            Double attackModifier = f.getDouble("attackModifier");
            Double defenseModifier = f.getDouble("defenseModifier");

            return new GameField(name, movementModifier,
                                 attackModifier, defenseModifier,
                                 accessible, flightOnly, file, dir, pack);
        }
    }
}
