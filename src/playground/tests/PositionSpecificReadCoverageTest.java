/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package playground.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.model.statistics.PositionSpecificReadCoverage;
import main.view.PSRCBarChart;

import java.io.File;

/**
 * This class tests the computation of the position specific read coverage (PSCR) and dislaying of the result as a
 * BarChart.
 *
 * @author Mauro Di Girolamo
 */
public class PositionSpecificReadCoverageTest extends Application {

    /**
     * The BAM file to test.
     */
    //private final File BAMFileToTest = new File( "./data/p7_mapped.bam" );
    private final File BAMFileToTest = new File( "../NoDropbox/Test/p7_mapped.bam" );

    /**
     * The start and end indexes of the section of the PSCR to show.
     */
    private final int startIndex = 0,
            endIndex = 10;


    /**
     * The width and height of the Scene which will be shown.
     */
    private final int sceneWidth = 500,
            sceneHeight = 400;


    /**
     * Computes the PSCR and shows a BarChart presenting the result.
     *
     * @param primaryStage
     *
     * @throws Exception
     */
    @Override
    public void start( Stage primaryStage ) throws Exception {
        PositionSpecificReadCoverage.computePSRC( BAMFileToTest );
        Pane myPane = new Pane( );
        myPane.getChildren( ).add( PSRCBarChart.createBarChartFromPSRC( startIndex, endIndex ) );
        primaryStage.setTitle( "Position Specific Read Coverage" );
        primaryStage.setScene( new Scene( myPane, sceneWidth, sceneHeight ) );
        primaryStage.show( );
        return;
    }


    /**
     * The main procedure of the test.
     *
     * @param args unused parameter
     */
    public static void main( String[] args ) {
        launch( args );
        return;
    }


}
