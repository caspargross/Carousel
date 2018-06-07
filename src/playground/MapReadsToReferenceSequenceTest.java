/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package playground;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Pair;
import main.model.os_related.OSGateway;
import main.model.os_related.os_communicator.OSCommunicator;
import main.model.os_related.os_communicator.interfaces.CanMapReadsToReferenceSequence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class illustrates how to properly call mapReadsToReferenceSequence( ) using the OS related classes.
 *
 * @author Mauro Di Girolamo
 */
public class MapReadsToReferenceSequenceTest {

    /**
     * Performs a test call of mapReadsToReferenceSequence( ) to demonstrate how to use the OS related classes for this
     * specific matter.
     *
     * @throws IOException
     */
    public void performTest( ) throws IOException {
        OSCommunicator osCommunicator = OSGateway.getCommunicator( ); // Get general OSCommunicator instance.
        if( osCommunicator instanceof CanMapReadsToReferenceSequence ) { // Check if the OSCommunicator is able to map reads to a reference sequence.
            CanMapReadsToReferenceSequence canMapReadsCommunicator = ( CanMapReadsToReferenceSequence ) osCommunicator; // Now we can explicitly cast to the type we need.
            ArrayList< Pair< EventType, EventHandler > > eventHandlerList = new ArrayList<>( ); // A list containing pairs each consisting of a listener and what type of event the listener should be registered to.
            eventHandlerList.add( new Pair<>( WorkerStateEvent.WORKER_STATE_SUCCEEDED, ( EventHandler< WorkerStateEvent > ) t -> System.out.println( "Succeeded" ) ) ); // Add a listener to the list to be triggered when the task succeeded.
            // Adjust the following paths so that they fit your needs:
            File referenceSequenceFile = new File( "reference.fasta" ),
                    readsFile = new File( "reads.fastq" ),
                    outputFile = new File( "out" ); // Please note that outputFile should not contain a file ending, since these will be added by the method, because two files with different file endings, .bam and .bai, will be created.
            canMapReadsCommunicator.mapReadsToReferenceSequence( referenceSequenceFile, readsFile, outputFile, eventHandlerList ); // Call mapReadsToReferenceSequence( ).
        } else // osCommunicator does not implement CanMapReadsToReferenceSequence, hence mapReadsToReferenceSequence( ) is not available.
            System.out.println( "Unfortunately, the feature of mapping reads to a reference sequence is not implemented for your OS." );
        return;
    }


}
