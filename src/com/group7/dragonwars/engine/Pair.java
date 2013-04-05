package com.group7.dragonwars.engine;

public class Pair<L, R> {

    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
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
