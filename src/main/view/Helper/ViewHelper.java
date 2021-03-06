package main.view.Helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import main.model.Read;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for the View, TODO: document properly
 * @author Felix
 */
public class ViewHelper {

    public static DoubleProperty scaleCount = new SimpleDoubleProperty(0);
    public static DoubleProperty rotationValue = new SimpleDoubleProperty(0);
    public static List<Pane> tickmarkList = new ArrayList<>();
    public static List<Scale> scaleList = new ArrayList<>();
    public static List<Scale> negativeScaleList = new ArrayList<>();

    public static Line[] circleOfTickmarks (GlobalInformation gInfo, double length, int degree) {
        Line[] lineArray = new Line[360 / degree];
        double tempSX, tempEX, tempSY, tempEY;
        for (int i = 0; i < lineArray.length; i++) {
            tempSX = gInfo.getCenter().getX()+  gInfo.getRadius() * Math.cos(Math.toRadians(i * degree));
            tempEX = gInfo.getCenter().getX()+ (gInfo.getRadius() + length) * (Math.cos(Math.toRadians(i * degree)));
            tempSY = gInfo.getCenter().getY()+  gInfo.getRadius() * (Math.sin(Math.toRadians(i * degree)));
            tempEY = gInfo.getCenter().getY()+ (gInfo.getRadius() + length) * (Math.sin(Math.toRadians(i * degree)));
            lineArray[i] = new Line(tempSX, tempSY, tempEX, tempEY);
            lineArray[i].setStrokeWidth((double)degree/20);
        }
        return lineArray;
    }
    public static Line[] circleOfTickmarks(GlobalInformation gInfo, double scalecount){
        int lineAmount = gInfo.getReferenceLength() /(int)(10000/scalecount);
        System.out.println(lineAmount);
        double degreeBetweenLines = ((gInfo.getReferenceLength() - (gInfo.getReferenceLength() %(int)(10000/scalecount)))/lineAmount)/gInfo.getReferenceLength()*360;
        Line[] lineArray = new Line[lineAmount];
        System.out.println(degreeBetweenLines);
        double length = 10/(scalecount*1.5);
        double tempSX,tempEX,tempSY,tempEY;
        for (int i = 0; i < lineArray.length; i++) {
            tempSX = gInfo.getCenter().getX()+  gInfo.getRadius() * Math.cos(Math.toRadians(i *degreeBetweenLines));
            tempEX = gInfo.getCenter().getX()+ (gInfo.getRadius() + length) * (Math.cos(Math.toRadians(i *degreeBetweenLines)));
            tempSY = gInfo.getCenter().getY()+  gInfo.getRadius() * (Math.sin(Math.toRadians(i *degreeBetweenLines)));
            tempEY = gInfo.getCenter().getY()+ (gInfo.getRadius() + length) * (Math.sin(Math.toRadians(i *degreeBetweenLines)));
            lineArray[i] = new Line(tempSX, tempSY, tempEX, tempEY);
            lineArray[i].setStrokeWidth(20/lineAmount);
        }
        return lineArray;
    }

    public static Text centerTextOnCoordinate(String text, double x, double y )
    {
        Text  txtShape = new Text( x, y, text );
        txtShape.setX( txtShape.getX() -  txtShape.getLayoutBounds().getWidth() / 2 );
        txtShape.setY(txtShape.getY() + txtShape.getLayoutBounds().getHeight()/4);
        return  txtShape;
    }
    //gets a Text of which the user wants to get the CenterCoordinates, returns a COordinate where the user can receive X Y coordinates seperately
    public static Coordinate getCenteredTextCoordinates(Text text){
        Coordinate temp = new Coordinate();
        temp.setX(text.getX()+text.getLayoutBounds().getWidth()/2);
        temp.setY(text.getY()-text.getLayoutBounds().getHeight()/4);        //DONT ASK WHY divided by 4
        return temp;
    }

    private void printGivenReadList(List< List< main.model.Read > > listOfReadLists){
        for(List<main.model.Read> readList:listOfReadLists){
            StringBuilder temp= new StringBuilder();
            if(!readList.isEmpty()){
                for(Read read:readList){
                    temp.append(" s: ").append(read.getAlignmentStart()).append(" l: ").append(read.getAlignmentLength()).append(" e: ").append(read.getAlignmentEnd());
                }
                System.out.println(temp);
            }

        }
    }
    private int calcReadCount(List< List< Read > >  listOfReadLists){
        int readcount = 0;
        for (List<Read> listOfReadList : listOfReadLists) {
            for(Read read:listOfReadList){
                readcount++;
            }
        }
        return readcount;
    }






}
