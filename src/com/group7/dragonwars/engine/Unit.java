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

import java.text.DecimalFormat;
import java.util.LinkedList;

public class Unit extends DrawableMapObject {

    private Integer maxMovement, movement;
    private Double maxHealth, health, lastDamage;
    private Double attack, meleeDefense, rangeDefense;
    private Position position;
    private Boolean hasFinishedTurn = false;
    private Boolean hasMoved = false;
    private Player owner;
    private Boolean isFlying;
    private Integer productionCost;

    private static DecimalFormat decformat = new DecimalFormat("#.##");

    public Unit(final String name, final Double maxHealth,
                final Integer maxMovement, final Double attack,
                final Double meleeDefense, final Double rangeDefense,
                final Boolean isFlying, final Integer productionCost,
                final String spriteLocation,
                final String spriteDir, final String spritePack) {
        super(name, spriteLocation, spriteDir, spritePack);

        this.maxHealth = maxHealth;
        this.health = this.maxHealth;
        this.lastDamage = 0.0;

        this.maxMovement = maxMovement;
        this.movement = this.maxMovement;

        this.attack = attack;
        this.meleeDefense = meleeDefense;
        this.rangeDefense = rangeDefense;

        this.isFlying = isFlying;
        this.productionCost = productionCost;

        generateInfo();
    }

    /* Used for copying the unit template */
    public Unit(final Unit unit) {
        super(unit.getName(), unit.getSpriteLocation(),
              unit.getSpriteDir(), unit.getSpritePack());

        this.maxHealth = unit.getMaxHealth();
        this.health = this.maxHealth;
        this.lastDamage = 0.0;

        this.maxMovement = unit.getMaxMovement();
        this.movement = this.maxMovement;

        this.attack = unit.getAttack();
        this.meleeDefense = unit.getMeleeDefense();
        this.rangeDefense = unit.getRangeDefense();

        this.owner = unit.getOwner();
        this.isFlying = unit.isFlying();
        this.productionCost = unit.getProductionCost();
        this.info = unit.info;
    }

    public Boolean isDead() {
        return health <= 0;
    }

    public Double getHealth() {
        return this.health;
    }

    public Double getMaxHealth() {
        return this.maxHealth;
    }

    public Double getLastDamage() {
        return this.lastDamage;
    }

    public Double getAttack() {
        return this.attack;
    }

    public Double getMeleeDefense() {
        return this.meleeDefense;
    }

    public Double getRangeDefense() {
        return this.rangeDefense;
    }

    public Position getPosition() {
        return this.position;
    }

    public Integer getRemainingMovement() {
        return this.movement;
    }

    public Integer getMaxMovement() {
        return this.maxMovement;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(final Player player) {
        this.owner = player;
    }

    public void reduceHealth(final Double damage) {
        this.lastDamage += -damage;
        this.health -= damage;

        if (this.health < 0) {
            this.health = 0.0;
        }
    }

    public void setPosition(final Position position) {
        this.position = position;
    }

    public void restoreHealth(final Double heal) {
        Double newHealth = this.health + heal;
        this.health = (newHealth <= maxHealth) ? newHealth : maxHealth;
    }

    public Boolean hasFinishedTurn() {
        return this.hasFinishedTurn;
    }

    public void setFinishedTurn(final Boolean b) {
        this.hasFinishedTurn = b;
    }

    public String toString() {
        return getName();
    }

    public Boolean isRanged() {
        return false;
    }

    public Boolean isFlying() {
        return this.isFlying;
    }

    public void resetLastDamage() {
        this.lastDamage = 0.0;
    }

    public Boolean reduceMovement(final Integer amount) {
        if (this.movement - amount < 0) {
            return false;
        }

        this.movement -= amount;
        return true;
    }

    public void setMoved(Boolean b) {
        hasMoved = true;
    }

    public Boolean hasMoved() {
        return hasMoved;
    }

    public void resetTurnStatistics() {
        movement = maxMovement;
        hasMoved = false;
        setFinishedTurn(false);
    }

    public Integer getProductionCost() {
        return this.productionCost;
    }

    @Override
    public final String getInfo() {
        String r = getName() + " ~ " + this.getOwner().getName() + "\n";
        r += "Health: " + decformat.format(getHealth());
        return r + this.info;
    }

    @Override
    public void generateInfo() {
        String  r =  "/"  + decformat.format(getMaxHealth()) + "\n";
        r += "Attack: " + decformat.format(getAttack()) + "\n";
        r += "Defense: " + decformat.format(getMeleeDefense()) + " (Melee) "
             + decformat.format(getRangeDefense()) + " (Ranged)\n";

        this.info = r;
    }
}
