package playground;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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

    private Pane createHelloWorld() {
        Pane myPane = new Pane();

        // Add Circle element to display
        Circle myCircle = new Circle(150, 150, 100,  Color.TRANSPARENT);
        myCircle.setStroke(Color.DARKBLUE);
        myPane.getChildren().add(myCircle);

        return myPane;
    }
}
