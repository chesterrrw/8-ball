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
        return Math.abs(Vectorio.dotProduct(new Vectorio(x - x1, y - y1), normal));//Since normal is already a unit vector
    }
    public void collision (Ball b){
        if (pointDistance (b.getX(), b.getY()) < 10){
            double difference;
            if (Math.abs(normal.getDirection() - b.getVelocity().getDirection()) > Math.PI/2.0)
                normal = new UnitVector((normal.getDirection() - Math.PI));
            difference = normal.getDirection() - b.getVelocity().getDirection();
            if (difference > 0.0)
                b.setVelocity(new Vectorio(b.getVelocity().getMagnitude(), normal.getDirection() - Math.PI + difference, true));
            else
                b.setVelocity(new Vectorio(b.getVelocity().getMagnitude(), normal.getDirection() + Math.PI + difference, true));
        }
    }
    public double getX1 (){return x1;}
    public double getX2 (){return x2;}
    public double getY1 (){return y1;}
    public double getY2 (){return y2;}
}
