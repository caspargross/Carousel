package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import main.model.CircularParser;
import main.model.Read;
import main.model.statistics.PositionSpecificReadCoverage;
import main.view.MainView;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;


import java.io.File;

import javafx.fxml.FXMLLoader;
import main.view.MiddlePane;
import main.view.PSRCBarChart;

public class FxmlController {

    // Controller elements
    // Controller elements
    private File fastaFile=null;
    private File bamFile=null;
    private int amountOfReads;

    // FXML Elements

    private Stage fileChooserWindow = null;

    @FXML
    private VBox fileLoaderVBox;

    @FXML
    private MiddlePane mainPane;

    @FXML
    private javafx.scene.control.TextField fastaPath;


    @FXML
    private javafx.scene.control.TextField bamPath;

    @FXML
    private ToggleGroup tgRadioButtons;

    @FXML
    private ToggleButton rbRandom;

    @FXML
    private ToggleButton rbStart;

    @FXML
    private ToggleButton rbLength;

    @FXML
    private ToggleButton rbCrossBeforeRandom;



    @FXML
    void menuExit(ActionEvent e)
    {

        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();

        System.out.println("Exit called");
    }



    @FXML
    void fileMenuConfirm(ActionEvent e) {
        if (fastaFile != null && bamFile != null) {
            try {
                startView();
                Stage stage = (Stage) fileLoaderVBox.getScene().getWindow();
                stage.close();

            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else {
            Alert inputFileMissing = new Alert(Alert.AlertType.ERROR, "Missing Input File");
        }
    }



    @FXML
    void fileMenuCancel(ActionEvent e) {
        Stage stage = (Stage) fileLoaderVBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    void menuLoad(ActionEvent e)
    {
        Parent root;
        try {

            root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/FileLoader.fxml"));
            fileChooserWindow = new Stage();
            fileChooserWindow.setTitle("Load");
            fileChooserWindow.setScene(new Scene(root, 450, 450));
            fileChooserWindow.show();
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
        PositionSpecificReadCoverage.computePSRC(this.bamFile);

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
            fastaPath.setText(myFasta.toString());
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
            bamPath.setText(myBam.toString());
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

        amountOfReads = 500;


        rbLength=new RadioButton();
        rbStart=new RadioButton();
        rbRandom=new RadioButton();
        rbCrossBeforeRandom=new RadioButton();
        tgRadioButtons = new ToggleGroup();

        rbLength.onActionProperty().setValue(e -> CircularParser.Reads.setAmountOfReadsShown(amountOfReads, CircularParser.Reads.Order.AlignmentLength));
        rbStart.onActionProperty().setValue(e -> CircularParser.Reads.setAmountOfReadsShown(amountOfReads, CircularParser.Reads.Order.AlignmentStart));
        rbRandom.onActionProperty().setValue(e -> CircularParser.Reads.setAmountOfReadsShown(amountOfReads, CircularParser.Reads.Order.Random));
        rbCrossBeforeRandom.onActionProperty().setValue(e -> CircularParser.Reads.setAmountOfReadsShown(amountOfReads, CircularParser.Reads.Order.CrossBorderBeforeRandom));


        tgRadioButtons.selectToggle(rbRandom);

    }





    private double downX, downY;

    private void startView() throws Exception {

        this.mainPane = new MiddlePane();
        this.mainPane.setStyle("-fx-background-color: #ffffff");

        String baiFileName = bamFile.getAbsolutePath();
        baiFileName = baiFileName.replace(".bam", ".bai");

        CircularParser.parse(fastaFile, bamFile, new File(baiFileName));


    }


}
