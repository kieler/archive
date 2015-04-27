/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2015 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.magicdraw.adapter;

/**
 * @author nbw
 *
 */
public class MagicDrawAdapterException extends RuntimeException {

    /** the serial version UID. */
    private static final long serialVersionUID = -4225901742169095576L;

    
    /**
     * Create an MagicDraw adapter exception with no parameters.
     */
    public MagicDrawAdapterException() {
        super();
    }
    
    /**
     * Create an MagicDraw adapter exception with a message.
     * 
     * @param message a message
     */
    public MagicDrawAdapterException(final String message) {
        super(message);
    }
    
    /**
     * Create an MagicDraw adapter exception with a message and a cause.
     * 
     * @param message a message
     * @param cause a cause
     */
    public MagicDrawAdapterException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    
}
