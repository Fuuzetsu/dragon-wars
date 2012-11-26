package wars.dragon.engine;
/* Generates a GameField based on a flat text file. Test solution. */

import java.util.*;

public class MapReader {
    
    public static GameField readMap(List<String> mapLines) {
	int longestLine = -1;
	for (String s : mapLines) {
	    if (s.length() > longestLine)
		longestLine = s.length();
	}

	List< List<Tile> > grid = 
	    new ArrayList< List<Tile> >(longestLine);
	
	/* Initialize (height x width) grid */
	for (Integer y = 0; y < mapLines.size(); y++) {
	    List<Tile> inner = new ArrayList<Tile>(mapLines.size());
	    grid.add(inner);
	}

	/* Fill out the grid with actual tiles */
	for (Integer height = 0; height < mapLines.size(); height++) {
	    for (Integer width = 0; width < longestLine; width++) {
		if (mapLines.get(height).length() > width) {
		    Character symbol = mapLines.get(height).charAt(width);
		    grid.get(height).add(getTile(symbol));
		}
		else {
		    grid.get(height).add(getTile(null));
		}
	    }
	}
	
	return new GameField(grid);

    }

    private static Tile getTile(Character symbol) {
	if (symbol == null || symbol == ' ')
	    return new Pit();
	switch(symbol) {
	case 'G': return new Grass();
	case 'W': return new Water();
	default: 
	    System.err.println("MapReader doesn't know the symbol " + symbol);
	    System.exit(1);
	    return null; /* Java, please. */
	}
    }	
}
