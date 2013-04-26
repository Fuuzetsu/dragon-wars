package com.group7.dragonwars.engine;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;


public class Player {
    private String name;
    private Boolean lost;
    private Integer gold = 5;
    private Integer colour;
    private Bitmap flag;
    private Map<String, Bitmap> unitSprites;

    List<Unit> ownedUnits = new ArrayList<Unit>();
    List<Building> ownedBuildings = new ArrayList<Building>();
    private boolean isAi = false;

    public Player(String name, final Integer colour) {
        this.name = name;
        this.colour = colour;
        this.lost = false;
    }

    public String getName() {
        return this.name;
    }

    public Boolean hasLost() {
        return this.lost || (ownedUnits.isEmpty() && ownedBuildings.isEmpty());
    }

    public Boolean hasMoveableUnits() {
        for (Unit u : ownedUnits)
            if (!u.hasFinishedTurn()) {
                return true;
            }

        return false;
    }

    public void removeUnit(Unit unit) {
        ownedUnits.remove(unit);
    }

    public void removeBuilding(Building building) {
        ownedBuildings.remove(building);
    }

    public List<Unit> getOwnedUnits() {
        return this.ownedUnits;
    }

    public void addUnit(Unit unit) {
        this.ownedUnits.add(unit);
    }

    public void addBuilding(Building building) {
        this.ownedBuildings.add(building);
    }

    public String toString() {
        return name;
    }

    public Integer getGoldAmount() {
        return this.gold;
    }

    public void setGoldAmount(Integer amount) {
        this.gold = amount;
    }

    public List<Building> getOwnedBuildings() {
        return this.ownedBuildings;
    }

    public Integer getColour() {
        return this.colour;
    }

    public Bitmap getFlag() {
        return flag;
    }

    public Boolean hasFlag() {
        return flag != null;
    }

    public void setFlag(Bitmap flag) {
        this.flag = flag;
    }

    public Bitmap getUnitSprite(final String unitName) {
        return unitSprites.get(unitName);
    }

    public void setUnitSprites(final  Map<String, Bitmap> sprites) {
        unitSprites = sprites;
    }

    public boolean isAi() {
        return this.isAi ;
    }

    public void setIsAi(boolean isAi) {
        this.isAi = isAi;
    }

    public void setGameState(GameState gameState) {
        return;// just here for the polymorphism
    }

    public void takeTurn() {
        return; /* Regular players do nothing here */
    }

    /* For AI to override */
    public void setState(final GameState gs) {
        return;
    }
}
