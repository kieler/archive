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

import java.io.DataInputStream;
import java.io.InputStreamReader;

/**
 * The class DataReceiver acts as a receiver thread that listens in a TCP
 * connection for incoming data or commands. For every received line the
 * receiveData-method of the MobileDataTable instance is called. The latter
 * will then process this line and trigger the necessary actions.
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 * 
 */
public class DataReceiver implements Runnable {

	/** The data input TCP stream. */
	private DataInputStream  dataInputStream;
	
	/** The terminate flag set when the {@link #terminate()}-method is 
	 * called. */
	private boolean terminate;

	//-------------------------------------------------------------------------
	
	/**
	 * Terminates the TCP connection on this client side and sets the flag to
	 * terminate the thread, i.e., the {@link #run()}-method.
	 */
	public void terminate() {
		this.terminate = true;
		try {this.dataInputStream.close();}catch(Exception e){}
	}

	//-------------------------------------------------------------------------
	
	/**
	 * Instantiates a new DataReceiver for a given DataInputStream.
	 * 
	 * @param dataInputStream the data input stream
	 */
	public DataReceiver(DataInputStream  dataInputStream) {
		this.dataInputStream = dataInputStream;
	}
	
	//-------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(this.dataInputStream);
			//until this thread should terminate...
			while (!terminate) {
				String inputData = "";
				while(!terminate) {
					char c = (char)isr.read();
					//detect linebreak
					if (c == 13) break;
					if (c != 10)
						inputData += c;
				}
				//handling of the inputData command or data line
				//is done by the following receiveData-method
				MobileDataTable.getInstance().receiveData(inputData);
			}
		} catch(Exception e){
			//if an exception occurs we need to disconnect (if possible)
			//and end this thread
			try {MobileDataTable.getInstance().disconnect();}
			catch(Exception ee){}
		}
	}

}
