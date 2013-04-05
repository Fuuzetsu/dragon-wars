package com.group7.dragonwars.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UnitFactory {
	private boolean isRanged;
	private String name;
	private Double maxHealth;
	private Integer maxMovement;
	private Double attack;
	private Double meleeDefense;
	private Double rangeDefense;
	private Double minRange;
	private Double maxRange;
	private Boolean isFlying;
	private Integer productionCost;
	private String spriteLocation;
	private String spriteDir;
	private String spritePack;

	static HashMap<String, UnitFactory> unit_factories;

	public UnitFactory(boolean isRanged, String name, Double maxHealth,
			Integer maxMovement, Double attack, Double meleeDefense,
			Double rangeDefense, Double minRange, Double maxRange,
			Boolean isFlying, Integer productionCost, String spriteLocation) {

		/* m..Range are ignored if !isRanged */

		this.spriteLocation = spriteLocation;
		spriteDir = "drawable";
		spritePack = "com.group7.dragonwars";

		this.isRanged = isRanged;
		this.maxHealth = maxHealth;

		this.maxMovement = maxMovement;

		this.attack = attack;
		this.meleeDefense = meleeDefense;
		this.rangeDefense = rangeDefense;

		this.minRange = minRange;
		this.maxRange = maxRange;

		this.isFlying = isFlying;
		this.productionCost = productionCost;
	}

	public String getName() {
		return name;
	}

	public Integer getProductionCost() {
		return productionCost;
	}

	public Unit produceUnit(Position position, Player owner) {
		Unit unit;
		if (isRanged) {
			unit = new RangedUnit(name, maxHealth, maxMovement, attack, meleeDefense,
					rangeDefense, minRange, maxRange, isFlying, productionCost, spriteLocation,
					spriteDir, spritePack);
		} else {
			unit = new Unit(name, maxHealth, maxMovement, attack, meleeDefense,
					rangeDefense, isFlying, productionCost, spriteLocation,
					spriteDir, spritePack);
		}
		unit.setOwner(owner);
		unit.setPosition(position);
		return unit;
	}

	static {
		 unit_factories = new HashMap<String, UnitFactory>();
		 unit_factories.put("Soldier", new UnitFactory(false, "Soldier", 10.0, 5, 2.0, 1.0, 2.0, 0.0, 0.0, false, 5, "soldier"));
		 unit_factories.put("Dragon", new UnitFactory(false, "Dragon", 30.0, 7, 3.0, 2.0, 2.0, 0.0, 0.0, true, 7, "small_dragon"));
	}

	public static HashMap<String, UnitFactory> getUnitFactories() {
		return unit_factories;
	}
}
