package com.group7.dragonwars.engine;

public interface FuncEx<I, O, E extends Exception> {
	public O apply(I i) throws E;
}