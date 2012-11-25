package wars.dragon.engine;

public class Map {

    List< List<GameField> > fields;
    
    public Map(List< List<GameField> > fields) {
	this.fields = fields;
    }
    
    public Boolean isInstantiated() {
	return fields =! null;
    }
    
    public GameField getField(Position position) {
	return getField(position.getX(), position.getY());
    }
    
    public GameField getField(Integer x, Integer y) {
	return fields.get(x).get(y);
    }
}
