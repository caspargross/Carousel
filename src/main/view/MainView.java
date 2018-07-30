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
import playground.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * class that contains the circularView elements, and in the future the labels/tickmarks in seperate Panes/Objects
 * has mouse-events for scaling/rotating
 */
public class MainView extends AnchorPane {

    private CircularView circularView;
    //private TickView tickView;
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
                constructCircularView(data);
            }

            if (firstParse) {
                constructCircularView(data);
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
    private void constructCircularView( List< List< Read > > listOfReadLists){
        GlobalInformation gInfo = new GlobalInformation();
        circularView = new CircularView(listOfReadLists,gInfo);
        for (int i = 0; i <circularView.getReadViews().length; i++){
            Arc temp = circularView.getReadViews()[i].getArchSegment().getInner();
            this.getChildren().add(temp);
        }
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
            double xtemp = (circularView.info.getCenter().getX()+(((circularView.info.getRadius()+5))*Math.cos(Math.toRadians(ViewHelper.rotationValue.getValue()+90))));
            double ytemp = circularView.info.getCenter().getY()+((circularView.info.getRadius())+5)*-Math.sin(Math.toRadians(ViewHelper.rotationValue.getValue()+90));

            Scale zoom = new Scale(1.5,1.5, xtemp,ytemp);
            Scale negativeScale = new Scale(1/1.5,1/1.5,xtemp,ytemp);
            if (se.getDeltaY() >0 && ViewHelper.scaleCount.getValue()>=0){


                ViewHelper.scaleList.add(zoom);
                ViewHelper.scaleCount.setValue(ViewHelper.scaleCount.getValue()+1);
                this.getTransforms().add(zoom);
                //Change the height
               //circularView.info.height.setValue(circularView.info.height.getValue()*(1/1.4));
                Pane tempPane = new Pane();
                if(18 / (1.5 * ViewHelper.scaleCount.getValue())>= 1) {
                    Line[] tempLineArray = ViewHelper.circleOfTickmarks(circularView.info, 10 / (1.5 * ViewHelper.scaleCount.getValue()), (int) (18 / (1.5 * ViewHelper.scaleCount.getValue())));
                    for (Line aTempLineArray : tempLineArray) {
                        tempPane.getChildren().add(aTempLineArray);
                    }
                }
                ViewHelper.tickmarkList.add(tempPane);
                this.getChildren().add(tempPane);

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
                this.getChildren().remove(ViewHelper.tickmarkList.get(ViewHelper.tickmarkList.size()-1));
                ViewHelper.tickmarkList.remove(ViewHelper.tickmarkList.size()-1);
                this.getTransforms().remove(ViewHelper.scaleList.get(ViewHelper.scaleList.size() - 1));
                ViewHelper.scaleList.remove(ViewHelper.scaleList.size() - 1);
                //Change the height
                //circularView.info.height.setValue(circularView.info.height.getValue()*1.4);

            }
        });

    }

    /**
     * sets the Drag-Implementation. the onMouseMoved ensures that we always have the t-1 current position to compare so whenever a new DragEvent starts we dont get huge Deltas
     * TODO: do math so that dragging clockwise turns it clockwise (propably do polar-coordinate math)
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
            double angle = Math.toRadians(delta2X+delta2Y);
            Rotate rotate = new Rotate(angle,800,500); //TODO: make this depending on the gInfo´s center - penalty is that this only can be created after we´ve parsed something (and thus have a referenceLength + a gINfo object)
            this.getTransforms().add(rotate);
            downX = me.getSceneX();
            downY = me.getSceneY();
            ViewHelper.rotationValue.setValue(ViewHelper.rotationValue.getValue()+angle);
        });
    }

    public void CacheTempSpeed(){
        circularView.cacheToSpeed();
    }

    public void CacheTempQuality(){
        circularView.cacheToQuality();
    }
    public void EnableCache(){
        circularView.enableCacheOfReadViews();
    }
    public void grabData(ObservableList<List<Read>>temp){
        data  = new ArrayList<>();
        for (List<Read> listOfRead:temp){
            ArrayList<Read> tempArray = new ArrayList<>();
            data.add(new ArrayList<>(listOfRead));
        }
    }
    public void changeColor(Color colorGapCloser, Color colorReversed, Color colorNormal){
        circularView.changeColor(colorGapCloser,colorReversed,colorNormal);
    }

}
