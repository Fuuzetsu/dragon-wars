package com.group7.dragonwars.engine;

import java.util.*;

import com.group7.dragonwars.engine.GoalArbitration.AtomicAction;
import com.group7.dragonwars.engine.GoalArbitration.StateTree;

public class PlayerAI extends Player {
    private GameState gameState;
    //Evaluator evaluator;
    List<AtomicAction> actions;

    public PlayerAI(String name, Integer colour, GameState gamestate) {
        super(name, colour);
        gameState = gamestate;
    }

    public PlayerAI(final String name, final Integer colour) {
        super(name, colour);
    }

    public boolean isAI() {
        return true;
        /* this is to be used to determine whether the user should be
         * allowed to control the current player's units' actions via
         * the touchscreen
         */
    }

    @Override
    public void setState(final GameState state) {
        gameState = state;
    }

    @Override
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
