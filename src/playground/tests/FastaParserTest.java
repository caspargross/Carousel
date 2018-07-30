/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package playground.tests;

import main.model.FastaParser;
import main.model.FastaSequence;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class tests the FastaParser class.
 */
public class FastaParserTest {

    /**
     * The FASTA file to test the parser with.
     */
    private final static File fastaFileToTest = new File( "./data/p7_ref.fasta" );


    /**
     * The code to be passed to the parser.
     */
    private final static FastaSequence.Code codeOfFile = FastaSequence.Code.NucleicAcid;


    /**
     * The main procedure of the test.
     * <p>
     * Firstly, FastaParser.parse( fastaFileToTest ) and secondly, FastaParser.parse( fastaFileToTest, codeOfFile ) will
     * be called with their respective outputs being printed.
     *
     * @param args unused parameter
     *
     * @throws IOException
     */
    public static void main( String[] args ) throws Exception {
        System.out.println( "=== Test without passing codeOfFile -> auto detect code ===" );
        printFastaSequences( FastaParser.parse( fastaFileToTest ) );
        System.out.println( "=== Test with passing codeOfFile ===" );
        printFastaSequences( FastaParser.parse( fastaFileToTest, codeOfFile ) );
        return;
    }

    /**
     * Prints a list of FastaSequence objects to System.out.
     *
     * @param fastaSequencesList the list of FastaSequences to print
     */
    private static void printFastaSequences( List< FastaSequence > fastaSequencesList ) {
        for( int index = 0; index < fastaSequencesList.size( ); index++ ) {
            FastaSequence fastaSequence = fastaSequencesList.get( index );
            System.out.println( "Sequence #" + index + ": {" );
            System.out.println( "\tIdentifier: " + fastaSequence.getIdentifier( ) );
            System.out.println( "\tDescription: " + fastaSequence.getDescription( ) );
            System.out.println( "\tCode: " + fastaSequence.getCode( ) );
            System.out.println( "\tSequence: " + fastaSequence.getSequence( ) );
            System.out.println( "}" );
            System.out.println( );
        }
        return;
    }

}
