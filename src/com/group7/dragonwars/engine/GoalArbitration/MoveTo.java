package com.group7.dragonwars.engine.GoalArbitration;

import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Unit;

public class MoveTo extends AtomicAction
{
	Position destination;
	
	public MoveTo(GameState gameState, Unit unit, Position destin, float value)
	{
		super(gameState,unit,value);
		destination = destin;
	}

	@Override
	public void Perform()
	{
		getUnit().setPosition(destination);
	}
}
