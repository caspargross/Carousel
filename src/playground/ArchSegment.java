package playground;

import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import jdk.nashorn.internal.objects.Global;

// Contains 2 Arcs (one Outer, one inner) and 2 Lines (one at start and the other is either right or left - depending on direction)
public class ArchSegment {
    private Line start;
    private Line stop;
    private Arc outer;
    private Arc inner;

    public ArchSegment(Read read, GlobalInformation info,double radius){
        //do Math to create Points A,B,C,D.
        //Convention: if direction is true (clockwise) the created ArchSegment looks like this:
        //              B
        //                                      D
        //           A
        //                                  C
        // if direction is false Line CD is left from AD

        Coordinate A = new Coordinate(radius*Math.cos(Math.toRadians((read.getStart()/info.getGlobalLength())*360)),radius*Math.sin(Math.toRadians((read.getStart()/info.getGlobalLength())*360)));
        Coordinate B = new Coordinate((radius+info.getHeight())*Math.cos(Math.toRadians((read.getStart()/info.getGlobalLength())*360)),(radius+info.getHeight())*Math.sin(Math.toRadians((read.getStart()/info.getGlobalLength())*360)));
        Coordinate C = new Coordinate(radius*Math.cos(Math.toRadians(((read.getStart()+read.getLength())/info.getGlobalLength())*360)),radius * (Math.sin(Math.toRadians(((read.getStart()+read.getLength())/info.getGlobalLength())*360))));
        Coordinate D = new Coordinate((radius+info.getHeight())*Math.cos(Math.toRadians(((read.getStart()+read.getLength())/info.getGlobalLength())*360)),(radius+info.getHeight()) * (Math.sin(Math.toRadians(((read.getStart()+read.getLength())/info.getGlobalLength())*360))));

        // Now Add the Center to the Coordinates
        A.add(info.getCenter());
        B.add(info.getCenter());
        C.add(info.getCenter());
        D.add(info.getCenter());

        // All 4 Coordinates are finished, time to setup the Lines+Arcs

        this.start = new Line(A.getX(),A.getY(),B.getX(),B.getY());
        this.stop = new Line(C.getX(),C.getY(),D.getX(),D.getY());
        //TODO: Arc stuff - Does the Arc properly draw negative length?
        this.outer = new Arc(info.getCenter().getX(),info.getCenter().getY(),radius+info.getHeight(),radius+info.getHeight(),(read.getStart()/info.getGlobalLength()*360),(read.getLength()/info.getGlobalLength())*360);
        outer.setStroke(Color.BLACK);
        outer.setFill(Color.TRANSPARENT);
        this.inner = new Arc(info.getCenter().getX(),info.getCenter().getY(),radius,radius,(read.getStart()/info.getGlobalLength()*360),(read.getLength()/info.getGlobalLength())*360);
        inner.setStroke(Color.BLACK);
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