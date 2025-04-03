// Creating a class called Vector class to open a basic window,
// and move the pacman character around the screen.
// It's sort of a grid situation to get us started. 

import java.lang.Math;
import java.time.Duration;
import java.time.Instant;


public class Vector {
    // x and y represent the vector's coordinates
    private double x;
    private double y;
    private final double thresh = 0.000001; // small threshold to handle floating-point precision issues
    private Instant lastTime;

    // default constructor initializes vector at (0,0)
    public Vector(){
        this.x = 0;
        this.y = 0;
        this.lastTime = Instant.now();

    }

    // constructor with parameters to set x and y values
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // getters and setters for x, y, and threshold
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY(){
        return y;
    }
    public void setY(double y){
        this.y = y;
    }
    public double getThresh(){
        return thresh;
    }

    // adds two vectors component-wise
    public Vector add(Vector other){
        return new Vector(this.x + other.x, this.y + other.y); // fixed misplaced '+'
    }

    // subtracts another vector from this one
    public Vector subtract(Vector other){
        return new Vector(this.x - other.x, this.y - other.y);
    }

    // negates both components of the vector
    public Vector negate(){
        return new Vector(-this.x, -this.y);
    }

    // multiplies the vector by a scalar
    public Vector multiply(double scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    // divides the vector by a scalar, avoiding division by zero
    public Vector divide(double scalar){
        if (Math.abs(scalar) > thresh) { // only divide if scalar is nonzero
            return new Vector(this.x / scalar, this.y / scalar);
        }
        return null; // return null to indicate invalid operation
    }

    // checks if two vectors are approximately equal within the threshold
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Vector other = (Vector) obj;

        return Math.abs(this.x - other.x) < this.thresh && Math.abs(this.y - other.y) < this.thresh;
    }

    // returns the squared magnitude of the vector (avoids unnecessary square root computation)
    public double magnitudeSquared() {
        return x * x + y * y;
    }

    // returns the magnitude (length) of the vector
    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    // creates and returns a copy of this vector
    public Vector copy() {
        return new Vector(this.x, this.y); // makes it easier to have an instance of it, so we don't have to modify the origianl
    }

    // returns the vector as an array of doubles
    public double[] asTuple() {
        return new double[]{x, y};
    }

    // returns the vector as an array of integers (casting components)
    public int[] asInt() {
        return new int[]{(int) x, (int) y};
    }

    // converts the vector to a string representation so that we can print it out
    @Override
    public String toString() {
        return "<" + x + ", " + y + ">";
    }


}