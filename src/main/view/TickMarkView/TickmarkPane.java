package main.view.TickMarkView;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import main.view.Helper.GlobalInformation;

/**
 * class containing a single Pane of tickmarks, constructor builds a circle of Tickmarks depending on the gInfo, and given paramteres.
 * Allows updating the height/width of the Ticks
 */
public class TickmarkPane extends Pane {
    private GlobalInformation globalInformation;
    private double lengthOfMark, interval;
    private double strokeWidth = 1;

    /**
     * Builds the circle of Tickmarks
     * @param gInfo globalInformation of our Read
     * @param lengthOfMark baseLength the Tick is supposed to have
     * @param interval determines every i-th BP a new Tick should appear
     */
    public TickmarkPane(GlobalInformation gInfo,double lengthOfMark,double interval){
        globalInformation = gInfo;
        this.lengthOfMark = lengthOfMark;
        this.interval = interval;
        circleOfTickmarks();

    }

    /**
     * does math to determine polar-cartesian transformation of the to be placed ticks
     */
    private void circleOfTickmarks(){
        int refLength = globalInformation.getReferenceLength();
        double centerX = globalInformation.getCenter().getX();
        double centerY = globalInformation.getCenter().getY();
        double radius = globalInformation.getRadius();
        double lengthofMarkTemp = lengthOfMark;
        int amountOfTicks = 1+ refLength/(int)interval;

        int counterOfBiggerTickmarks =0;
        for(int i = 0;i < amountOfTicks; i++){
            double  tempLength = lengthOfMark;
            if(counterOfBiggerTickmarks==5) {

                lengthofMarkTemp = lengthofMarkTemp+1;
            }
            if(counterOfBiggerTickmarks==0){
                lengthofMarkTemp = lengthofMarkTemp*2;
            }
            double tempSX = centerX + radius * Math.cos(Math.toRadians((((double) (i * interval) / (double) refLength)) * 360));
            double tempSY = centerY + radius * Math.sin(Math.toRadians((((double) (i * interval) / (double) refLength)) * 360));
            double tempEX = centerX + (radius +  lengthofMarkTemp) * Math.cos(Math.toRadians((((double) (i * interval) / (double) refLength)) * 360));
            double tempEY = centerY + (radius + lengthofMarkTemp) * Math.sin(Math.toRadians((((double) (i * interval) / (double) refLength)) * 360));
            Line temp = new Line(tempSX, tempSY, tempEX, tempEY);
            temp.setStrokeWidth(strokeWidth);
            this.getChildren().add(temp);
            lengthofMarkTemp = tempLength;

            if(counterOfBiggerTickmarks==5){
                counterOfBiggerTickmarks =1;
            }
            else {
                counterOfBiggerTickmarks++;
            }
        }


    }

    /**
     * updates the height of all Ticks in this Pane
     * @param shrinkRatio ratio with which the exisiting height should be multiplied
     */
    public void updateHeight(double shrinkRatio){
        this.getChildren().clear();
        lengthOfMark=lengthOfMark*shrinkRatio;
        circleOfTickmarks();
    }

    /**
     * updates the width of all the Ticks in this Pane
     * @param shrinkRatio ratio with which the existing width should be multiplied
     */
    public void updateWidth(double shrinkRatio){
       this.getChildren().clear();
       strokeWidth=strokeWidth*shrinkRatio;
       circleOfTickmarks();
    }
}
