package main.view;
import javafx.scene.CacheHint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import playground.Coordinate;
import main.model.Read;

/**
 * Class that represents a single Read visually. For circular viewing purposes this class contains 2 Lines and 2 Arcs.
 * The functionality of this class contain the creation and receiving the visual Elements, aswell as the option for the parent class to update the Height of the Element (For Zooming Purposes)
 * @author Felix
 */
public class ArchSegment {

    /**
     * The Visual Elements, self explanatory
     */
    private Line start = new Line();
    private Line stop = new Line();
    private Arc outer = new Arc();
    private Arc inner = new Arc();

    /**
     * Information that is stored after the ArchSegment has been initialised, this is needed to update the height later on.
     */
    private Color archColor = Color.BLACK;
    private boolean isGapcloser,isReversed;
    private int alignmentStart, alignmentEnd,readLength,level;
    private GlobalInformation info;

    /**
     * helper function to get the proper ReadLength of a gapClosing read. TODO: Delete if read.java has a working implementation of getLength
     */
    private void calculateReadLengthOfCircularRead(){
        if(isGapcloser){
            readLength = alignmentEnd+ ((int)info.getGlobalLength()-alignmentStart);
        }
        else{
            readLength = alignmentEnd-alignmentStart;
        }
    }

    /**
     * helper function to determine our standard color of the Read. (Gapclosers => RED, Reversed Reads => BLUE, else => BLACK(
     */
    private void determinteStandardColor(){
        if(isGapcloser){
            archColor = Color.RED;
        }
        if(isReversed){
            archColor = Color.BLUE;
        }
    }

    /**
     * Function to update / set the Stroke Width. In the current Test this is bound to the Height of an element and thus gets updated when ZOOM happens
     * @param newValue the new Strokewidth that the archSegment is to be set at. (Currently divided by 10)
     */
    public void setStrokeWidth(double newValue){
        double width = newValue/10;
        start.setStrokeWidth(width);
        stop.setStrokeWidth(width);
        inner.setStrokeWidth(width);
        outer.setStrokeWidth(width);
    }

    /**
     * Function to update/set the Stroke Color of the ArchSegment. Implemented for future usage when the user can custom-design their desired Colors for the 3 cases (gapClosing,isReversed,else)
     * @param color the Color the archsegment is to be set
     */
    public void setColor(Color color){
        start.setStroke(color);
        stop.setStroke(color);
        inner.setStroke(color);
        outer.setStroke(color);
    }

