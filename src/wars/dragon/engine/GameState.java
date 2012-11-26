package wars.dragon.engine;

import java.util.*;

public class GameState {
    
    Map map;
    List<Player> players = new ArrayList<Player>();
    Integer turns = 0;


    public static void main(String[] argv) {
	if (argv.length == 1) 
	    System.out.println(MapReader.readMap(GameState.readFile(argv[0])));
	else {
	    System.err.println("USAGE: java Game <mapName>");
	    System.exit(1);
	}	    
    }
    
    private static List<String> readFile(String filename) {
	List<String> text = new ArrayList<String>();
	
	try {
	    BufferedReader in = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = in.readLine()) != null) 
		text.add(line);
	    in.close();
	}
	catch (FileNotFoundException fnf) {
	    System.err.println("Couldn't find " + fnf.getMessage());
	    System.exit(1);
	}
	catch (IOException ioe) {
	    System.err.println("Couldn't read " + ioe.getMessage());
	    System.exit(1);
	}
	return text;
    }    


    public Integer getTurns() {
	return this.turns;
    }

    public Map getMap() {
	return this.map;
    }
    
    
}