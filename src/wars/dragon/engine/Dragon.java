package wars.dragon.engine;

public class Dragon extends Unit implements RangedUnit {
    private String name = "Dragon";

    public Soldier() {
	super(10, 5, 3, 3, 2);
    }

    public Double getRange() {
	return 5;
    }
