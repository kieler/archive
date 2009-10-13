/******************************************************************************
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
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

import javax.microedition.lcdui.Display;

/**
 * The class Backlight implements a separate thread that
 * turns on the back light of the phone display for a
 * duration of time. This duration is defined when creating
 * an instance of this class and promptly starts when the
 * thread is created (and started) in the constructor.
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 * 
 */
public class Backlight implements Runnable {
	
	/** The end time. This is calculated just before the thread for this
	 * back light task is started. */
	long endTime;
	
	//-------------------------------------------------------------------------
	
	/**
	 * Instantiates a new Backlight. It first calculates the endTime and after
	 * that starts the thread that will use the display's flash-method to 
	 * light the phones display until the endTime is passed.
	 * 
	 * @param milliseconds the milliseconds of the duration to light the 
	 * display of the phone
	 */
	public Backlight(int milliseconds) {
		endTime = System.currentTimeMillis() + milliseconds;
		new Thread(this).start();
	}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//for the duration = until endTime passed
		while(System.currentTimeMillis() < endTime) {
			try{
				//trigger the display's flash-method
				Display display = 
					Display.getDisplay(MobileDataTable.getInstance());
				//flash for 50ms (prevents flashing/blinking)
				display.flashBacklight(50);
				//sleep for 25ms
				Thread.sleep(25);
			}catch(Exception e){}
		}
	}

}
