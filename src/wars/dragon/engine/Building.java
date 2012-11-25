package wars.dragon.engine;

public abstract class Building {

    private String buildingName;
    private Integer captureDifficulty, remainingCaptureTime;
    private Double attackBonus, defenseBonus;
    private Player owner;    
    private Boolean goalBuilding;

    public Building(String name, Integer captureDifficulty, Double attackBonus,
		    Double defenseBonus, Boolean goalBuilding) {
	this.buildingName = name;

	this.captureDifficulty = captureDifficulty;
	this.remainingCaptureTime = this.captureDifficulty;

	this.attackBonus = attackBonus;
	this.defenseBonus = defenseBonus;

	this.goalBuilding = goalBuilding;
    }

    public String getName() { 
	return this.buildingName;
    }

    public Integer getCaptureDifficulty() { 
	return this.captureDifficulty;
    }

    public Integer getRemainingCaptureTime() {
	return this.remainingCaptureTime;
    }

    public Double getAttackBonus() {
	return this.attackBonus;
    }

    public Double getDefenseBonus() {
	return this.defenseBonus;
    }

    public Boolean hasOwner() {
	return this.owner != null;
    }

    public Player getOwner() {
	return this.owner;
    }

    public void setOwner(Player player) {
	this.owner = player;
    }

    public Boolean isGoalBuilding() {
	return this.goalBuilding;
    }

    public void reduceCaptureTime(Integer captureAmount) {
	this.remainingCaptureTime -= captureAmount;

	if (this.remainingCaptureTime < 0)
	    this.remainingCaptureTime = 0;
    }

    public void resetCaptureTime() {
	this.remainingCaptureTime = this.captureDifficulty;
    }
