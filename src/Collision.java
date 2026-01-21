//Represents collision with two balls.

public class Collision {

    private UnitVector normal;
    private Ball b1;
    private Ball b2;
    private int frames;
    private static long lastClack = 50;

    //Creates a Collision object, defining the normal vector and setting frames to 0. Parameters: Two Balls
    public Collision(Ball b1, Ball b2){
        this.b1 = b1;
        this.b2 = b2;
        normal = new UnitVector(b2.getX() - b1.getX(), b2.getY() - b1.getY());
        frames = 0;
    }
    //Checks if two collision objects are the same by comparing ball IDs.
    public boolean equals(Object o){
        Collision c = (Collision) o;
        //Ball 1 and ball 2 colliding is the same as ball 2 and ball 1 colliding. Thus, check both orders.
        if (b1.getID() == c.b1.getID() && b2.getID() == c.b2.getID()) return true;
        return b2.getID() == c.b1.getID() && b1.getID() == c.b2.getID();
    }
    //Rudimentary HashCode function. Parameters: none. Return: hashcode
    public int hashCode(){
        return b1.getID()*b2.getID();
    }
    //Handles the collision with vector math. Gives new velocities to both balls in the collision.
    //No parameters or return
    public void handle(){
        //Ball 1 is stationary in this frame of reference
        //Change velocity of ball 2 to the frame of ball 1 using vector subtraction
        Vectorio v2 = Vectorio.subtraction(b2.getVelocity(), b1.getVelocity());
        //Use dot product to find normal component of v2 in Ball 1 reference frame
        Vectorio v2n = new Vectorio(normal.getX()*Vectorio.dotProduct(v2, normal), normal.getY()*Vectorio.dotProduct(v2, normal));
        //Since a vector in 2D is equal to the normal + tangential components, subtract v2n from v2 to get
        //the tangential component of v2 in Ball 1 reference frame
        Vectorio v2t = Vectorio.subtraction(v2, v2n);
        //With one ball stationary, normal and tangential components swap.
        //In Ball 1 reference frame, v2n becomes v1, and v2t becomes v2
        //Add these to b1 velocity to get velocities in the original frame of reference
        Vectorio v1f = Vectorio.addition(b1.getVelocity(), v2n);
        Vectorio v2f = Vectorio.addition(b1.getVelocity(), v2t);
        //Set ball velocities to the new velocities
        b1.setVelocity(v1f);
        b2.setVelocity(v2f);
        //Clack sound
        if (Main.shotState == 0){//Only play clack when the physics is actually running (not when aiming)
            if (System.currentTimeMillis() - lastClack > 50) {//To minimize audio glitches, 50 ms delay between clacks
                Main.sounds.playClack(v2n.getMagnitude());
                lastClack = System.currentTimeMillis();
            }
        }
    }
    public void iterateFrame(){
        frames++;
    }
    public int getFrames(){
        return frames;
    }
    public String toString(){
        return "Ball " + b1.getID() + " and Ball " + b2.getID();
    }
}
