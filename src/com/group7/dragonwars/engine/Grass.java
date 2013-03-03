package com.group7.dragonwars.engine;

/* Test class */
public class Grass extends GameField {
    
    public Grass() {
	super("Grass", 100.0, 100.0, 100.0);
    }

    public Boolean doesAcceptUnit(Unit unit) {
	return true;
    }
}
    
