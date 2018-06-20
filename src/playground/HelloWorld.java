package playground;

import com.sun.deploy.uitoolkit.DragContext;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.input.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import jdk.jfr.events.ExceptionThrownEvent;
import javax.xml.stream.events.Attribute;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.*;


public class HelloWorld extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = createHelloWorld();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
}
    // Stuff for Tickmarks currently still global Variables
    List<Pane> tickmarkList = new ArrayList<Pane>();
    List<Scale> scaleList = new ArrayList<Scale>();
    double oldMousePos;
    double scaleCount=0;

    //gets a Text of which the user wants to get the CenterCoordinates, returns a COordinate where the user can receive X Y coordinates seperately
    private Coordinate getCenteredTextCoordinates(Text text){
        Coordinate temp = new Coordinate();
        temp.setX(text.getX()+text.getLayoutBounds().getWidth()/2);
        temp.setY(text.getY()-text.getLayoutBounds().getHeight()/4);        //DONT ASK WHY divided by 4
        return temp;
    }
    // Accounts for the Width of a given string in a Text. so that XY coordinates get adjusted and the Object is centered.
    private Text centerTextOnCoordinate( String text, double x, double y )
    {
        Text  txtShape = new Text( x, y, text );
        txtShape.setX( txtShape.getX() -  txtShape.getLayoutBounds().getWidth() / 2 );
        txtShape.setY(txtShape.getY() + txtShape.getLayoutBounds().getHeight()/4);
        return  txtShape;
    }
    //produces an array of lines / tickmarks for a full circle. Radius r, length of the tickmarks l, and degree d between tickmarks is given
    // TODO: Check if Arguments are valid (r > 0, d > 0 < 360, l > 0)
    private Line[] circleOfTickmarks (GlobalInformation gInfo, double length, int degree) {
        Line[] lineArray = new Line[360 / degree];
        double tempSX, tempEX, tempSY, tempEY;
        for (int i = 0; i < lineArray.length; i++) {
            tempSX = gInfo.getCenter().getX()+  gInfo.getRadius() * Math.cos(Math.toRadians(i * degree));
            tempEX = gInfo.getCenter().getX()+ (gInfo.getRadius() + length) * (Math.cos(Math.toRadians(i * degree)));
            tempSY = gInfo.getCenter().getY()+  gInfo.getRadius() * (Math.sin(Math.toRadians(i * degree)));
            tempEY = gInfo.getCenter().getY()+ (gInfo.getRadius() + length) * (Math.sin(Math.toRadians(i * degree)));
            lineArray[i] = new Line(tempSX, tempSY, tempEX, tempEY);
        }
        return lineArray;
    }

    private Pane createHelloWorld() {
        Pane myPane = new Pane();

        // Add Circle element to display
        Circle myCircle = new Circle(150, 150, 100,  Color.TRANSPARENT);
        myCircle.setStroke(Color.DARKBLUE);
        myPane.getChildren().add(myCircle);




        //TEST READ ARRAY

        Read[] readArray= new Read[6];
        readArray[0] = new Read(2000,1000,true,"READ1","CIGAR1",false);
        readArray[1] = new Read(7000,5000,false,"READ2","CIGAR2",false);
        readArray[2] = new Read(13000,500,true,"READ3","CIGAR3",false);
        readArray[3] = new Read( 4000,500,true,"READ4","CIGAR4",false);
        readArray[4] = new Read(1000,2000,false,"READ5","CIGAR5OVERLAP",true);
        readArray[5] = new Read(18000,2000,true,"READ6","CIGAR6",false);

        // LEVEL ARRAY
        DoubleProperty[] levelArray = new SimpleDoubleProperty[6];
        for(int i = 0; i < levelArray.length;i++){
            levelArray[i] = new SimpleDoubleProperty();
            levelArray[i].setValue(i);
        }
        //GlobalInformation
        Coordinate center = new Coordinate(150,150);
        GlobalInformation gInfo = new GlobalInformation(center,100,10,20000);
        //CIRCULAR VIEW
        CircularView demo = new CircularView(readArray,gInfo,levelArray);
        ReadView[] temp = demo.getReadViews();
        for(int i = 0; i < demo.getReadViews().length;i++){
            myPane.getChildren().addAll(temp[i].getArchSegment().getInner(),temp[i].getArchSegment().getOuter(),temp[i].getArchSegment().getStart(),temp[i].getArchSegment().getStop());
        }


        // Add special tickmars + Labels to display
        Line line15 = new Line(150,40,150,50);
        Text line15Text = centerTextOnCoordinate("15",150,60);
        Line line0 = new Line(260,150,250,150);
        Text line0Text = centerTextOnCoordinate("0",240,150);
        Line line5 = new Line(150,250,150,260);
        Text line5Text = centerTextOnCoordinate("5",150,240);
        Line line10 = new Line(50,150,40,150);
        Text line10Text = centerTextOnCoordinate("10",60,150);

        myPane.getChildren().addAll(line0,line5,line5Text,line10,line10Text,line15,line15Text);


        // use the tickmark FUnction to generate an Array of lines, and add them to the Pane
        Line[] tickmarkArray = circleOfTickmarks(gInfo,10,18);
        for(int i = 0; i < tickmarkArray.length; i++){
            myPane.getChildren().add(tickmarkArray[i]);
        }


        //Mouse-Drag to Rotate stuff
        Pane label0Pane = new Pane();
        Pane label5Pane = new Pane();
        Pane label10Pane = new Pane();
        Pane label15Pane = new Pane();

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


                Rotate rotate = new Rotate();
                rotate.setAngle((event.getSceneX()-oldMousePos));
                rotate.setPivotX(150);
                rotate.setPivotY(150);
                myPane.getTransforms().add(rotate);
                gInfo.setRotation(gInfo.getRotation()+rotate.getAngle());
                Rotate rotate0 = new Rotate((-(event.getSceneX()-oldMousePos)),getCenteredTextCoordinates(line0Text).getX(),getCenteredTextCoordinates(line0Text).getY());
                Rotate rotate5 = new Rotate((-(event.getSceneX()-oldMousePos)),getCenteredTextCoordinates(line5Text).getX(),getCenteredTextCoordinates(line5Text).getY());
                Rotate rotate10 = new Rotate((-(event.getSceneX()-oldMousePos)),getCenteredTextCoordinates(line10Text).getX(),getCenteredTextCoordinates(line10Text).getY());
                Rotate rotate15 = new Rotate((-(event.getSceneX()-oldMousePos)),getCenteredTextCoordinates(line15Text).getX(),getCenteredTextCoordinates(line15Text).getX());
                label0Pane.getTransforms().add(rotate0);
                label5Pane.getTransforms().add(rotate5);
                label10Pane.getTransforms().add(rotate10);
                label15Pane.getTransforms().add(rotate15);
                System.out.println("current gInfo rotation"+ gInfo.getRotation());
                oldMousePos = event.getSceneX();
            }
        });
        label0Pane.getChildren().add(line0Text);
        label5Pane.getChildren().add(line5Text);
        label10Pane.getChildren().add(line10Text);
        label15Pane.getChildren().add(line15Text);
        myPane.getChildren().addAll(label0Pane,label5Pane,label10Pane,label15Pane);

        //Mouse-Zoom to Zoom Note only DeltaY is relevant (vertical scroll amount)

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
                    System.out.println(myPane.getTransforms().toString());
                    System.out.println(gInfo.getRotation());
                    System.out.println("X: "+xtemp + " Y: " + ytemp );

                    Pane tempPane = new Pane();
                    scaleCount++;
                    Line[] tempLineArray = circleOfTickmarks(gInfo,10/(1.5*scaleCount),(int) (18/(1.5*scaleCount)));
                    for(int i = 0; i < tempLineArray.length;i++){
                        tempPane.getChildren().add(tempLineArray[i]);
                    }
                    tickmarkList.add(tempPane);
                    myPane.getChildren().add(tempPane);

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
                }

                System.out.println(event.getDeltaY()*1/40);
            }
        });
        System.out.println(line0Text.getX()+" "+ line0Text.getLayoutBounds().getWidth()+" "+line0Text.getY() + " "+line0Text.getLayoutBounds().getHeight());
        System.out.println(line5Text.getX()+" "+ line5Text.getLayoutBounds().getWidth()+" "+line5Text.getY() + " "+line5Text.getLayoutBounds().getHeight());
        System.out.println(line10Text.getX()+" "+ line10Text.getLayoutBounds().getWidth()+" "+line10Text.getY() + " "+line10Text.getLayoutBounds().getHeight());
        System.out.println(line15Text.getX()+" "+ line15Text.getLayoutBounds().getWidth()+" "+line15Text.getY() + " "+line15Text.getLayoutBounds().getHeight());

        //myPane.setRotate(-90.0);        // To make sure 0 is at the Top
        return myPane;

    }
}
