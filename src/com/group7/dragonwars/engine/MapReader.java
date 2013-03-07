package com.group7.dragonwars.engine;

/* Generates a GameField based on a flat text file. Test solution. */

import java.util.*;
import org.json.*;

public class MapReader {

    public static Map readMap(List<String> mapLines) throws JSONException {
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
        JSONArray terrain = m.getJSONArray("terrain");
        JSONArray buildingPos = m.getJSONArray("buildingPos");


		/* Fill in a HashMap for look-up */
        HashMap<Character, JSONObject> fields = new HashMap<Character, JSONObject>();
        Iterator<?> iter = fs.keys();
        while (iter.hasNext()) {
        	String key = (String) iter.next(); /* We have to cast ;_; */
        	fields.put(key.charAt(0), fs.getJSONObject(key));
        }

		/* HashMap for buildings */
        HashMap<Character, JSONObject> buildings = new HashMap<Character, JSONObject>();
        Iterator<?> bIter = bs.keys();
        while (bIter.hasNext()) {
        	String key = (String) bIter.next(); /* We have to cast ;_; */
        	fields.put(key.charAt(0), bs.getJSONObject(key));
        }

        List<List<GameField>> grid = MapReader.listifyJSONArray(new MapReader.TerrainGetter(fields), terrain);
        List<List<Building>> buildingGrid = MapReader.listifyJSONArray(new MapReader.BuildingGetter(buildings), buildingPos);


        Integer gSize = -1;

        for (List<GameField> x : grid)
            if (x.size() > gSize)
                gSize = x.size();

        JSONArray starting = m.getJSONArray("starting");

        /* Fill out the grid with buildings */
        for (Integer height = 0; height < grid.size(); height++) {
            for (Integer width = 0; width < gSize; width++) {
                GameField gf = grid.get(height).get(width);

                if (buildingGrid.get(height).get(width) != null)
                    gf.setBuilding(buildingGrid.get(height).get(width));
            }
        }

        return new Map(grid);

    }

    private static <O> List<List<O>> listifyJSONArray
    (Func<Character, O> f, JSONArray xs) throws JSONException {
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

    private static class BuildingGetter implements Func<Character, Building> {

		private HashMap<Character, JSONObject> map;

		public BuildingGetter(HashMap<Character, JSONObject> m) {
			this.map = m;
		}

        public Building apply(Character c) {
			if (!this.map.containsKey(c))
				return null; /* TODO throw MapException */

			JSONObject f = this.map.get(c);

			String name = f.getString("name");
			String file = f.getString("file");
			String pack = f.getString("package");
			String path = f.getString("path");
			Integer captureDifficulty = f.getInt("captureDifficulty");
			Double attackBonus = f.getInt("attackBonus");
			Double defenseBonus = f.getInt("defenseBonus");
			Boolean goalBuilding = f.getBoolean("goalBuilding");

			return new Building(name, captureDifficulty, attackBonus,
								defenseBonus, goalBuilding, file);
        }
    }

    private static <I, O> List<O> map(Func<I, O> f, List<I> ls) {
        List<O> os = new ArrayList<O>();

        for (I l : ls)
            os.add(f.apply(l));

        return os;
    }

    private static class TerrainGetter implements Func<Character, GameField> {

		private HashMap<Character, JSONObject> map;

		public TerrainGetter(HashMap<Character, JSONObject> m) {
			this.map = m;
		}

        public GameField apply(Character c) {
			if (!this.map.containsKey(c))
				return null; /* TODO throw MapException */

			JSONObject f = this.map.get(c);

			String name = f.getString("name");
			String file = f.getString("file");
			String pack = f.getString("package");
			String path = f.getString("path");
			Boolean accessible = f.getBoolean("accessible");
			Boolean flightOnly = accessible ? f.getBoolean("flightOnly") : false;
			Double movementModifier = f.getDouble("movementModifier");
			Double attackModifier = f.getDouble("attackModifier");
			Double defenseModifier = f.getDouble("defenseModifier");

			return new GameField(name, movementModifier, attackModifier, defenseModifier,
								 accessible, flightOnly, file);

        }
    }
}
