package de.cau.cs.kieler.sim.mobile.table;

import javax.microedition.lcdui.Display;

public class Backlight implements Runnable {
	
	long endTime;
	
	public Backlight(int milliseconds) {
		endTime = System.currentTimeMillis() + milliseconds;
		new Thread(this).start();
	}

	public void run() {
		while(System.currentTimeMillis() < endTime) {
			try{
				Display display = 
					Display.getDisplay(MobileDataTable.getInstance());
				display.flashBacklight(100);
				Thread.sleep(50);
			}catch(Exception e){}
		}
	}

}
