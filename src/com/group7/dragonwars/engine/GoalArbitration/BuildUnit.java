package com.group7.dragonwars.engine.GoalArbitration;

import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Unit;
import com.group7.dragonwars.engine.Building;

public class BuildUnit extends AtomicAction {
    Position destination;
    int unit_num;

    public BuildUnit(GameState gameState, Unit unit, Position destin, float value, int unit_num) {
        super(gameState,unit,value);
        destination = destin;
        this.unit_num = unit_num;
    }

    @Override
    public void Perform() {
	Building building = super.gameState.getField(destin).getBuilding();
        super.gameState.produceUnit(destin, building.getProducibleUnits().get(unit_num));
    }
}

