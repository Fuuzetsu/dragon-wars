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

        HashMap<Character, GameField> fields = new HashMap<Character, GameField>();

        JSONObject fs = m.getJSONObject("fields");
        JSONArray terrain = m.getJSONArray("terrain");
        JSONArray buildingPos = m.getJSONArray("buildingPos");

        // Iterator<?> iter = fs.keys();
        // while (iter.hasNext()) {
        // 	String key = (String) iter.next(); /* We have to cast ;_; */
        // 	fields.put(key.charAt(0), MapReader.getGameField(key.charAt(0))); /* TODO GF handling */
        // }
        // System.out.println("after gameFields");


        List<List<GameField>> grid = MapReader.listifyJSONArray(new MapReader.TerrainGetter(), terrain);
        List<List<Building>> buildingGrid = MapReader.listifyJSONArray(new MapReader.BuildingGetter(), buildingPos);


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

    private static class TerrainGetter implements Func<Character, GameField> {
        public GameField apply(Character c) {
            if (c == null || c == ' ')
                return new Pit();

            switch (c) {
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
                System.err.println("MapReader doesn't know the symbol " + c);
                System.exit(1);
                return null; /* Java, please. */
            }
        }
    }
}
