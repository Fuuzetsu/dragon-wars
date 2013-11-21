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
import java.util.List;

public final class Building extends DrawableMapObject {

    private Integer captureWorth;
    private Integer captureDifficulty, remainingCaptureTime;
    private Double attackBonus, defenseBonus;
    private Player owner = null;
    private Boolean goalBuilding;
    private Player lastCapturer;
    private List<Unit> producibleUnits = new ArrayList<Unit>();
    private Position position;


    public Building(final String name, final Integer captureDifficulty,
                    final Double attackBonus, final Double defenseBonus,
                    final Boolean goalBuilding, final Integer captureWorth,
                    final String spriteLocation, final String spriteDir,
                    final String spritePack) {
        super(name, spriteLocation, spriteDir, spritePack);

        this.captureDifficulty = captureDifficulty;
        this.remainingCaptureTime = this.captureDifficulty;

        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;

        this.goalBuilding = goalBuilding;
        this.captureWorth = captureWorth;

        generateInfo();

    }

    public Building(final Building building) {
        super(building.getName(), building.getSpriteLocation(),
              building.getSpriteDir(), building.getSpritePack());

        this.captureDifficulty = building.getCaptureDifficulty();
        this.remainingCaptureTime = this.captureDifficulty;

        this.attackBonus = building.getAttackBonus();
        this.defenseBonus = building.getDefenseBonus();

        this.goalBuilding = building.isGoalBuilding();
        this.captureWorth = building.getCaptureWorth();

        this.info = building.info;

    }

    public Boolean canProduceUnits() {
        return !this.producibleUnits.isEmpty();
    }

    public void addProducibleUnit(final Unit unit) {
        this.producibleUnits.add(unit);
        this.generateInfo();
    }

    public List<Unit> getProducibleUnits() {
        return this.producibleUnits;
    }

    public String toString() {
        return getName();
    }

    public Player getLastCapturer() {
        return this.lastCapturer;
    }

    public void setLastCapturer(final Player player) {
        this.lastCapturer = player;
    }

    public Integer getCaptureDifficulty() {
        return this.captureDifficulty;
    }

    public Integer getRemainingCaptureTime() {
        return this.remainingCaptureTime;
    }

    public Double getAttackBonus() {
        return this.attackBonus;
    }

    public Double getDefenseBonus() {
        return this.defenseBonus;
    }

    public Boolean hasOwner() {
        return this.owner != null;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(final Player player) {
        this.owner = player;
    }

    public Boolean isGoalBuilding() {
        return this.goalBuilding;
    }

    public void reduceCaptureTime(final Integer captureAmount) {
        this.remainingCaptureTime -= captureAmount;

        if (this.remainingCaptureTime <= 0) {
            this.remainingCaptureTime = 0;
            this.owner.removeBuilding(this);
            this.owner = this.lastCapturer;
            this.lastCapturer.addBuilding(this);
        }
    }

    public void resetCaptureTime() {
        this.remainingCaptureTime = this.captureDifficulty;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(final Position pos) {
        this.position = pos;
    }

    public Integer getCaptureWorth() {
        return this.captureWorth;
    }

    @Override
    public String getInfo() {
        String r = getName();

        if (hasOwner()) {
            r += " ~ " + getOwner().getName();
        }

        r += "\n";

        return r + this.info;
    }

    @Override
    public void generateInfo() {
        String r = "";

        if (canProduceUnits()) {
            r += "Produces:\n";

            for (Unit u : getProducibleUnits()) {
                r += " " + u + " - "
                     + u.getProductionCost() + " Gold\n";
            }
        }

        this.info = r;

    }
}
