/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

import htsjdk.samtools.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class which parses a FASTA file.
 *
 * @author Mauro Di Girolamo
 */
public class FastaParser {

    /**
     * The prefix of headers used in FASTA files.
     */
    private static final char headerPrefix = '>';


    /**
     * Reads in and parses a FASTA file and returns a respective list of FastaSequence objects.
     * <p>
     * Parses the FASTA file according to this structure:
     * <p>
     * <pre>{@code > [Identifier] [Description]}</pre>
     * <p>
     * [Sequence]
     * <p>
     * That is to say, identifier will contain the first word after the <pre>{@code >}</pre> digit and description will contain the rest of
     * the header line.
     *
     * @param fastaFile the FASTA file to open and parse
     *
     * @return a list of FastaSequence objects
     *
     * @throws Exception
     */
    public static List< FastaSequence > parse( File fastaFile, FastaSequence.Code code ) throws Exception {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( fastaFile ) );
        List< FastaSequence > fastaSequences = new ArrayList<>( );
        String line,
                currentIdentifier = null,
                currentDescription = null;
        StringBuilder currentSequence = new StringBuilder( );
        int lineCounter = 1;
        boolean additionalLoopStep = true;
        while( ( line = bufferedReader.readLine( ) ) != null || additionalLoopStep ) {
            if( line == null ) {
                /*
                Simulate the reading of exactly one additional header at the end such that the last FastaSequence object will be created and added properly. I like this more than extracting parts of the code into a method and then calling the respective method one more time after the loop in order to add the last FastaSequence object.
                 */
                line = String.valueOf( headerPrefix );
                additionalLoopStep = false;
            } else if( StringUtil.isBlank( line ) ) // Ignore empty lines.
                continue;
            if( line.charAt( 0 ) == headerPrefix ) { // Line is a header.
                if( currentIdentifier != null ) { // Found another header instead of a sequence.
                    if( currentSequence.length( ) == 0 )
                        throw new Exception( "Expected line " + lineCounter + " in " + fastaFile.getName( ) + " to contain a sequence." );
                    else
                        fastaSequences.add( new FastaSequence( currentIdentifier, currentDescription, currentSequence.toString( ), code ) );
                }
                currentSequence = new StringBuilder( );
                String effectiveHeader = line.substring( 1 ).trim( );
                int identifierEnd = effectiveHeader.indexOf( " " );
                /*
                Extract identifier and description according to this syntax::
                > [Identifier] [Description]
                 */
                currentIdentifier = effectiveHeader.substring( 0, identifierEnd >= 0 ? identifierEnd : effectiveHeader.length( ) );
                currentDescription = identifierEnd >= 0 ? effectiveHeader.substring( identifierEnd, effectiveHeader.length( ) ).trim( ) : "";
            } else if( currentIdentifier == null ) // Line is not a header line, but we also have no current header.
                throw new Exception( "Expected line " + lineCounter + " in " + fastaFile.getName( ) + " to start with a " + headerPrefix + "." );
            else // Line is not a header, but we have a current header, so append line to currentSequence.
                currentSequence.append( line.trim( ) );
            lineCounter++;
        }
        return fastaSequences;
    }

    /**
     * Alias of FastaParser.parse( fastaFile, null ).
     * <p>
     * If no code is passed, the automatic detection of the codes will be invoked.
     * <p>
     * Note that this can be used to parse a FASTA file containing different types of sequence data, e.g. containing
     * both nucleic acid and amino acid sequences.
     * <p>
     * However in general, not passing an expected code should only be used if necessary, because it might cause a
     * slightly worse performance (if the first tried code does not match the sequence) and also the exception messages
     * can not explicitly name invalid characters if the automatic detections is invoked (follows directly logically
     * from the absence of an expected code) which might make finding coding mistakes harder. Additionally, it might
     * result into detecting an unwanted code if the unwanted code is a subset of the the wanted one, yet tried out
     * firstly. For instance, by IUB/IUPAC code definitions which are set by default, the NucleicAcid code is a subset
     * of the AminoAcid code, so if a sequence ought to be defined over the AminoAcid code contains no character of the
     * AminoAcid code minus the NucleicAcid code, the automatic detection would result into unwittingly setting the
     * sequence to be defined over the NucleicAcid code, because it is tried out firstly.
     *
     * @param fastaFile the FASTA file to parse
     *
     * @return a list of FastaSequence objects
     *
     * @throws Exception
     */
    public static List< FastaSequence > parse( File fastaFile ) throws Exception {
        return FastaParser.parse( fastaFile, null );
    }

}