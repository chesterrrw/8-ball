import java.util.*;

public class Ball {

    private int ID;
    private double x;
    private double y;
    private int colour;
    private Vectorio velocity;
    private static final int radius = 14;
    private static final double friction = -0.015;
    private static final double frictionS = 0.9975;

    public double lastCollisionX;
    public double lastCollisionY;

    public static HashSet<Collision> collisions = new HashSet<Collision>();

    public Ball(int ID, double x, double y, int colour){
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.colour = colour;
        velocity = new Vectorio(0, 0, true);
    }
    public boolean equals(Object o){
        Ball b = (Ball) o;
        return (b.getID() == ID);
    }
    public void checkCollision(Ball b){
        double distance = Math.sqrt(Math.pow(x - b.x, 2) + Math.pow(y - b.y, 2));
        //Within range and not the same ball
        if (distance <= radius*2.03 && !this.equals(b)){
            //Check collision course
            if (Vectorio.dotProduct(new Vectorio(b.x - x, b.y - y), Vectorio.subtraction(b.velocity, velocity)) < 0.0) {
                //If balls glitching together, ie distance between centers is less than diameter, "walk" backwards slowly until the balls are no longer touching
                while (distance < radius * 2) {
                    x -= velocity.getX() * 0.05 * Main.physicsFreq;
                    y -= velocity.getY() * 0.05 * Main.physicsFreq;
                    b.x -= b.velocity.getX() * 0.05 * Main.physicsFreq;
                    b.y -= b.velocity.getY() * 0.05 * Main.physicsFreq;
                    distance = Math.sqrt(Math.pow(x - b.x, 2) + Math.pow(y - b.y, 2));
                }
                collisions.add(new Collision(this, b));
            }
        }
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
    public Vectorio getVelocity(){
        return velocity;
    }
    public void setVelocity(Vectorio v){
        velocity = v;
    }
    public void move(){
        x += velocity.getX() * Main.physicsFreq;
        y += velocity.getY() * Main.physicsFreq;
        /*
        if (velocity.getMagnitude() >= friction*(-1))
            velocity.changeMag(friction);
        else
            velocity.setMagnitude(0.0);

         */
        velocity.scale(frictionS);//Approximate friction with exponential decay
        if (velocity.getMagnitude() < 5) velocity.setMagnitude(0.0);//Dead stop since exponential decay will never reach 0
    }
}
