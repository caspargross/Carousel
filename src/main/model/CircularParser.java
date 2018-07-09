/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

import htsjdk.samtools.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.*;

/**
 * Note: This is the first version of the CircularParser. It does not yet account for sequencing errors (e.g. overlaps
 * in the reference sequence or a read missing bases in homopolymers such as polyA regions).
 * <p>
 * Parses a BAM file constructing cross-border reads if present. A read is called cross-border if it is aligned at least
 * two times to the reference sequence such that it overlaps both at the beginning and at the end of the reference
 * sequences in a fitting manner such that these occurrences of the same read can be combined to one occurrence.
 *
 * @author Mauro Di Girolamo
 */
public class CircularParser {

    /**
     * This class contains all the Read objects resulting from parsing.
     */
    public static class Reads {

        /**
         * A list containing all Read objects which are hidden and should thus not be displayed by the view.
         */
        private static List< Read > Hidden;


        /**
         * A list containing all Read objects which should be displayed by the view. However, there is only a getter to
         * get this attribute's size, because CircularParser.Reads.Sorted will be presented to the view to get the
         * actual Read objects.
         */
        private static List< Read > Shown;

        /**
         * Returns CircularParser.Shown.size( ).
         *
         * @return CircularParser.Shown.size()
         */
        public static int getReadAmount( ) {
            return Shown.size( );
        }


        /**
         * A list of lists of Read objects which I came up with to help the view to visually align the reads onto the
         * circular reference sequence more easily. The fact that this structure contains many empty lists is knowingly
         * tolerated, because making the algorithm quicker is more valuable than saving space. Especially for this
         * reason, e.g. a SortedMap was not chosen.
         * <p>
         * You can run the printReadsSorted test in CircularParserTest which visualizes the structure to get a quick
         * idea of how it is defined.
         * <p>
         * The list contains lists of Read objects such that the indices of the outer list correspond with the alignment
         * start values of the Read objects contained in an inner list. The Read objects in an inner lists are ordered
         * by their alignment lengths in descending order.
         * <p>
         * More formally: Let
         * <p>
         * - List( i ) := Shown.get( i ) be the i-th List in CircularParser.Reads.Shown
         * <p>
         * - Read( i, j ) := List( i ).get( j ) be the j-th Read object in the i-th List and
         * <p>
         * - Indices( List ) be the set of all possible indices of a List.
         * <p>
         * Then, the sorted structure can be defined as follows:
         * <p>
         * [ Read( i, j ).getAlignmentStart( ) = i + 1 ] for all i in Indices( CircularParser.Reads.Shown ) and j in
         * Indices( List( i ) )
         * <p>
         * and
         * </p>
         * [ ( j < k ) => ( Read( i, j ).getAlignmentLength( ) >= Read( i, k ).getAlignmentLength( ) ) ] for all i in
         * Indices( CircularParser.Reads.Shown ) and j, k in Indices( List( i ) )
         * <p>
         * holds.
         */
        private static ObservableList< List< Read > > Sorted = FXCollections.observableArrayList( );


        /**
         * Returns CircularParser.Reads.Sorted.
         *
         * @return CircularParser.Reads.Sorted
         */
        public static ObservableList< List< Read > > getReadsSorted( ) {
            return Sorted;
        }

