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

    public boolean equals(Position other) {
        if (other == null) {
            return false;
        }

        return getX() == other.getX() && getY() == other.getY();
    }

    public String toString() {
        return String.format("(%d, %d)", this.pair.getLeft(),
                              this.pair.getRight());
    }

}
