/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

import htsjdk.samtools.Cigar;

/**
 * A class representing a Read in a for the project suitable representation.
 *
 * @author Mauro Di Girolamo
 */
public class Read {

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
     *
     * @param name
     * @param alignmentStart
     * @param alignmentEnd
     * @param sequence
     * @param cigar
     */
    public Read( String name, int alignmentStart, int alignmentEnd, String sequence, Cigar cigar ) {
        this.name = name;
        this.alignmentStart = alignmentStart;
        this.alignmentEnd = alignmentEnd;
        this.sequence = sequence;
        this.cigar = cigar;
    }

    /**
     * Checks whether the Read is circular and returns this information as a boolean.
     * <p>
     * Take a look at the {@link CircularParser#constructCircularRead} method to get a better understanding of why this
     * check does the desired.
     *
     * @return true if the Read is circular, false otherwise
     */
    public boolean isCircular( ) {
        return this.alignmentStart > this.alignmentEnd;
    }

}
