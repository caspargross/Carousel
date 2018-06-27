package main.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import playground.Coordinate;
import main.model.Read;

// Contains 2 Arcs (one Outer, one inner) and 2 Lines (one at start and the other is either right or left - depending on direction)
public class ArchSegment {
    private Line start;
    private Line stop;
    private Arc outer;
    private Arc inner;

    public ArchSegment(Read read, GlobalInformation info, int level){
        //do Math to create Points A,B,C,D.
        //Convention: if direction is true (clockwise) the created ArchSegment looks like this:
        //              B-  -   -   _   _
        //            /                      -    -  D
        //           A  -      -    _               /
        //                               -     -  C
        // if direction is false Line CD is left from AD

        //TEST: swap start/end if is circular
        int readStart,readEnd,readLength;
        int circular;
        Color archColor = Color.BLACK;
        if(read.getNegativeStrandFlag()){
            archColor =Color.BLUE;
        }
        if(read.isCircular()) {
             readStart = read.getAlignmentEnd();
             readEnd = read.getAlignmentStart();
             readLength = read.getAlignmentEnd()+ ((int)info.getGlobalLength()-read.getAlignmentStart());
             archColor = Color.RED;
        }
        else {
            readStart = read.getAlignmentStart();
            readEnd = read.getAlignmentEnd();
            circular=1;
            readLength = read.getAlignmentLength();
        }


        //TODO this is kinda useless - could just be an int, maybe clean up with refractor
        DoubleProperty radius = new SimpleDoubleProperty();
       radius.setValue(level*info.getHeight() + info.getRadius());


        Coordinate A = new Coordinate(radius.getValue()*Math.cos(Math.toRadians((read.getAlignmentStart()/info.getGlobalLength())*360)),radius.getValue()*Math.sin(Math.toRadians((read.getAlignmentStart()/info.getGlobalLength())*360)));
        Coordinate B = new Coordinate((radius.getValue()+info.getHeight())*Math.cos(Math.toRadians((read.getAlignmentStart()/info.getGlobalLength())*360)),(radius.getValue()+info.getHeight())*Math.sin(Math.toRadians((read.getAlignmentStart()/info.getGlobalLength())*360)));
        Coordinate C = new Coordinate(radius.getValue()*Math.cos(Math.toRadians(((read.getAlignmentStart()+read.getAlignmentLength())/info.getGlobalLength())*360)),radius.getValue() * (Math.sin(Math.toRadians(((read.getAlignmentStart()+read.getAlignmentLength())/info.getGlobalLength())*360))));
        Coordinate D = new Coordinate((radius.getValue()+info.getHeight())*Math.cos(Math.toRadians(((read.getAlignmentStart()+read.getAlignmentLength())/info.getGlobalLength())*360)),(radius.getValue()+info.getHeight()) * (Math.sin(Math.toRadians(((read.getAlignmentStart()+read.getAlignmentLength())/info.getGlobalLength())*360))));

        // Now Add the Center to the Coordinates
        A.add(info.getCenter());
        B.add(info.getCenter());
        C.add(info.getCenter());
        D.add(info.getCenter());

        // All 4 Coordinates are finished, time to setup the Lines+Arcs
        // NOTE: ARC Degrees work counterclockwise (rising) -> start needs to be the "stop" and 90° = 15k label
        this.start = new Line(A.getX(),A.getY(),B.getX(),B.getY());
        this.stop = new Line(C.getX(),C.getY(),D.getX(),D.getY());
        this.start.setStroke(archColor);
        this.stop.setStroke(archColor);
        //TODO: Arc stuff - Does the Arc properly draw negative length? - Yes it does
        this.outer = new Arc(info.getCenter().getX(),info.getCenter().getY(),radius.getValue()+info.getHeight(),radius.getValue()+info.getHeight(),((info.getGlobalLength()-read.getAlignmentStart())/info.getGlobalLength())*360,(readLength/info.getGlobalLength())*-360);
        outer.setStroke(archColor);
        outer.setFill(Color.TRANSPARENT);
        this.inner = new Arc(info.getCenter().getX(),info.getCenter().getY(),radius.getValue(),radius.getValue(),((info.getGlobalLength()-read.getAlignmentStart())/info.getGlobalLength())*360,(readLength/info.getGlobalLength())*-360);
        inner.setStroke(archColor);
        inner.setFill(Color.TRANSPARENT);

    }
    // GETTER
    public Arc getInner() {
        return inner;
    }

    public Arc getOuter() {
        return outer;
    }

    public Line getStart() {
        return start;
    }

    public Line getStop() {
        return stop;
    }
}
