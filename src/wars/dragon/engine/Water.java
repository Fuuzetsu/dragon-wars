//package wars.dragon.engine;

/* Test class */
public class Water extends GameField {
    
    public Water() {
	super("Water", 50.0);
    }

    public Boolean doesAcceptUnit(Unit unit) {
	return true;
    }
}
