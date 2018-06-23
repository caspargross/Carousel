/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model.os_related;

import main.model.os_related.os_communicator.LinuxCommunicator;
import main.model.os_related.os_communicator.MacCommunicator;
import main.model.os_related.os_communicator.OSCommunicator;
import main.model.os_related.os_communicator.WindowsCommunicator;

/**
 * Static class which serves as a gateway to communicate with the OS. Determines the OS and creates a specific
 * OSCommunicator singleton in its static initialization block.
 *
 * @author Mauro Di Girolamo
 */
public class OSGateway {

    /**
     * Represents the detectable OS types. Each type correspondents to a specific OSCommunicator.
     */
    public enum OSType {
        Windows,
        Linux,
        Mac,
        Unknown
    }

    private static OSType osType;

    /**
     * Retruns OSGateway.osType.
     *
     * @return OSGateway.osType
     */
    public static OSType getOSType( ) {
        return osType;
    }

    /**
     * Singleton which contains an instance of the specific OSCommunicator.
     */
    private static OSCommunicator osCommunicator = null;

    /**
     * Returns OSCommunicator.osCommunicator.
     *
     * @return OSCommunicator.osCommunicator
     *
     * @throws Exception
     */
    public static OSCommunicator getCommunicator( ) throws Exception {
        if( osCommunicator == null )
            throw new Exception( "Either no OSCommunicator is available for the OS used or the detection failed." );
        else
            return osCommunicator;
    }

    /**
     * Static initialization block which determines the OS and creates a specific OSCommunicator singleton.
     */
    static {
        determineOS( );
        createSpecificOSCommunicator( );
    }


    /**
     * Determines the OS setting OSCommunicator.osType accordingly.
     */
    private static void determineOS( ) {
        String osName = System.getProperty( "os.name" );
        if( osName.contains( "Linux" ) )
            osType = OSType.Linux;
        else if( osName.contains( "Mac" ) )
            osType = OSType.Mac;
        else if( osName.contains( "Windows" ) )
            osType = OSType.Windows;
        else
            osType = osType.Unknown;
        return;
    }


    /**
     * Correspondents each OSType value to the specific OSCommunicator class and creates a respective instance into
     * OSCommunicator.osCommunicator.
     */
    private static void createSpecificOSCommunicator( ) {
        switch( osType ) {
            case Linux:
                osCommunicator = new LinuxCommunicator( );
                break;

            case Mac:
                osCommunicator = new MacCommunicator( );
                break;

            case Windows:
                osCommunicator = new WindowsCommunicator( );
                break;

            case Unknown:
                break;
        }
        return;
    }

}



