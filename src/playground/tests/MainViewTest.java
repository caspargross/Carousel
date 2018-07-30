package playground.tests;

import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import main.model.CircularParser;

import java.io.File;


import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import main.model.Read;
import main.view.CircularView.CircularView;
import main.view.Helper.GlobalInformation;
import main.view.Helper.Coordinate;

import java.util.List;

public class MainViewTest extends Application {
    private double downX, downY;

    @Override
    public void start( Stage primaryStage ) throws Exception {

        Pane myPane = new Pane( );
        primaryStage.setTitle( "Test of the MainView class" );
        primaryStage.setScene( new Scene( myPane, 400, 400));
        primaryStage.show( );

        final File BAMFileToTest = new File("./data/sampleC_02_lr_mapped.bam"),
                   BAIFileToTest = new File("./data/sampleC_02_lr_mapped.bam.bai"),
                   referenceSequenceFile = new File("./data/sampleC_02.fasta");

        CircularParser.parse(referenceSequenceFile, BAMFileToTest, BAIFileToTest);
        int referenceLength = CircularParser.ReferenceSequences.Current.getLength();
        ObservableList< List< Read > > temp = CircularParser.Reads.getReadsSorted();
        int longAmount = 0;
        for(int i = 0; i < temp.size();i++){
            for(int j = 0; j < temp.get(i).size();j++){
                if(temp.get(i).get(j).getAlignmentLength()/referenceLength >0.5)longAmount++;
            }
        }
        DoubleProperty height = new SimpleDoubleProperty(2);
        Coordinate center = new Coordinate(450,450);
        GlobalInformation gInfo = new GlobalInformation();
        CircularView circularView = new CircularView(temp,gInfo);
        System.out.println(circularView.getReadViews().length);
        for (int i = 0; i <circularView.getReadViews().length; i++){
            myPane.getChildren().add(circularView.getReadViews()[i].getArchSegment().getInner());
        }


        System.out.println(CircularParser.Reads.getReadsSorted());
        System.out.println(longAmount);
        long timeBefore, timeAfter;
        timeBefore = System.currentTimeMillis();
        myPane.getTransforms().add(Transform.rotate(10,450,450));
        timeAfter = System.currentTimeMillis();
        System.out.println("Rotation took " +(timeAfter-timeBefore) + " milliseconds");
        timeBefore = System.currentTimeMillis();
        myPane.getTransforms().add(Transform.rotate(10,450,450));
        timeAfter = System.currentTimeMillis();
        System.out.println("Rotation took " +(timeAfter-timeBefore) + " milliseconds");
        timeBefore = System.currentTimeMillis();
        myPane.getTransforms().add(Transform.rotate(10,450,450));
        timeAfter = System.currentTimeMillis();
        System.out.println("Rotation took " +(timeAfter-timeBefore) + " milliseconds");
        timeBefore = System.currentTimeMillis();
        myPane.getTransforms().add(Transform.rotate(10,450,450));
        timeAfter = System.currentTimeMillis();
        System.out.println("Rotation took " +(timeAfter-timeBefore) + " milliseconds");


        myPane.setOnMousePressed((me) ->{
            downX = me.getSceneX();

        });


        myPane.setOnMouseDragged((me) ->{
            myPane.setCache(true);
            myPane.setCacheHint(CacheHint.SPEED);
            long timeBefore2 = System.currentTimeMillis();
            double deltaX = downX -me.getSceneX();
            //myPane.getTransforms().add(Transform.rotate(Math.toRadians(deltaX),450,450));
            myPane.getTransforms().set(0,myPane.getTransforms().get(0).createConcatenation(Transform.rotate(Math.toRadians(deltaX),450,450)));
            long timeAfter2 = System.currentTimeMillis();
            System.out.println("Rotation took " +(timeAfter2-timeBefore2) + " milliseconds");
            myPane.setCache(false);
        });
        /*
        CircularParser.Reads.getReadsSorted().addListener((ListChangeListener)(c -> {
            ObservableList< List< Read > > temp = CircularParser.Reads.getReadsSorted();
            int referenceLength = CircularParser.getReferenceSequenceLength();
            System.out.println("list changed" + CircularParser.getReferenceSequenceLength());
            DoubleProperty height = new SimpleDoubleProperty(2);
            Coordinate center = new Coordinate(450,450);
            GlobalInformation gInfo = new GlobalInformation(center,100,height.getValue(),referenceLength);
            System.out.println(temp.toString());
            CircularView circularView = new CircularView(temp,gInfo);
            for (int i = 0; i < circularView.getReadViews().length-50; i++){
                myPane.getChildren().add(circularView.getReadViews()[i].getArchSegment().getInner());
            }

        }));
        myPane.setOnMouseClicked((me)->{
            try {
                CircularParser.parse(BAMFileToTest);

            }
            catch (Exception e){

            }

        });*/
        return;
    }


    /**
     * The main procedure of the test.
     *
     * @param args unused parameter
     */
    public static void main( String[] args ) {
        launch(args);

    }
}
