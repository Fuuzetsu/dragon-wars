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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.group7.dragonwars.GameView;

public final class GameState {

    private GameMap map;
    private Logic logic;
    private List<Player> players = new ArrayList<Player>();
    private Player winner = null;
    private Integer playerIndex = 0;
    private Integer turns = 1;
    private Boolean gameFinished = false;
    private Statistics stats = new Statistics();
    private InformationState info;
    private GameView gvCallback;

    public GameState(final GameMap map, final Logic logic,
                     final List<Player> players, final GameView gv) {
        this.map = map;
        this.logic = logic;
        this.players = players;
        this.info = new InformationState(this);
        this.gvCallback = gv;

        for (Player p : players) {
            p.setGameState(this);
        }
    }

    public List<Position> getUnitDestinations(final GameField field) {
        return info.getUnitDestinations(field);
    }


    public void startFrame() {
        info.startFrame();
    }

    public void endFrame() {
        info.endFrame();
    }

    public Double getFps() {
        return info.getFps();
    }

    public List<Position> getCurrentPath() {
        return info.getPath();
    }

    public void setPath(final List<Position> path) {
        info.setPath(path);
    }

    public Set<Position> getAttackables() {
        return info.getAttackables();
    }

    public void attack(final Unit attacker, final Unit defender) {
        Set<Position> attackable = logic.getAttackableUnitPositions(map,
                                   attacker);

        //if (!attackable.contains(defender.getPosition()))
        //    return;
        boolean contains = false;

        for (Position pos : attackable) {
            if (pos.equals(defender.getPosition())) {
                contains = true;
            }
        }

        if (!contains) {
            return;
        }

        Pair<Double, Double> damage = logic.calculateDamage(map, attacker,
                                      defender);

        defender.reduceHealth(damage.getLeft());
        gvCallback.addDamagedUnit(defender);
        Boolean died = removeUnitIfDead(defender);
        stats.increaseStatistic("Damage dealt", damage.getLeft());

        if (died) {
            return;
        }

        /* Possibly counter */
        attacker.reduceHealth(damage.getRight());
        gvCallback.addDamagedUnit(attacker);
        removeUnitIfDead(attacker);

        stats.increaseStatistic("Damage received", damage.getRight());

    }

    public Boolean move(final Unit unit, final Position destination) {
        /* We are assuming that the destination was already
         * checked to be within this unit's reach
         */

        List<Position> path = logic.findPath(map, unit, destination);
        Integer movementCost = logic.calculateMovementCost(map, unit, path);

        if (!map.isValidField(destination) || path.size() == 0) {
            return false;
        }

        GameField destField = map.getField(destination);

        if (destField.hostsUnit()) {
            return false;
        }


        /* Double check */
        if (unit.getRemainingMovement() < movementCost) {
            return false;
        }


        GameField currentField = map.getField(unit.getPosition());
        destField.setUnit(unit);
        unit.reduceMovement(movementCost);

        currentField.setUnit(null);
        unit.setPosition(destination);
        unit.setMoved(true);

        stats.increaseStatistic("Distance travelled", 1.0 * movementCost);

        return true;

    }

    private Boolean removeUnitIfDead(final Unit unit) {
        if (unit.isDead()) {
            map.getField(unit.getPosition()).setUnit(null);
            unit.getOwner().removeUnit(unit);
            stats.increaseStatistic("Units killed");
            return true;
        }

        return false;
    }

    private void updateBuildingCaptureCounters() {
        for (GameField gf : map) {

            /* No building. */
            if (!gf.hostsBuilding()) {
                continue;
            }

            Building b = gf.getBuilding();

            /* Unit on the building. */
            if (gf.hostsUnit()) {
                Unit unit = gf.getUnit();
                Integer turnReduce = unit.getHealth().intValue();

                /* Unit already owns the building or is capturing. */
                if (unit.getOwner().equals(b.getLastCapturer())) {
                    b.reduceCaptureTime(turnReduce);
                    continue;
                } else {
                    b.resetCaptureTime();
                    b.setLastCapturer(unit.getOwner());
                    b.reduceCaptureTime(turnReduce);
                }
            } else { /* No unit on the building. */
                if (b.hasOwner()) {
                    continue;
                } else {
                    b.resetCaptureTime();
                }
            }
        }
    }

    public Statistics getStatistics() {
        return stats;
    }

    public void nextPlayer() throws GameFinishedException {
        Iterator<Player> iter = players.iterator();

        while (iter.hasNext()) {
            Player p = iter.next();

            if (p.hasLost()) {
                iter.remove();
            }
        }

        if (players.size() <= 1) {
            this.winner = players.get(0);
            this.gameFinished = true;
            throw new GameFinishedException(players.get(0));
        }

        playerIndex++;

        if (playerIndex == players.size()) {
            playerIndex = 0;
            advanceTurn();
        }

        if (getCurrentPlayer().isAi()) {
            getCurrentPlayer().takeTurn();

            /* Uh oh, dirty hack for concurrent mod 9h before presentation. */
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                /* Just let it slide and pray for the best */
            }

            nextPlayer();
        }
    }

    private void advanceTurn() {
        updateBuildingCaptureCounters();

        for (Player p : players) {
            Integer goldWorth = 0;

            for (Building b : p.getOwnedBuildings()) {
                goldWorth += b.getCaptureWorth();
            }

            p.setGoldAmount(goldWorth + p.getGoldAmount());

            stats.increaseStatistic("Gold received", 1.0 * goldWorth);

            for (Unit u : p.getOwnedUnits()) {
                u.resetTurnStatistics();
            }
        }

        ++this.turns;
        stats.increaseStatistic("Turns taken");
    }

    public Integer getTurns() {
        return this.turns;
    }

    public GameMap getMap() {
        return this.map;
    }

    public Logic getLogic() {
        return this.logic;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public Player getCurrentPlayer() {
        if (players.size() > playerIndex) {
            return players.get(playerIndex);
        } else {
            return players.get(players.size() - 1);
        }
    }

    public Player getWinner() {
        return this.winner;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(final boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public Boolean produceUnit(final GameField field, final Unit unit) {
        // produces a unit "at" a building
        if (!field.hostsBuilding() || field.hostsUnit()) {
            return false;
        }

        Building building = field.getBuilding();

        for (Unit u : building.getProducibleUnits()) {
            if (u.getName().equals(unit.getName())) {
                Player player = building.getOwner();

                if (player.getGoldAmount() < u.getProductionCost()) {
                    return false;
                }

                Unit newUnit = new Unit(u);
                newUnit.setPosition(building.getPosition());
                newUnit.setOwner(player);

                player.setGoldAmount(
                    player.getGoldAmount() - unit.getProductionCost());
                player.addUnit(newUnit);
                newUnit.setFinishedTurn(true);
                field.setUnit(newUnit);
                stats.increaseStatistic("Units produced");
                return true;
            }
        }

        return false;
    }

}
