public class Cue {
    private UnitVector directionVector;
    private Line vectorLine;
    private double pullBack;
    private boolean show;
    private double powerPercent;
    public static double maxPull = 300;
    public Cue (){
        directionVector = new UnitVector(0);
        pullBack = 0;
        show = true;
        vectorLine = new Line((int) Main.cueBall.getX(), (int) Main.cueBall.getY(),
                (int) (Main.cueBall.getX()+directionVector.getX()*-5000),
                (int) (Main.cueBall.getY()+directionVector.getY()*-5000));
        powerPercent = 0;
    }
    public void calcAngle(Ball b, double x, double y){
        UnitVector temp = new UnitVector(x - (b.getX() + Main.offsetX), y - (b.getY() + Main.offsetY));
        //Allow for control from both sides of the cue. If the new direction would turn the cue more than 90 degrees
        //(ie backwards in any way), this means the user wants to control from the other side. Detect this with dot product
        if (Vectorio.dotProduct(temp, directionVector) < 0){
            directionVector.setDirection(temp.getX()*-1, temp.getY()*-1);//Reverse direction
        }
        else
            directionVector = temp;//Normal aiming
        vectorLine = new Line((int) b.getX(), (int) b.getY(), (int) (b.getX()+directionVector.getX()*-5000), (int)(b.getY()+directionVector.getY()*-5000));
    }
    public double getAngle(){
        return directionVector.getDirection();
    }
    public void setPullBack(double pullBack){
        if (pullBack < 0) this.pullBack = 0;
        else this.pullBack = Math.min(pullBack *0.5, 150);
        powerPercent = Math.min(1, pullBack / maxPull);
    }
    public double getPullBack(){
        return pullBack;
    }
    public void striking(){
        pullBack -= maxPull * powerPercent * 0.05;
        if (pullBack <= 0) Main.shotState = 3;
    }
    public UnitVector getDirection(){
        return directionVector;
    }
    public double getPowerPercent(){
        return powerPercent;
    }
    public void setShow(boolean show){
        this.show = show;
    }
    public boolean getShow(){
        return show;
    }
    public Line getLine(){
        return vectorLine;
    }
}
