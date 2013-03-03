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

	public Boolean equals(Position other) {
		return this.getX() == other.getX() && this.getY() == other.getY();
	}

	public String toString() {
		return String.format("(%d, %d)", this.pair.getLeft(),
				this.pair.getRight());
	}

}
