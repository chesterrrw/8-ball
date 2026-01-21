//Special kind of vector with a length of one

public class UnitVector extends Vectorio{
    //Constructs a unit vector with just a direction. Parameters: direction in radians
    public UnitVector(double direction){
        super(1, direction, true);
    }
    //Constructs a unit vector with x and y components. Automatically normalizes (make length 1) with arctangent.
    //Parameters: x y components
    public UnitVector(double x, double y){
        super(1, Math.atan2(y, x), true);
    }
    public void setDirection(double x, double y){
        super.setDirection(Math.atan2(y, x));//Normalize
    }
}