    /**
     * Used in the constructor to create the first visual elements, and further later used on ZOOMing(and thus increasing/decreasing the height of the elements).
     * @param height height of the Element.
     */
    public void updateHeight(double height){
        //calculate the effective radius based on height,level,radius
        double effradius = info.getRadius()+ level*height + height/2;
        //do Math to create Points A,B,C,D.
        //Convention: if direction is true (clockwise) the created ArchSegment looks like this:
        //              B-  -   -   _   _
        //            /                      -    -  D
        //           A  -      -    _               /
        //                               -     -  C
        // if direction is false Line CD is left from AD
/*
        Coordinate A = new Coordinate(effradius*Math.cos(Math.toRadians((alignmentStart/info.getGlobalLength())*360)),effradius*Math.sin(Math.toRadians((alignmentStart/info.getGlobalLength())*360)));
        Coordinate B = new Coordinate((effradius+info.getHeight())*Math.cos(Math.toRadians((alignmentStart/info.getGlobalLength())*360)),(effradius+info.getHeight())*Math.sin(Math.toRadians((alignmentStart/info.getGlobalLength())*360)));
        Coordinate C = new Coordinate(effradius*Math.cos(Math.toRadians(((alignmentStart+readLength)/info.getGlobalLength())*360)),effradius * (Math.sin(Math.toRadians(((alignmentStart+readLength)/info.getGlobalLength())*360))));
        Coordinate D = new Coordinate((effradius+info.getHeight())*Math.cos(Math.toRadians(((alignmentStart+readLength)/info.getGlobalLength())*360)),(effradius+info.getHeight()) * (Math.sin(Math.toRadians(((alignmentStart+readLength)/info.getGlobalLength())*360))));

        // Now Add the Center to the Coordinates
        A.add(info.getCenter());
        B.add(info.getCenter());
        C.add(info.getCenter());
        D.add(info.getCenter());

        // All 4 Coordinates are finished, time to setup the Lines+Arcs
        // NOTE: ARC Degrees work counterclockwise (rising) -> start needs to be the "stop" and 90° = 15k label


        start.setStartX(A.getX());
        start.setStartY(A.getY());
        start.setEndX(B.getX());
        start.setEndY(B.getY());
        stop.setStartX(C.getX());
        stop.setStartY(C.getY());
        stop.setEndX(D.getX());
        stop.setEndY(D.getY());

        start.setStroke(archColor);
        stop.setStroke(archColor);

        outer.setCenterX(info.getCenter().getX());
        outer.setCenterY(info.getCenter().getY());
        outer.setRadiusX(effradius+info.getHeight());
        outer.setRadiusY(effradius+info.getHeight());
        outer.setStartAngle(((info.getGlobalLength()-alignmentStart)/info.getGlobalLength())*360);
        outer.setLength((readLength/info.getGlobalLength())*-360);
        outer.setStroke(archColor);
        outer.setFill(Color.TRANSPARENT);*/

        inner.setCenterX(info.getCenter().getX());
        inner.setCenterY(info.getCenter().getY());
        inner.setRadiusX(effradius);
        inner.setRadiusY(effradius);
        inner.setStartAngle(((info.getGlobalLength()-alignmentStart)/info.getGlobalLength())*360);
        inner.setLength((readLength/info.getGlobalLength())*-360);
        inner.setStroke(archColor);
        inner.setFill(Color.TRANSPARENT);
        inner.setStrokeWidth(height);
        inner.setCacheHint(CacheHint.SPEED);


    }

    /**
     * Constructor of the Archsegment, receiving (read+global) information, aswell as a Level where the Element in going to get placed
     * Functionality is to set the fields of the Archsegment with values, then update the Height (and thus the 2 Lines + 2 Arcs)
     * setStrokeWidth is set as 3 as a standard TODO: find a good value (propably needs to be changed dynamically aswell).
     * @param read given Read that is going to be visually represented by this ArchSegment
     * @param info given gInfo that represents the information available from the reference + general drawing Information
     * @param level given Level at which the Segment is placed in the Ring.
     */
    public ArchSegment(Read read, GlobalInformation info, int level){
        this.info = info;
        this.isGapcloser=read.isCrossBorder();
        this.isReversed=read.getNegativeStrandFlag();
        this.alignmentStart =read.getAlignmentStart(); //TODO: Check if thos need both to be decremented by 1 or not
        this.alignmentEnd = read.getAlignmentEnd();
        this.level = level;
        calculateReadLengthOfCircularRead();
        determinteStandardColor();
        updateHeight(info.getHeight());
        //setStrokeWidth(2);

    }

    /**
     * Getter for the Inner ARC
     * @return returns the Inner Arc of the ArchSegment
     */
    public Arc getInner() {
        return inner;
    }

    /**
     * Getter for the Outer Arc
     * @return returns the Outer Arc of the ArchSegment
     */
    public Arc getOuter() {
        return outer;
    }

    /**
     * Getter for the first Line(at read.start (regardless of circular or not)
     * @return returns the first Line, regardless of isCircular is always located at Read.AlignmentStart
     */
    public Line getStart() {
        return start;
    }

    /**
     * Getter for the second Line(at read.end (regardless of circular or not)
     * @return returns the second Line, regardless of isCircular is always located at Read.AlignmentEnd
     */
    public Line getStop() {
        return stop;
    }
}
