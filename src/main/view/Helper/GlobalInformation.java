package main.view.Helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import main.model.CircularParser;

/**
 * class containg general information that is needed to visual Elements, create objects.
 * WIP: rework the concept of the globalInformation (given a referenceLength = globalLength) in the constructor we set the radius,center, etc
 * @author Felix
 */
public class GlobalInformation {
    private Coordinate center;
    private double radius;
    public DoubleProperty height;
    private int referenceLength;
    private double rotation = 0.0;


    /**
     * Different Approach:
     * since currently a gInfo is only created after we´ve parsed an object we can move the hard-coded elements (radius,height,center) here, and grab the referenceLength from the Parser
     * whenever a second File is parsed we simply have to update the refernceLength, the rest is as usual (implemented but not used - whenever a 2nd parse happens we create new gInfo+CircularVIew
     */
    public GlobalInformation(){
        this.center =new Coordinate(800,500); //TODO: change dynamically depending on parent. (When fxml has proper values)
        this.radius = 100;
        this.height = new SimpleDoubleProperty(2.5);
        this.referenceLength = CircularParser.ReferenceSequences.Current.getLength();
    }
    public void updateReferenceLength(){
        this.referenceLength = CircularParser.ReferenceSequences.Current.getLength();
    }

    //GETTER
    public Coordinate getCenter(){
        return center;
    }

    public int getReferenceLength() {
        return referenceLength;
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

    public void setReferenceLength(int referenceLength) {
        this.referenceLength = referenceLength;
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
