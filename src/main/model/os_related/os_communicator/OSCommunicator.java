/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model.os_related.os_communicator;

import java.io.*;

/**
 * Base class of an OSCommunicator.
 *
 * @author Mauro Di Girolamo
 */
abstract public class OSCommunicator {

    /**
     * The output stream of the last command executed.
     */
    private InputStream lastOutputStream = null;

    /**
     * Returns OSCommunicator.lastOutputStream.
     *
     * @return OSCommunicator.lastOutputStream
     */
    public InputStream getLastOutputStream( ) {
        return lastOutputStream;
    }

    /**
     * The error stream of the last command executed.
     */
    private InputStream lastErrorStream = null;


    /**
     * Returns OSCommunicator.lastErrorStream.
     *
     * @return OSCommunicator.lastErrorStream
     */
    public InputStream getLastErrorStream( ) {
        return lastErrorStream;
    }

    /**
     * Executes a given command in the OS specific shell.
     *
     * @param command the command to execute
     *
     * @throws IOException
     */
    public void executeCommand( String command ) throws IOException {
        Process shellProcess = new ProcessBuilder( getShellName( ) ).start( );
        BufferedWriter shellWriter = new BufferedWriter( new OutputStreamWriter( shellProcess.getOutputStream( ) ) );
        shellWriter.write( command + "\n" );
        shellWriter.close( );
        /*
        The following assignment is correct.
        From the official oracle documentation:
        "getInputStream( ): Returns the input stream connected to the normal output of the subprocess.."
         */
        lastOutputStream = shellProcess.getInputStream( );
        lastErrorStream = shellProcess.getErrorStream( );
        return;
    }

    /**
     * Exectues a given command and returns true if the command executes without errors.
     *
     * @param command the command to execute
     *
     * @return whether the command executed without errors
     *
     * @throws IOException
     */
    public boolean commandExecutesWithoutErrors( String command ) throws IOException {
        executeCommand( command );
        return readInputStreamToString( lastErrorStream ).isEmpty( );
    }

    /**
     * Reads an InputStream into a String and returns it.
     *
     * @param is the InputStream to convert
     *
     * @return the InputStream read into a String
     *
     * @throws IOException
     */
    public String readInputStreamToString( InputStream is ) throws IOException {
        if( is == null )
            throw new NullPointerException( "OSCommunicator.readInputStreamToString( is ): Parameter is equals null." );
        else {
            BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
            StringBuilder builder = new StringBuilder( );
            String line = null;
            while( ( line = reader.readLine( ) ) != null )
                builder.append( line + System.getProperty( "line.separator" ) );
            return builder.toString( );
        }
    }

    /**
     * Returns the name of the shell to be used ofor the specific OS types.
     *
     * @return
     */
    abstract String getShellName( );

}