package com.group7.dragonwars.engine;

import java.util.*;
import java.lang.Math;
import android.util.Log;

/* Class containing things like damage calculation and path finding. */
public class Logic {

    public final static String TAG = "Logic";

    public List<Position> findPath(GameMap map, Unit unit, Position destination) {
        return AStar(map, unit, destination);
    }

    public Integer calculateMovementCost(GameMap map, Unit unit, List<Position> path) {
        Double totalCost = 0.0;
        for (Position pos : path) {
            totalCost += getMovementCost(map, unit, pos);
        }

        totalCost = Math.ceil(totalCost);


        return totalCost.intValue();
    }

    public List<Position> destinations(GameMap map, Unit unit) {
        List<Position> checked = new ArrayList<Position>();

        List<Position> mapPositions = new ArrayList<Position>();
        for (int x = 0; x < map.getWidth(); ++x)
            for (int y = 0; y < map.getHeight(); y++)
                mapPositions.add(new Position(x, y));

        for (Position p : mapPositions) {
            Boolean c = false;

            for (Position x : checked)
                if (p.equals(x)) {
                    c = true;
                    break;
                }

            if (!map.isValidField(p) || c)
                continue;

            List<Position> path = AStar(map, unit, p);

            for (Position y : path){
                Boolean b = false;
                for (Position x : checked)
                    if (y.equals(x)) {
                        b = true;
                        break;
                    }

                if (!b)
                    checked.add(y);

            }
        }
        return checked;
    }

    public Pair<Double, Double> calculateDamage(GameMap map, Unit attacker,
            Unit defender) {
        return new Pair<Double, Double>
               (calculateRawDamage(map, attacker, defender),
                calculateCounterDamage(map, attacker, defender));
    }

    public Double calculateRawDamage(GameMap map, Unit attacker, Unit defender) {
        final Double DEFENDER_DISADVANTAGE = 0.75;
        GameField attackerField = map.getField(attacker.getPosition());
        GameField defenderField = map.getField(defender.getPosition());

        Double attackerMod = attackerField.getAttackModifier();
        Double defenderMod = defenderField.getDefenseModifier();

        Double defense = defender.getHealth() * (defenderMod / 100);
        defense *= DEFENDER_DISADVANTAGE;

        Double rawDamage = attacker.getHealth() * (attackerMod / 100);

        Double damage = rawDamage - defense;

        return (defense < 0) ? 0 : damage;
    }

    public Double calculateCounterDamage(GameMap map, Unit attacker, Unit defender) {
        Double initialDamage = calculateRawDamage(map, attacker, defender);
        Double defenderHealth = defender.getHealth() - initialDamage;
        defenderHealth = (defenderHealth < 0) ? 0 : defenderHealth;

        return calculateTheoreticalCounterDamage(map, defender, attacker,
                defenderHealth);
    }

    private Double calculateTheoreticalCounterDamage(GameMap map, Unit attacker,
            Unit defender, Double atkHealth) {
        /* No defense disadvantage on a counter. */
        GameField attackerField = map.getField(attacker.getPosition());
        GameField defenderField = map.getField(defender.getPosition());

        Double attackerMod = attackerField.getAttackModifier();
        Double defenderMod = defenderField.getDefenseModifier();

        Double defense = defender.getHealth() * (defenderMod / 100);

        Double rawDamage = atkHealth * (attackerMod / 100);

        Double damage = rawDamage - defense;

        return (defense < 0) ? 0 : damage;
    }

