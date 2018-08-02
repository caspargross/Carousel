/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

import htsjdk.samtools.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Design related note: Defining multiple nested class resulting into a rather big class is a drawback willingly taken
 * to model the relation and natural high coupling of the classes. Using nested classes substructures a class
 * maintaining the desired private visibilities while on top of that being able to be defined static to model the
 * context appropriately. Despite the desire of having static attributes, the absence of a native singleton support of
 * Java would have anyway made solutions necessary that I find ugly. Moreover, using nested classes many getter are
 * unnecessary.
 */

/**
 * Note: This is the first version of the CircularParser. It does not yet account for sequencing errors (e.g. overlaps
 * in the reference sequence or a read missing bases in homopolymers such as polyA regions).
 * <p>
 * This is the core class of the model. Detects cross-border reads which are reads aligned at least two times to the
 * reference sequence such that it overlaps both at the beginning and at the end of the reference sequences in a fitting
 * manner such that these occurrences of the same read can be combined to one occurrence.
 * <p>
 * Apart from that, offers many interfaces to be used by the view / control.
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
         * - <pre>{@code List( i ) := Shown.get( i )}</pre> be the i-th List in CircularParser.Reads.Shown
         * <p>
         * - <pre>{@code Read( i, j ) := List( i ).get( j )}</pre> be the j-th Read object in the i-th List and
         * <p>
         * - <pre>{@code Indices( List )}</pre> be the set of all possible indices of a List.
         * <p>
         * Then, the sorted structure can be defined as follows:
         * <p>
         * <pre>{@code [ Read( i, j ).getAlignmentStart( ) = i + 1 ] for all i in Indices( CircularParser.Reads.Shown ) and j in
         * Indices( List( i ) )}</pre>
         * <p>
         * and
         * </p>
         * <pre>{@code [ ( j < k ) => ( Read( i, j ).getAlignmentLength( ) >= Read( i, k ).getAlignmentLength( ) ) ] for all i in
         * Indices( CircularParser.Reads.Shown ) and j, k in Indices( List( i ) )}</pre>
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
            int referenceSequenceLength = ReferenceSequences.Current.getLength( );
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
             * Order Read objects such that <pre>{@code readA < readB}</pre> holds, if either
             * <p>
             * <pre>{@code readA is cross-border and readB is not}</pre>
             * <p>
             * or
             * <p>
             * <pre>{@code readA and readB are either both cross-border or both not cross-border and readA.getRandomID( ) <
             * readB.getRandomID( )}</pre>
             * <p>
             * holds.
             */
            public final static Comparator< Read > CrossBorderBeforeRandom = ( Read readOne, Read readTwo ) -> readOne.isCrossBorder( ) ^ readTwo.isCrossBorder( ) ? ( readOne.isCrossBorder( ) ? -1 : 1 ) : ( readOne.getRandomID( ) < readTwo.getRandomID( ) ? -1 : 1 ); // No need to return 0 at any point, because by definition there will never be equal objects in this order.

        }


        /**
         * The default amount of reads to display to the view. The CircularParser.parse( ) method will pass a value such
         * that this amount of reads will be showed by the view with the CrossBorderBeforeRandom order being applied
         * before hiding.
         */
        private static final int defaultAmountOfReadsToShow = 500;

        /**
         * Sets the amount of reads to be shown, that is to say to be contained in CircularParser.Reads.Sorted.
         * <p>
         * Will move Read objects from CircularParser.Reads.Shown to CircularParser.Reads.Hidden or vica versa depending
         * on the parameters and the size of CircularParser.Reads.Shown.
         * <p>
         * The passed order relation is applied before transferring any Read objects from one list to the other.
         *
         * @param amountToShow  the amount of Read objects to be shown
         * @param orderRelation the order relation to be applied before transferring any Read objects
         */
        public static void setAmountOfReadsShown( int amountToShow, Comparator< Read > orderRelation ) throws Exception {
            if( Shown.size( ) == amountToShow )
                return;
            else if( amountToShow < 0 || amountToShow > Shown.size( ) + Hidden.size( ) )
                throw new Exception( "The value of amountToShow must be between 0 and Shown.size( ) + Hidden.size( )." );
            else {
                int amountToTransfer;
                List< Read > source,
                        target;
                if( Shown.size( ) > amountToShow ) { // Hide reads.
                    amountToTransfer = Shown.size( ) - amountToShow;
                    source = Shown;
                    target = Hidden;
                } else { // Shown.size() < amountToShow holds, thus unhide reads.
                    amountToTransfer = amountToShow - Shown.size( );
                    source = Hidden;
                    target = Shown;
                    orderRelation = orderRelation.reversed( ); // Reverse order due to the logic of unhiding.
                }
                if( orderRelation == null )
                    orderRelation = Order.Random;
                Collections.sort( source, orderRelation ); // Apply order relation.
                for( ; amountToTransfer > 0; amountToTransfer-- ) {
                    // Always transfer last Read object from source to target (which is preferable when using an ArrayList):
                    Read readToHide = source.get( source.size( ) - 1 );
                    source.remove( source.size( ) - 1 );
                    target.add( readToHide );
                }
                createSorted( ); // Create the new new Sorted list.
                return;
            }
        }

    }


    /**
     * This class contains all reference sequences resulting from parsing.
     */
    public static class ReferenceSequences {

        /**
         * A list of FastaSequence objects containing the raw parsed data.
         */
        private static List< FastaSequence > referenceSequences;

        /**
         * The index of the reference sequence from the referenceSequences list which is currently selected.
         */
        private static int indexOfCurrentReferenceSequence;


        /**
         * Parses a FASTA file and extracts the reference sequences contained.
         *
         * @param referenceSequence              the FASTA file to extract the sequences from
         * @param referenceSequenceIndexToSelect the index of the reference sequence which should be selected
         *
         * @throws Exception
         */
        private static void parseReferenceSequences( File referenceSequence, int referenceSequenceIndexToSelect ) throws Exception {
            referenceSequences = FastaParser.parse( referenceSequence, FastaSequence.Code.NucleicAcid );
            if( referenceSequences.size( ) == 0 )
                throw new Exception( "No reference sequence was found in file " + referenceSequence.toPath( ) + "!" );
            if( referenceSequenceIndexToSelect < 0 || referenceSequenceIndexToSelect > referenceSequences.size( ) - 1 )
                throw new Exception( "The value of referenceSequenceIndexToSelect is not between 0 and referenceSequences.size() - 1." );
            indexOfCurrentReferenceSequence = referenceSequenceIndexToSelect;
            return;
        }


        /**
         * Returns a list of string objects containing the identifiers of all the reference sequences.
         *
         * @return a list of string objects containing the identifiers of all the reference sequences
         */
        public static List< String > getIdentifiers( ) {
            return referenceSequences.stream( ).map( FastaSequence::getIdentifier ).collect( Collectors.toList( ) );
        }


        /**
         * This class contains attributes and methods regarding the current selected reference sequence.
         */
        public static class Current {

            /**
             * Returns CircularParser.ReferenceSequences.referenceSequences.get( indexOfCurrentReferenceSequence ).
             *
             * @return CircularParser.ReferenceSequences.referenceSequences.get(indexOfCurrentReferenceSequence)
             */
            public static FastaSequence getFastaSequence( ) {
                return referenceSequences.get( indexOfCurrentReferenceSequence );
            }


            /**
             * Returns CircularParser.ReferenceSequences.referenceSequences.get( indexOfCurrentReferenceSequence
             * ).getSequence( ).length( ).
             *
             * @return CircularParser.ReferenceSequences.referenceSequences.get(indexOfCurrentReferenceSequence
             *).getSequence().length()
             */
            public static int getLength( ) {
                return referenceSequences.get( indexOfCurrentReferenceSequence ).getSequence( ).length( );
            }

        }

    }


    /**
     * Iterates over all mapped reads in order to detect cross-border reads and save these into a convenient
     * representation setting the attributes of the ReferenceSequences and Reads classes.
     * <p>
     * The SamReader.query( ) method will be used if readsBAIFile is not null. Due to the fact that there have in fact
     * been contradicting measurements on different hardware as to whether the SamReader.query( ) version is actually
     * faster than the version with just looping over the entire BAM file once, both versions remain implemented.
     *
     * @param referenceSequencesFASTAFile a FASTA file containing the reference sequence(s)
     * @param referenceSequenceToSelect   the index of the reference sequence to be used
     * @param readsBAMFile                a BAM file containing the reads to be parsed
     * @param readsBAIFile                the reads BAI file, may be null
     */
    public static void parse( File referenceSequencesFASTAFile, int referenceSequenceToSelect, File readsBAMFile, File readsBAIFile ) throws Exception {

        /*
        Remove all reads currently parsed from the internal lists:
         */
        Reads.Hidden = new ArrayList<>( );
        Reads.Shown = new ArrayList<>( );
        Read.resetRandomNumbers( );

        /*
        Open reference sequence FASTA, reads BAM and BAI file:
         */
        ReferenceSequences.parseReferenceSequences( referenceSequencesFASTAFile, referenceSequenceToSelect );
        SamReader reader = SamReaderFactory.makeDefault( ).open( readsBAIFile != null ? SamInputResource.of( readsBAMFile ).index( readsBAIFile ) : SamInputResource.of( readsBAMFile ) ); // Open BAI file only if it is available.
        int referenceSequenceLength = ReferenceSequences.Current.getLength( );
        if( referenceSequenceLength != reader.getFileHeader( ).getSequenceDictionary( ).getReferenceLength( ) )
            throw new Exception( "The reference sequences FASTA file does not match the BAM file, because the length of the selected reference sequence differs." );

        /*
        Now, branch into the two cases of having a BAI file or not:
        */
        if( reader.hasIndex( ) ) {

            /*
            BAI file is available.
            Therefore, the SamReader.query( ) method can be used.
             */

            /*
            Get two seperate disjunct read sets, one containing the reads which overlap the interval [1,1] and the other containing the reads which overlap the interval [referenceSequenceLength, referenceSequenceLength]:
            */
            SAMRecordIterator interestingReadsAtBeginningIterator = reader.query( new QueryInterval[]{ new QueryInterval( referenceSequenceToSelect, 1, 1 ) }, false ); // contained: false, so that the reads only need to overlap the interval. I pass a QueryInterval array containing only one element instead of using the overloaded version of the query method which expects only a single interval to be passed, because that version would require passing the reference sequence name as a String rather than its ID.

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
            SAMRecordIterator interestingReadsAtEndIterator = reader.query( new QueryInterval[]{ new QueryInterval( referenceSequenceToSelect, referenceSequenceLength, referenceSequenceLength ) }, false );
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

        } else {

            /*
            BAI file is not available.
            Therefore, the SamReader.query( ) method can not be used.
            */

            /*
            Instead, a HashMap structured such that duplicate reads can be found easily will be created. This is achieved by using the read names as the index of the HashMap and a list of Read object as the values so that each list contains duplicates of the same read being mapped to different positions of the reference sequence.
             */
            HashMap< String, List< Read > > readMap = new HashMap<>( );
            for( SAMRecord newReadRecord : reader ) {
                if( newReadRecord.getReferenceIndex( ) == referenceSequenceToSelect ) {
                    String readName = newReadRecord.getReadName( );
                    List< Read > oldReads;
                    if( ( oldReads = readMap.get( readName ) ) != null ) { // We see this read for at least the second time, check for Plasmid:
                        Cigar newCigar = newReadRecord.getCigar( );
                        boolean newReadSuccessfullyMerged = false,
                                newReadAtStart = newReadRecord.getAlignmentStart( ) == 1, // 1-based coordinate system, so 1 is first position.
                                newReadAtEnd = newReadRecord.getAlignmentEnd( ) == referenceSequenceLength;
                        if( newCigar.isClipped( ) && ( newReadAtStart || newReadAtEnd ) ) // New Read overlaps at the start or the end of the reference sequence.
                            for( Read oldRead : oldReads ) // Loop through all duplicates of this read which have already been found.
                                if( !oldRead.isCrossBorder( ) ) { // oldRead is a candidate to combine.
                                    Cigar oldCigar = oldRead.getCigar( );
                                    boolean rightNewLeftOld = newCigar.isRightClipped( ) &&
                                                              oldCigar.isLeftClipped( ) &&
                                                              oldRead.getAlignmentStart( ) == 1, // 1-based coordinate system, so 1 is first position.
                                            leftNewRightOld = newCigar.isLeftClipped( ) &&
                                                              oldCigar.isRightClipped( ) &&
                                                              oldRead.getAlignmentEnd( ) == referenceSequenceLength;
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
            }

            /*
            Unpack the HashMap and add all values to Reads.Shown:
             */
            Collection< List< Read > > readMapValueCollection = readMap.values( );
            for( List< Read > readMapValueList : readMapValueCollection )
                Reads.Shown.addAll( readMapValueList );

        }

        /*
         * Hide an appropriate amount of reads (which also creates CircularParser.Reads.Sorted):
         */
        Reads.setAmountOfReadsShown( Reads.defaultAmountOfReadsToShow, Reads.Order.CrossBorderBeforeRandom );

        return;
    }

    /**
     * No value passed for referenceSequenceToSelect:
     * <p>
     * Alias of CircularParser.parse( referenceSequencesFASTAFile, 0, readsBAMFile, readsBAIFile ).
     *
     * @param referenceSequencesFASTAFile a FASTA file containing the reference sequence(s)
     * @param readsBAMFile                a BAM file containing the reads to be parsed
     * @param readsBAIFile                the reads BAI file, may be null
     *
     * @throws Exception
     */
    public static void parse( File referenceSequencesFASTAFile, File readsBAMFile, File readsBAIFile ) throws Exception {
        parse( referenceSequencesFASTAFile, 0, readsBAMFile, readsBAIFile );
        return;
    }

    /**
     * No value passed for readsBAIFile:
     * <p>
     * Alias of CircularParser.parse( referenceSequencesFASTAFile, referenceSequenceToSelect, readsBAMFile, null ).
     *
     * @param referenceSequencesFASTAFile a FASTA file containing the reference sequence(s)
     * @param referenceSequenceToSelect   the index of the reference sequence to be used
     * @param readsBAMFile                a BAM file containing the reads to be parsed
     *
     * @throws Exception
     */
    public static void parse( File referenceSequencesFASTAFile, int referenceSequenceToSelect, File readsBAMFile ) throws Exception {
        parse( referenceSequencesFASTAFile, referenceSequenceToSelect, readsBAMFile, null );
        return;
    }

    /**
     * No value passed for neither referenceSequenceToSelect nor readsBAIFile:
     * <p>
     * Alias of CircularParser.parse( referenceSequencesFASTAFile, 0, readsBAMFile, null ).
     *
     * @param referenceSequencesFASTAFile a FASTA file containing the reference sequence(s)
     * @param readsBAMFile                a BAM file containing the reads to be parsed
     */
    public static void parse( File referenceSequencesFASTAFile, File readsBAMFile ) throws Exception {
        parse( referenceSequencesFASTAFile, 0, readsBAMFile, null );
        return;
    }


    /**
     * Constructs a cross-border read given two presentations of the same read aligned fittingly such that one overlaps
     * at the beginning and the other one at the end. Updates alignmentStart / getAlignmentEnd, builds a new fitting
     * Cigar combing the two old ones and transfers the sequence from the newReadRecord if the oldRead is hard clipped.
     * After applying the algorithm, <pre>{@code ( Read is combined <=> read.alignmentStart > read.alignmentEnd )}</pre>
     * holds.
     *
     * @param oldRead       the read which was already seen by the CircularParser
     * @param newReadRecord the new read found in the BAM record
     * @param oldReadIsLeft whether the oldRead is left (that is to say, mapped to the beginning of the reference
     *                      sequence)
     *
     * @return oldRead
     */
    private static Read constructCrossBorderRead( Read oldRead, SAMRecord newReadRecord, boolean oldReadIsLeft ) {
        int referenceSequenceLength = ReferenceSequences.Current.getLength( );
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
