package playground;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jdk.jfr.events.ExceptionThrownEvent;
import javax.xml.stream.events.Attribute;
import java.lang.Object;
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
    private Line[] circleOfTickmarks (double centerX, double centerY, double radius, double length, int degree) {
        Line[] lineArray = new Line[360 / degree];
        double tempSX, tempEX, tempSY, tempEY;
        for (int i = 0; i < lineArray.length; i++) {
            tempSX = centerX+  radius * Math.cos(Math.toRadians(i * degree));
            tempEX = centerX+ (radius + length) * (Math.cos(Math.toRadians(i * degree)));
            tempSY = centerY+  radius * (Math.sin(Math.toRadians(i * degree)));
            tempEY = centerY+ (radius + length) * (Math.sin(Math.toRadians(i * degree)));
            lineArray[i] = new Line(tempSX, tempSY, tempEX, tempEY);
        }
        return lineArray;
    }

    // Creates an arc-segment as a path to draw it later on the display. Receives the distance from center r/radius the position of the center, the height of the segment, the length, a start position and a boolean which determines the color depending on the direction of the segment
    // TODO: propably better to create a arc-segment class consisting of 2 lines and 2 archsegments.
    // Note: maybe make a class for center coordinates aswell. otherwise way too many argument.
    // ToDO: currently start = 0 is at 0 degree in polar coordinates = 3´o clock - should be at 0
    private static Path drawArchsegmentPath(double centerX, double centerY, double radius, double height, double length, double start, boolean direction){
        //create the 4 Coordinates out of whoom the Path later will be constructed. Point A,B are from the first line, C,D are depending on direction further to 0 or closer
        double aX,bX,cX,dX,aY,bY,cY,dY;
        //depeding on direction: length will be positive / negative

        bX = centerX+ radius*Math.cos(Math.toRadians((start/20000)*360));
        bY = centerY+  radius * (Math.sin(Math.toRadians((start/20000)*360)));
        aX = centerX+ (radius+height)*Math.cos(Math.toRadians((start/20000)*360));
        aY = centerY+  (radius+height) * (Math.sin(Math.toRadians((start/20000)*360)));
        cX = centerX+ radius*Math.cos(Math.toRadians(((start+length)/20000)*360));
        cY = centerY+  radius * (Math.sin(Math.toRadians(((start+length)/20000)*360)));
        dX = centerX+ (radius+height)*Math.cos(Math.toRadians(((start+length)/20000)*360));
        dY = centerY+  (radius+height) * (Math.sin(Math.toRadians(((start+length)/20000)*360)));

        // since arcs behaved weirdly when traversing from B to C (inward bow) with fix1 + fix 2 we travel with "the cursor" to C so we can co from C to B with an arc + continue afterwards from C to D
        Path temp = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(aX);
        moveTo.setY(aY);
        temp.getElements().add(moveTo);
        LineTo firstLine = new LineTo();
        firstLine.setX(bX);
        firstLine.setY(bY);
        temp.getElements().add(firstLine);
        MoveTo fix1 = new MoveTo();
        fix1.setX(cX);
        fix1.setY(cY);
        temp.getElements().add(fix1);
        ArcTo outerArc = new ArcTo();
        outerArc.setX(bX);
        outerArc.setY(bY);
        outerArc.setRadiusX(150);
        outerArc.setRadiusY(150);
        temp.getElements().add(outerArc);
        MoveTo fix2 = new MoveTo();
        fix2.setX(cX);
        fix2.setY(cY);
        temp.getElements().add(fix2);
        LineTo secondLine = new LineTo();
        secondLine.setX(dX);
        secondLine.setY(dY);
        temp.getElements().add(secondLine);
        ArcTo innerArc = new ArcTo();
        innerArc.setX(aX);
        innerArc.setY(aY);
        innerArc.setRadiusX(150);
        innerArc.setRadiusY(150);
        temp.getElements().add(innerArc);
        return temp;

    }

    private Pane createHelloWorld() {
        Pane myPane = new Pane();

        // Add Circle element to display
        Circle myCircle = new Circle(150, 150, 100,  Color.TRANSPARENT);
        myCircle.setStroke(Color.DARKBLUE);
        myPane.getChildren().add(myCircle);

        // Add special tickmars + Labels to display
        Line line0 = new Line(150,40,150,50);
        myPane.getChildren().add(line0);
        Text line0Text = centerTextOnCoordinate("0",150,60);
        myPane.getChildren().add(line0Text);

        Line line5 = new Line(260,150,250,150);
        myPane.getChildren().add(line5);
        Text line5Text = centerTextOnCoordinate("5",240,150);
        myPane.getChildren().add(line5Text);

        Line line10 = new Line(150,250,150,260);
        myPane.getChildren().add(line10);
        Text line10Text = centerTextOnCoordinate("10",150,240);
        myPane.getChildren().add(line10Text);

        Line line15 = new Line(50,150,40,150);
        myPane.getChildren().add(line15);
        Text line15Text = centerTextOnCoordinate("15",60,150);
        myPane.getChildren().add(line15Text);


        // use the tickmark FUnction to generate an Array of lines, and add them to the Pane
        Line[] tickmarkArray = circleOfTickmarks(150,150, 100,5,10);
        for(int i = 0; i < tickmarkArray.length; i++){
            myPane.getChildren().add(tickmarkArray[i]);
        }

        // test of the drawArchsegement section. Note: currently the different levels of segments are determined by the radius. Later on a "level" could simply be used as an attribute to determine on which layer of the circles the segment should be drawn onto
        Path test = drawArchsegmentPath(150,150,150,10,2000,5000,true);
        test.setStroke(Color.GREEN);
        myPane.getChildren().add(test);
        Path test3 = drawArchsegmentPath(150,150,140,10,1000,5000,true);
        test3.setStroke(Color.RED);
        myPane.getChildren().add(test3);
        Path test2 = drawArchsegmentPath(150,150,150,10,2000,1000,false);
        myPane.getChildren().add(test2);

        Arc testarc = new Arc(150,150,110,110,45,270);
        testarc.setType(ArcType.OPEN);
        testarc.setFill(Color.TRANSPARENT);
        testarc.setStroke(Color.BLACK);
        myPane.getChildren().add(testarc);

        Arc arcOpen = new Arc();
        arcOpen.setCenterX(150.0f);
        arcOpen.setCenterY(150.0f);
        arcOpen.setRadiusX(25.0f);
        arcOpen.setRadiusY(25.0f);
        arcOpen.setStartAngle(45.0f);
        arcOpen.setLength(270.0f);
        arcOpen.setType(ArcType.OPEN);
        arcOpen.setStroke(Color.BLACK);
        arcOpen.setFill(Color.TRANSPARENT);
        myPane.getChildren().add(arcOpen);
        return myPane;

    }
}