    private List<Position> AStar(GameMap map, Unit unit, Position destination) {
        if (!map.isValidField(destination))
            return new ArrayList<Position>(0);

        List<Position> expanded = new ArrayList<Position>();
        Comparator<Pair<List<Position>, Double>> comp = new AStarComparator();
        PriorityQueue<Pair<List<Position>, Double>> queue = new PriorityQueue<Pair<List<Position>, Double>>(
            10, comp);
        List<Position> root = new ArrayList<Position>();
        root.add(unit.getPosition());

        queue.add(new Pair<List<Position>, Double>(root, 0.0));

        while (queue.size() != 0) {
            Pair<List<Position>, Double> posP = queue.poll();
            List<Position> poss = posP.getLeft();

            Position lastPos = poss.get(poss.size() - 1);

            if (lastPos.equals(destination))
                return poss;

            Boolean c = false;
            for (Position x : expanded)
                if (lastPos.equals(x)) {
                    c = true;
                    break;
                }

            if (c)
                continue;

            expanded.add(lastPos);

            /* Get heuristic */
            Integer h = getManhattanDistance(lastPos, destination);
            /* Get cost */
            Double g = getMovementCost(map, unit, lastPos);
            Double pathCost = posP.getRight() + h * g;

            if (pathCost > unit.getRemainingMovement())
            	continue;

            for (Position p : getAdjacentPositions(lastPos)) {
                if (map.isValidField(p) && map.getField(p).doesAcceptUnit(unit)) {
                    if (map.getField(p).hostsUnit()) {
                        Player op = map.getField(p).getUnit().getOwner();

                        if (!op.equals(unit.getOwner()))
                            continue;
                    }

                    List<Position> plan = new ArrayList<Position>(poss);
                    plan.add(p);
                    queue.add(new Pair<List<Position>, Double>(plan, pathCost));
                }
            }
        }

        return new ArrayList<Position>(); /* Search failed */
    }

    private List<Position> getAdjacentPositions(Position pos) {
        List<Position> positions = new ArrayList<Position>();
        positions.add(new Position(pos.getX(), pos.getY() + 1));
        positions.add(new Position(pos.getX(), pos.getY() - 1));
        positions.add(new Position(pos.getX() + 1, pos.getY()));
        positions.add(new Position(pos.getX() - 1, pos.getY()));
        return positions;
    }

    private class AStarComparator implements
        Comparator<Pair<List<Position>, Double>> {
        public int compare(Pair<List<Position>, Double> p1,
                           Pair<List<Position>, Double> p2) {
            if (p1.getRight() < p2.getRight())
                return -1;

            if (p1.getRight() > p2.getRight())
                return 1;

            return 0;
        }
    }

    private Double getMovementCost(GameMap map, Unit unit, Position origin) {
        /* g(x) for search */
        // flying units ignore this; always 1
        if (unit.isFlying())
            return 1.0;

        return map.getField(origin).getMovementModifier();
    }

    public Set<Position> getAttackableUnitPositions(GameMap map, Unit unit) {
        Set<Position> atkFields = getAttackableFields(map, unit);
        Set<Position> atkUnits = new HashSet<Position>();

        for (Position p : atkFields) {
            if (map.getField(p).hostsUnit()) {
                Player uOwner = map.getField(p).getUnit().getOwner();

                if (!uOwner.equals(unit.getOwner()))
                    atkUnits.add(p);
            }
        }

        return atkUnits;
    }

    private Set<Position> getAttackableFields(GameMap map, Unit unit) {
        if (!unit.isRanged())
            return getPositionsInRange(map, unit.getPosition(), 1.0);

        RangedUnit ru = (RangedUnit) unit;
        return getPositionsInRange(map, ru.getPosition(), ru.getMinRange(),
                                   ru.getMaxRange());
    }

    private Set<Position> getPositionsInRange(GameMap map, Position origin,
            Double range) {
        Set<Position> positions = new HashSet<Position>();
        Double maxr = Math.ceil(range);

        for (Integer x = 0; x < maxr * 2; x++) {
            for (Integer y = 0; y < maxr * 2; y++) {
                Position newP = new Position(x, y);

                // Pair<Integer, Integer> dist = getManhattanDistance(origin,
                // newP);
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

    private Set<Position> getPositionsInRange(GameMap map, Position origin,
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
    private Integer getManhattanDistance(Position origin, Position destination) {
        /* h(x) */
        Pair<Integer, Integer> distance = getDistanceAway(origin, destination);

        return distance.getLeft() + distance.getRight();
    }

    private Pair<Integer, Integer> getDistanceAway(Position origin,
            Position destination) {
        Integer x = Math.abs(origin.getX() - destination.getX());
        Integer y = Math.abs(origin.getY() - destination.getY());
        return new Pair<Integer, Integer>(x, y);
    }

}
