package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import main.model.CircularParser;
import main.model.statistics.PositionSpecificReadCoverage;
import main.view.MainView;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.IOException;


import java.io.File;

import javafx.fxml.FXMLLoader;
import main.view.PSRCBarChart;

public class FxmlController {

    // Controller elements
    // Controller elements
    private File fastaFile;
    private File bamFile;

    // FXML Elements

    @FXML
    private MainView mainPane;

    @FXML
    void menuExit(ActionEvent e)
    {

        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();

        System.out.println("Exit called");
    }

    @FXML
    void menuLoad(ActionEvent e)
    {
        Parent root;
        try {

            root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/FileLoader.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Load");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
            // Hide this current window (if this is what you want)
            //((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void menuHist(ActionEvent e) throws Exception {

        int startIndex = 0;
        int endIndex = 10;


        Stage histStage = new Stage();
        histStage.setTitle("Position specific read coverage");
        PositionSpecificReadCoverage.computePSRC(bamFile);

        Pane histPane = new Pane( );
        histPane.getChildren( ).add( PSRCBarChart.createBarChartFromPSRC( startIndex, endIndex ) );
        histStage.setScene( new Scene( histPane, 500, 500 ) );
        histStage.show( );
        return;
    }





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
            WritableImage img = new WritableImage((int) mainPane.getWidth() + 1, (int) mainPane.getHeight() + 1);

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
    private SplitPane mainSplitPane;

    @FXML
    void initialize() {
    }





    private double downX, downY;

    private void startView() throws Exception {

        //this.mainPane.setStyle("-fx-background-color: #ffffff");

        String baiFileName = bamFile.getAbsolutePath();
        baiFileName = baiFileName.replace(".bam", ".bai");

        CircularParser.parse(fastaFile, bamFile, new File(baiFileName));


    }


}
