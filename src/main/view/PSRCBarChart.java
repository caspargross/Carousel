/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.view;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import main.model.statistics.PositionSpecificReadCoverage;

/**
 * A class which creates a BarChart to show a certain section of the PSRC.
 * <p>
 * For more information on the PSRC, please take a look at the documentation of the {@link
 * main.model.statistics.PositionSpecificReadCoverage} class.
 *
 * @author Mauro Di Girolamo
 */
public class PSRCBarChart {

    /**
     * Creates a BartChart to show a certain section of the PSRC provided by the model.
     *
     * @param startIndex the start index of the section to show, inclusive
     * @param endIndex   the end index of the section to show, inclusive
     *
     * @return a new BartChart object for the GUI
     *
     * @throws Exception
     */
    public static BarChart createBarChartFromPSRC( int startIndex, int endIndex ) throws Exception {
        int[] positionSpecificReadAmount = PositionSpecificReadCoverage.getPositionSpecificReadAmount( );
        if( startIndex < 0 || positionSpecificReadAmount.length - 1 < endIndex )
            throw new Exception( "startIndex and endIndex must be between 0 and reference sequence length - 1 (" + ( positionSpecificReadAmount.length - 1 ) + ")" );
        else {
            CategoryAxis xAxis = new CategoryAxis( );
            xAxis.setLabel( "Position on reference sequence" );

            NumberAxis yAxis = new NumberAxis( );
            yAxis.setLabel( "Amount of reads aligned" );

            BarChart< String, Number > barChart = new BarChart<>( xAxis, yAxis );
            barChart.setLegendVisible( false );
            barChart.setBarGap( 0 );

            XYChart.Series series = new XYChart.Series( );
            for( int index = startIndex; index <= endIndex; index++ )
                series.getData( ).add( new XYChart.Data( String.valueOf( index ), positionSpecificReadAmount[ index ] ) );
            barChart.getData( ).addAll( series );

            return barChart;
        }
    }

}
