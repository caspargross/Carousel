package main.view.CircularView;

import javafx.scene.CacheHint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import main.model.Read;
import main.view.Helper.GlobalInformation;

/**
 * Class that represents a single Read visually. For circular viewing purposes this class contains 2 Lines and 2 Arcs.
 * The functionality of this class contain the creation and receiving the visual Elements, aswell as the option for the parent class to update the Height of the Element (For Zooming Purposes)
 * @author Felix
 */
public class ArchSegment {

    /**
     * The Visual Elements, self explanatory
     */
    private Arc inner = new Arc();

    /**
     * Information that is stored after the ArchSegment has been initialised, this is needed to update the height later on.
     */
    private Color archColor = Color.rgb(77,175,74);
    private boolean isGapcloser,isReversed;
    private int alignmentStart, alignmentEnd,readLength,level;
    private GlobalInformation info;

    /**
     * helper function to determine our standard color of the Read.
     */
    private void determinteStandardColor(){
        if(isGapcloser){
            archColor = Color.rgb(228,26,28);
        }
        if(isReversed){
            archColor = Color.rgb(55,126,184);
        }
    }

    /**
     * Function to update / set the Stroke Width. In the current Test this is bound to the Height of an element and thus gets updated when ZOOM happens
     * @param newValue the new Strokewidth that the archSegment is to be set at. (Currently divided by 10)
     */
    public void setStrokeWidth(double newValue){
        double width = newValue/10;
        inner.setStrokeWidth(width);

    }

    /**
     * Function to update/set the Stroke Color of the ArchSegment. Implemented for future usage when the user can custom-design their desired Colors for the 3 cases (gapClosing,isReversed,else)
     * @param color the Color the archsegment is to be set
     */
    public void setColor(Color color){
        inner.setStroke(color);

    }

    /**
     * Used in the constructor to create the first visual elements, and further later used on ZOOMing(and thus increasing/decreasing the height of the elements).
     * @param height height of the Element.
     */
    public void updateHeight(double height){
        //calculate the effective radius based on height,level,radius
        double effradius = info.getRadius()+ level*height + height/2;

        inner.setCenterX(info.getCenter().getX());
        inner.setCenterY(info.getCenter().getY());
        inner.setRadiusX(effradius);
        inner.setRadiusY(effradius);
        inner.setStartAngle(((info.getReferenceLength()-(double)alignmentStart)/info.getReferenceLength())*360);
        inner.setLength(((double)readLength/(double)info.getReferenceLength())*-360);
        inner.setStroke(archColor);
        inner.setFill(Color.TRANSPARENT);
        inner.setStrokeWidth(height);
        inner.setCacheHint(CacheHint.SPEED);


    }

    /**
     * Constructor of the Archsegment, receiving (read+global) information, aswell as a Level where the Element in going to get placed
     * Functionality is to set the fields of the Archsegment with values, then update the Height (and thus the 2 Lines + 2 Arcs)
     * @param read given Read that is going to be visually represented by this ArchSegment
     * @param info given gInfo that represents the information available from the reference + general drawing Information
     * @param level given Level at which the Segment is placed in the Ring.
     */
    public ArchSegment(Read read, GlobalInformation info, int level){
        this.info = info;
        this.isGapcloser=read.isCrossBorder();
        this.isReversed=read.getNegativeStrandFlag();
        this.alignmentStart =read.getAlignmentStart();
        this.alignmentEnd = read.getAlignmentEnd();
        this.level = level;
        this.readLength = read.getAlignmentLength();
        determinteStandardColor();
        updateHeight(info.getHeight());

    }

    /**
     * Getter for the Inner ARC
     * @return returns the Inner Arc of the ArchSegment
     */
    public Arc getInner() {
        return inner;
    }

}
