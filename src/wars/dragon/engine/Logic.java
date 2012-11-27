//package wars.dragon.engine;

import java.util.*;
import java.lang.*;

/* Class containing things like damage calculation and path finding. */
public class Logic {
    
    public Set<Position> getAttackableUnits(Map map, Unit unit) {
	Set<Position> atkFields = getAttackableFields(map, unit);
	Set<Position> atkUnits = new HashSet<Position>();
	for (Position p : atkFields)
	    if (map.getField(p).hostsUnit())
		atkUnits.add(p);

	return atkUnits;
    }

    private Set<Position> getAttackableFields(Map map, Unit unit) {
	if (!unit.isRanged())
	    return getPositionsInRange(unit.getPosition(), 1.0);

	RangedUnit ru = (RangedUnit) unit;
	return getPositionsInRange(ru.getPosition(), 
				   ru.getMinRange(), ru.getMaxRange());
    }

    private Set<Position> getPositionsInRange(Position origin, Double range) {
	Set<Position> positions = new HashSet<Position>();
	Double maxr = Math.ceil(range);
	
	for (Integer x = 0; x < maxr * 2; x++) {
	    for (Integer y = 0; y < maxr * 2; y++) {
		Position newP = new Position(x, y);
		// Pair<Integer, Integer> dist = getManhattanDistance(origin, 
		//						   newP);
		if (x < maxr)
		    newP = new Position(newP.getX() - x, newP.getY());
		else if (x > maxr)
		    newP = new Position(newP.getX() + x, newP.getY());

		if (y < maxr)
		    newP = new Position(newP.getX(), newP.getY() - x);
		else if (y > maxr)
		    newP = new Position(newP.getX(), newP.getY() + y);

		if (newP.equals(origin))
		    continue;

		positions.add(newP);
		
	    }
	}	
	return positions;		
    }

    private Set<Position> getPositionsInRange(Position origin, 
					      Double minRange, Double maxRange) {
	Set<Position> positions = getPositionsInRange(origin, maxRange);
	Set<Position> filtered = new HashSet<Position>();
	for (Position p : positions) {
	    Pair<Integer, Integer> dist;
	    dist = getDistanceAway(origin, p);
	    if (Math.hypot(dist.getLeft(), dist.getRight()) < minRange)
		continue;
	    filtered.add(p);
	}
	
	return filtered;
    }


    /* Used as a heuristic for A* */
    private Integer getManhattanDistance(Position origin, 
					 Position destination) {
	Pair<Integer, Integer> distance = getDistanceAway(origin, 
							  destination);

	return distance.getLeft() + distance.getRight();
    }

    private Pair<Integer, Integer> getDistanceAway(Position origin, 
						   Position destination) {
	Integer x = Math.abs(origin.getX() - destination.getX());
	Integer y = Math.abs(origin.getY() - destination.getY());
	return new Pair<Integer, Integer>(x, y);
    }
	
    

}
