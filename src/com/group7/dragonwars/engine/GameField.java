package com.group7.dragonwars.engine;

public class GameField extends DrawableMapObject {

    private Unit hostedUnit;
    private Building hostedBuilding;
    private Double movementModifier;
    private Double defenseModifier, attackModifier;
    private Boolean flightOnly, accessible;

    public Boolean doesAcceptUnit(Unit unit) {
        Boolean canStep = false;

        if (this.flightOnly)
            canStep = unit.isFlying();

        return this.accessible || canStep;
    }

    public GameField(String fieldName, Double movementModifier, Double attackModifier,
                     Double defenseModifier, Boolean accessible, Boolean flightOnly,
                     String spriteLocation, String spriteDir, String spritePack) {
        super(fieldName, spriteLocation, spriteDir, spritePack);

        this.hostedUnit = null;
        this.hostedBuilding = null;
        this.movementModifier = movementModifier;
        this.attackModifier = attackModifier;
        this.defenseModifier = defenseModifier;
        this.accessible = accessible;
        this.flightOnly = flightOnly;

        generateInfo();
    }

    public Double getDefenseModifier() {
        if (this.hostsBuilding()) {
            Double mod = this.hostedBuilding.getDefenseBonus();
            mod += this.defenseModifier;
            return mod / 2;
        }

        return this.defenseModifier;
    }

    public Double getAttackModifier() {
        if (this.hostsBuilding()) {
            Double mod = this.hostedBuilding.getAttackBonus();
            mod += this.attackModifier;
            return mod / 2;
        }

        return this.attackModifier;
    }

    public Double getMovementModifier() {
        return this.movementModifier;
    }


    public Boolean hostsUnit() {
        return this.hostedUnit != null;
    }

    public Boolean hostsBuilding() {
        /* Could be used by the drawing routine. */
        return this.hostedBuilding != null;
    }

    public Unit getUnit() {
        return this.hostedUnit;
    }

    public Building getBuilding() {
        return this.hostedBuilding;
    }

    /* This will clobber old units/buildings as it is now. */
    public void setBuilding(Building building) {
        this.hostedBuilding = building;
    }

    public void setUnit(Unit unit) {
        this.hostedUnit = unit;
    }

    public String toString() {
        return getName();
    }

    public void generateInfo() {
        String r = getName() + "\n";
        r += "Attack: " + getAttackModifier()
            + " Defense: " + getDefenseModifier()
            + " Move: " + getMovementModifier() + "\n";
        this.info = r;
    }
}
