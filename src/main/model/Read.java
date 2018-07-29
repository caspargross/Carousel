/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class representing a Read in a for the project suitable representation.
 *
 * @author Mauro Di Girolamo
 */
public class Read {

    /**
     * The unique, random identification number of the Read object.
     */
    private int randomID;

    /**
     * Returns Read.randomID.
     *
     * @return Read.randomID
     */
    public int getRandomID( ) {
        return randomID;
    }


    /**
     * The unique name of the read.
     */
    private String name;

    /**
     * Returns Read.readName.
     *
     * @return Read.readName
     */
    public String getName( ) {
        return name;
    }


    /**
     * The position at which the alignment of the read starts (with 1 being the first position).
     */
    private int alignmentStart;

    /**
     * Returns Read.alignmentStart.
     *
     * @return Read.alignmentStart
     */
    public int getAlignmentStart( ) {
        return alignmentStart;
    }

    /**
     * Assigns a new value to Read.alignmentStart.
     *
     * @param alignmentStart the new value for Read.alignmentStart
     */
    public void setAlignmentStart( int alignmentStart ) {
        this.alignmentStart = alignmentStart;
    }


    /**
     * The position at which the alignment of the read ends (with 1 being the first position).
     */
    private int alignmentEnd;

    /**
     * Returns Read.alignmentEnd.
     *
     * @return Read.alignmentEnd
     */
    public int getAlignmentEnd( ) {
        return alignmentEnd;
    }

    /**
     * Assigns a new value to Read.alignmentEnd.
     *
     * @param alignmentEnd the new value for Read.alignmentEnd
     */
    public void setAlignmentEnd( int alignmentEnd ) {
        this.alignmentEnd = alignmentEnd;
    }


    /**
     * Checks whether the Read is cross-border and returns this information as a boolean.
     * <p>
     * Take a look at the {@link CircularParser#constructCrossBorderRead} method to get a better understanding of why
     * this check does the desired.
     *
     * @return true if the Read is cross-border, false otherwise
     */
    public boolean isCrossBorder( ) {
        return this.alignmentStart > this.alignmentEnd;
    }


    /**
     * The length of the alignment. I need to explicitly save this, because the reference sequence length is needed to
     * compute a cross-border read's length. Instead of calling CircularParser.getReferenceSequenceLength( ) every time
     * or adding a duplicate static reference sequence length attribute to the Read class, I prefer adding a length
     * attribute which favours lower coupling of the classes.
     * <p>
     * See the documentation of {@link CircularParser#constructCrossBorderRead} to understand this problem better.
     */
    private int alignmentLength;

    /**
     * Returns Read.alignmentLength.
     *
     * @return Read.alignmentLength
     */
    public int getAlignmentLength( ) {
        return alignmentLength;
    }

    /**
     * Assigns a new value to Read.alignmentLength.
     *
     * @param alignmentLength the new value for Read.alignmentLength
     */
    public void setAlignmentLength( int alignmentLength ) {
        this.alignmentLength = alignmentLength;
    }


    /**
     * The mapping quality of the read.
     */
    private int mappingQuality;

    /**
     * Returns Read.mappingQuality.
     *
     * @return Read.mappingQuality
     */
    public int getMappingQuality( ) {
        return mappingQuality;
    }


    /**
     * A boolean flag determining whether the read was reversely read off.
     */
    private boolean negativeStrandFlag;

    /**
     * Returns Read.negativeStrandFlag.
     *
     * @return Read.negativeStrandFlag.
     */
    public boolean getNegativeStrandFlag( ) {
        return negativeStrandFlag;
    }


    /**
     * The sequence of the Read as stored in the BAM file (therefore, for example hard clipped bases are missing).
     */
    private String sequence;

    /**
     * Returns Read.sequence.
     *
     * @return Read.sequence
     */
    public String getSequence( ) {
        return sequence;
    }

    /**
     * Assigns a new value to Read.sequence.
     *
     * @param sequence the new value for Read.sequence
     */
    public void setSequence( String sequence ) {
        this.sequence = sequence;
    }


    /**
     * The Cigar describing how the read aligns with the reference.
     */
    private Cigar cigar;

    /**
     * Assigns a new value to Read.cigar
     *
     * @param cigar the new value for Read.cigar
     */
    public void setCigar( Cigar cigar ) {
        this.cigar = cigar;
    }

    /**
     * Returns Read.cigar.
     *
     * @return Read.cigar
     */
    public Cigar getCigar( ) {
        return cigar;
    }


    /**
     * Creates a new Read.
     * <p>
     * This method is private, because the static Read.createNewReadFromSAMRecord( ) should be used to create a Read
     * object to ensure the random IDs are set properly.
     *
     * @param randomID
     * @param name
     * @param alignmentStart
     * @param alignmentEnd
     * @param sequence
     * @param cigar
     */
    private Read( int randomID, String name, int alignmentStart, int alignmentEnd, int alignmentLength, int mappingQuality, boolean negativeStrandFlag, String sequence, Cigar cigar ) {
        this.randomID = randomID;
        this.name = name;
        this.alignmentStart = alignmentStart;
        this.alignmentEnd = alignmentEnd;
        this.alignmentLength = alignmentLength;
        this.mappingQuality = mappingQuality;
        this.negativeStrandFlag = negativeStrandFlag;
        this.sequence = sequence;
        this.cigar = cigar;
    }


    /**
     * A list of random read list numbers for assuring they are unique for each Read object.
     */
    private static List< Integer > randomIDsAssigned = new ArrayList< Integer >( );

    /**
     * Unassigns a random number such that it is free to be assigned to a different Read object again. This method
     * should be called whenever a Read object is deleted singly. Because destructors are deprecated in Java, I prefer
     * to use this method rather than a destructor.
     *
     * @param randomNumberToUnassign the random number to be unassigned
     */
    public static void unassignRandomNumber( Integer randomNumberToUnassign ) throws Exception {
        if( !randomIDsAssigned.contains( randomNumberToUnassign ) )
            throw new Exception( "Tried to unassign a random number which has not been assigned: " + randomNumberToUnassign );
        else
            randomIDsAssigned.remove( randomNumberToUnassign );
        return;
    }

    /**
     * Assings a new ArrayList to Read.readRandomNumbers, that is to say unassigns all random numbers.
     * <p>
     * Should only be used if there is a 100% certainty that there are currently no Read objects saved anywhere,
     * otherwise duplicate random IDs might be the consequence.
     */
    public static void resetRandomNumbers( ) {
        randomIDsAssigned = new ArrayList<>( );
        return;
    }


    /**
     * A Random generator for the definition of Read.createNewReadFromSAMRecord( ).
     */
    private static Random random = new Random( 1 );

    /**
     * Creates a new Read object given a SAMRecord representation of the Read.
     *
     * @param record
     *
     * @return the Read representation of the read
     */
    public static Read createNewReadFromSAMRecord( SAMRecord record ) throws Exception {
        if( randomIDsAssigned.size( ) == Integer.MAX_VALUE )
            throw new Exception( "Can not handle more Read objects than there are different integer values." );
        else {
            int randomNumber;
            do
                randomNumber = random.nextInt( );
            while( randomIDsAssigned.contains( randomNumber ) );
            return new Read( randomNumber, record.getReadName( ), record.getAlignmentStart( ), record.getAlignmentEnd( ), record.getAlignmentEnd( ) - record.getAlignmentStart( ) + 1, record.getMappingQuality( ), record.getReadNegativeStrandFlag( ), record.getReadString( ), record.getCigar( ) );
        }
    }

}
