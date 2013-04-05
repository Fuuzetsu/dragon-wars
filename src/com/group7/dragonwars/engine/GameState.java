package com.group7.dragonwars.engine;

import java.util.*;
import java.io.*;
import java.lang.Math;

import android.util.Log;

public class GameState {

    GameMap map;
    Logic logic;
    List<Player> players = new ArrayList<Player>();
    List<Player> playersPlaying;
    Player currentPlayer;
    Integer turns = 0;

    public GameState(GameMap map, Logic logic, List<Player> players) {
        this.map = map;
        this.logic = logic;

        this.players = players;
        this.playersPlaying = this.players;
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

    public Boolean move(Unit unit, Position destination) {
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
        if (unit.getRemainingMovement() < movementCost)
            return false;


        GameField currentField = map.getField(unit.getPosition());
        destField.setUnit(unit);
        unit.reduceMovement(movementCost);

        currentField.setUnit(null);
        unit.setPosition(destination);

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

    public void nextPlayer() {
        if (playersPlaying.size() == 0) {
            advanceTurn();
            playersPlaying = players;
        } else {
            playersPlaying.remove(0);
        }

    }

    public void advanceTurn() {
        updateBuildingCaptureCounters();

        for (Player p : players) {
            Integer goldWorth = 0;

            for (Building b : p.getOwnedBuildings())
                goldWorth += b.getCaptureWorth();
            p.setGoldAmount(goldWorth + p.getGoldAmount());
        }
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

    public Player getCurrentPlayer() {
    	return players.get(turns % players.size());
    	// :S
    }

    /* I'll just roll with GF and String for now; should be easy to change */
    public Boolean produceUnit(final GameField field, final Unit unit) {
    	// produces a unit "at" a building
        if (!field.hostsBuilding() || field.hostsUnit())
            return false;

        Building building = field.getBuilding();

        for (Unit u : building.getProducableUnits()) {
            if (u.getName().equals(unit.getName())) {
            	Player player = building.getOwner();

            	if (player.getGoldAmount() < u.getProductionCost()) {
                    return false;
            	}

                Unit newUnit = new Unit(u);

            	field.setUnit(newUnit);
            	player.setGoldAmount(player.getGoldAmount() - unit.getProductionCost());
                return true;
            }
        }

        return false;

        /*Player player = building.getOwner();

        if (player.getGoldAmount() < unit.getProductionCost())
            return false;

        unit.setOwner(player);
        unit.setPosition(building.getPosition());
        player.setGoldAmount(player.getGoldAmount() - unit.getProductionCost());
        field.setUnit(unit);*/
    }

}
