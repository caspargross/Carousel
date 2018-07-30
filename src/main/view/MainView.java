package main.view;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import main.model.CircularParser;
import main.model.Read;
import playground.Coordinate;
import playground.ViewHelper;

import java.util.ArrayList;
import java.util.List;


public class MainView extends AnchorPane {

    private CircularView circularView;
    //private TickView tickView;
    //private LabelView labelView;
    private double downX;
    private double downY;
    private boolean firstParse= true;
    private boolean secondWindow = false;
    private Stage secondStage;
    private ObservableList< List< Read > > data;
    private ArrayList<Point2D> point2DArrayList = new ArrayList<Point2D>();
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
    private void constructCircularView( ObservableList< List< Read > > listOfReadLists){
        int referenceLength = CircularParser.ReferenceSequences.Current.getLength();

        //GlobalInformation gInfo = new GlobalInformation(referenceLength);
        System.out.println("List Changed");
        System.out.println("ReferenceLength of "+CircularParser.ReferenceSequences.Current.getLength());

        DoubleProperty height = new SimpleDoubleProperty(2.5);
        Coordinate center = new Coordinate(800,500);
        GlobalInformation gInfo = new GlobalInformation(center,100,height.getValue(),referenceLength);
        circularView = new CircularView(listOfReadLists,gInfo);
        //circularView.enableCacheOfReadViews();
        List<Node> newChildren = new ArrayList<>();
        for (int i = 0; i <circularView.getReadViews().length; i++){
            Arc temp = circularView.getReadViews()[i].getArchSegment().getInner();
            newChildren.add(temp);


        }
        this.getChildren().addAll(newChildren);




    }
    private void setupZoom(){
        this.setOnScroll((se)->{

            double xtemp = (circularView.info.getCenter().getX()+(((circularView.info.getRadius()+5))*Math.cos(Math.toRadians(ViewHelper.rotationValue.getValue()+90))));
            double ytemp = circularView.info.getCenter().getY()+((circularView.info.getRadius())+5)*-Math.sin(Math.toRadians(ViewHelper.rotationValue.getValue()+90));
            //this.getChildren().remove(imageCollection);
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
                    for (int i = 0; i < tempLineArray.length; i++) {
                        tempPane.getChildren().add(tempLineArray[i]);
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
            if(ViewHelper.scaleCount.getValue() >= 70){
                System.out.println("entered the case where a new thread is gonna start");
                openNewSceneWithGivenPane(testRenderInNewScene());
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
            Rotate rotate = new Rotate(angle,800,500);
            //this.getTransforms().add(this.getTransforms().get(0).createConcatenation(rotate));
            this.getTransforms().add(rotate);
            downX = me.getSceneX();
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

    public Pane testRenderInNewScene(){

        Pane returnPane = new Pane();
        final List<Node> listOfCurrentPaneChildren = new ArrayList<>();
        for(Node children :this.getChildren()){

            listOfCurrentPaneChildren.add(children);
        }
        final List<Transform> listOfCurrentPaneTransform = new ArrayList<>();
        for(Transform transform: this.getTransforms()){
            listOfCurrentPaneTransform.add(transform);
        }
        System.out.println(listOfCurrentPaneTransform.toString());
        if(returnPane.getChildren().isEmpty()) System.out.println("empty");

            Task<Pane> renderTask = new Task<Pane>() {

                long timbefore,
                     timeafter;

                @Override
                protected Pane call() throws Exception {
                    timbefore=System.currentTimeMillis();
                    returnPane.getTransforms().addAll(listOfCurrentPaneTransform);
                    returnPane.getChildren().addAll(listOfCurrentPaneChildren);
                    for (Node children:returnPane.getChildren()){
                        children.setCacheHint(CacheHint.QUALITY);
                        children.setCacheHint(CacheHint.SPEED);
                        children.setCache(true);
                    }

                    return returnPane;
                }
                @Override
                protected void succeeded(){
                    timeafter=System.currentTimeMillis();
                    super.succeeded();
                    System.out.println("thread done: Took "+(timeafter-timbefore) );

                }
                @Override
                protected  void cancelled(){
                    System.out.println("thread cancelled");
                }

            };


            //Thread renderThread = new Thread(renderTask);
            //RenderThread.setDaemon(true);
            //renderThread.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(renderTask);
                }
            }).start();


        this.circularView.disableCacheOfReadViews();
        return returnPane;

    }
    public void openNewSceneWithGivenPane(Pane pane){
            if(!secondWindow) {
                Scene scene = new Scene(pane, 1400, 800);
                secondStage = new Stage();
                secondStage.setTitle("ShowCase of the Quality and performance of rendering in a new Task");
                secondStage.setScene(scene);
                secondStage.showAndWait();
                secondWindow=true;
            }
            else{
                secondStage.setScene(new Scene(pane,1400,800));
            }


    }
    public void grabData(ObservableList<List<Read>>temp){
        //this.data = FXCollections.observableArrayList(CircularParser.Reads.getReadsSorted());
        data = FXCollections.observableArrayList();


        for (List<Read> listOfRead:temp){
            ArrayList<Read> tempArray = new ArrayList<>();
            data.add(new ArrayList<>(listOfRead));
        }

        //this.data= FXCollections.observableArrayList(temp);
        //System.out.println(CircularParser.Reads.getReadsSorted().toString());

        //System.out.println(data.toString());
        //System.out.println(CircularParser.Reads.getReadsSorted().toString());
        //FXCollections.copy(data,CircularParser.Reads.getReadsSorted());
    }

}
