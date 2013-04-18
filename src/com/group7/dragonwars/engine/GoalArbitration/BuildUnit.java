package com.group7.dragonwars.engine.GoalArbitration;

import android.util.Log;

import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Unit;

public class BuildUnit extends AtomicAction {
    Position destination;

    public BuildUnit(GameState gameState, Unit unit, Position destin, float value) {
        super(gameState, unit, value);
        destination = destin;
    }

    @Override
    public void Perform() {
        Log.d("BU", "Unit being built");
        super.gameState.produceUnit(gameState.getMap().getField(destination), actionUnit);
    }
}

