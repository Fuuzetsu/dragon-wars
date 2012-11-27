//package wars.dragon.engine;

public class Dragon extends Unit implements RangedUnit {

    public Dragon() {
	super("Dragon", 10.0, 5, 3.0, 3.0, 2.0);
    }

    public Double getMaxRange() {
	return 5.0;
    }

    public Double getMinRange() {
	return 2.0;
    }


}
