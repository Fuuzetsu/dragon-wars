package com.group7.dragonwars.engine.GoalArbitration;

import java.util.List;

import com.group7.dragonwars.engine.GameMap;
import com.group7.dragonwars.engine.GameState;
import com.group7.dragonwars.engine.Logic;
import com.group7.dragonwars.engine.Pair;
import com.group7.dragonwars.engine.Player;
import com.group7.dragonwars.engine.Position;
import com.group7.dragonwars.engine.Unit;


public class StateTree {
    private int maxSize = 200;
    private GameState gameState;
    private Node base = null;
    private Player stateTreeOwner;
    private Logic logic = new Logic();
    private List<AtomicAction> actions;

    public StateTree(GameState gamestate, int maxsize, Player owner) {
        gameState = gamestate;
        stateTreeOwner = owner;
    }

    private void Explore() {
        base = new Node(null, 0, 0, null);
        base.setSize(1);

        for (Player player : gameState.getPlayers()) {
            if (player.equals(stateTreeOwner))
                continue;

            for (Unit playerUnit : stateTreeOwner.getOwnedUnits()) {
                float bestValue = -1;
                AtomicAction currentBest = null;

                for (Unit unit : player.getOwnedUnits()) {
                    if (base.getSize() >= maxSize)
                        break;

                    //Evaluate cost and gain, don't add if below threshold
                    List<Position> vfs = logic.findValidFieldsNextToUnit(gameState.getMap(), playerUnit, unit);
                    if (vfs.isEmpty()) {
                        continue;
                    }
                    Pair<Pair<Double, Double>, Position> dmgpos = getBestAttackPosition(
                        gameState.getMap(), playerUnit, unit, vfs);
                    float damageRatio = (float)(dmgpos.getLeft().getLeft()/dmgpos.getLeft().getRight());

                    if (damageRatio < 0)		// In enemy's favour
                        continue;
                    /*List<Position> path = logic.findPath(gameState.getMap(), playerUnit, dmgpos.getRight());

                    calculate direction to move or attack
                    if (path.size() == 1) {
                        //calculate whether this move is better than the current best
                        if (damageRatio > bestValue) {
                            currentBest = new AttackAt(gameState, playerUnit, unit, damageRatio, null);
                            bestValue = damageRatio;
                        }
                    } else if (path.size() > 1) {
                        currentBest =  new MoveTo(gameState, playerUnit,
                                                     unit.getPosition(), damageRatio/path.size());

                        //calculate whether this move is better than the current best
                    if (damageRatio/path.size() > bestValue) { */
                    if (damageRatio > bestValue) {
                        currentBest = new AttackAt(gameState, playerUnit, unit, damageRatio, dmgpos.getRight());
                        //bestValue = damageRatio/path.size();
                        bestValue = damageRatio;
                    }
                    //}
                }

                if (base.getSize() >= maxSize)
                    break;

                base.AddChildNode(bestValue, currentBest);
            }

        }
        
        for (Building building : gameState.getMap().entrySet()) {
            if (!building.getOwner().equals(stateTreeOwner)) {
                continue;
            }
	    Unit bestBuildable = getBestBuildableUnit(building);
            
            
        }

        actions = base.getActions();
        base.Collapse();

    }

    public List<AtomicAction> getActions() {
        Explore();
        return actions;
    }

    private Pair<Pair<Double, Double>, Position>
        getBestAttackPosition(final GameMap map, final Unit attacker, final Unit defender,
                              final List<Position> validPositions) {
        if (validPositions.size() == 1) {
            Position p = validPositions.get(0);
            return new Pair<Pair<Double, Double>, Position>(
                logic.calculateDamageFrom(map, attacker, defender, p), p);
        } else {
            Double ratio = null; /* Damn it Java */
            Pair<Double, Double> bestRatioDamage = null;
            Position movePos = null;
            for (Position p : validPositions) {
                Pair<Double, Double> damageExchange =
                    logic.calculateDamageFrom(map, attacker, defender, p);
                Double pRatio = damageExchange.getLeft() / damageExchange.getRight();
                if (pRatio == null || bestRatioDamage == null || movePos == null) {
                    ratio = pRatio;
                    bestRatioDamage = damageExchange;
                    movePos = p;

                } else if (pRatio > ratio) {
                    ratio = pRatio;
                    bestRatioDamage = damageExchange;
                    movePos = p;
                }
            }

            return new Pair<Pair<Double, Double>, Position>(bestRatioDamage, movePos);
        }
    }

    private Unit getBestBuildableUnit(Building building) {
        ArrayList<Unit> buildable = building.getProducibleUnits();
        if (buildable.size() == 0) {
            return null;
        }
        bestUnit = buildable.get(0);
        for (Unit unit : buildable) {
	    cost = unit.getProductionCost();
            if (cost <= stateTreeOwner.getGoldAmount() && cost > bestUnit.getProductionCost) {
                bestUnit = unit;
            }
        }
        if (bestUnit.getProductionCost() > stateTreeOwner.getGoldAmount()) {
            return null;
        } else {
            return bestUnit;
        }
    }
}

