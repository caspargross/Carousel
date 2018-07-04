package playground.tests;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import main.model.CircularParser;
import main.model.Read;
import main.view.CircularView;
import main.view.GlobalInformation;
import main.view.ReadView;
import playground.Coordinate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import playground.ViewHelper;
import javax.swing.text.View;

/**
 * This class tests the CircularView classes and subclasses aswell as the combination with the CircularParser class
 * NOTE: currently the method for creating a Level-Array is highly primitve, this is simply a proof of concept
 * @author Felix
 */
public class ReadsToCircleTest extends Application    {

    /**
     * The Bamfile to test
     */
    private final File BAMFileToTest = new File("./data/01_plB.bam");
    /**
     * The hashmap to be used of the circular parser
     */
    private static HashMap< String, ArrayList< Read > > readMap;
    /**
     * the Arraylist containing Arraylist (hashmap -> arraylistList convertion (contains all duplicates)
     */
    private List<ArrayList<Read>> readArrayListList = new ArrayList<>();
    /**
     * the Arraylist containg only single reads and no duplicates, same as the array @author decided to not-use it out of convenience
     */
    private List<Read> readArrayList = new ArrayList<>();
    /**
     * Array of Reads, contains only single reads and no duplicates, same as the arrayList @author choose it out of convenience
     */
    private Read[] readArray;