        /**
         * Creates CircularParser.Reads.Sorted using CircularParser.Reads.Show. See the attribute's documenation for
         * more information.
         */
        private static void createSorted( ) {
            Collections.sort( Reads.Shown, Order.AlignmentStart );
            List< List< Read > > newSorted = new ArrayList<>( ); // Values will be added to this list first to avoid triggering the listener multiple times.
            /*
             Insert all Read object into the sub lists without order and sort all sub lists afterwards.
             Always directly inserting a Read at its current proper position potentially requires multiple array shifts of the internal array of the array lists, therefore sorting the sub lists once at the end is better.
            */
            for( Read readToInsert : Reads.Shown ) {
                while( newSorted.size( ) < readToInsert.getAlignmentStart( ) ) // Add new ArrayList objects if necessary.
                    newSorted.add( new ArrayList< Read >( ) );
                newSorted.get( readToInsert.getAlignmentStart( ) - 1 ).add( readToInsert );
            }
            for( List< Read > readList : newSorted ) // Sort all sub lists.
                Collections.sort( readList, Order.AlignmentLength.reversed( ) );
            while( newSorted.size( ) < referenceSequenceLength ) // Fill up until the size is referenceSequenceLength if necessary.
                newSorted.add( new ArrayList< Read >( ) );
            Sorted.setAll( newSorted ); // Assign everything at once so that the listener triggers only once.
            return;
        }


        /**
         * A nested class which contains various total orders which can be passed as an argument to
         * CircularParser.Reads.hide( ).
         */
        public static class Order {

            /**
             * Order Read objects by their random ID in ascending order.
             */
            public final static Comparator< Read > Random = Comparator.comparingInt( Read::getRandomID );


            /**
             * Order Read objects by their alignment start in ascending order.
             */
            public final static Comparator< Read > AlignmentStart = Comparator.comparingInt( Read::getAlignmentStart );


            /**
             * Order Read objects by their alignment length in ascending order.
             */
            public final static Comparator< Read > AlignmentLength = Comparator.comparingInt( Read::getAlignmentLength );


            /**
             * Order Read objects such that readA < readB holds, if either
             * <p>
             * readA is cross-border and readB is not
             * <p>
             * or
             * <p>
             * readA and readB are either both cross-border or both not cross-border and readA.getRandomID( ) <
             * readB.getRandomID( ) holds.
             */
            public final static Comparator< Read > CrossBorderBeforeRandom = ( Read readOne, Read readTwo ) -> readOne.isCrossBorder( ) ^ readTwo.isCrossBorder( ) ? ( readOne.isCrossBorder( ) ? -1 : 1 ) : ( readOne.getRandomID( ) < readTwo.getRandomID( ) ? -1 : 1 ); // No need to return 0 at any point, because by definition there will never be equal objects in this order.

        }

        /**
         * Orders CircularParser.Reads.Shown given a total order and then moves the first amountToHide elements from
         * CircularParser.Reads.Shown to CircularParser.Reads.Hidden.
         *
         * @param amountToHide  the total amount of reads to hide
         * @param orderRelation a total order to define which reads will rather be hidden; null means randomly
         */
        public static void hide( int amountToHide, Comparator< Read > orderRelation ) throws Exception {
            if( amountToHide <= 0 )
                throw new Exception( "Invalid value for amountToHide: " + amountToHide + "." );
            else {
                if( orderRelation == null )
                    orderRelation = Order.Random;
                Collections.sort( Shown, orderRelation ); // Apply order relation.
                for( ; amountToHide > 0; amountToHide-- ) {
                    // Move the last Read in Shown (which is preferable when using an ArrayList) to Hidden:
                    Read readToHide = Shown.get( Shown.size( ) - 1 );
                    Shown.remove( Shown.size( ) - 1 );
                    Hidden.add( readToHide );
                }
                createSorted( );
                return;
            }
        }

        /**
         * Overloaded version of CircularParser.Reads.hide( ). Computes the absolute amount of reads and calls the
         * actual implementation of the method.
         *
         * @param percentageToHide the percentage of reads to hide
         * @param orderRelation    an order relation to define which reads will rather be hidden; null means randomly
         */
        public static void hide( float percentageToHide, Comparator< Read > orderRelation ) throws Exception {
            hide( ( int ) ( percentageToHide * Shown.size( ) ), orderRelation );
        }

    }


    /**
     * The length of the reference sequence.
     */
    private static int referenceSequenceLength;

