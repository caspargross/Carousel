/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model.statistics;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

import java.io.File;
import java.io.IOException;

/**
 * A class which computes the position specific read coverage (PSRC) which is the amount of reads aligned to a certain
 * position of the reference sequence.
 *
 * @author Mauro Di Girolamo
 */
public class PositionSpecificReadCoverage {

    /**
     * An array containing the amount of reads aligned to a certain position. Its size is the length of the reference
     * sequence and the index correspondent to the positions on the reference sequence minus one (due to the 1-based
     * coordinate system of SAM/BAM files).
     */
    private static int[] positionSpecificReadAmount;

    /**
     * Returns PositionSpecificReadCoverage.positionSpecificReadAmount.
     *
     * @return PositionSpecificReadCoverage.positionSpecificReadAmount
     */
    public static int[] getPositionSpecificReadAmount( ) {
        return positionSpecificReadAmount;
    }


    /**
     * The total amount of reads aligned to the reference sequence.
     */
    private static int totalReadsAlignedAmount;

    /**
     * Returns PositionSpecificReadCoverage.totalReadsAlignedAmount.
     *
     * @return PositionSpecificReadCoverage.totalReadsAlignedAmount
     */
    public int getTotalReadsAlignedAmount( ) {
        return totalReadsAlignedAmount;
    }


    /**
     * A helper class used to store the amount of reads starting and ending at a position.
     */
    private static class PositionSpecificInformation {
        public int readsStartingCount = 0,
                readsEndingCount = 0;
    }


    /**
     * Computes the position specific read coverage given a BAM file and saves it into the positionSpecificReadAmount
     * attribute while also setting currentReadAmount to the total amounts of reads aligned.
     *
     * @param bamFile the BAM file to compute the position specific read coverage from
     *
     * @throws IOException
     */
    public static void computePSRC( File bamFile ) throws IOException {
        SamReader reader = SamReaderFactory.makeDefault( ).open( bamFile );
        final long referenceLength = reader.getFileHeader( ).getSequenceDictionary( ).getReferenceLength( );
        if( referenceLength > Integer.MAX_VALUE ) {
            throw new IOException( "Unfortunately, the reference sequence is too long (its size is bigger than Integer.MAX_VALUE) to compute the position specific read coverage." );
        } else {
            PositionSpecificInformation[] positionSpecificInformation = new PositionSpecificInformation[ ( int ) referenceLength ];
            for( int index = 0; index < positionSpecificInformation.length; index++ ) // a foreach loop won't work here
                positionSpecificInformation[ index ] = new PositionSpecificInformation( );
            for( SAMRecord newReadRecord : reader ) {
                positionSpecificInformation[ newReadRecord.getAlignmentStart( ) - 1 ].readsStartingCount++;
                positionSpecificInformation[ newReadRecord.getAlignmentEnd( ) - 1 ].readsEndingCount++;
                totalReadsAlignedAmount++;
            }
            positionSpecificReadAmount = new int[ ( int ) referenceLength ];
            int currentReadAmount = 0;
            for( int i = 0; i < positionSpecificInformation.length; i++ ) {
                currentReadAmount += positionSpecificInformation[ i ].readsStartingCount;
                currentReadAmount -= positionSpecificInformation[ i ].readsEndingCount;
                positionSpecificReadAmount[ i ] = currentReadAmount;
            }
        }
        return;
    }

}
