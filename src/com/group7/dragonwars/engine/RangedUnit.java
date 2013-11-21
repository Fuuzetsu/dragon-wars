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
