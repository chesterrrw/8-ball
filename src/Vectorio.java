public class Vectorio {//Cannot be named "Vector" since a Vector class already exists

    private double magnitude;
    private double direction;
    private double x;
    private double y;

    public Vectorio (double magnitude, double direction, boolean flag){
        //Seemingly useless flag variable exists so two constructors with same parameter type can exist
        this.magnitude = magnitude;
        this.direction = direction;
        x = magnitude * Math.cos(direction);
        y = magnitude * Math.sin(direction);
    }
    public Vectorio (double x, double y){
        this.x = x;
        this.y = y;
        magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));//Pythagorean theorem
        direction = Math.atan2(y, x);
    }
    public static double dotProduct(Vectorio v1, Vectorio v2){
        return v1.x * v2.x + v1.y * v2.y;
    }
    public static Vectorio addition (Vectorio v1, Vectorio v2){
        return new Vectorio(v1.x + v2.x, v1.y + v2.y);
    }
    public static Vectorio subtraction (Vectorio v1, Vectorio v2){
        return new Vectorio(v1.x - v2.x, v1.y - v2.y);
    }
    public void scale (double scaleFactor){
        magnitude *= scaleFactor;
        x = magnitude * Math.cos(direction);
        y = magnitude * Math.sin(direction);
    }
    public void changeMag (double change){
        magnitude += change;
        x = magnitude * Math.cos(direction);
        y = magnitude * Math.sin(direction);
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getMagnitude(){
        return magnitude;
    }
    public void setMagnitude(double mag){
        magnitude = mag;
        x = 0;
        y = 0;
    }
    public String toString(){
        return "(" + x + ", " + y + ")" + "\naka M: " + magnitude + ", D: " + direction;
    }
}
