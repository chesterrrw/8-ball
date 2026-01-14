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
    }
    public void calcAngle(Ball b, double x, double y){
        directionVector.setDirection(x - (b.getX() + Main.offsetX), y - (b.getY() + Main.offsetY));
        vectorLine = new Line((int) b.getX(), (int) b.getY(), (int) (b.getX()+directionVector.getX()*5000), (int)(b.getY()+directionVector.getY()*5000));
    }
    public double getAngle(){
        return directionVector.getDirection();
    }
    public void setPullBack(double pullBack){
        if (pullBack < 0) this.pullBack = 0;
        else this.pullBack = pullBack *0.5;
        powerPercent = Math.min(1, pullBack / maxPull);
    }
    public double getPullBack(){
        return pullBack;
    }
    public void striking(){
        pullBack -= maxPull * powerPercent * 0.05;
        if (pullBack <= 0) Main.shotState = 3;
    }
    public double getDirection(){
        return directionVector.getDirection() - Math.PI;
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
}
