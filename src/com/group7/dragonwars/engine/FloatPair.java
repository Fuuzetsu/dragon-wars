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

public final class FloatPair {
    private Pair<Float, Float> pair;

    public FloatPair(final Float x, final Float y) {
        this.pair = new Pair<Float, Float>(x, y);
    }

    public Float getX() {
        return this.pair.getLeft();
    }

    public Float getY() {
        return this.pair.getRight();
    }


    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FloatPair)) {
            return false;
        }

        FloatPair that = (FloatPair) other;
        return this.getX() == that.getX() && this.getY() == that.getY();
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    public String toString() {
        return String.format("(%d, %d)", this.pair.getLeft(),
                             this.pair.getRight());
    }
}