    /**
     * Returns CircularParser.referenceSequenceLength.
     *
     * @return CircularParser.referenceSequenceLength
     */
    public static int getReferenceSequenceLength( ) {
        return referenceSequenceLength;
    }


    /**
     * Iterates over all mapped reads in order to detect cross-border reads and save these into a convenient
     * representation. The Read objects will then be saved into the readList and an appropriate subset of the Read
     * references are added to the viewReadList to be displayed by the view.
     * <p>
     * The algorithmn uses a HashMap structured such that duplicate reads can be found easily. This is achieved by using
     * the read names as the index of the HashMap and a list of Read object as the values so that each list contains
     * duplicates of the same read being mapped to different positions of the reference sequence. parse( ) uses this
     * attribute to save its output.
     *
     * @param referenceSequence a FASTQ file containing the reference sequence
     * @param readsBAMFile      a BAM file containing the reads to be parsed
     * @param readsBAIFile      the BAI file which fits to the BAM file
     */
    public static void parse( File referenceSequence, File readsBAMFile, File readsBAIFile ) throws Exception {

        /*
        Remove all reads currently parsed from the internal lists:
         */
        Reads.Hidden = new ArrayList<>( );
        Reads.Shown = new ArrayList<>( );
        Read.resetRandomNumbers( );

        /*
        Open reference sequence FASTA, reads BAM and BAI file:
         */
        int referenceIndex = 0;
        SamReader reader = SamReaderFactory.makeDefault( ).open( SamInputResource.of( readsBAMFile ).index( readsBAIFile ) );
        referenceSequenceLength = reader.getFileHeader( ).getSequenceDictionary( ).getSequence( referenceIndex ).getSequenceLength( );

        /*
        Get two seperate disjunct read sets, one containing the reads which overlap the interval [1,1] and the other containing the reads which overlap the interval [referenceSequenceLength, referenceSequenceLength]:
         */
        SAMRecordIterator interestingReadsAtBeginningIterator = reader.query( new QueryInterval[]{ new QueryInterval( referenceIndex, 1, 1 ) }, false ); // contained: false, so that the reads only need to overlap the interval. I pass a QueryInterval array containing only one element instead of using the overloaded version of the query method which expects only a single interval to be passed, because that version would require passing the reference sequence name as a String rather than its ID.

        /*
        Add all reads which overlap [1,1] to a HashMap or to Reads.Shown if they are not right clipped:
         */
        HashMap< String, List< Read > > readMap = new HashMap<>( );
        while( interestingReadsAtBeginningIterator.hasNext( ) ) {
            SAMRecord interestingReadRecordAtBeginning = interestingReadsAtBeginningIterator.next( );
            if( !interestingReadRecordAtBeginning.getCigar( ).isLeftClipped( ) )
                Reads.Shown.add( Read.createNewReadFromSAMRecord( interestingReadRecordAtBeginning ) );
            else {
                String interestingReadName = interestingReadRecordAtBeginning.getReadName( );
                List< Read > readsWithSameNameAlreadySeenList = readMap.get( interestingReadName );
                if( readsWithSameNameAlreadySeenList == null ) { // We have not seen a read with this name yet, so put a new list into the HashMap.
                    readsWithSameNameAlreadySeenList = new ArrayList< Read >( );
                    readsWithSameNameAlreadySeenList.add( Read.createNewReadFromSAMRecord( interestingReadRecordAtBeginning ) );
                    readMap.put( interestingReadName, readsWithSameNameAlreadySeenList );
                } else
                    readsWithSameNameAlreadySeenList.add( Read.createNewReadFromSAMRecord( interestingReadRecordAtBeginning ) );
            }

        }
        interestingReadsAtBeginningIterator.close( );

        /*
        Now loop over all reads which overlap [referenceSequenceLength, referenceSequenceLength], either combine them with a fitting read of the readMap or add them to Reads.Shown if we can not combine them.
         */
        SAMRecordIterator interestingReadsAtEndIterator = reader.query( new QueryInterval[]{ new QueryInterval( referenceIndex, referenceSequenceLength, referenceSequenceLength ) }, false );
        while( interestingReadsAtEndIterator.hasNext( ) ) {
            SAMRecord newReadRecord = interestingReadsAtEndIterator.next( );
            String readName = newReadRecord.getReadName( );
            List< Read > oldReads;
            if( newReadRecord.getCigar( ).isRightClipped( ) && ( oldReads = readMap.get( readName ) ) != null ) {
                Read oldRead = oldReads.get( 0 );
                constructCrossBorderRead( oldRead, newReadRecord, true );
                oldReads.remove( 0 );
                Reads.Shown.add( oldRead );
            } else // The read is either nor right clipped or do not have any candidate to merge, so just add this read to Reads.Shown:
                Reads.Shown.add( Read.createNewReadFromSAMRecord( newReadRecord ) );
        }
        interestingReadsAtEndIterator.close( );

        /*
        Unpack the HashMap and add all values to Reads.Shown:
         */
        Collection< List< Read > > readMapValueCollection = readMap.values( );
        for( List< Read > readMapValueList : readMapValueCollection )
            Reads.Shown.addAll( readMapValueList );

        /*
        Add all other Reads which overlap neither [1,1] nor [referenceSequenceLength, referenceSequenceLength] <=> are completely contained in [2, referenceSequenceLength - 1]:
         */
        SAMRecordIterator uninterestingReadsIterator = reader.query( new QueryInterval[]{ new QueryInterval( 0, 2, referenceSequenceLength - 1 ) }, true ); // true: Resulting reads must be contained within the interval.
        while( uninterestingReadsIterator.hasNext( ) )
            Reads.Shown.add( Read.createNewReadFromSAMRecord( uninterestingReadsIterator.next( ) ) );
        uninterestingReadsIterator.close( );

        /*
         * Create CircularParser.Reads.Sorted:
         */
        Reads.createSorted( );

        return;
    }

