//package wars.dragon.engine;

public abstract class RangedUnit extends Unit {
    
    private Double minRange, maxRange;

    public RangedUnit(String name, Double maxHealth, Integer maxMovement, 
		      Double attack, Double meleeDefense, Double rangeDefense,
		      Double minRange, Double maxRange) {
	super(name, maxHealth, maxMovement, attack, meleeDefense, rangeDefense);
	this.minRange = minRange;
	this.maxRange = maxRange;
    }

    public Double getMaxRange() {
	return this.maxRange;
    }
			  
    public Double getMinRange() {
	return this.minRange;
    }

    public Boolean isRanged() {
	return false;
    }

}
