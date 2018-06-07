package playground;

public class Coordinate {

    //simple Coordinate class, to ease math + giving arguments

    private double x;
    private double y;
    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    //SETTER
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    //GETTER
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    //Function to Add 2 Coordinates
    public void add(Coordinate B){
        this.x += B.getX();
        this.y += B.getY();
    }
}