    /**
     * Alternative version of CircularParser.parse( ) which does not require a BAI file. Therefore, it is a bit less
     * efficient (around 20% slower in my local measurements). Uses a HashMap as well.
     *
     * @param readsBAMFile a BAM file containing the reads to be parsed
     */
    public static void parse( File readsBAMFile ) throws Exception {

        /*
        Remove all reads currently parsed from the internal lists:
         */
        Reads.Hidden = new ArrayList<>( );
        Reads.Shown = new ArrayList<>( );

        HashMap< String, List< Read > > readMap = new HashMap<>( );
        SamReader reader = SamReaderFactory.makeDefault( ).open( readsBAMFile );
        final long referenceLength = reader.getFileHeader( ).getSequenceDictionary( ).getReferenceLength( );

        /*
        Detect cross-border reads:
         */
        for( SAMRecord newReadRecord : reader ) {
            String readName = newReadRecord.getReadName( );
            List< Read > oldReads;
            if( ( oldReads = readMap.get( readName ) ) != null ) { // We see this read for at least the second time, check for Plasmid:
                Cigar newCigar = newReadRecord.getCigar( );
                boolean newReadSuccessfullyMerged = false,
                        newReadAtStart = newReadRecord.getAlignmentStart( ) == 1, // 1-based coordinate system, so 1 is first position.
                        newReadAtEnd = newReadRecord.getAlignmentEnd( ) == referenceLength;
                if( newCigar.isClipped( ) && ( newReadAtStart || newReadAtEnd ) ) // New Read overlaps at the start or the end of the reference sequence.
                    for( Read oldRead : oldReads ) // Loop through all duplicates of this read which have already been found.
                        if( !oldRead.isCrossBorder( ) ) { // oldRead is a candidate to combine.
                            Cigar oldCigar = oldRead.getCigar( );
                            boolean rightNewLeftOld = newCigar.isRightClipped( ) &&
                                                      oldCigar.isLeftClipped( ) &&
                                                      oldRead.getAlignmentStart( ) == 1, // 1-based coordinate system, so 1 is first position.
                                    leftNewRightOld = newCigar.isLeftClipped( ) &&
                                                      oldCigar.isRightClipped( ) &&
                                                      oldRead.getAlignmentEnd( ) == referenceLength;
                            if( rightNewLeftOld || leftNewRightOld ) { // We can combined oldRead and newReadRecord!
                                constructCrossBorderRead( oldRead, newReadRecord, rightNewLeftOld );
                                if( !newReadSuccessfullyMerged )
                                    newReadSuccessfullyMerged = true;
                                break; // Can only merge one read once.
                            }
                        }
                if( !newReadSuccessfullyMerged ) // We were not able to merge the new read into a old read, so just add it to the proper List in the HashMap
                    oldReads.add( Read.createNewReadFromSAMRecord( newReadRecord ) );
            } else { // First time we see the read, just add it to the HashMap:
                readMap.put( readName, new ArrayList<>( ) );
                readMap.get( readName ).add( Read.createNewReadFromSAMRecord( newReadRecord ) );
            }
        }

        /*
        Unpack the HashMap and add all values to Reads.Shown:
         */
        Collection< List< Read > > readMapValueCollection = readMap.values( );
        for( List< Read > readMapValueList : readMapValueCollection )
            Reads.Shown.addAll( readMapValueList );

        /*
         * Create Reads.Sorted:
         */
        Reads.createSorted( );

        return;
    }


