package main.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import playground.Coordinate;

/**
 * class containg general information that is needed to visual Elements, create objects.
 * WIP: rework the concept of the globalInformation (given a referenceLength = globalLength) in the constructor we set the radius,center, etc
 */
public class GlobalInformation {
    private Coordinate center;
    private double radius;
    public DoubleProperty height= new SimpleDoubleProperty();
    private double globalLength;
    private double rotation = 0.0; //TODO: Currently rotation for the Scroll-calculation is in Globalinformation. This is only used for the main so it should go back to as a global variable?



    //TODO: Constructor right now needs a lot of inputs, that can be easily calculated from a given referenceLength(global Length). Can´t be changed right now because Standard Zoom is still WIP
    //CONSTRUCTOR
    public GlobalInformation(Coordinate center, double radius, double height, double globalLength){
        this.center = center;
        this.radius=radius;
        this.height.setValue(height);
        this.globalLength = globalLength;
    }

    /**
     * Different approach of a constructor - given a referenceLength it is dynamically decided what are good choices for  a radius, thus the center coordinates, the height of the segments (and strokewidth),
     * @param referenceLength given referenceLength of a parsed File
     */
    public GlobalInformation(double referenceLength){

    }

    //GETTER
    public Coordinate getCenter(){
        return center;
    }

    public double getGlobalLength() {
        return globalLength;
    }

    public double getHeight() {
        return height.getValue();
    }

    public double getRadius() {
        return radius;
    }

    public double getRotation() {
        return rotation;
    }


    //SETTER

    public void setCenter(Coordinate center) {
        this.center = center;
    }

    public void setGlobalLength(double globalLength) {
        this.globalLength = globalLength;
    }

    public void setHeight(double height) {
        this.height.setValue(height);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

}
