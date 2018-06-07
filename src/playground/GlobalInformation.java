package playground;

public class GlobalInformation {
    private Coordinate center;
    private double radius;
    private double height;
    private double globalLength;


    //CONSTRUCTOR
    public GlobalInformation(Coordinate center, double radius, double height, double globalLength){
        this.center = center;
        this.radius=radius;
        this.height=height;
        this.globalLength = globalLength;
    }

    //GETTER
    public Coordinate getCenter(){
        return center;
    }

    public double getGlobalLength() {
        return globalLength;
    }

    public double getHeight() {
        return height;
    }

    public double getRadius() {
        return radius;
    }

    //SETTER

    public void setCenter(Coordinate center) {
        this.center = center;
    }

    public void setGlobalLength(double globalLength) {
        this.globalLength = globalLength;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

}