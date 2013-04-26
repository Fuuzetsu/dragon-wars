package com.group7.dragonwars.engine;

public class RangedUnit extends Unit {

    private Double minRange, maxRange;

    public RangedUnit(final String name, final Double maxHealth,
                      final Integer maxMovement, final Double attack,
                      final Double meleeDefense, final Double rangeDefense,
                      final Double minRange, final Double maxRange,
                      final Boolean flying, final Integer productionCost,
                      final String spriteLocation,
                      final String spriteDir, final String spritePack) {
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