    /**
     * Constructs a cross-border read given two presentations of the same read aligned fittingly such that one overlaps
     * at the beginning and the other one at the end. Updates alignmentStart / getAlignmentEnd, builds a new fitting
     * Cigar combing the two old ones and transfers the sequence from the newReadRecord if the oldRead is hard clipped.
     * After applying the algorithm, ( Read is combined &lt;=&gt; read.alignmentStart &gt; read.alignmentEnd ) holds.
     *
     * @param oldRead       the read which was already seen by the CircularParser
     * @param newReadRecord the new read found in the BAM record
     * @param oldReadIsLeft whether the oldRead is left (that is to say, mapped to the beginning of the reference
     *                      sequence)
     *
     * @return oldRead
     */
    private static Read constructCrossBorderRead( Read oldRead, SAMRecord newReadRecord, boolean oldReadIsLeft ) {
        Cigar newCigar = newReadRecord.getCigar( ),
                oldCigar = oldRead.getCigar( );
        if( oldReadIsLeft ) {
            oldRead.setAlignmentStart( newReadRecord.getAlignmentStart( ) );
            oldRead.setAlignmentLength( oldRead.getAlignmentLength( ) + ( referenceSequenceLength - newReadRecord.getAlignmentStart( ) + 1 ) );
            oldRead.setCigar( CigarOperation.concatenateCigars( CigarOperation.copyAndRemoveLast( newCigar ), CigarOperation.copyAndRemoveFirst( oldCigar ) ) );
        } else {
            oldRead.setAlignmentEnd( newReadRecord.getAlignmentEnd( ) );
            oldRead.setAlignmentLength( oldRead.getAlignmentLength( ) + newReadRecord.getAlignmentEnd( ) );
            oldRead.setCigar( CigarOperation.concatenateCigars( CigarOperation.copyAndRemoveLast( oldCigar ), CigarOperation.copyAndRemoveFirst( newCigar ) ) );
        }
        if( ( oldReadIsLeft ? oldCigar.getFirstCigarElement( ) : oldCigar.getLastCigarElement( ) ).getOperator( ) == CigarOperator.HARD_CLIP ) // oldRead is hard clipped, so we have to transfer the sequence from the newReadRecord.
            oldRead.setSequence( newReadRecord.getReadString( ) );
        return oldRead;
    }

}
