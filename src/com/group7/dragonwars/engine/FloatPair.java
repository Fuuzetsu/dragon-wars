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
