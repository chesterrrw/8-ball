public class Collision {

    private UnitVector normal;
    private Ball b1;
    private Ball b2;
    private int frames;

    public Collision(Ball b1, Ball b2){
        this.b1 = b1;
        this.b2 = b2;
        normal = new UnitVector(b2.getX() - b1.getX(), b2.getY() - b1.getY());
        frames = 0;
    }
    public boolean equals(Object o){
        Collision c = (Collision) o;
        if (b1.getID() == c.b1.getID() && b2.getID() == c.b2.getID()) return true;
        return b2.getID() == c.b1.getID() && b1.getID() == c.b2.getID();
    }
    public int hashCode(){
        return b1.getID()*b2.getID();
    }
    public void handle(){
        //Ball 1 is stationary in this frame of reference
        //Change velocity of ball 2 to the frame of ball 1
        Vectorio v2 = Vectorio.subtraction(b2.getVelocity(), b1.getVelocity());
        //Normal component of v2 in Ball 1 reference frame
        Vectorio v2n = new Vectorio(normal.getX()*Vectorio.dotProduct(v2, normal), normal.getY()*Vectorio.dotProduct(v2, normal));
        //Tangential component of v2 in Ball 1 reference frame
        Vectorio v2t = Vectorio.subtraction(v2, v2n);
        //v2n becomes v1 in Ball 1 reference frame, and v2t becomes v2, and then add to b1 velocity in stationary frame
        Vectorio v1f = Vectorio.addition(b1.getVelocity(), v2n);
        Vectorio v2f = Vectorio.addition(b1.getVelocity(), v2t);
        b1.setVelocity(v1f);
        b2.setVelocity(v2f);
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
