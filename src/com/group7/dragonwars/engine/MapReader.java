package com.group7.dragonwars.engine;

/* Generates a GameField based on a flat text file. Test solution. */

import android.util.Log;
import java.util.*;
import org.json.*;


public class MapReader {
    final private static String TAG = "MapReader";

    public static GameMap readMap(List<String> mapLines) throws JSONException {
        String jsonSource = "";

        for (String s : mapLines)
            jsonSource += s + "\n";

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


        /* Make a fake player list for now */
        List<Player> playerList = new ArrayList<Player>();
        for (Integer i = 0; i < players; ++i)
            playerList.add(new Player("Player " + (i + 1)));

        /* Fill in a HashMap for look-up */
        HashMap<Character, JSONObject> fields = new HashMap<Character, JSONObject>();
        Iterator<?> iter = fs.keys();

        while (iter.hasNext()) {
            String key = (String) iter.next(); /* We have to cast ;_; */
            fields.put(key.charAt(0), fs.getJSONObject(key));
        }

        HashMap<Character, GameField> fieldsInfo = new HashMap<Character, GameField>();
        iter = fs.keys();

        while (iter.hasNext()) {
            String key = (String) iter.next();
            fieldsInfo.put(key.charAt(0), new MapReader.TerrainGetter(fields).apply(key.charAt(0)));
        }

        /* HashMap for buildings */
        HashMap<Character, JSONObject> buildings = new HashMap<Character, JSONObject>();
        Iterator<?> bIter = bs.keys();

        while (bIter.hasNext()) {
            String key = (String) bIter.next();
            buildings.put(key.charAt(0), bs.getJSONObject(key));
        }

        HashMap<Character, Building> buildingsInfo = new HashMap<Character, Building>();
        bIter = bs.keys();

        while (bIter.hasNext()) {
            String key = (String) bIter.next();
            buildingsInfo.put(key.charAt(0), new MapReader.BuildingGetter(buildings).apply(key.charAt(0)));
        }

        /* HashMap for units to be used for the current map throughout the game */
        HashMap<Character, Unit> units = new HashMap<Character, Unit>();
        Iterator<?> uIter = us.keys();

        while (uIter.hasNext()) {
            String key = (String) uIter.next();
            units.put(key.charAt(0), new MapReader.UnitGetter().apply(us.getJSONObject(key)));
        }


        List<List<GameField>> grid = MapReader.listifyJSONArray(new MapReader.TerrainGetter(fields), terrain);
        //List<List<Building>> buildingGrid = MapReader.listifyJSONArray(new MapReader.BuildingGetter(buildings), buildingPos);

        MapReader.setBuildings(grid, playerList, units, buildingsInfo, startingBuildingPos);
        MapReader.spawnUnits(grid, playerList, units, startingUnitPos);

        return new GameMap(grid, units, buildingsInfo, fieldsInfo, playerList);

    }

    private static <O> List<List<O>> listifyJSONArray
    (FuncEx<Character, O, JSONException> f, JSONArray xs) throws JSONException {
        List<List<O>> v = new ArrayList<List<O>>();
        List<List<Character>> cs = new ArrayList<List<Character>>();

        for (Integer i = 0; i < xs.length(); i++) {
            String s = xs.getString(i);
            List<Character> t = new ArrayList<Character>();

            for (Integer j = 0; j < s.length(); j++)
                t.add(s.charAt(j));

            cs.add(t);
        }

        for (List<Character> ys : cs)
            v.add(map(f, ys));

        return v;
    }

