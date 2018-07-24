package main.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import main.model.CircularParser;
import main.model.Read;
import playground.Coordinate;
import playground.ViewHelper;

import javax.swing.text.View;



import java.util.ArrayList;
import java.util.List;


public class MainView extends AnchorPane {

    private CircularView circularView;
    //private TickView tickView;
    //private LabelView labelView;
    private double downX;
    private double downY;

    public ArrayList<Point2D> getPoint2DArrayList() {
        return point2DArrayList;
    }

    private ArrayList<Point2D> point2DArrayList = new ArrayList<Point2D>();
    public MainView(){


        CircularParser.Reads.getReadsSorted().addListener((ListChangeListener)(c -> {
            constructCircularView(CircularParser.Reads.getReadsSorted());//TODO: not empty the list of the circular parser x)
            setupZoom(); //TODO: Currently this is here because gInfo doesnt exist beforehand.Yet the necesssary information (radius, center is propably predertmined in our application anyway. IF thats decided we can move this part back in the constructor
        }
        ));
        setupRotate();
        this.setStyle("-fx-background-color : White");
        setupTestCacheKeys();
        requestFocus();





    }
    private void constructCircularView( ObservableList< List< Read > > listOfReadLists){
        int referenceLength = CircularParser.ReferenceSequences.getCurrentReferenceSequenceLength();
        //GlobalInformation gInfo = new GlobalInformation(referenceLength);
        System.out.println("ReferenceLength of "+CircularParser.ReferenceSequences.getCurrentReferenceSequenceLength());
        System.out.println("List Changed");
        DoubleProperty height = new SimpleDoubleProperty(2);
        Coordinate center = new Coordinate(700,400);
        GlobalInformation gInfo = new GlobalInformation(center,100,height.getValue(),referenceLength);
        circularView = new CircularView(listOfReadLists,gInfo);
        circularView.enableCacheOfReadViews();
        for (int i = 0; i <circularView.getReadViews().length; i++){
            Arc temp = circularView.getReadViews()[i].getArchSegment().getInner();
            this.getChildren().add(temp);
        }



    }
    private void setupZoom(){
        this.setOnScroll((se)->{

            double xtemp = (circularView.info.getCenter().getX()+(((circularView.info.getRadius()+5))*Math.cos(Math.toRadians(ViewHelper.rotationValue.getValue()+90))));
            double ytemp = circularView.info.getCenter().getY()+((circularView.info.getRadius())+5)*-Math.sin(Math.toRadians(ViewHelper.rotationValue.getValue()+90));

            Scale zoom = new Scale(1.5,1.5, xtemp,ytemp);
            Scale negativeScale = new Scale(1/1.5,1/1.5,xtemp,ytemp);
            if (se.getDeltaY() >0){
                ViewHelper.scaleList.add(zoom);
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue()+1);
                this.getTransforms().add(zoom);
                //Change the height
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
            else if (se.getDeltaY() >0&& ViewHelper.scaleCount.getValue()<1){
                ViewHelper.scaleCount.set(ViewHelper.scaleCount.getValue()+1);
                this.getTransforms().remove(ViewHelper.negativeScaleList.get(ViewHelper.negativeScaleList.size()-1));
                ViewHelper.negativeScaleList.remove(ViewHelper.negativeScaleList.size()-1);
            }
            if(se.getDeltaY()<0 && ViewHelper.scaleCount.getValue()>0) {
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue() - 1);
                this.getChildren().remove(ViewHelper.tickmarkList.get(ViewHelper.tickmarkList.size()-1));
                ViewHelper.tickmarkList.remove(ViewHelper.tickmarkList.size()-1);
                this.getTransforms().remove(ViewHelper.scaleList.get(ViewHelper.scaleList.size() - 1));
                ViewHelper.scaleList.remove(ViewHelper.scaleList.size() - 1);
                //Change the height
                //circularView.info.height.setValue(circularView.info.height.getValue()*1.2);

            }
            else if(se.getDeltaY()<0 && ViewHelper.scaleCount.getValue()<=0) {
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue() - 1);
                ViewHelper.negativeScaleList.add(negativeScale);
                this.getTransforms().add(negativeScale);
            }

        });

    }
    private void setupRotate(){
        this.setOnMouseMoved((me)->{
            downX = me.getSceneX();
            downY = me.getSceneY();
        });
        this.setOnMouseDragged((me)->{

            double deltaX = me.getSceneX()-downX;
            double deltaY = me.getSceneY()-downY;
            double angle = Math.toRadians(deltaX*20);
            Rotate rotate = new Rotate(angle,700,400);
            this.getTransforms().add(rotate);
            downX = me.getSceneX();
            ViewHelper.rotationValue.setValue(ViewHelper.rotationValue.getValue()+angle);

        });
    }

    private void setupTestCacheKeys(){
        System.out.println("setting up cachetestkeys");
        this.setOnKeyTyped(ke->{
            System.out.println("key was pressed");
            KeyCode keyCode = ke.getCode();
            if(keyCode.equals(KeyCode.S)){
                System.out.println("S PRESSED");
                return;
            }
        });
    }
    public void CacheTempSpeed(){
        //circularView.disableCacheOfReadViews();
        circularView.cacheToQuality();
        circularView.cacheToSpeed();
        circularView.cacheToQuality();
        circularView.cacheToSpeed();
        //circularView.enableCacheOfReadViews();
    }

    public void CacheTempQuality(){
        //circularView.disableCacheOfReadViews();
        circularView.cacheToQuality();
        //circularView.enableCacheOfReadViews();
    }


}
