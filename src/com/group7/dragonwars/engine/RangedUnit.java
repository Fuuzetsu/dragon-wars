package com.group7.dragonwars.engine;

public class RangedUnit extends Unit {

    private Double minRange, maxRange;

    public RangedUnit(String name, Double maxHealth, Integer maxMovement,
                      Double attack, Double meleeDefense, Double rangeDefense,
                      Double minRange, Double maxRange, Boolean flying,
                      Integer productionCost, String spriteLocation,
                      String spriteDir, String spritePack) {
        super(name, maxHealth, maxMovement, attack, meleeDefense, rangeDefense,
              flying, productionCost, spriteLocation, spriteDir, spritePack);
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public Double getMaxRange() {
        return this.maxRange;
    }

    public Double getMinRange() {
        return this.minRange;
    }

    public Boolean isRanged() {
        return true;
    }

}
