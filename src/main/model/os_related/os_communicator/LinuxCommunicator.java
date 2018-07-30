/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model.os_related.os_communicator;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Pair;
import main.model.os_related.os_communicator.interfaces.CanMapReadsToReferenceSequence;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An OSCommunicator specific for Linux. Is able to create a multiple alignment.
 *
 * @author Mauro Di Girolamo
 */
public class LinuxCommunicator extends OSCommunicator implements CanMapReadsToReferenceSequence {

    /**
     * Runs a shell command to map reads to a reference sequence given a file with the reference sequence, a file with
     * reads and an output file path. The shell command will be executed within a new thread. Please note that
     * outputFile should not contain a file ending, since these will be added by the method, because two files with
     * different file endings, .bam and .bai, will be created.
     *
     * @param referenceSequenceFile the file containing the reference sequence with file ending
     * @param readsFile             the file containing the reads with file ending
     * @param outputFile            the output file without file endings
     * @param eventHandlerList      a list of handler to be added to the JavaFX Task
     *
     * @throws IOException
     */
    @Override
    public void mapReadsToReferenceSequence( File referenceSequenceFile, File readsFile, File outputFile, List< Pair< EventType, EventHandler > > eventHandlerList ) throws IOException {
        int threads = this.determineAmountOfCPUCores( ) - 1;
        Task< Void > task = new Task< Void >( ) {
            @Override
            protected Void call( ) throws Exception {
                executeCommand( "minimap2 -ax map-ont -t " + threads + " " + referenceSequenceFile + " " + readsFile + " \\\n" +
                                "| samtools sort \\\n" +
                                "| samtools view -F 4 -b -o " + outputFile + ".bam\n" +
                                "samtools index " + outputFile + ".bam " + outputFile + ".bai" );
                return null;
            }
        };
        for( Pair< EventType, EventHandler > eventPair : eventHandlerList )
            task.addEventHandler( eventPair.getKey( ), eventPair.getValue( ) );
        Thread th = new Thread( task );
        th.setDaemon( true );
        th.start( );
        return;
    }

    /**
     * Determines the amount of virtual CPU cores.
     *
     * @return the amount of virtual CPU cores
     */
    private int determineAmountOfCPUCores( ) {
        return Runtime.getRuntime( ).availableProcessors( );
    }

    /**
     * Returns the specific Linux shell name.
     *
     * @return the specific Linux shell name
     */
    @Override
    protected String getShellName( ) {
        return "bash";
    }

}
