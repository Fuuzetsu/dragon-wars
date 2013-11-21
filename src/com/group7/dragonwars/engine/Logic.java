/* This file is part of Dragon Wars.
 *
 * Dragon Wars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragon Wars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dragon Wars.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.group7.dragonwars.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/* Class containing things like damage calculation and path finding. */
public final class Logic {

    private static final String TAG = "Logic";

    public List<Position> findValidFieldsNextToUnit(final GameMap map,
                                                    final Unit attackerUnit,
                                                    final Unit targetUnit) {
        List<Position> potential = getValidNeighbours(map,
                                                      targetUnit.getPosition());
        List<Position> dests = destinations(map, attackerUnit);
        Iterator<Position> iter = potential.iterator();

        /* Remove positions we can't reach anyway */
        while (iter.hasNext()) {
            Position p = iter.next();
            GameField gf = map.getField(p);

            if ((gf.hostsUnit() && !gf.getUnit().equals(attackerUnit))
                    || !dests.contains(p)) {
                iter.remove();
            }
        }

        return potential;
    }

    public List<Position> findPath(final GameMap map, final Unit unit,
                                   final Position destination) {
        return AStar(map, unit, destination);
    }

    public Integer calculateMovementCost(final GameMap map, final Unit unit,
                                         final List<Position> path) {
        Double totalCost = 0.0;

        for (Position pos : path) {
            if (pos.equals(unit.getPosition())) {
                continue;
            }

            totalCost += getMovementCost(map, unit, pos);
        }

        totalCost = Math.ceil(totalCost);


        return totalCost.intValue();
    }

    public List<Position> destinations(final GameMap map, final Unit unit) {
        Set<Position> checked = new HashSet<Position>();
        Set<Position> reachable = new HashSet<Position>();

        Position unitPosition = unit.getPosition();
        List<Node> start
            = new ArrayList<Node>();
        start.add(new Node(unitPosition, 0.0, 0.0));

        checked.add(unitPosition);
        reachable.add(unitPosition);

        List<Node> next = nextPositions(map, start);

        while (next.size() != 0) {
            List<Node> newNext = new ArrayList<Node>();

            for (Node n : next) {
                checked.add(n.getPosition());

                if (unit.getRemainingMovement() < n.getG()) {
                    continue;
                }

                if (map.getField(n.getPosition()).doesAcceptUnit(unit)) {
                    if (map.getField(n.getPosition()).hostsUnit()) {
                        Player op = map.getField(n.getPosition())
                                    .getUnit().getOwner();

                        if (!op.equals(unit.getOwner())) {
                            continue;
                        }
                    }

                    reachable.add(n.getPosition());
                    List<Node> thisNext = new ArrayList<Node>(5);
                    thisNext.add(n);
                    thisNext = nextPositions(map, thisNext);

                    for (Node thisNode : thisNext) {
                        if (!checked.contains(thisNode.getPosition())) {
                            newNext.add(thisNode);
                        }
                    }
                }
            }

            next = newNext;
        }

        List<Position> shown = new ArrayList<Position>();

        for (Position p : reachable) {
            if (map.getField(p).canBeStoppedOn()) {
                shown.add(p);
            }
        }

        return shown;
    }

    public List<Node>
    nextPositions(final GameMap map, final List<Node> toCheck) {

        List<Node> result = new ArrayList<Node>();

        for (Node n : toCheck) {
            Double costSoFar = n.getG();
            Position currentPosition = n.getPosition();
            List<Position> adj = getValidNeighbours(map, currentPosition);

            for (Position pos : adj) {
                GameField cField = map.getField(pos);
                Double newCost = costSoFar + cField.getMovementModifier();
                result.add(new Node(pos, newCost, 0.0));
            }
        }

        return result;
    }


    /* Returns damage as if the attacker was standing on a different position */
    public Pair<Double, Double> calculateDamageFrom(final GameMap map,
                                                    final Unit attacker,
                                                    final Unit defender,
                                                    final Position position) {
        /* We can cheat and temporarily set a unit's position to the fake one */
        Position originalPosition = attacker.getPosition();
        attacker.setPosition(position);
        Pair<Double, Double> damage = calculateDamage(map, attacker, defender);
        attacker.setPosition(originalPosition);

        return damage;
    }


    public Pair<Double, Double> calculateDamage(final GameMap map,
                                                final Unit attacker,
                                                final Unit defender) {
        return new Pair<Double, Double>(
            calculateRawDamage(map, attacker, defender),
            calculateCounterDamage(map, attacker, defender));
    }


    public Double calculateRawDamage(final GameMap map, final Unit attacker,
                                     final Unit defender) {
        GameField defenderField = map.getField(defender.getPosition());

        Double fieldDefense = defenderField.getDefenseModifier() - 1;
        Double unitDefense = attacker.isRanged()
            ? defender.getRangeDefense() : defender.getMeleeDefense() - 1;

        Double damage = attacker.getAttack()
            + (2 * attacker.getAttack()
               * (attacker.getHealth() / attacker.getMaxHealth()));

        Double finalDamage = damage - (((fieldDefense * damage) / 2)
                                       + ((unitDefense * damage) / 2));

        return attacker.getHealth() > 0.0 ? finalDamage : 0.0;

    }

