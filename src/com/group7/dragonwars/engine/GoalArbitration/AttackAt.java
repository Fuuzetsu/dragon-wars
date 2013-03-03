package com.group7.dragonwars.engine.GoalArbitration;

import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Unit;

public class AttackAt extends AtomicAction {
    Unit targetUnit;

    public AttackAt(GameState gameState, Unit unit, Unit target, float value) {
        super(gameState,unit,value);
        targetUnit = target;
    }

    @Override
    public void Perform() {
        super.gameState.attack(super.getUnit(), targetUnit);
    }
}
