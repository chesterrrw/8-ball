public class Line {

    private UnitVector directionVector;
    private UnitVector normal;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private static final double elasticity = 0.9;

    public Line (int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        directionVector = new UnitVector((x2 - x1), (y2 - y1));
        normal = new UnitVector(directionVector.getY(), -directionVector.getX());
    }
    public double pointDistance (double x, double y, boolean checkEnds){
        //If the point is beyond the ends of the line, return big number so collision isn't triggered
        //Centre of circle not being within the ends of the line doesn't mean the closest point on the circumference isn't
        //Add and subtract (since there are two possible normal vectors) the radius of the ball in the normal direction to get position of the closest point
        if (checkEnds && ((x + normal.getX() * 14 < Math.min(x1, x2) && x - normal.getX() * 14 < Math.min(x1, x2)) ||
                (x + normal.getX() * 14 > Math.max(x1, x2) && x - normal.getX() * 14 > Math.max(x1, x2)) ||
                (y + normal.getY() * 14 < Math.min(y1, y2) && y - normal.getY() * 14 < Math.min(y1, y2)) ||
                (y + normal.getY() * 14 > Math.max(y1, y2) && y - normal.getY() * 14 > Math.max(y1, y2))))
                return 100;
        return Math.abs(Vectorio.dotProduct(new Vectorio(x - x1, y - y1), normal));//Since normal is already a unit vector
    }
    public boolean collision (Ball b){
        //if (pointDistance (b.getX(), b.getY()) > pointDistance(b.getX() + b.getVelocity().getX()*0.01*Main.physicsFreq, b.getY() + b.getVelocity().getY()*0.01*Main.physicsFreq)) return false;
        if (Math.sqrt(Math.pow((b.lastCollisionX - b.getX()), 2) + Math.pow((b.lastCollisionY - b.getY()), 2)) < 14) return true;
        if (pointDistance (b.getX(), b.getY(), true) < 16){
            while (pointDistance(b.getX(), b.getY(), true) < 14){
                b.changeX(b.getVelocity().getX() * -0.1 * Main.physicsFreq);
                b.changeY(b.getVelocity().getY() * -0.1 * Main.physicsFreq);
                normal = new UnitVector(directionVector.getY(), -directionVector.getX());
                //System.out.println(b.getX());
            }
            double difference;
            if (Math.abs(normal.getDirection() - b.getVelocity().getDirection()) > Math.PI/2.0)
                normal = new UnitVector((normal.getDirection() - Math.PI));
            difference = normal.getDirection() - b.getVelocity().getDirection();
            b.setVelocity(new Vectorio(b.getVelocity().getMagnitude()*elasticity, normal.getDirection() - Math.PI + difference, true));
            //System.out.println(b.getVelocity());
            b.lastCollisionX = b.getX();
            b.lastCollisionY = b.getY();
            return true;
        }
        return false;
    }
    public double getX1 (){return x1;}
    public double getX2 (){return x2;}
    public double getY1 (){return y1;}
    public double getY2 (){return y2;}
}
