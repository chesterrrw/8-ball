//Represents a mathematical line in 2D space

public class Line {

    private UnitVector directionVector;
    private UnitVector normal;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private static final double elasticity = 0.9;

    //Constructs a line going from (x1, y1) to (x2, y2), and defines direction and normal vectors
    //Parameters: x1, y1, x2, y2
    public Line (int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        //Note how these are both unit vectors, so they automatically are given lengths 1
        directionVector = new UnitVector((x2 - x1), (y2 - y1));
        normal = new UnitVector(directionVector.getY(), -directionVector.getX());
    }
    //Returns the shortest distance between the point (x, y) and the line. If checkEnds is true, it checks if the
    //intersection point is outside the ends of the line. If false, it assumes the line extends infinitely
    //Parameters: point (x, y), checkEnds variable
    public double pointDistance (double x, double y, boolean checkEnds){
        //If the point is beyond the ends of the line, return big number so collision isn't triggered
        //Centre of circle not being within the ends of the line doesn't mean the closest point on the circumference isn't
        //Add and subtract (since there are two possible normal vectors) the radius of the ball in the normal direction to get position of the closest point
        if (checkEnds && ((x + normal.getX() * 14 < Math.min(x1, x2) && x - normal.getX() * 14 < Math.min(x1, x2)) ||
                (x + normal.getX() * 14 > Math.max(x1, x2) && x - normal.getX() * 14 > Math.max(x1, x2)) ||
                (y + normal.getY() * 14 < Math.min(y1, y2) && y - normal.getY() * 14 < Math.min(y1, y2)) ||
                (y + normal.getY() * 14 > Math.max(y1, y2) && y - normal.getY() * 14 > Math.max(y1, y2))))
                return 100;
        //Formula is much simpler since normal is already of length 1. Just use dot product projection
        return Math.abs(Vectorio.dotProduct(new Vectorio(x - x1, y - y1), normal));
    }
    //Checks for and handles ball collision with line. Parameters: Ball b. Return: boolean (if collision occurred or not)
    public boolean collision (Ball b){
        //Use pointDistance to check if the ball touches the wall. Using 18 to ensure the collision is counted even for high speed balls
        if (pointDistance (b.getX(), b.getY(), true) < 18){
            //The ball must have moved at least one radius (14 pixels) from the last collision to this collision.
            //If not, no collision is counted.
            if (Math.sqrt(Math.pow((b.lastCollisionX - b.getX()), 2) + Math.pow((b.lastCollisionY - b.getY()), 2)) < 14){
                //System.out.println("problem");
                return true;
            }
            int counter = 0;
            //If the distance is greater than the radius, this means the ball is moving slowly and the collision was
            // detected before the ball arrived at the wall. Walk the ball FORWARD. counter to prevent infinite loop
            // if actually the ball overshot significantly
            while (pointDistance(b.getX(), b.getY(), false) > Ball.radius && counter < 1000){
                b.changeX(b.getVelocity().getX() * 0.1 * Main.physicsFreq);
                b.changeY(b.getVelocity().getY() * 0.1 * Main.physicsFreq);
                normal = new UnitVector(directionVector.getY(), -directionVector.getX());
                //System.out.println("broke1");
                counter++;
            }
            //If the distance is less than the radius, the ball overshot. Walk the ball BACKWARDS until this is not the case.
            //Go slowly (0.02x speed) since the ball must be moving relatively quickly to have overshot
            //Use 12 as the threshold to allow for some buffer and prevent the ball from reversing all the way out
            while (pointDistance(b.getX(), b.getY(), true) < 12){
                b.changeX(b.getVelocity().getX() * -0.02 * Main.physicsFreq);
                b.changeY(b.getVelocity().getY() * -0.02 * Main.physicsFreq);
                normal = new UnitVector(directionVector.getY(), -directionVector.getX());
                //System.out.println("broke2");
            }
            double x = b.getX();
            double y = b.getY();
            //If after backing up, the ball cannot be moved along the normal to get a valid collision, this indicates
            //a corner collision. This is a special case that must be handled separately
            if ((x + normal.getX() * 14 < Math.min(x1, x2) && x - normal.getX() * 14 < Math.min(x1, x2)) ||
                    (x + normal.getX() * 14 > Math.max(x1, x2) && x - normal.getX() * 14 > Math.max(x1, x2)) ||
                    (y + normal.getY() * 14 < Math.min(y1, y2) && y - normal.getY() * 14 < Math.min(y1, y2)) ||
                    (y + normal.getY() * 14 > Math.max(y1, y2) && y - normal.getY() * 14 > Math.max(y1, y2))){
                //If corner collision, the ball is on either end of the line. Use the closest end (since it can only
                //touch one corner at a time: the closest one). Set the velocity to the reverse of the direction towards the corner
                if (Math.pow(x - x1, 2) + Math.pow(y - y1, 2) < Math.pow(x - x2, 2) + Math.pow(y - y2, 2))
                    b.setVelocity(new Vectorio(b.getVelocity().getMagnitude()*elasticity, Math.atan2(y - y1, x - x1), true));
                else
                    b.setVelocity(new Vectorio(b.getVelocity().getMagnitude()*elasticity, Math.atan2(y - y2, x - x2), true));
                //System.out.println("here");
            }
            else {//Normal wall collision
                double difference;
                //Fix normal vector direction
                if (Math.abs(normal.getDirection() - b.getVelocity().getDirection()) > Math.PI / 2.0)
                    normal = new UnitVector((normal.getDirection() - Math.PI));
                //Simple reflection using difference between normal direction and velocity direction
                difference = normal.getDirection() - b.getVelocity().getDirection();
                b.setVelocity(new Vectorio(b.getVelocity().getMagnitude() * elasticity, normal.getDirection() - Math.PI + difference, true));
                System.out.println("normal");
            }
            //Update last collision coordinates
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
