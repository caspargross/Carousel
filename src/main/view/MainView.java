package main.view;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import main.model.CircularParser;
import main.model.Read;
import main.view.CircularView.CircularView;
import main.view.Helper.GlobalInformation;
import main.view.TickMarkView.TickmarkView;
import main.view.Helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * class that contains the circularView elements, and in the future the labels/tickmarks in seperate Panes/Objects
 * has mouse-events for scaling/rotating
 */
public class MainView extends AnchorPane {
    private GlobalInformation gInfo;
    private CircularView circularView;
    private TickmarkView tickmarkView;
    //private LabelView labelView;
    private double downX;
    private double downY;
    private boolean firstParse= true;
    private List< List< Read > > data;

    /**
     * on construction we add a listener to the Model - whenever that changes parsing is complete and the View can work.
     * If the data happens to change again, we simply create new View-elements and delete the olds.
     */
    public MainView(){


        CircularParser.Reads.getReadsSorted().addListener((ListChangeListener)(c -> {
            grabData(CircularParser.Reads.getReadsSorted());

            if (!firstParse) {

                this.getChildren().remove(0, this.getChildren().size());
                constructView(data);
            }

            if (firstParse) {
                constructView(data);
                setupRotate();
                setupZoom(); //TODO: Currently this is here because gInfo doesnt exist beforehand.Yet the necesssary information (radius, center is propably predertmined in our application anyway. IF thats decided we can move this part back in the constructor
                firstParse = false;
            }
        }
        ));
        setupRotate();
        this.setStyle("-fx-background-color : White");
    }

    /**
     * Wrapper Function so the lambda function of the Listener on the model is cleaner; basically creates a CircularView object
     * @param listOfReadLists
     */
    private void constructView( List< List< Read > > listOfReadLists){
        gInfo = new GlobalInformation();
        circularView = new CircularView(listOfReadLists,gInfo);
        for (int i = 0; i <circularView.getReadViews().length; i++){
            Arc temp = circularView.getReadViews()[i].getArchSegment().getInner();
            this.getChildren().add(temp);
        }
        tickmarkView = new TickmarkView(gInfo);
        this.getChildren().add(tickmarkView);
    }

    /**
     * Sets the Scroll event, split into 4 cases:
     * - Zoomed at base-camera-position/already in negative Zoom
     *  - Zoom further in
     *  - Zoom further out
     * - Zoomed in already
     *  - Zoom further in
     *  - Zoom further out
     *  In the ViewHelper we simply keep track of our scales/tickmarks so we can adjust the applied scales/tickmarks on the View accordingly.
     */
    private void setupZoom(){
        this.setOnScroll((se)->{
            double xtemp = (circularView.info.getCenter().getX()+(((circularView.info.getRadius()))*Math.cos(Math.toRadians(ViewHelper.rotationValue.getValue()+90))));
            double ytemp = circularView.info.getCenter().getY()+((circularView.info.getRadius()))*-Math.sin(Math.toRadians(ViewHelper.rotationValue.getValue()+90));

            Scale zoom = new Scale(1.5,1.5, xtemp,ytemp);
            Scale negativeScale = new Scale(1/1.5,1/1.5,xtemp,ytemp);
            if (se.getDeltaY() >0 && ViewHelper.scaleCount.getValue()>=0){
                ViewHelper.scaleList.add(zoom);
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue()+1);
                this.getTransforms().add(zoom);
                //Change the height
                gInfo.height.setValue(gInfo.height.getValue()*(1/1.4));
            }
            else if (se.getDeltaY() >0&& ViewHelper.scaleCount.getValue()<0){
                ViewHelper.scaleCount.set(ViewHelper.scaleCount.getValue()+1);
                this.getTransforms().remove(ViewHelper.negativeScaleList.get(ViewHelper.negativeScaleList.size()-1));
                ViewHelper.negativeScaleList.remove(ViewHelper.negativeScaleList.size()-1);
            }
            if(se.getDeltaY()<0 && ViewHelper.scaleCount.getValue()<=0) {
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue() - 1);
                ViewHelper.negativeScaleList.add(negativeScale);
                this.getTransforms().add(negativeScale);
            }
            else if(se.getDeltaY()<0 && ViewHelper.scaleCount.getValue()>0) {
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue() - 1);
                this.getTransforms().remove(ViewHelper.scaleList.get(ViewHelper.scaleList.size() - 1));
                ViewHelper.scaleList.remove(ViewHelper.scaleList.size() - 1);
                //Change the height
                gInfo.height.setValue(gInfo.height.getValue()*1.4);
            }
        });

    }

    /**
     * sets the Drag-Implementation. the onMouseMoved ensures that we always have the t-1 current position to compare so whenever a new DragEvent starts we dont get huge Deltas
     * Future aim: make the Rotation depending from where we drag: Dragging clockwise around the circle rotates clockwise, and vice-versa.
     * Problem: the me.Coordinates are also affected by the current Scales
     */
    private void setupRotate(){
        this.setOnMouseMoved((me)->{
            downX = me.getSceneX();
            downY = me.getSceneY();
        });
        this.setOnMouseDragged((me)->{
            double deltaX = me.getSceneX()-downX;
            double deltaY = me.getSceneY()-downY;
            double delta2X = me.getSceneX()-800;
            double delta2Y = me.getSceneY()-500;
            double angle = Math.toRadians(deltaX*15);
            Rotate rotate = new Rotate(angle,gInfo.getCenter().getX(),gInfo.getCenter().getY());
            this.getTransforms().add(rotate);
            downX = me.getSceneX();
            downY = me.getSceneY();
            ViewHelper.rotationValue.setValue(ViewHelper.rotationValue.getValue()+angle);
        });
    }
    // Debugging tools to evaluate Caching Performance - Conclusion either we have a massive Ressource-Hog or Caching was bad
    public void CacheTempSpeed(){
        circularView.cacheToSpeed();
    }

    public void CacheTempQuality(){
        circularView.cacheToQuality();
    }
    public void EnableCache(){
        circularView.enableCacheOfReadViews();
    }

    /**
     * Function that creates a new Object of the model-data, since Java is pass-by value (and passes Objects as reference-value) we need to make this a bit more complicated
     * Our datastructure is an "object of ojbect of primitives" so we actually need to create 2 levels of new Objects to store the data safely.
     * @param temp the data from our CircularParser
     */
    public void grabData(ObservableList<List<Read>>temp){
        data  = new ArrayList<>();
        for (List<Read> listOfRead:temp){
            ArrayList<Read> tempArray = new ArrayList<>();
            data.add(new ArrayList<>(listOfRead));
        }
    }

    /**
     * Function that allows setting all 3 colors (gapclosing,reverse,normal) at once, useful if you want the user to pick packages of colors,
     * extra work for the FXML to save the current colors if we only want to change one
     * If FXML implements color-changing another Function will be introduced to allow changing a specific color only
     * @param colorGapCloser new Color of the Gapclosing Reads
     * @param colorReversed new Color of the reversed Reads
     * @param colorNormal new Color of all the normal Reads
     */
    public void changeColor(Color colorGapCloser, Color colorReversed, Color colorNormal){
        circularView.changeColor(colorGapCloser,colorReversed,colorNormal);
    }

}
