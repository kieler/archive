package de.cau.cs.kieler.sim.mobile.table;

import java.io.DataInputStream;
import java.io.InputStreamReader;

public class DataReceiver implements Runnable {

	private DataInputStream  dataInputStream;
	private boolean terminate;

	//-------------------------------------------------------------------------
	
	public void terminate() {
		this.terminate = true;
		try {this.dataInputStream.close();}catch(Exception e){}
	}

	//-------------------------------------------------------------------------
	
	public DataReceiver(DataInputStream  dataInputStream) {
		this.dataInputStream = dataInputStream;
	}
	
	//-------------------------------------------------------------------------
	
	public void run() {
		String data;
		
		try {
			InputStreamReader isr = new InputStreamReader(this.dataInputStream);
		
			while (!terminate) {
				String inputData = "";
				while(!terminate) {
					char c = (char)isr.read();
					//detect linebreak
					if (c == 13) break;
					if (c != 10)
						inputData += c;
				}
				MobileDataTable.getInstance().receiveData(inputData);
			}
		} catch(Exception e){
			try {MobileDataTable.getInstance().disconnect();}
			catch(Exception ee){}
		}
	}

}
