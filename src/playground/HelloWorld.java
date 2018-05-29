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
        return myPane;
    }
}
