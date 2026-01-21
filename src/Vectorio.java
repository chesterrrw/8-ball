//Principle class for performing vector math. Each Vectorio object represents a standard 2-dimensional mathematical
//vector, having a magnitude and a direction.

public class Vectorio {//Cannot be named "Vector" since a Vector class already exists

    private double magnitude;
    private double direction;
    private double x;
    private double y;

    //Constructs a new Vectorio given a magnitude and direction, and calculates the corresponding x and y components
    //using sine and cosine. The flag variable exists to differentiate this constructor with the next one below
    //Parameters: magnitude of vector, direction in radians, flag explained above
    public Vectorio (double magnitude, double direction, boolean flag){
        this.magnitude = magnitude;
        this.direction = direction;
        x = magnitude * Math.cos(direction);
        y = magnitude * Math.sin(direction);
    }
    //Constructs a new Vectorio given x and y components, and calculates the corresponding magnitude and direction
    //using Pythagorean theorem and inverse tangent respectively
    public Vectorio (double x, double y){
        this.x = x;
        this.y = y;
        magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));//Pythagorean theorem
        direction = Math.atan2(y, x);
    }
    //Takes dot product of two vectors. Parameters: two Vectorio objects. Return: double
    public static double dotProduct(Vectorio v1, Vectorio v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }
    //Adds two vectors. Parameters: two Vectorio objects. Return: Added vectors as a new Vectorio
    public static Vectorio addition (Vectorio v1, Vectorio v2){
        return new Vectorio(v1.x + v2.x, v1.y + v2.y);
    }
    //Subtracts two vectors. Parameters: two Vectorio objects. Return: Subtracted vectors as a new Vectorio
    public static Vectorio subtraction (Vectorio v1, Vectorio v2){
        return new Vectorio(v1.x - v2.x, v1.y - v2.y);
    }
    //Scale the magnitude of the vector by the scaleFactor. Update xy components using sine and cosine
    public void scale (double scaleFactor){
        magnitude *= scaleFactor;
        x = magnitude * Math.cos(direction);
        y = magnitude * Math.sin(direction);
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getDirection(){
        return direction;
    }
    public void setDirection(double direction){
        this.direction = direction;
        //If changing direction, need to use sine and cosine to redo directions
        x = magnitude * Math.cos(direction);
        y = magnitude * Math.sin(direction);
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
