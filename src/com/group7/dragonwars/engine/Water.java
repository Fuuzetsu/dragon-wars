package com.group7.dragonwars.engine;

/* Test class */
public class Water extends GameField {

    public Water() {
        super("Water", 50.0, 120.0, 80.0);
    }

    public Boolean doesAcceptUnit(Unit unit) {
        return true;
    }
}
