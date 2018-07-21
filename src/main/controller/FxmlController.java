package main.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import main.model.CircularParser;
import main.model.Read;
import main.view.CircularView;
import main.view.GlobalInformation;
import playground.Coordinate;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.IOException;


import java.io.File;
import java.util.List;

public class FxmlController {

    @FXML
    private Pane mainPane;

    @FXML
    void menuOpenFasta (ActionEvent e) {

        FileChooser fc = new FileChooser();
        fc.setTitle("OpenBamFile");


        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("FASTA files (*.fasta)", "*.fasta");
        fc.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File myFasta = fc.showOpenDialog(null);
        if(myFasta != null) {
            System.out.println("File Open Button pressed");
            System.out.println(myFasta.toString());
            //code to load the bam file and do sth meaningful with it
        }
    }






    @FXML
    void menuSaveImage (ActionEvent e) {

        FileChooser fc = new FileChooser();
        fc.setTitle("OpenBamFile");


        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fc.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File myImage = fc.showSaveDialog(null);
        if(myImage != null) {
            System.out.println("Save Button pressed");
            Scene scene = mainPane.getScene(); //.snapshot();
            WritableImage img = new WritableImage((int) scene.getWidth() + 1, (int) scene.getHeight() + 1);
            scene.snapshot(img);

            BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
            try {
                ImageIO.write(bImage, "png", myImage);
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }

    }


    @FXML
    void menuOpenBam (ActionEvent e) {

        FileChooser fc = new FileChooser();
        fc.setTitle("OpenBamFile");


        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("BAM files (*.bam)", "*.bam");
        fc.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File myBam = fc.showOpenDialog(null);
        if(myBam != null) {
            System.out.println("File Open Button pressed");
            System.out.println(myBam.toString());
            //code to load the bam file and do sth meaningful with it
        }
    }

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    void initialize() {





        try {
            startView();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private double downX, downY;

    private void startView() throws Exception {

        Pane myPane = new Pane( );

        final File onlyBAMFileToTest = new File("./data/03_plB.bam");
        final File BAMFileToTest = new File("./data/sampleC_02_lr_mapped.bam");
        final File BAIFileToTest = new File("./data/sampleC_02_lr_mapped.bam.bai");
        final File referenceSequencfile = new File("./data/sampleC_02.fasta");

        CircularParser.parse(onlyBAMFileToTest);
        int referenceLength = CircularParser.getReferenceSequenceLength();
        ObservableList< List< Read > > temp = CircularParser.Reads.getReadsSorted();
        int longAmount = 0;
        for(int i = 0; i < temp.size();i++){
            for(int j = 0; j < temp.get(i).size();j++){
                if(temp.get(i).get(j).getAlignmentLength()/referenceLength >0.5)longAmount++;
            }
        }
        DoubleProperty height = new SimpleDoubleProperty(2);
        Coordinate center = new Coordinate(450,450);
        GlobalInformation gInfo = new GlobalInformation(center,100,height.getValue(),referenceLength);
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
        System.out.println("MyPane Children: ");
        System.out.println(myPane.getChildren().toString());

        this.mainPane.getChildren().addAll(myPane.getChildren());
        System.out.println("Children added:");
        System.out.println(mainPane.getChildren().toString());

    }


}
