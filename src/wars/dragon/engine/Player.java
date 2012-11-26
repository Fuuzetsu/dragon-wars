package wars.dragon.engine;

import java.util.*;

public class Player {
    private String name;
    private Boolean lost;
    List<Unit> ownedUnits = new ArrayList<Unit>();

    public Player(String name) {
	this.name = name;
    }

    public String getName() {
	return this.name;
    }

    public Boolean hasLost() { 
	return this.lost || ownedUnits.isEmpty();
    }

    public Boolean hasMoveableUnits() {
	for (Unit u : ownedUnits)
	    if (!u.hasFinishedTurn())
		return true;
	return false;
    }
}
