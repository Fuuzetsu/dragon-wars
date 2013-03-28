package com.group7.dragonwars.engine;

import java.util.*;
import java.io.*;

public class GameState {

    GameMap map;
    Logic logic;
    List<Player> players = new ArrayList<Player>();
    Integer turns = 0;

    public GameState(GameMap map, Logic logic) {
        this.map = map;
        this.logic = logic;

        // Test data
        this.players = new ArrayList<Player>(2);
        players.add(new Player("Shana"));
        players.add(new Player("Yukari"));
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
