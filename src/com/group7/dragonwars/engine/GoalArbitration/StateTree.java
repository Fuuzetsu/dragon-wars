package com.group7.dragonwars.engine.GoalArbitration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.group7.dragonwars.engine.Building;
import com.group7.dragonwars.engine.GameField;
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

    public StateTree(final GameState gamestate, final int maxsize,
                     final Player owner) {
        gameState = gamestate;
        stateTreeOwner = owner;
    }

    private void Explore() {
        base = new Node(null, 0, 0, null);
        base.setSize(1);

        for (Player player : gameState.getPlayers()) {
            if (player.equals(stateTreeOwner)) {
                continue;
            }

            for (Unit playerUnit : stateTreeOwner.getOwnedUnits()) {
                float bestValue = -1;
                AtomicAction currentBest = null;

                for (Unit unit : player.getOwnedUnits()) {
                    if (base.getSize() >= maxSize) {
                        break;
                    }

                    //Evaluate cost and gain, don't add if below threshold
                    List<Position> vfs = logic.findValidFieldsNextToUnit(
                        gameState.getMap(), playerUnit, unit);

                    if (vfs.isEmpty()) {
                        continue;
                    }

                    Pair<Pair<Double, Double>, Position> dmgpos
                        = getBestAttackPosition(gameState.getMap(), playerUnit,
                                                unit, vfs);
                    float damageRatio = (float)(dmgpos.getLeft().getLeft()
                                                / dmgpos.getLeft().getRight());

                    if (damageRatio < 0) {      // In enemy's favour
                        continue;
                    }

                    if (damageRatio > bestValue) {
                        currentBest =
                            new AttackAt(gameState, playerUnit, unit,
                                         damageRatio, dmgpos.getRight());
                        bestValue = damageRatio;
                    }
                }

                if (currentBest == null) { /* No unit to attack */
                    GameField curField
                        = gameState.getMap().getField(playerUnit.getPosition());

                    if (!(curField.hostsBuilding()
                            && !curField.getBuilding().getOwner()
                          .equals(stateTreeOwner))) {
                        /* We're standing on a building we don't own */
                        List<Position> dests
                            = logic.destinations(gameState.getMap(),
                                                 playerUnit);

                        for (Position p : dests) {
                            GameField gf = gameState.getMap().getField(p);

                            if (gf.hostsBuilding() && !gf.getBuilding()
                                .getOwner().equals(stateTreeOwner)
                                && !gf.hostsUnit()) {
                                currentBest = new MoveTo(gameState,
                                                         playerUnit, p, 1);
                                break; /* Naive building picking */
                            }
                        }
                    }
                }

                if (base.getSize() >= maxSize) {
                    break;
                }

                base.AddChildNode(bestValue, currentBest);
            }

        }

        int goldAmount = stateTreeOwner.getGoldAmount();

        for (Building building : stateTreeOwner.getOwnedBuildings()) {
            Position p = building.getPosition();

            if (!gameState.getMap().getField(p).hostsUnit()) {
                Log.d("StateTree", "Trying to build at " + building.getName());
                Unit bestBuildable = getBestBuildableUnit(building, goldAmount);

                if (bestBuildable == null) {
                    continue;
                }

                goldAmount -= bestBuildable.getProductionCost();
                AtomicAction bestAction =
                    new BuildUnit(gameState, bestBuildable,
                                  building.getPosition(),
                                  bestBuildable.getProductionCost());
                base.AddChildNode(bestBuildable.getProductionCost(),
                                  bestAction);
            }
        }

        actions = base.getActions();
        base.Collapse();

    }

    public List<AtomicAction> getActions() {
        Explore();
        return actions;
    }

    private Pair<Pair<Double, Double>, Position>
    getBestAttackPosition(final GameMap map, final Unit attacker,
                          final Unit defender,
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
                Double pRatio
                    = damageExchange.getLeft() / damageExchange.getRight();

                if (pRatio == null || bestRatioDamage == null
                    || movePos == null) {
                    ratio = pRatio;
                    bestRatioDamage = damageExchange;
                    movePos = p;

                } else if (pRatio > ratio) {
                    ratio = pRatio;
                    bestRatioDamage = damageExchange;
                    movePos = p;
                }
            }

            return new Pair<Pair<Double, Double>, Position>(bestRatioDamage,
                                                            movePos);
        }
    }

    private Unit getBestBuildableUnit(final Building building,
                                      final int goldAmount) {

        List<Unit> buildable = building.getProducibleUnits();

        if (buildable.size() == 0) {
            return null;
        }

        Unit bestUnit = buildable.get(0);

        for (Unit unit : buildable) {
            int cost = unit.getProductionCost();

            if (cost <= goldAmount && cost > bestUnit.getProductionCost()) {
                bestUnit = unit;
            }
        }

        if (bestUnit.getProductionCost() > goldAmount) {
            return null;
        } else {
            return bestUnit;
        }
    }
}
