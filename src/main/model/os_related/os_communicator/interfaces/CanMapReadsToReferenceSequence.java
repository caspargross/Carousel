/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model.os_related.os_communicator.interfaces;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * OSCommunicators which implement this interface are able to map reads to a reference sequence.
 *
 * @author Mauro Di Girolamo
 */
public interface CanMapReadsToReferenceSequence {

    /**
     * The signature for a function which runs a shell command to map reads to a reference sequence given a file with
     * the reference sequence, a file with reads and an output file path.
     *
     * @param referenceSequenceFile
     * @param readsFile
     * @param outputFile
     *
     * @throws IOException
     */
    abstract void mapReadsToReferenceSequence( File referenceSequenceFile, File readsFile, File outputFile, List< Pair< EventType, EventHandler > > eventHandlerList ) throws IOException;

}