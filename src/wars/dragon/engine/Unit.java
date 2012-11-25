package wars.dragon.engine;

public abstract class Unit {

    private Integer health;
    private Double attack, meleeDefense, rangeDefense;
    private Position position;

    public Unit(Integer health, Double attack, 
		Double meleeDefense, Double rangeDefense) {
	this.health = health;
	this.attack = attack;
	this.meleeDefense = meleeDefense;
	this.rangeDefense = rangeDefense;
    }

    public Boolean isDead() {
	return (health <= 0);
    }

    public void setPosition(Position position) {
	this.position = position;
    }

    public Integer getHealth() { return this.health; }
    public Double getAttack() { return this.attack; }
    public Double getMeleeDefense() { return this.meleeDefense; }
    public Double getRangeDefense() { return this.rangeDefense; }
    public Position getPosition() { return this.position; }

}
    
