/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model.os_related.os_communicator;

/**
 * An OSCommunicator specific for Windows.
 *
 * @author Mauro Di Girolamo
 */
public class WindowsCommunicator extends OSCommunicator {

    /**
     * Returns the specific Windows shell name.
     *
     * @return the specific Windows shell name
     */
    @Override
    protected String getShellName( ) {
        return "cmd";
    }

}