//package wars.dragon.engine;
/* Generates a GameField based on a flat text file. Test solution. */

import java.util.*;

public class MapReader {
    
    public static Map readMap(List<String> mapLines) {
	int longestLine = -1;
	for (String s : mapLines) {
	    if (s.length() > longestLine)
		longestLine = s.length();
	}

	List< List<GameField> > grid = 
	    new ArrayList< List<GameField> >(longestLine);
	
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
		    grid.get(height).add(getGameField(symbol));
		}
		else {
		    grid.get(height).add(getGameField(null));
		}
	    }
	}
	
	return new Map(grid);

    }

    private static GameField getGameField(Character symbol) {
	if (symbol == null || symbol == ' ')
	    return new Pit();
	switch(symbol) {
	case 'G': return new Grass();
	case 'W': return new Water();
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
