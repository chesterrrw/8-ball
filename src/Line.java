public class Line {

    private UnitVector directionVector;
    private UnitVector normal;
    private double x1;
    private double x2;
    private double y1;
    private double y2;

    public Line (double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        directionVector = new UnitVector((x2 - x1), (y2 - y1));
        normal = new UnitVector(directionVector.getY(), -directionVector.getX());
    }

    public double pointDistance (double x, double y){
        return Math.abs(Vectorio.dotProduct(new Vectorio(x - x1, y - y1), normal));
    }
}
