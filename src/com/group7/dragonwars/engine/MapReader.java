package com.group7.dragonwars.engine;

/* Generates a GameField based on a flat text file. Test solution. */

import java.util.*;
import org.json.*;

public class MapReader {

    public static Map readMap(List<String> mapLines) throws JSONException {
        int longestLine = -1;
		String jsonSource = "";

        for (String s : mapLines) {
            if (s.length() > longestLine)
                longestLine = s.length();
			jsonSource += s + "\n";
        }

		System.out.println("Printing raw JSON map.");
		System.out.println(jsonSource);


		JSONObject m = new JSONObject(jsonSource);
		String mapName = m.getString("mapName");
		Integer sizeX = m.getInt("sizeX");
		Integer sizeY = m.getInt("sizeY");
		Integer players = m.getInt("players");

		HashMap<Character, GameField> fields = new HashMap<Character, GameField>();

		JSONObject fs = m.getJSONObject("fields");
		JSONArray terrain = m.getJSONArray("terrain");
		JSONArray buildingPos = m.getJSONArray("buildings");

		Iterator<?> iter = fs.keys();
		while (iter.hasNext()) {
			String key = (String) iter.next(); /* We have to cast ;_; */
			fields.put(key.charAt(0), MapReader.getGameField(key.charAt(0))); /* TODO GF handling */
		}

        List<List<GameField>> grid = new ArrayList<List<GameField>>(longestLine);
		List<List<Building>> buildingGrid = MapReader.listifyJSONArray(new MapReader.BuildingGetter(), buildingPos);

        /* Initialize (height x width) grid */
        for (Integer y = 0; y < mapLines.size(); y++) {
            List<GameField> inner = new ArrayList<GameField>(mapLines.size());
            grid.add(inner);
        }


		JSONArray starting = m.getJSONArray("starting");

        /* Fill out the grid with actual tiles */
        for (Integer height = 0; height < mapLines.size(); height++) {
            for (Integer width = 0; width < longestLine; width++) {
                if (mapLines.get(height).length() > width) {
                    Character symbol = mapLines.get(height).charAt(width);
					GameField gf = fields.get(symbol);
					if (buildingGrid.get(height).get(width) != null)
						gf.setBuilding(buildingGrid.get(height).get(width));
                    grid.get(height).add(gf);
                } else {
                    grid.get(height).add(getGameField(null));
                }
            }
        }

        return new Map(grid);

    }

	private static <O> List<List<O>> listifyJSONArray(Func<Character, O> f, JSONArray xs) throws JSONException {
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
		public Building apply(Character c) {
			switch (c) {
			case 'C':
				return new Building("Castle", 2, 3.0, 4.0, true);
			case 'V':
				return new Building("Village", 2, 3.0, 4.0, false);
			default:
				return null; /* Temporary for testing */
			}
		}
	}

	private static <I, O> List<O> map(Func<I, O> f, List<I> ls) {
		List<O> os = new ArrayList<O>();
		for (I l : ls)
			os.add(f.apply(l));
		return os;
	}

    private static GameField getGameField(Character symbol) {
        if (symbol == null || symbol == ' ')
            return new Pit();

        switch (symbol) {
        case 'G':
            return new Grass();

        case 'W':
            return new Water();

        case 'D':
            GameField fa = new Grass();
            fa.setUnit(new Dragon());
            return fa;

        case 'S':
            GameField fu = new Grass();
            fu.setUnit(new Soldier());
            return fu;

        default:
            System.err.println("MapReader doesn't know the symbol " + symbol);
            System.exit(1);
            return null; /* Java, please. */
        }
    }

}
