package com.group7.dragonwars.engine;

public class Position {

    private Pair<Integer, Integer> pair;

    public Position(Integer x, Integer y) {
        this.pair = new Pair<Integer, Integer>(x, y);
    }

    public Integer getX() {
        return this.pair.getLeft();
    }

    public Integer getY() {
        return this.pair.getRight();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Position)) {
            return false;
        }
        Position that = (Position) other;

        return getX() == that.getX() && getY() == that.getY();
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
