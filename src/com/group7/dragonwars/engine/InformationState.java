package com.group7.dragonwars.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Used by GameState to provide data to GameView in a transparent fashion.
 * All the logic of whether to recalculate something or not will be moved here.
 */
public class InformationState {

    private GameState containingState;
    private GameField currentlySelected;
    private GameField lastField;
    private Set<Position> lastAttackables;
    private List<Position> lastDestinations;
    private Unit lastUnit;
    private List<Position> path;
    private Long timeElapsed = 0L;
    private Long framesSinceLastSecond = 0L;
    private Double fps = 0.0;
    private Logic logic = new Logic();


    public InformationState(GameState state) {
        containingState = state;
    }


    public List<Position> getPath() {
        if (path == null) {
            return new ArrayList<Position>(0);
        }

        return path;
    }

    public void setPath(List<Position> path) {
        this.path = path;
    }

    public Set<Position> getAttackables() {
        if (lastAttackables == null) {
            return new HashSet<Position>(0);
        }

        return lastAttackables;
    }

    public List<Position> getUnitDestinations() {
        return getUnitDestinations(currentlySelected);
    }

    public List<Position> getUnitDestinations(GameField field) {
        List<Position> unitDests =  new ArrayList<Position>(0);

        if (field == null) { // || !containingState.getMap().isValidField(field)) {
            return unitDests;
        }

        if (!field.hostsUnit()) {
            lastUnit = null;
            lastField = null;
            lastAttackables = null;
            path = null;
            return unitDests;
        }

        Unit u = field.getUnit();
        if (containingState.getCurrentPlayer().isAI() || u.getOwner() != containingState.getCurrentPlayer() ||
            u.hasFinishedTurn()) {
            lastUnit = null;
            lastField = null;
            path = null;
            lastAttackables = null;

            return unitDests;
        }

        if (u.hasMoved()) {
            lastUnit = null;
            lastField = null;
            path = null;
            lastAttackables = logic.getAttackableUnitPositions(containingState.getMap(), u);
            return unitDests;
        }

        if (lastDestinations == null || lastUnit == null || lastField == null ||
            lastAttackables == null) {
            lastUnit = u;
            lastField = field;
            unitDests = logic.destinations(containingState.getMap(), u);
            lastDestinations = unitDests;
            lastAttackables = logic.getAttackableUnitPositions(containingState.getMap(), u);
            path = null;
            return unitDests;
        }

        if (u.equals(lastUnit) && field.equals(lastField)) {
            return lastDestinations;
        }

        return unitDests;
    }



}
