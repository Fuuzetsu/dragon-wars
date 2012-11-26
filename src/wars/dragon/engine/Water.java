package wars.dragon.engine;

/* Test class */
public class Water extends GameField {
    
    public Water() {
	super("Water", 0.5);
    }

    public Boolean doesAcceptUnit(Unit unit) {
	return true;
    }
}
