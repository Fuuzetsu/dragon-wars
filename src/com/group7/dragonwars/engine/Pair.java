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

public class Pair<L, R> {

    private L left;
    private R right;

    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    public void setLeft(final L l) {
        this.left = l;
    }

    public void setRight(final R r) {
        this.right = r;
    }

    public String toString() {
        return String.format("(%s, %s)", left, right);
    }

    @Override
    public int hashCode() {
        int hashFirst = left != null ? left.hashCode() : 0;
        int hashSecond = right != null ? right.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Pair)) {
            return false;
        }

        Pair that = (Pair) other;

        return left.equals(that.getLeft()) && right.equals(that.getRight());
    }
}
