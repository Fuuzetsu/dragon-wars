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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Used by GameState to provide data to GameView in a transparent fashion.
 * All the logic of whether to recalculate something or not will be moved here.
 */
public final class InformationState {

    private GameState containingState;
    private GameField currentlySelected;
    private GameField lastField;
    private Set<Position> lastAttackables;
    private List<Position> lastDestinations;
    private Unit lastUnit;
    private List<Position> path;
    private Long startingTime;
    private Long timeElapsed = 0L;
    private Long framesSinceLastSecond = 0L;
    private Double fps = 0.0;
    private Logic logic = new Logic();


    public InformationState(final GameState state) {
        containingState = state;
    }


    public List<Position> getPath() {
        if (path == null || lastDestinations == null
            || lastDestinations.size() == 0) {
            return new ArrayList<Position>(0);
        }

        return path;
    }

    public void setPath(final List<Position> path) {
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

    public List<Position> getUnitDestinations(final GameField field) {
        List<Position> unitDests =  new ArrayList<Position>(0);

        if (field == null) {
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

        if (containingState.getCurrentPlayer().isAi() || u.getOwner()
            != containingState.getCurrentPlayer() || u.hasFinishedTurn()) {
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
            lastAttackables
                = logic.getAttackableUnitPositions(containingState.getMap(), u);
            return unitDests;
        }

        if (lastDestinations == null || lastUnit == null || lastField == null
            || lastAttackables == null) {
            lastUnit = u;
            lastField = field;
            unitDests = logic.destinations(containingState.getMap(), u);
            lastDestinations = unitDests;
            lastAttackables
                = logic.getAttackableUnitPositions(containingState.getMap(), u);
            path = null;
            return unitDests;
        }

        if (u.equals(lastUnit) && field.equals(lastField)) {
            return lastDestinations;
        }

        return unitDests;
    }

    public void startFrame() {
        startingTime = System.currentTimeMillis();
    }

    public void endFrame() {
        framesSinceLastSecond++;

        timeElapsed += System.currentTimeMillis() - startingTime;

        if (timeElapsed >= 1000) {
            fps = framesSinceLastSecond / (timeElapsed * 0.001);
            framesSinceLastSecond = 0L;
            timeElapsed = 0L;
        }
    }

    public Double getFps() {
        return fps;
    }



}
