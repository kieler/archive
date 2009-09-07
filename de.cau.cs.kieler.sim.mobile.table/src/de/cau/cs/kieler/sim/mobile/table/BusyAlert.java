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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;

/**
 * The Class BusyAlert. An alert that displays a continuously
 * running gauge before automatically timing out. This is used to display
 * an ongoing connection process for example.
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 * 
 */
public class BusyAlert extends Alert {
	
   /** The gauge to be displayed. */
   private Gauge gauge;
   
   //--------------------------------------------------------------------------
   
   /**
    * Instantiates a new busy alert.
    * 
    * @param title the title of the alert
    * @param message the text message to display
    * @param image the image or null
    * @param timeout the time to display the alert
    */
   public BusyAlert( String title,
		   			 String message,
		   			 Image image,
		   			 int timeout){
	   	super(title);
	   	super.setString(message);
	   	super.setImage(image);
	   	super.setTimeout(timeout);
        gauge = new Gauge( null, false,
                Gauge.INDEFINITE,
                Gauge.CONTINUOUS_RUNNING );
	   	super.setIndicator(gauge);
    }
   
}

