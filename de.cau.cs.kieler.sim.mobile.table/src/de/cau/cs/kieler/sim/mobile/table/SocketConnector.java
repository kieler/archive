/******************************************************************************
 * KIELER - Kiel Integrated Environment for Layout for the Eclipse RCP
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 ******************************************************************************/
package de.cau.cs.kieler.sim.mobile.table;

/**
 * The class SocketConnector is used to trigger the connection process
 * asynchronously within a different thread. After the connect-method
 * returned (and the connection was successfully made or an error 
 * prevented the connection) this thread terminates silently.
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 * 
 */
public class SocketConnector implements Runnable {
	
	/**
	 * Instantiates a new SocketConnector thread and promptly starts it.
	 */
	public SocketConnector() {
		new Thread(this).start();
	}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//use  the connect-method of the single MobileDataTable instance
		MobileDataTable.getInstance().connect();
	}

}
