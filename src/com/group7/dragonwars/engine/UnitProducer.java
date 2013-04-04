package com.group7.dragonwars.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UnitProducer {
	boolean isRanged;
	String name;
	Double maxHealth;
	Integer maxMovement;
	Double attack;
	Double meleeDefense;
	Double rangeDefense;
	Double minRange;
	Double maxRange;
	Boolean isFlying;
	Integer productionCost;
	String spriteLocation;
	String spriteDir;
	String spritePack;
	
	static HashMap<String, UnitProducer> unit_producers;

	public UnitProducer(boolean isRanged, String name, Double maxHealth,
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
		 unit_producers = new HashMap<String, UnitProducer>();
		 unit_producers.put("Soldier", new UnitProducer(false, "Soldier", 10.0, 5, 2.0, 1.0, 2.0, 0.0, 0.0, false, 5, "soldier"));
		 unit_producers.put("Dragon", new UnitProducer(false, "Dragon", 30.0, 7, 3.0, 2.0, 2.0, 0.0, 0.0, true, 7, "small_dragon"));
	}
	
	public static HashMap<String, UnitProducer> getUnitProducers() {
		return unit_producers;
	}
}
