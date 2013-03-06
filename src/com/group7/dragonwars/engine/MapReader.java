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
		Iterator<?> iter = fs.keys();
		while (iter.hasNext()) {
			String key = (String) iter.next(); /* We have to cast ;_; */
			fields.put(key.charAt(0), MapReader.getGameField(key.charAt(0))); /* TODO GF handling */
		}




        List<List<GameField>> grid = new ArrayList<List<GameField>>(longestLine);

        /* Initialize (height x width) grid */
        for (Integer y = 0; y < mapLines.size(); y++) {
            List<GameField> inner = new ArrayList<GameField>(mapLines.size());
            grid.add(inner);
        }

        /* Fill out the grid with actual tiles */
        for (Integer height = 0; height < mapLines.size(); height++) {
            for (Integer width = 0; width < longestLine; width++) {
                if (mapLines.get(height).length() > width) {
                    Character symbol = mapLines.get(height).charAt(width);
                    grid.get(height).add(fields.get(symbol));
                } else {
                    grid.get(height).add(getGameField(null));
                }
            }
        }

        return new Map(grid);

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