    public Double calculateCounterDamage(final GameMap map, final Unit attacker,
                                         final Unit defender) {
        Double initialDamage = calculateRawDamage(map, attacker, defender);
        Double defenderHealth = defender.getHealth() - initialDamage;
        defenderHealth = (defenderHealth < 0) ? 0 : defenderHealth;

        return calculateTheoreticalCounterDamage(map, defender, attacker,
                defenderHealth);
    }

    private Double calculateTheoreticalCounterDamage(
        final GameMap map, final Unit attacker,
        final Unit defender, final Double atkHealth) {
        GameField defenderField = map.getField(defender.getPosition());

        double fieldDefense = defenderField.getDefenseModifier() - 1;
        double unitDefense = attacker.isRanged()
            ? defender.getRangeDefense() : defender.getMeleeDefense() - 1;

        double damage = attacker.getAttack()
            + (2 * attacker.getAttack()
               * (atkHealth / attacker.getMaxHealth()));

        double finalDamage = damage - (((fieldDefense * damage) / 2)
                                       + ((unitDefense * damage) / 2));

        return attacker.getHealth() > 0 ? finalDamage : 0;
    }

    private List<Position> AStar(final GameMap map, final Unit unit,
                                 final Position destination) {
        if (!map.isValidField(destination)) {
            return new ArrayList<Position>(0);
        }

        PriorityQueue<Node> openSet
            = new PriorityQueue<Node>(10, new AStarComparator());
        Set<Node> closedSet = new HashSet<Node>();

        Node root = new Node(unit.getPosition(), 0.0,
                             1.0 * getManhattanDistance(
                                 unit.getPosition(), destination));
        openSet.add(root);

        while (openSet.size() != 0) {
            Node current = openSet.poll();

            if (current.getPosition().equals(destination)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Position n : getValidNeighbours(map, current.getPosition())) {
                GameField gf = map.getField(n);

                if (!gf.doesAcceptUnit(unit)) {
                    continue;
                }

                Node neigh = new Node(n, gf.getMovementModifier(),
                                      1.0 * getManhattanDistance(
                                          unit.getPosition(), destination));

                Double tentG = current.getG() + neigh.getG();

                if (closedSet.contains(neigh)) {
                    if (tentG >= neigh.getG()) {
                        continue;
                    }
                }

                if ((!openSet.contains(neigh)) || tentG < neigh.getG()) {
                    neigh.setParent(current);

                    if (!openSet.contains(neigh)) {
                        openSet.add(neigh);
                    }
                }
            }
        }

        return new ArrayList<Position>(0); /* Search failed */

    }

    private List<Position> getValidNeighbours(final GameMap map,
                                              final Position pos) {
        List<Position> positions = new ArrayList<Position>(4);
        positions.add(new Position(pos.getX(), pos.getY() + 1));
        positions.add(new Position(pos.getX(), pos.getY() - 1));
        positions.add(new Position(pos.getX() + 1, pos.getY()));
        positions.add(new Position(pos.getX() - 1, pos.getY()));
        List<Position> validPositions = new ArrayList<Position>(4);

        for (Position p : positions) {
            if (map.isValidField(p)) {
                validPositions.add(p);
            }
        }

        return validPositions;
    }

    private List<Position> getAdjacentPositions(final Position pos) {
        List<Position> positions = new ArrayList<Position>();
        positions.add(new Position(pos.getX(), pos.getY() + 1));
        positions.add(new Position(pos.getX(), pos.getY() - 1));
        positions.add(new Position(pos.getX() + 1, pos.getY()));
        positions.add(new Position(pos.getX() - 1, pos.getY()));
        return positions;
    }

    private List<Position> getValidSurroundingPositions(final GameMap map,
            final Position pos) {
        List<Position> positions = getValidNeighbours(map, pos);
        Position nep, nwp, sep, swp;
        Integer i = pos.getX(), j = pos.getY();
        nep = new Position(i - 1, j - 1);

        if (map.isValidField(nep)) {
            positions.add(nep);
        }

        nwp = new Position(i + 1, j - 1);

        if (map.isValidField(nwp)) {
            positions.add(nwp);
        }

        sep = new Position(i - 1, j + 1);

        if (map.isValidField(sep)) {
            positions.add(sep);
        }

        swp = new Position(i + 1, j + 1);

        if (map.isValidField(swp)) {
            positions.add(swp);
        }

        return positions;

    }

    private class AStarComparator implements
        Comparator<Node> {
        public int compare(final Node a, final Node b) {
            Double t = a.getF() - b.getF();

            if (t > 0) {
                return 1;
            }

            if (t < 0) {
                return -1;
            }

            return 0;
        }
    }

