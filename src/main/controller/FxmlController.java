package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import main.model.CircularParser;
import main.view.MiddlePane;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;

import javax.imageio.ImageIO;
import java.io.IOException;


import java.io.File;

public class FxmlController {

    // FXML Elements

    @FXML
    private AnchorPane mainPane;

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
            this.fastaFile = myFasta;
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

            SnapshotParameters snParams = new SnapshotParameters();
            mainPane.snapshot(snParams, img);

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
            this.bamFile = myBam;
        }


        try {
            startView();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    void initialize() {




    }

    // Controller elements
    File fastaFile;
    File bamFile;



    private double downX, downY;

    private void startView() throws Exception {

        this.mainPane.setStyle("-fx-background-color: #ffffff");
        this.mainPane.getChildren().clear();
        this.mainPane.getChildren().add(new MiddlePane());

        String baiFileName = bamFile.getAbsolutePath();
        baiFileName = baiFileName.replace(".bam", ".bai");

        CircularParser.parse(fastaFile, bamFile, new File(baiFileName));


    }


}
