package com.group7.dragonwars.engine.GoalArbitration;

import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Unit;




//abstract class from which "AttackUnit, DefendTile, MoveTo, CaptureTile" etc are derived from
public abstract class AtomicAction {
    private float actionValue = 0;
    protected GameState gameState;
    protected Unit actionUnit;

    public AtomicAction(final GameState gamestate, final Unit unit,
                        final float value) {
        actionValue = value;
        gameState = gamestate;
        actionUnit = unit;
    }

    public abstract void Perform();

    public Unit getUnit() {
        return actionUnit;
    }
    public float getActionValue() {
        return actionValue;
    }
}
