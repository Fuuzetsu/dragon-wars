//package wars.dragon.engine;

/* Test class */
public class Pit extends GameField {
    
    public Pit() {
	super("Pit", 1.0);
    }

    public Boolean doesAcceptUnit(Unit unit) {
	return false;
    }
}
