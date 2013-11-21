/* This file is part of Dragon Wars.
 *
 * Dragon Wars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragon Wars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dragon Wars.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.group7.dragonwars.engine;

import java.util.List;

import com.group7.dragonwars.engine.GoalArbitration.AtomicAction;
import com.group7.dragonwars.engine.GoalArbitration.StateTree;

public class PlayerAI extends Player {
    private GameState gameState;
    //Evaluator evaluator;
    List<AtomicAction> actions;

    public PlayerAI(final String name, final Integer colour) {
        super(name, colour);
    }

    @Override
    public void setGameState(final GameState gameState) {
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
