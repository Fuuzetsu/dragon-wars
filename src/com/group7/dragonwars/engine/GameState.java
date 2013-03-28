package com.group7.dragonwars.engine;

import java.util.*;
import java.io.*;
import java.lang.Math;

public class GameState {

    GameMap map;
    Logic logic;
    List<Player> players = new ArrayList<Player>();
    Integer turns = 0;

    public GameState(GameMap map, Logic logic, List<Player> players) {
        this.map = map;
        this.logic = logic;

        this.players = players;
    }


    private static List<String> readFile(String filename) {
        List<String> text = new ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = in.readLine()) != null)
                text.add(line);

            in.close();
        } catch (FileNotFoundException fnf) {
            System.err.println("Couldn't find " + fnf.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Couldn't read " + ioe.getMessage());
            System.exit(1);
        }

        return text;
    }

    public void attack(Unit attacker, Unit defender) {
        Set<Position> attackable = logic.getAttackableUnitPositions(map,
                                   attacker);

        if (!attackable.contains(defender.getPosition()))
            return;

        Pair<Double, Double> damage = logic.calculateDamage(map, attacker,
                                      defender);

        defender.reduceHealth(damage.getLeft());

        Boolean died = removeUnitIfDead(defender);

        if (died)
            return;

        /* Possibly counter */
        attacker.reduceHealth(damage.getRight());
        removeUnitIfDead(attacker);

    }

    private Boolean move(Unit unit, Position destination) {
        /* We are assuming that the destination was already
         * checked to be within this unit's reach
         */

        List<Position> path = logic.findPath(map, unit, destination);
        Integer movementCost = logic.calculateMovementCost(map, unit, path);

        if (!map.isValidField(destination))
            return false;

        GameField destField = map.getField(destination);
        if (destField.hostsUnit())
            return false;


        /* Double check */
        if (unit.getRemainingMovement() > movementCost)
            return false;


        GameField currentField = map.getField(unit.getPosition());
        destField.setUnit(unit);
        unit.reduceMovement(movementCost);


        currentField.setUnit(null);

        return true;

    }

    private Boolean removeUnitIfDead(Unit unit) {
        if (unit.isDead()) {
            map.getField(unit.getPosition()).setUnit(null);
            unit.getOwner().removeUnit(unit);
            return true;
        }

        return false;
    }

    private void updateBuildingCaptureCounters() {
        for (GameField gf : map) {

            /* No building. */
            if (!gf.hostsBuilding())
                continue;

            Building b = gf.getBuilding();

            /* Unit on the building. */
            if (gf.hostsUnit()) {
                Unit unit = gf.getUnit();
                Integer turnReduce = unit.getHealth().intValue();

                /* Unit already owns the building or is capturing for >1 turn. */
                if (unit.getOwner().equals(b.getLastCapturer())) {
                    b.reduceCaptureTime(turnReduce);
                    continue;
                } else {
                    b.resetCaptureTime();
                    b.setLastCapturer(unit.getOwner());
                    b.reduceCaptureTime(turnReduce);
                }
            }
            /* No unit on the building. */
            else {
                if (b.hasOwner())
                    continue;
                else
                    b.resetCaptureTime();
            }
        }
    }

    public void advanceTurn() {
        updateBuildingCaptureCounters();
        ++this.turns;
    }

    public Integer getTurns() {
        return this.turns;
    }

    public GameMap getMap() {
        return this.map;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

}
