package com.group7.dragonwars.engine;

import java.util.*;

import com.group7.dragonwars.engine.GoalArbitration.AtomicAction;
import com.group7.dragonwars.engine.GoalArbitration.StateTree;

public class PlayerAI extends Player {
    GameState gameState;
    //Evaluator evaluator;
    List<AtomicAction> actions;

    public PlayerAI(String name, Integer colour, GameState gamestate) {
        super(name, colour);
        gameState = gamestate;
    }

    public void takeTurn() {
        UpdateActions();

        for (AtomicAction action : actions) {
            action.Perform();
        }

        actions.clear();
    }

    private void UpdateActions() {
        StateTree currentGameState = new StateTree(gameState, 200, this);
        actions = currentGameState.getActions();
    }
}