    /**
     * Uses the TestFile in the circularParser class, generates a globalInformation out of the referenceeLength, creates a CircularView and later displays the elements of the CircularView on a new Scene
     * @param primaryStage
     * @throws Exception
     */
    // Stuff for Tickmarks currently still global Variables
    List<Pane> tickmarkList = new ArrayList<Pane>();
    List<Scale> scaleList = new ArrayList<Scale>();
    double oldMousePos;
    double scaleCount=0;
    @Override
    public void start( Stage primaryStage ) throws Exception {

        CircularParser.parseBAMFile(BAMFileToTest);
        readMap = CircularParser.getReadMap();
        readArrayListList.addAll(readMap.values());
        readArray = new Read[readArrayListList.size()];
        for(int index = 0; index <readArrayListList.size();index++){
            readArrayList.add(readArrayListList.get(index).get(0));
            readArray[index] = readArrayListList.get(index).get(0);
        }

        Pane myPane = new Pane( );
        DoubleProperty height = new SimpleDoubleProperty(1);
        Coordinate center = new Coordinate(450,450);//TODO: looks funny if height = 1
        GlobalInformation gInfo = new GlobalInformation(center,100,height.getValue(),CircularParser.getReferenceLength(BAMFileToTest));


        CircularView demo = new CircularView(readArray,gInfo);
        ReadView[] temp = demo.getReadViews();
        for(int i = 0; i < demo.getReadViews().length;i++){
            myPane.getChildren().addAll(temp[i].getArchSegment().getInner(),temp[i].getArchSegment().getOuter(),temp[i].getArchSegment().getStart(),temp[i].getArchSegment().getStop());
        }
        primaryStage.setTitle( "Reads in Circular Display" );
        primaryStage.setScene( new Scene( myPane, 1800, 1000 ) );
        primaryStage.show( );
        /*
        for(int i = 0; i < readArray.length;i++){
            if(readArray[i].isCircular()){
                System.out.println("circular, length; " + readArray[i].getAlignmentLength()+" start; "+readArray[i].getAlignmentStart()+" end: "+ readArray[i].getAlignmentEnd());
            }
            System.out.println(demo.getLevelArray()[i]);
        }*/

        /*myPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                myPane.getChildren().remove(0,myPane.getChildren().size());
                demo.info.height.setValue(demo.info.height.getValue()*1.5);
                System.out.println("click");
                for(int i = 0; i < demo.getReadViews().length;i++){
                    myPane.getChildren().addAll(temp[i].getArchSegment().getInner(),temp[i].getArchSegment().getOuter(),temp[i].getArchSegment().getStart(),temp[i].getArchSegment().getStop());
                }
            }
        });*/
        // Add special tickmars + Labels to display
        Line line15 = new Line(450,340,450,350);
        Text line15Text = ViewHelper.centerTextOnCoordinate("15",450,360);
        Line line0 = new Line(560,450,550,450);
        Text line0Text = ViewHelper.centerTextOnCoordinate("0",540,450);
        Line line5 = new Line(450,550,450,560);
        Text line5Text = ViewHelper.centerTextOnCoordinate("5",450,540);
        Line line10 = new Line(350,450,340,450);
        Text line10Text = ViewHelper.centerTextOnCoordinate("10",360,450);
        //Mouse-Drag to Rotate stuff
        Pane label0Pane = new Pane();
        Pane label5Pane = new Pane();
        Pane label10Pane = new Pane();
        Pane label15Pane = new Pane();


        myPane.getChildren().addAll(line0,line5,line10,line15);
        //This is to make sure, that when a completely new MouseDrag happens our circle doesnt wildy jump around because oldMousePos is 0
        myPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                oldMousePos = event.getSceneX();
            }
        });
        myPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(Math.pow(event.getSceneX()-oldMousePos,2)>20) {
                    Rotate rotate = new Rotate();
                    rotate.setAngle((event.getSceneX() - oldMousePos));
                    rotate.setPivotX(450);
                    rotate.setPivotY(450);
                    myPane.getTransforms().add(rotate);
                    gInfo.setRotation(gInfo.getRotation() + rotate.getAngle());
                    Rotate rotate0 = new Rotate((-(event.getSceneX() - oldMousePos)), ViewHelper.getCenteredTextCoordinates(line0Text).getX(), ViewHelper.getCenteredTextCoordinates(line0Text).getY());
                    Rotate rotate5 = new Rotate((-(event.getSceneX() - oldMousePos)), ViewHelper.getCenteredTextCoordinates(line5Text).getX(), ViewHelper.getCenteredTextCoordinates(line5Text).getY());
                    Rotate rotate10 = new Rotate((-(event.getSceneX() - oldMousePos)), ViewHelper.getCenteredTextCoordinates(line10Text).getX(), ViewHelper.getCenteredTextCoordinates(line10Text).getY());
                    Rotate rotate15 = new Rotate((-(event.getSceneX() - oldMousePos)), ViewHelper.getCenteredTextCoordinates(line15Text).getX(), ViewHelper.getCenteredTextCoordinates(line15Text).getY());
                    label0Pane.getTransforms().add(rotate0);
                    label5Pane.getTransforms().add(rotate5);
                    label10Pane.getTransforms().add(rotate10);
                    label15Pane.getTransforms().add(rotate15);
                    oldMousePos = event.getSceneX();
                }
            }
        });

        label0Pane.getChildren().add(line0Text);
        label5Pane.getChildren().add(line5Text);
        label10Pane.getChildren().add(line10Text);
        label15Pane.getChildren().add(line15Text);
        myPane.getChildren().addAll(label15Pane,label0Pane,label5Pane,label10Pane);

        myPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                //Divide in 2 Cases: getDeltaY is negative (downscroll) and getDeltaY is positive (upscroll)

                //depending on Zoom-factor the ticklines need to be at a shorter length + a smaller degree amount



                //Create a Scale, to transform
                //Depending on Rotation we want to zoom into a different Point.
                //TODO: This is propably the moment where i should´ve creted a helper function for the whole cartesian -> polar coordinate calculation.. - Maybe still do it
                double xtemp = (gInfo.getCenter().getX()+(((gInfo.getRadius()+5))*Math.cos(Math.toRadians(gInfo.getRotation()+90))));
                double ytemp = gInfo.getCenter().getY()+((gInfo.getRadius())+5)*-Math.sin(Math.toRadians(gInfo.getRotation()+90));
                Scale Zoom = new Scale(1.5,1.5, xtemp,ytemp);

                if(event.getDeltaY()>0){
                    scaleList.add(Zoom);
                    myPane.getTransforms().add(Zoom);
                    Pane tempPane = new Pane();
                    scaleCount++;
                    /* Test of the different tickmark method
                    Line[] tempLineArray = ViewHelper.circleOfTickmarks(gInfo, scaleCount);
                    for (int i = 0; i < tempLineArray.length; i++) {
                        tempPane.getChildren().add(tempLineArray[i]);
                    }*/

                    if(18 / (1.5 * scaleCount)>= 1) {
                        Line[] tempLineArray = ViewHelper.circleOfTickmarks(gInfo, 10 / (1.5 * scaleCount), (int) (18 / (1.5 * scaleCount)));
                        for (int i = 0; i < tempLineArray.length; i++) {
                            tempPane.getChildren().add(tempLineArray[i]);
                        }
                    }
                    tickmarkList.add(tempPane);
                    myPane.getChildren().add(tempPane);
                    demo.info.height.setValue(demo.info.height.getValue()/(1.1));

                }
                if(event.getDeltaY()<0 && scaleCount>0){
                    /*try {
                        //myPane.getTransforms().add(Zoom.createInverse());
                        //System.out.println(myPane.getTransforms().toString());
                    }
                    catch (Exception exc){
                            System.out.println("Inverse scale could not be created"); // THIS SHOULD NEVER HAPPEN - the given scale is hardcoded & inversible
                    }*/
                    scaleCount--;
                    //removes 1 pane of tickmarks, since we no  longer are zoomed in this far
                    myPane.getChildren().remove(tickmarkList.get(tickmarkList.size()-1));
                    tickmarkList.remove(tickmarkList.size()-1);
                    //removes 1 scale element since we zoomed out
                    myPane.getTransforms().remove(scaleList.get(scaleList.size()-1));
                    scaleList.remove(scaleList.get(scaleList.size()-1));
                    demo.info.height.setValue(demo.info.height.getValue()*1.1);
                }
            }
        });


    }

    /**
     * The main procedure of the test.
     *
     * @param args unused parameter
     */

    public static void main( String[] args ) {
        launch( args );

    }

}


