/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

import htsjdk.samtools.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Note: This is the first version of the CircularParser. It does not yet account for sequencing errors (e.g. overlaps
 * in the reference sequence or a read missing bases in homopolymers such as polyA regions). I am currently working on
 * finding a solution to these problems, but firstly, a statistical analysis will be implemented to help finding a good
 * algorithmic solution.
 * <p>
 * Parses a BAM file constructing circular reads if present. A read is called circular if it is aligned at least two
 * times to the reference sequence such that it overlaps both at the beginning and at the end of the reference sequences
 * in a fitting manner such that these occurrences of the same read can be combined to one occurrence.
 *
 * @author Mauro Di Girolamo
 */
public class CircularParser {

    /**
     * A HashMap structured such that duplicate reads can be found easily. This is achieved by using the read names as
     * the index of the HashMap and a list of Read object as the values so that each list contains duplicates of the
     * same read being mapped to different positions of the reference sequence. parseBAMFile( ) uses this attribute to
     * save its output.
     */
    private static HashMap< String, ArrayList< Read > > readMap;

    /**
     * Returns CircularParser.readMap.
     *
     * @return CircularParser.readMap
     */
    public static HashMap< String, ArrayList< Read > > getReadMap( ) {
        return readMap;
    }

    /**
     * Iterates over all mapped reads in a BAM file representing them as Read objects while detecting circular reads and
     * saving these into a convenient representation. The output is saved into the readMap attribute.
     *
     * @param bamFile the BAM file to be parsed
     */
    public static void parseBAMFile( File bamFile ) {
        readMap = new HashMap<>( );
        SamReader reader = SamReaderFactory.makeDefault( ).open( bamFile );
        final long referenceLength = reader.getFileHeader( ).getSequenceDictionary( ).getReferenceLength( );
        for( SAMRecord newReadRecord : reader ) {
            String readName = newReadRecord.getReadName( );
            ArrayList< Read > oldReads;
            if( ( oldReads = readMap.get( readName ) ) != null ) { // We see this read for at least the second time, check for Plasmid:
                Cigar newCigar = newReadRecord.getCigar( );
                boolean newReadSuccessfullyMerged = false,
                        newReadAtStart = newReadRecord.getAlignmentStart( ) == 1, // 1-based coordinate system, so 1 is first position.
                        newReadAtEnd = newReadRecord.getAlignmentEnd( ) == referenceLength;
                if( newCigar.isClipped( ) && ( newReadAtStart || newReadAtEnd ) ) // New Read overlaps at the start or the end of the reference sequence.
                    for( Read oldRead : oldReads ) // Loop through all duplicates of this read which have already been found.
                        if( !oldRead.isCircular( ) ) { // oldRead is a candidate to combine.
                            Cigar oldCigar = oldRead.getCigar( );
                            boolean rightNewLeftOld = newCigar.isRightClipped( ) &&
                                                      oldCigar.isLeftClipped( ) &&
                                                      oldRead.getAlignmentStart( ) == 1, // 1-based coordinate system, so 1 is first position.
                                    leftNewRightOld = newCigar.isLeftClipped( ) &&
                                                      oldCigar.isRightClipped( ) &&
                                                      oldRead.getAlignmentEnd( ) == referenceLength;
                            if( rightNewLeftOld || leftNewRightOld ) { // We can combined oldRead and newReadRecord!
                                constructCircularRead( oldRead, newReadRecord, rightNewLeftOld );
                                if( !newReadSuccessfullyMerged )
                                    newReadSuccessfullyMerged = true;
                                break; // Can only merge one read once.
                            }
                        }
                if( !newReadSuccessfullyMerged ) // We were not able to merge the new read into a old read, so just add it to the proper List in the HashMap
                    oldReads.add( createNewReadFromSAMRecord( newReadRecord ) );
            } else { // First time we see the read, just add it to the HashMap:
                readMap.put( readName, new ArrayList<>( ) );
                readMap.get( readName ).add( createNewReadFromSAMRecord( newReadRecord ) );
            }
        }
        return;
    }


    /**
     * Constructs a circular read given two presentations of the same read aligned fittingly such that one overlaps at
     * the beginning and the other one at the end. Updates alignmentStart / getAlignmentEnd, builds a new fitting Cigar
     * combing the two old ones and transfers the sequence from the newReadRecord if the oldRead is hard clipped. After
     * applying the algorithm, ( Read is combined &lt;=&gt; read.alignmentStart &gt; read.alignmentEnd ) holds.
     *
     * @param oldRead       the read which was already seen by the CircularParser
     * @param newReadRecord the new read found in the BAM record
     * @param oldReadIsLeft whether the oldRead is left (that is to say, mapped to the beginning of the reference
     *                      sequence)
     */
    private static void constructCircularRead( Read oldRead, SAMRecord newReadRecord, boolean oldReadIsLeft ) {
        Cigar newCigar = newReadRecord.getCigar( ),
                oldCigar = oldRead.getCigar( );
        if( oldReadIsLeft ) {
            oldRead.setAlignmentStart( newReadRecord.getAlignmentStart( ) );
            oldRead.setCigar( CigarOperation.concatenateCigars( CigarOperation.copyAndRemoveLast( newCigar ), CigarOperation.copyAndRemoveFirst( oldCigar ) ) );
        } else {
            oldRead.setAlignmentEnd( newReadRecord.getAlignmentEnd( ) );
            oldRead.setCigar( CigarOperation.concatenateCigars( CigarOperation.copyAndRemoveLast( oldCigar ), CigarOperation.copyAndRemoveFirst( newCigar ) ) );
        }
        if( ( oldReadIsLeft ? oldCigar.getFirstCigarElement( ) : oldCigar.getLastCigarElement( ) ).getOperator( ) == CigarOperator.HARD_CLIP ) // oldRead is hard clipped, so we have to transfer the sequence from the newReadRecord.
            oldRead.setSequence( newReadRecord.getReadString( ) );
        return;
    }


    /**
     * Creates a new Read object given a SAMRecord representation of the Read.
     *
     * @param record
     *
     * @return the Read representation of the read
     */
    private static Read createNewReadFromSAMRecord( SAMRecord record ) {
        return new Read( record.getReadName( ), record.getAlignmentStart( ), record.getAlignmentEnd( ), record.getReadNegativeStrandFlag( ), record.getReadString( ), record.getCigar( ) );
    }

}
