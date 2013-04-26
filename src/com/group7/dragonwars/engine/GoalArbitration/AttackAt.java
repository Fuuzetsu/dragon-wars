package com.group7.dragonwars.engine.GoalArbitration;

import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Unit;


public class AttackAt extends AtomicAction {
    private Unit targetUnit;
    private Position moveTo;

    public AttackAt(final GameState gameState, final Unit unit,
                    final Unit target, final float value,
                    final Position moveTo) {
        super(gameState, unit, value);
        targetUnit = target;
        this.moveTo = moveTo;
    }

    @Override
    public void Perform() {
        Boolean moved = true;

        if (moveTo != null) {
            super.gameState.move(getUnit(), moveTo);
        }

        /* Only attack if we managed to move (or pretended to) */
        if (moved) {
            super.gameState.attack(getUnit(), targetUnit);
        }
    }
}