    private List<Position> reconstructPath(final Node node) {
        List<Position> path = new ArrayList<Position>();
        path.add(node.getPosition());
        Node parent = node.getParent();

        while (parent != null) {
            path.add(parent.getPosition());
            parent = parent.getParent();
        }

        return path;
    }

    private class Node {
        private Node parent;
        private Position p;
        private Double g, h;

        public Node(final Position p, final Double g, final Double h) {
            this.p = p;
            this.g = g;
            this.h = h;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(final Node parent) {
            this.parent = parent;
            this.g = this.g + parent.getG();
        }

        public Double getH() {
            return h;
        }

        public Double getG() {
            return g;
        }

        public Double getF() {
            return h + g;
        }

        public Position getPosition() {
            return p;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof Node)) {
                return false;
            }

            Node that = (Node) other;
            return p.equals(that.getPosition());
        }

        @Override
        public int hashCode() {
            return p.hashCode();
        }
    }

    private Double getMovementCost(final GameMap map, final Unit unit,
                                   final Position origin) {
        /* g(x) for search */
        // flying units ignore this; always 1
        if (unit.isFlying()) {
            return 1.0;
        }

        return map.getField(origin).getMovementModifier();
    }

    public Set<Position> getAttackableUnitPositions(final GameMap map,
                                                    final Unit unit,
                                                    final Position position) {
        Set<Position> atkFields = getAttackableFields(map, unit, position);
        Set<Position> atkUnits = new HashSet<Position>();

        for (Position p : atkFields) {
            if (map.isValidField(p)) {
                if (map.getField(p).hostsUnit()) {
                    Player uOwner = map.getField(p).getUnit().getOwner();

                    if (!uOwner.equals(unit.getOwner())) {
                        atkUnits.add(p);
                    }
                }
            }
        }

        return atkUnits;
    }

    public Set<Position> getAttackableUnitPositions(final GameMap map,
                                                    final Unit unit) {

        return getAttackableUnitPositions(map, unit, unit.getPosition());
    }

    private Set<Position> getAttackableFields(final GameMap map,
                                              final Unit unit,
                                              final Position position) {
        if (!unit.isRanged()) {
            return new HashSet<Position>(
                       getValidSurroundingPositions(map, position));
        }

        RangedUnit ru = (RangedUnit) unit;
        return getPositionsInRange(map, position, ru.getMinRange(),
                                   ru.getMaxRange());
    }

    public Set<Position> getAttackableFields(final GameMap map,
                                             final Unit unit) {
        /*if (!unit.isRanged())
            return getPositionsInRange(map, unit.getPosition(), 1.0);

        RangedUnit ru = (RangedUnit) unit;
        return getPositionsInRange(map, ru.getPosition(), ru.getMinRange(),
                                   ru.getMaxRange());*/
        return getAttackableFields(map, unit, unit.getPosition());
    }

    private Set<Position> getPositionsInRange(final GameMap map,
                                              final Position origin,
                                              final Double range) {
        Set<Position> positions = new HashSet<Position>();
        Double maxr = Math.ceil(range);

        for (Integer x = 0; x < maxr * 2; x++) {
            for (Integer y = 0; y < maxr * 2; y++) {
                Position newP = new Position(x, y);

                // Pair<Integer, Integer> dist = getManhattanDistance(origin,
                // newP);
                if (x < maxr) {
                    newP = new Position(newP.getX() - x, newP.getY());
                } else if (x > maxr) {
                    newP = new Position(newP.getX() + x, newP.getY());
                }

                if (y < maxr) {
                    newP = new Position(newP.getX(), newP.getY() - x);
                } else if (y > maxr) {
                    newP = new Position(newP.getX(), newP.getY() + y);
                }

                if (newP.equals(origin) || !map.isValidField(newP)) {
                    continue;
                }

                positions.add(newP);

            }
        }

        return positions;
    }

    private Set<Position> getPositionsInRange(final GameMap map,
                                              final Position origin,
                                              final Double minRange,
                                              final Double maxRange) {
        Set<Position> positions = getPositionsInRange(map, origin, maxRange);
        Set<Position> filtered = new HashSet<Position>();

        for (Position p : positions) {
            Pair<Integer, Integer> dist;
            dist = getDistanceAway(origin, p);

            if (Math.hypot(dist.getLeft(), dist.getRight()) < minRange) {
                continue;
            }

            filtered.add(p);
        }

        return filtered;
    }

    /* Used as a heuristic for A* */
    private Integer getManhattanDistance(final Position origin,
                                         final Position destination) {
        /* h(x) */
        Pair<Integer, Integer> distance = getDistanceAway(origin, destination);

        return distance.getLeft() + distance.getRight();
    }

    private Pair<Integer, Integer> getDistanceAway(final Position origin,
                                                   final Position destination) {
        Integer x = Math.abs(origin.getX() - destination.getX());
        Integer y = Math.abs(origin.getY() - destination.getY());
        return new Pair<Integer, Integer>(x, y);
    }

}
