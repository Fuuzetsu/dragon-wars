package com.group7.dragonwars.engine.GoalArbitration;

import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Unit;
import com.group7.dragonwars.engine.Building;

public class BuildUnit extends AtomicAction {
    Position destination;

    public BuildUnit(GameState gameState, Unit unit, Position destin, float value) {
        super(gameState,unit,value);
        destination = destin;
    }

    @Override
    public void Perform() {
        super.gameState.produceUnit(destin, unit);
    }
}

