public class UnitVector extends Vectorio{

    public UnitVector(double direction){
        super(1, direction, true);
    }
    public UnitVector(double x, double y){
        super(1, Math.atan2(y, x), true);
    }
}
