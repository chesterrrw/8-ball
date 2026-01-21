import java.util.*;

//Represents a ball on the table

public class Ball implements Comparable <Ball>{

    private int ID;
    private double x;
    private double y;
    private String colour;
    private Vectorio velocity;
    public static final int radius = 14;
    private static final double friction = -0.015;
    private static final double frictionS = 0.998;

    public double lastCollisionX;
    public double lastCollisionY;

    public static HashSet<Collision> collisions = new HashSet<Collision>();

    //Contructs a new Ball object. Parameters: ID, starting xy coordinates
    public Ball(int ID, double x, double y){
        this.ID = ID;
        this.x = x;
        this.y = y;
        if (ID < 8) colour = "blue";
        if (ID > 8 && ID != 16) colour = "red";
        if (ID == 16) colour = "cue";
        if (ID == 8) colour = "black";
        velocity = new Vectorio(0, 0, true);
        //Set lastCollisionX to big number so the first collision is always calculated
        lastCollisionX = 10000;
        lastCollisionY = 10000;
    }
    //Equals method. Parameter: Object o. Return: boolean equals return.
    public boolean equals(Object o){
        Ball b = (Ball) o;
        return (b.getID() == ID);
    }
    //Checks if two balls are colliding or not, and adds tries to add a new Collision object to the HashSet for handling
    public boolean checkCollision(Ball b){
        if (b.ID == this.ID) return false;//ball cannot collide with itself
        double distanceSqrd = squaredDistance(b.x, b.y);
        //Within range and not the same ball
        if (distanceSqrd <= Math.pow((radius*2.03), 2) && !this.equals(b)){
            //Check collision course
            if (Vectorio.dotProduct(new Vectorio(b.x - x, b.y - y), Vectorio.subtraction(b.velocity, velocity)) < 0.0) {
                //Overshoot correction. If balls glitching together, ie distance between centers is less than
                // diameter, "walk" backwards slowly until the balls are no longer touching
                while (distanceSqrd < Math.pow((radius*2), 2)) {
                    x -= velocity.getX() * 0.05 * Main.physicsFreq;
                    y -= velocity.getY() * 0.05 * Main.physicsFreq;
                    b.x -= b.velocity.getX() * 0.05 * Main.physicsFreq;
                    b.y -= b.velocity.getY() * 0.05 * Main.physicsFreq;
                    distanceSqrd = squaredDistance(b.x, b.y);//Update distance
                }
                collisions.add(new Collision(this, b));//Add new collision object
                return true;
            }
        }
        return false;
    }
    //Calculates the squared distance between the ball and a point. Not the actual distance since square
    // root is computationally expensive. Parameters: xy coordinates. Return: distance squared
    public double squaredDistance(double x, double y){
        return Math.pow((this.x - x), 2) + Math.pow((this.y - y), 2);//Pythagorean theorem
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public void changeX(double delta){
        x += delta;
    }
    public void changeY(double delta){
        y += delta;
    }
    public int getID(){
        return ID;
    }
    public String getColour(){
        return colour;
    }
    public Vectorio getVelocity(){
        return velocity;
    }
    public void setVelocity(Vectorio v){
        velocity = v;
    }
    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }
    //Moves the ball. Called once per frame. Parameters: none. Return: none
    public void move(){
        //velocity has units pixels/second, but since this method is called once per frame, we need pixels/frame.
        //Therefore, multiply velocity by Main.physicsFreq (1/300)
        x += velocity.getX() * Main.physicsFreq;
        y += velocity.getY() * Main.physicsFreq;
        velocity.scale(frictionS);//Approximate friction with exponential decay
        if (velocity.getMagnitude() < 4) velocity.setMagnitude(0.0);//Dead stop since exponential decay will never reach 0
    }
    //To sort by distance to the cue ball. Parameters: Ball b. Return: compareTo integer return.
    @Override
    public int compareTo(Ball b) {
        double thisDistance = Math.pow(x - Main.cueBall.x, 2) + Math.pow(y - Main.cueBall.y, 2);
        double thatDistance = Math.pow(b.x - Main.cueBall.x, 2) + Math.pow(b.y - Main.cueBall.y, 2);
        return (int) (thisDistance - thatDistance);
    }
}
