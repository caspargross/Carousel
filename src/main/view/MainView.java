package main.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.CacheHint;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import main.model.CircularParser;
import main.model.Read;
import playground.Coordinate;
import playground.ViewHelper;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.transform.Transform;


public class MainView extends AnchorPane {

    private CircularView circularView;
    //private TickView tickView;
    //private LabelView labelView;
    // Transformation Property
    public Property<Transform> woldTransformProperty;
    private double downX;
    private double downY;
    public MainView(){


        CircularParser.Reads.getReadsSorted().addListener((ListChangeListener)(c -> {
            constructCircularView(CircularParser.Reads.getReadsSorted());//TODO: not empty the list of the circular parser x)
            setupZoom(); //TODO: Currently this is here because gInfo doesnt exist beforehand.Yet the necesssary information (radius, center is propably predertmined in our application anyway. IF thats decided we can move this part back in the constructor
        }
        ));
        setupRotate();
        this.setStyle("-fx-background-color : White");
        this.setHeight(800);

        // Add WorldTransform
        woldTransformProperty = new SimpleObjectProperty<>(new Transform() {
            @Override
            public void impl_apply(Affine3D t) {

            }

            @Override
            public BaseTransform impl_derive(BaseTransform t) {
                return null;
            }
        });

        woldTransformProperty.addListener((observable, oldValue, newValue) -> {
            System.out.println("woldTransform Rotated");
            this.getTransforms().setAll(newValue);
        });

    }
    private void constructCircularView( ObservableList< List< Read > > listOfReadLists){
        int referenceLength = CircularParser.ReferenceSequences.getCurrentReferenceSequenceLength();
        //GlobalInformation gInfo = new GlobalInformation(referenceLength);
        System.out.println("ReferenceLength of "+CircularParser.ReferenceSequences.getCurrentReferenceSequenceLength());
        System.out.println("List Changed");
        DoubleProperty height = new SimpleDoubleProperty(2);
        Coordinate center = new Coordinate(1000,450);
        GlobalInformation gInfo = new GlobalInformation(center,100,height.getValue(),referenceLength);
        circularView = new CircularView(listOfReadLists,gInfo);
        System.out.println(circularView.getReadViews().length);
        circularView.enableCacheOfReadViews();
        for (int i = 0; i <circularView.getReadViews().length; i++){
            Arc temp = circularView.getReadViews()[i].getArchSegment().getInner();
            this.getChildren().add(temp);
        }

    }
    private void setupZoom(){
        this.setOnScroll((se)->{
            circularView.disableCacheOfReadViews(); // We dont want this to look like a blurry mess
            double xtemp = (circularView.info.getCenter().getX()+(((circularView.info.getRadius()+5))*Math.cos(Math.toRadians(ViewHelper.rotationValue.getValue()+90))));
            double ytemp = circularView.info.getCenter().getY()+((circularView.info.getRadius())+5)*-Math.sin(Math.toRadians(ViewHelper.rotationValue.getValue()+90));
            Scale zoom = new Scale(1.5,1.5, xtemp,ytemp);
            if (se.getDeltaY() >0){
                circularView.disableCacheOfReadViews(); // We dont want this to look like a blurry mess
                ViewHelper.scaleList.add(zoom);
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue()+1);
                this.getTransforms().add(zoom);
                //Add a tempPane, with tickmarks on it
                //Add the tempPane to the tickmarkView
                //circularView.info.height.setValue(circularView.info.height.getValue()*(1/1.2));
                Pane tempPane = new Pane();
                if(18 / (1.5 * ViewHelper.scaleCount.getValue())>= 1) {
                    Line[] tempLineArray = ViewHelper.circleOfTickmarks(circularView.info, 10 / (1.5 * ViewHelper.scaleCount.getValue()), (int) (18 / (1.5 * ViewHelper.scaleCount.getValue())));
                    for (int i = 0; i < tempLineArray.length; i++) {
                        tempPane.getChildren().add(tempLineArray[i]);
                    }
                }
                ViewHelper.tickmarkList.add(tempPane);
                this.getChildren().add(tempPane);

            }
            if(se.getDeltaY()<0 && ViewHelper.scaleCount.getValue()>0) {

                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue() - 1);
                this.getChildren().remove(ViewHelper.tickmarkList.get(ViewHelper.tickmarkList.size()-1));
                ViewHelper.tickmarkList.remove(ViewHelper.tickmarkList.size()-1);
                //Change the height
                this.getTransforms().remove(ViewHelper.scaleList.get(ViewHelper.scaleList.size() - 1));
                ViewHelper.scaleList.remove(ViewHelper.scaleList.size() - 1);
                //circularView.info.height.setValue(circularView.info.height.getValue()*1.2);

            }
            circularView.enableCacheOfReadViews();
        });

    }
    private void setupRotate(){
        this.setOnMouseMoved((me)->{
            downX = me.getSceneX();
            downY = me.getSceneY();
        });
        this.setOnMouseDragged((me)->{
            //circularView.enableCacheOfReadViews();
            double deltaX = me.getSceneX()-downX;
            double deltaY = me.getSceneY()-downY;
            double angle = Math.toRadians(deltaX*20);
            Rotate rotate = new Rotate(angle,1000,450);
            //this.getTransforms().add(rotate);
            this.woldTransformProperty.setValue(rotate.createConcatenation(this.woldTransformProperty.getValue()));
            downX = me.getSceneX();
            ViewHelper.rotationValue.setValue(ViewHelper.rotationValue.getValue()+angle);
            System.out.println(ViewHelper.rotationValue.getValue());
        });
    }
}