    private static void setBuildings(List<List<GameField>> grid, List<Player> players, HashMap<Character, Unit> units,
                                     HashMap<Character, Building> buildings, JSONArray posInfo) throws JSONException {
        Log.d(TAG, "Running setBuildings");
        Log.d(TAG, "The list of players contains " + players);
        for (Integer i = 0; i < posInfo.length(); ++i) {
            Log.d(TAG, "Grabbing info for building number " + i);
            JSONObject buildingInfo = posInfo.getJSONObject(i);
            Building buildingT = buildings.get(buildingInfo.getString("building").charAt(0));
            Building building = new Building(buildingT);
            Integer playerOwner = buildingInfo.getInt("owner");
            Integer posX = buildingInfo.getInt("posX");
            Integer posY = buildingInfo.getInt("posY");
            JSONArray prod = buildingInfo.getJSONArray("produces");

            for (Integer j = 0; j < prod.length(); ++j) {
                Unit unit = units.get(prod.getString(j).charAt(0));
                building.addProducableUnit(unit);
            }

            Log.d(TAG, "Cast all the values into Java types for building " + i);

            /* TODO proper choice of player */
            if (playerOwner == 0)
                building.setOwner(new Player("Gaia"));
            else {
                Player p = players.get(playerOwner - 1);
                building.setOwner(p);
                p.addBuilding(building);
            }

            GameField gf = grid.get(posY).get(posX);
            building.setPosition(new Position(posX, posY));
            gf.setBuilding(building);


        }
        Log.d(TAG, "Leaving setBuildings");

    }

    private static void spawnUnits(List<List<GameField>> grid, List<Player> players,
                                   HashMap<Character, Unit> units, JSONArray posInfo) throws JSONException {
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
            if (playerOwner == 0)
                unit.setOwner(new Player("Gaia"));
            else {
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

    private static class BuildingGetter implements FuncEx<Character, Building, JSONException> {

        private HashMap<Character, JSONObject> map;

        public BuildingGetter(HashMap<Character, JSONObject> m) {
            this.map = m;
        }

        public Building apply(Character c) throws JSONException {
            if (!this.map.containsKey(c))
                return null; /* TODO throw MapException */

            JSONObject f = this.map.get(c);

            String name = f.getString("name");
            String file = f.getString("file");
            String pack = f.getString("package");
            String dir = f.getString("dir");
            Integer captureDifficulty = f.getInt("captureDifficulty");
            Double attackBonus = f.getDouble("attackBonus");
            Double defenseBonus = f.getDouble("defenseBonus");
            Boolean goalBuilding = f.getBoolean("goalBuilding");

            return new Building(name, captureDifficulty, attackBonus,
                                defenseBonus, goalBuilding, file, dir, pack);

        }
    }

    private static class UnitGetter implements FuncEx<JSONObject, Unit, JSONException> {
        public Unit apply(JSONObject f) throws JSONException {

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

            if (f.getBoolean("ranged"))
                return new RangedUnit(name, maxHealth, maxMovement, attack, meleeDefense,
                                      rangeDefense, f.getDouble("minRange"), f.getDouble("maxRange"),
                                      flying, file, dir, pack);

            return new Unit(name, maxHealth, maxMovement, attack, meleeDefense,
                            rangeDefense, flying, file, dir, pack);

        }
    }


    private static <I, O, E extends Exception> List<O> map(FuncEx<I, O, E> f, List<I> ls) throws E {
        List<O> os = new ArrayList<O>();

        for (I l : ls)
            os.add(f.apply(l));

        return os;
    }

    private static class TerrainGetter implements FuncEx<Character, GameField, JSONException> {

        private HashMap<Character, JSONObject> map;

        public TerrainGetter(HashMap<Character, JSONObject> m) {
            this.map = m;
        }

        public GameField apply(Character c) throws JSONException {
            if (!this.map.containsKey(c))
                return null; /* TODO throw MapException */

            JSONObject f = this.map.get(c);

            String name = f.getString("name");
            String file = f.getString("file");
            String pack = f.getString("package");
            String dir = f.getString("dir");
            Boolean accessible = f.getBoolean("accessible");
            Boolean flightOnly = accessible ? f.getBoolean("flightOnly") : false;
            Double movementModifier = f.getDouble("movementModifier");
            Double attackModifier = f.getDouble("attackModifier");
            Double defenseModifier = f.getDouble("defenseModifier");

            return new GameField(name, movementModifier, attackModifier, defenseModifier,
                                 accessible, flightOnly, file, dir, pack);
        }
    }
}
