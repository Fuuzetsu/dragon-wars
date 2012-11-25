package wars.dragon.engine;

public abstract class GameField {

    String fieldName;
    Unit hostedUnit;
    Building hostedBuilding;
    Double movementModifier;

    public abstract Boolean doesAcceptUnit(Unit);

    public GameField(String fieldName, Double movementModifier) {
	this.fieldName = fieldName;
	this.movementModifier = movementModifier;
    }

    public Boolean hostsUnit() {
	return this.hostedUnit != null;
    }

    public Boolean hostsBuilding() {
	/* Could be used by the drawing routine. */
	return this.hostedBuilding != null;
    }
    
    public Unit getUnit() {
	return this.hostedUnit;
    }

    public Building getBuilding() {
	return this.hostedBuilding;
    }

    /* This will clobber old units/buildings as it is now. */
    public setBuilding(Building building) {
	this.hostedBuilding = building;
    }

    public setUnit(Unit unit) {
	this.hostedUnit = unit;
    }
}
