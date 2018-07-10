package main.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Transform;
import main.model.CircularParser;
import main.model.Read;
import playground.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class MainView extends AnchorPane {

    private CircularView circularView;
    //private TickView tickView;
    //private LabelView labelView;

    private double downX;
    private double downY;
    public MainView(){


        CircularParser.Reads.getReadsSorted().addListener((ListChangeListener)(c -> {
            constructCircularView(CircularParser.Reads.getReadsSorted());
        }
        ));
        this.setOnMousePressed((me)->{
            downX = me.getSceneX();
            downY = me.getSceneY();
        });
        this.setOnMouseDragged((me)->{
            double deltaX = me.getSceneX()-downX;
            double deltaY = me.getSceneY()-downY;
            this.getTransforms().add(Transform.rotate(Math.toRadians(deltaX),1000,450));
        });
        this.setStyle("-fx-background-color : White");
        this.setHeight(800);
        this.setWidth(1600);
    }
    private void constructCircularView( ObservableList< List< Read > > listOfReadLists){
        int referenceLength = CircularParser.getReferenceSequenceLength();
        //GlobalInformation gInfo = new GlobalInformation(referenceLength);
        System.out.println("ReferenceLength of "+CircularParser.getReferenceSequenceLength());
        System.out.println("List Changed");
        DoubleProperty height = new SimpleDoubleProperty(2);
        Coordinate center = new Coordinate(1000,450);
        GlobalInformation gInfo = new GlobalInformation(center,100,height.getValue(),referenceLength);
        circularView = new CircularView(listOfReadLists,gInfo);
        System.out.println(circularView.getReadViews().length);
        for (int i = 0; i <circularView.getReadViews().length; i++){
            this.getChildren().add(circularView.getReadViews()[i].getArchSegment().getInner());
        }
    }
}
