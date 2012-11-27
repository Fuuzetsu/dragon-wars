//package wars.dragon.engine;

import java.util.*;
import java.lang.*;

/* Class containing things like damage calculation and path finding. */
public class Logic {
    
    public List<Position> findPath(Map map, Unit unit, Position destination) {
	return AStar(map, unit, destination);
    }

    private List<Position> AStar(Map map, Unit unit, Position destination) {
	Set<Position> expanded = new HashSet<Position>();
	Comparator< Pair<List<Position>, Double> > comp = new AStarComparator();
	PriorityQueue< Pair<List<Position>, Double> > queue = 
	    new PriorityQueue< Pair<List<Position>, Double> >(10, comp);
	List<Position> root = new ArrayList<Position>();
	root.add(unit.getPosition());
	queue.add(new Pair<List<Position>, Double>(root, 0.0));
	while (queue.size() != 0) {
	    Pair<List<Position>, Double> posP = queue.poll();
	    List<Position> poss = posP.getLeft();
	    Position lastPos = poss.get(poss.size() - 1);
	    if (lastPos.equals(destination))
		return poss;

	    if (expanded.contains(lastPos))
		continue;

	    expanded.add(lastPos);

	    /* Get heuristic */
	    Integer h = getManhattanDistance(lastPos, destination);
	    /* Get cost */
	    Double g = getMovementCost(map, unit, lastPos);
	    Double pathCost = h + g + posP.getRight();
	    for (Position p : getAdjacentPositions(lastPos)) {
		if (map.isValidField(p)) {
		    List<Position> plan = new ArrayList<Position>(poss);
		    plan.add(p);
		    queue.add(new Pair<List<Position>, Double>(plan, pathCost));
		}
	    }		
	}
	List<Position> dummy = new ArrayList<Position>();
	return dummy; /* Search failed */
    }

    private List<Position> getAdjacentPositions(Position pos) {
	List<Position> positions = new ArrayList<Position>();
	positions.add(new Position(pos.getX(), pos.getY() + 1));
	positions.add(new Position(pos.getX(), pos.getY() - 1));
	positions.add(new Position(pos.getX() + 1, pos.getY()));
	positions.add(new Position(pos.getX() - 1, pos.getY()));
	return positions;
    }

    private class AStarComparator implements Comparator< Pair<List<Position>, Double> > {
	public int compare(Pair<List<Position>, Double> p1, Pair<List<Position>, Double> p2) {
	    if (p1.getRight() < p2.getRight())
		return -1;
	    if (p1.getRight() > p2.getRight())
		return 1;

	    return 0;
	}
    }

    private Double getMovementCost(Map map, Unit unit, Position origin) {
	/* g(x) for search */
	// flying units ignore this; always 1
	return (100 / map.getField(origin).getMovementModifier());
    }
    
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
	    return getPositionsInRange(map, unit.getPosition(), 1.0);

	RangedUnit ru = (RangedUnit) unit;
	return getPositionsInRange(map, ru.getPosition(), 
				   ru.getMinRange(), ru.getMaxRange());
    }

    private Set<Position> getPositionsInRange(Map map, Position origin, Double range) {
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

		if (newP.equals(origin) || !map.isValidField(newP))
		    continue;
		
		positions.add(newP);
		
	    }
	}	
	return positions;		
    }

    private Set<Position> getPositionsInRange(Map map, Position origin, 
					      Double minRange, Double maxRange) {
	Set<Position> positions = getPositionsInRange(map, origin, maxRange);
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
	/* h(x) */
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
