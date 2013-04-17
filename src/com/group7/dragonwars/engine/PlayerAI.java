package com.group7.dragonwars.engine;

import java.util.*;

import com.group7.dragonwars.engine.GoalArbitration.AtomicAction;
import com.group7.dragonwars.engine.GoalArbitration.StateTree;

public class PlayerAI extends Player {
    GameState gameState;
    //Evaluator evaluator;
    List<AtomicAction> actions;

    public PlayerAI(String name, Integer colour, GameState gameState) {
        super(name, colour);
        this.gameState = gameState;
    }
    
    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    
    @Override
    public boolean isAi() {
        return true;
        /* this is to be used to determine whether the user should be
         * allowed to control the current player's units' actions via
         * the touchscreen
         */
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
