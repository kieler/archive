package de.cau.cs.kieler.sim.mobile.table;

public class SocketConnector implements Runnable {
	
	public SocketConnector() {
		new Thread(this).start();
	}

	public void run() {
		MobileDataTable.getInstance().connect();
	}

}
