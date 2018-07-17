/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package playground.tests;

import main.model.CircularParser;
import main.model.Read;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class can be used to test the CircularParser.parse( ) method.
 *
 * @author Mauro Di Girolamo
 */
public class CircularParserTest {

    /**
     * The files to be used in the test. If necessary, feel free to adjust them to fit your needs.
     */
    private static File referenceSequenceFile = new File( "./data/p7_ref.fasta" ),
            readsFile = new File( "./data/p7_mapped.bam" ),
            indexFile = new File( "./data/p7_mapped.bai" );


    /**
     * An abstract class representing a test case.
     */
    abstract private static class TestCase {
        public abstract void perform( ) throws Exception;
    }

    /**
     * Compares the execution time of CircularParser.parse( ) of the version which uses SamReader.query (and thus
     * requires a BAI file) versus the version without using SamReader.query (and thus just looping over all
     * SamReadRecord objects) and prints the result to System.out.
     */
    private static TestCase compareExecutionTime = new TestCase( ) {
        /**
         * Overrides TestCase.perform( ).
         * @throws Exception
         */
        public void perform( ) throws Exception {
            long timeBefore,
                    timeAfter,
                    timeFirstVersion,
                    timeSecondVersion;

            timeBefore = System.currentTimeMillis( );
            CircularParser.parse( readsFile );
            timeAfter = System.currentTimeMillis( );

            timeFirstVersion = timeAfter - timeBefore;
            printParsingResult( "SamReader.query-version", timeFirstVersion );

            timeBefore = System.currentTimeMillis( );
            CircularParser.parse( referenceSequenceFile, readsFile, indexFile );
            timeAfter = System.currentTimeMillis( );

            timeSecondVersion = timeAfter - timeBefore;
            printParsingResult( "Loop-over-all-SamRecord-objects-version", timeSecondVersion );

            long timeDifference = Math.abs( timeFirstVersion - timeSecondVersion );
            double ratio = timeFirstVersion < timeSecondVersion ? ( ( double ) timeFirstVersion / ( double ) timeSecondVersion ) : ( ( double ) timeSecondVersion / ( double ) timeFirstVersion ),
                    percentageFaster = Math.round( ( 1 - ratio ) * 10000 ) / 100.0;
            System.out.println( "Difference: " + timeDifference + " milliseconds which is " + percentageFaster + "% faster." );

            return;
        }
    };

    /**
     * Prints CircularParser.Reads.Sorted to System.out as a formatted visualization.
     */
    private static TestCase printReadsSorted = new TestCase( ) {
        /**
         * Overrides TestCase.perform( ).
         * @throws Exception
         */
        public void perform( ) throws Exception {
            CircularParser.parse( referenceSequenceFile, readsFile, indexFile );
            List< List< Read > > Sorted = CircularParser.Reads.getReadsSorted( );
            for( int index = 0; index < Sorted.size( ); index++ ) {
                System.out.println( "List #" + index + ": {" );
                for( int readIndex = 0; readIndex < Sorted.get( index ).size( ); readIndex++ ) {
                    Read read = Sorted.get( index ).get( readIndex );
                    System.out.println( "\tRead #" + readIndex + ": Read.getAlignmentStart( ) = " + read.getAlignmentStart( ) + " and Read.getAlignmentLength( ) = " + read.getAlignmentLength( ) + "." );
                }
                System.out.println( "}" + System.getProperty( "line.separator" ) );
            }
            return;
        }
    };

    /**
     * Tests CircularParser.Reads.hide( ) using the CircularParser.Reads.Order.CircularThenRandom order.
     */
    private static TestCase hideReadsCircularThenRandom = new TestCase( ) {
        /**
         * The amount of reads to hide in the test.
         */
        private float amountToHide = 0.994f;

        /**
         * Overrides TestCase.perform( ).
         * @throws Exception
         */
        public void perform( ) throws Exception {
            CircularParser.parse( referenceSequenceFile, readsFile, indexFile );
            int[] readCounts = countReadsParsed( );
            System.out.println( "Before hiding: " + readCounts[ 0 ] + " of " + readCounts[ 1 ] + " reads are circular." );
            CircularParser.Reads.hide( amountToHide, CircularParser.Reads.Order.CrossBorderBeforeRandom );
            readCounts = countReadsParsed( );
            System.out.println( "After hiding: " + readCounts[ 0 ] + " of " + readCounts[ 1 ] + " reads are circular." );
        }
    };

    /**
     * The test which should be performed.
     */
    private static TestCase testCaseToPerform = hideReadsCircularThenRandom;


    /**
     * The main procedure of the test. Performs the TestCase set in CircularParserTest.testCaseToPerform.
     *
     * @param args unused parameter
     *
     * @throws IOException
     */
    public static void main( String[] args ) throws Exception {
        testCaseToPerform.perform( );
        return;
    }


    /**
     * Counts the amount of circular and total reads and returns the information as an integer array of size 2.
     *
     * @return an integer array of size 2 with the first index containing the circular read amount and the second index
     * containing the total read amount
     */
    private static int[] countReadsParsed( ) {
        int totalReadAmount = 0,
                circularReadAmount = 0;
        for( List< Read > readList : CircularParser.Reads.getReadsSorted( ) )
            for( Read read : readList ) {
                if( read.isCrossBorder( ) )
                    circularReadAmount++;
                totalReadAmount++;
            }
        return new int[]{ circularReadAmount, totalReadAmount };
    }


    /**
     * Counts the amount of circular and total reads and prints the result to System.out adding also a given description
     * and a given execution time.
     *
     * @param description  the description which will be prepended to the output
     * @param exectionTime an execution time which will be appended to the output
     */
    private static void printParsingResult( String description, long exectionTime ) {
        int[] readCounts = countReadsParsed( );
        System.out.println( description + ": " + readCounts[ 0 ] + " of " + readCounts[ 1 ] + " reads are circular. Execution time in milliseconds: " + String.valueOf( exectionTime ) + "." );
        return;
    }

}
