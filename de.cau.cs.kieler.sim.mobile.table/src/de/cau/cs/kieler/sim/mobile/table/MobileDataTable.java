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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

import org.json.me.JSONArray;
import org.json.me.JSONObject;


public class MobileDataTable extends MIDlet implements CommandListener,
													   ItemCommandListener {

	public Image signalImage;
	public Image variableImage;
	
	private boolean master;

	private String host;
	private int port;
	
	private Display display;
	private Command exit;
	
	private int currentForm;
	private final static int FORM_TABLE = 1;
	private final static int FORM_EDIT = 2;
	private final static int FORM_CONNECT = 3;
	
	ChoiceGroup typeEdit;
	
	private TextField keyEdit; 
	private TextField valueEdit;
	private TableData currentTableData;
	
	private Command connectCommand;
	private Command disconnectCommand;
	private Command okCommand;
	private Command cancelCommand;

	private Command addCommand;
	private Command deleteCommand;
	private Command modifyCommand;
	private Command presentCommand;
	private Command absentCommand;
	
	private Command stepTableCommand;
	private Command pauseTableCommand;
	private Command stepCommand;
	private Command pauseCommand;
	private Command runCommand;
	private Command stopCommand;
	
	private ImageItem stepButton;
	private ImageItem pauseButton;
	private ImageItem runButton;
	private ImageItem stopButton;
	
	private StringItem debug;
	
	private Image stepImage;
	private Image pauseImage;
	private Image runImage;
	private Image stopImage;
	
	private Image stepImageDisabled;
	private Image pauseImageDisabled;
	private Image runImageDisabled;
	private Image stopImageDisabled;
	
	private Form table;
	
	private final long MAXREAD = 100;
	
	private boolean runMode;
	private boolean executing;
	
	private SocketConnection connection;
	private PrintStream writer;
	private DataReceiver reader;
	
	private Coordinates coordiates;
	
	private TableDataList tableDataList;
	private static MobileDataTable instance;
	public Item lastSelected;
	
	//-------------------------------------------------------------------------

	public static MobileDataTable getInstance() {
		return instance;
	}
	
	//-------------------------------------------------------------------------

	public void displayLastSelected() {
		synchronized(this) {
			if (lastSelected != null) {
				if (lastSelected instanceof TableDataItem) {
					this.display.setCurrentItem(lastSelected);
				}
				else {
					//iff master & run -> pause
					if ((this.master)&&(this.runMode))
						this.display.setCurrentItem(this.pauseButton);
					else
						this.display.setCurrentItem(lastSelected);
				}
			}
			else {
				//if nothing was selected, show the table!
				this.display.setCurrent(this.table);
			}
		}
	}
	
	public void setLastSelected(Item item, boolean reset) {
		synchronized(this) {
			if (reset) {
				if (this.lastSelected == item)
					this.lastSelected = null;
			}
			else
				this.lastSelected = item;
		}
	}
	
	public Item getLastSelected() {
		return this.lastSelected;
	}

	//-------------------------------------------------------------------------

	public void editTableEntry(TableData tableData) {
		//create a new tableData
		if (tableData == null) {
			if (!tableDataList.contains("")) {
				tableDataList.add(new TableData(tableDataList));
			}
			tableData = tableDataList.get("");
		}
		
		this.currentTableData = tableData;
		
		//now edit entry
		//connection form
		Form editForm = new Form("Mobile Table - Edit");
		
		String[] types = {"Variable","Signal"};
		typeEdit = new ChoiceGroup("Type", 
												ChoiceGroup.EXCLUSIVE, 
												types, 
												null);
		keyEdit = new TextField("Key", 
											"", 
											255, 
											TextField.ANY); 
		valueEdit = new TextField("Value", 
										   "", 
										   255, 
										   TextField.ANY); 
		
		editForm.append(typeEdit);
		editForm.append(keyEdit);
		editForm.append(valueEdit);
		editForm.addCommand(okCommand);
		editForm.addCommand(cancelCommand);
		editForm.setCommandListener(this);
		
		//set data
		keyEdit.setString(tableData.getKey());
		valueEdit.setString(tableData.getValue());
		if (tableData.isSignal()) {
			typeEdit.setSelectedIndex(1, true);
		}
		else {
			typeEdit.setSelectedIndex(0, true);
		}
		
		currentForm = MobileDataTable.FORM_EDIT;
		this.display.setCurrent(editForm);
	}

	//-------------------------------------------------------------------------
	
	public void sendAllData() {
		String returnString = "";
		for (int c = 0; c < tableDataList.size(); c++) {
			TableData tableData = tableDataList.get(c);
			if (tableData.isModified()) {
				if (!tableData.isSignal()) {
					//if NO signal
					if (!returnString.equals(""))
						returnString += ",";
					String key = tableData.getKey();
					String value = tableData.getValue();
					//only add if there is any value
					if (!value.equals("")) //value = "\"\"";
						returnString += "\""+key+"\":"+value+"}";
				}
				else if (tableData.isPresent()) {
					//if signal is marked as present
					if (!returnString.equals(""))
						returnString += ",";
					String key = tableData.getKey();
					String value = tableData.getValue();
					if (value.equals("")) 
						returnString += "\""+key+"\":{\""
								+JSONSignalValues.presentKey+"\":true}";
					else
						returnString += "\""+key+"\":{\""
								+JSONSignalValues.presentKey
								+"\":true,\"value\":"+value+"}";
				} else {
					//if signals is marked as absent
					if (!returnString.equals(""))
						returnString += ",";
					String key = tableData.getKey();
					String value = tableData.getValue();
					if (value.equals("")) 
						returnString += "\""+key+"\":{\""
									+JSONSignalValues.presentKey+"\":false}";
					else
						returnString += "\""+key+"\":{\""
									+JSONSignalValues.presentKey
									+"\":false,\"value\":"+value+"}";
				}
				//we have sent all modified values => reset
				synchronized(tableData) {
					tableData.setModified(false);
				}
			}
		}
		if (!returnString.equals("")) {
			if (connection != null) {
				this.sendCommand("{"+returnString+"}");
			}
		}
	}

	//-------------------------------------------------------------------------

	public boolean isMaster() {
		return this.master;
	}
	
	//-------------------------------------------------------------------------

	public void setTitle(String title) {
		this.table.setTitle(title);
	}
	
	//-------------------------------------------------------------------------

	public void refreshList() {
		try {
			//backup key of itemToFocus 
			String itemToFocusKey = null;
			boolean found = true; //assume a button was focused
			if (lastSelected instanceof TableDataItem) {
				itemToFocusKey = ((TableDataItem)lastSelected)
										.getTableData().getKey();
				found = false; //ok, an item had the focus
			}
			
			Displayable backup = this.display.getCurrent();
			this.table.deleteAll();

			if (master) {
				table.append(stepButton);
				table.append(runButton);
				table.append(pauseButton);
				table.append(stopButton);
				table.append(new Spacer(200, 10));
			}
			
			
			for (int c = 0; c < tableDataList.size(); c ++) {
				TableData tableData = tableDataList.get(c);
				
				TableDataItem item = new TableDataItem(tableData,
					this.table.getWidth());
				item.setAlignTop(true);

				//try to find old item to focus
				if ((!found) && (tableData.getKey().equals(itemToFocusKey))
				    || ("*"+tableData.getKey()).equals(itemToFocusKey)
				    || (tableData.getKey().equals("*"+itemToFocusKey))) {

					item.setStatus(1); // 1 == IN
					item.refresh();
					lastSelected = item;
					found = true;
				}
				else if (found) {
					//item below the last selected = cursor UPPER
					item.setStatus(0); // 0 == UPPER
				}
				else {
					//item above the last selected = cursor LOWER
					item.setStatus(2); // 2 == LOWER
				}
				
				item.addCommand(this.modifyCommand);
				item.addCommand(this.deleteCommand);
				if (tableData.isSignal()) {
					if (tableData.isPresent()) {
						item.setDefaultCommand(this.absentCommand);
						item.addCommand(this.absentCommand);
					}
					else {
						item.setDefaultCommand(this.presentCommand);
						item.addCommand(this.presentCommand);
					}
				} else {
					item.setDefaultCommand(modifyCommand);
				}
				item.setItemCommandListener(this);
				table.append(item);
			}
			
			this.displayLastSelected();
		}catch(Exception e){}
		new Backlight(10000);
	}

	//-------------------------------------------------------------------------

	/**
	 * Reset the modified display tags. This is called whenever data comes in
	 * or a step has been made. A tableData is only reset to not being 
	 * displayed as modified, if it has already been sent and hence its 
	 * modified tag is already set to false.
	 */
	public void resetModifiedDisplayTags() {
		for (int c = 0; c < tableDataList.size(); c++) {
			TableData tableData = tableDataList.get(c);
			synchronized(tableData) {
				if (tableData.isModifiedDisplay())
					if (!tableData.isModified())
						tableData.setModifiedDisplay(false);
			}
		}
		
	}
	
	//-------------------------------------------------------------------------

	public void receiveData(String data) {
		if (data.startsWith("E")) {
			this.table.addCommand(this.stepCommand);
			this.table.addCommand(this.runCommand);
			this.table.addCommand(this.pauseCommand);
			this.master = true;
			this.setLastSelected(this.lastSelected, true);
			this.refreshList();
		}
		else if (data.startsWith("D")) {
			this.table.removeCommand(this.stopCommand);
			this.table.removeCommand(this.stepCommand);
			this.table.removeCommand(this.runCommand);
			this.table.removeCommand(this.pauseCommand);
			this.master = false;
			this.setLastSelected(this.lastSelected, true);
			this.refreshList();
		}
		else if (data.substring(0, 1).equals("I")) {
			//initial keys
			data = data.substring(1, data.length());
			try {
				JSONObject jSONObject =
								new JSONObject(data);
				int c = 0;
				while (jSONObject.has(c+"")) {
					String interfaceKey = jSONObject.getString(c+"");
					TableData tableData = new TableData(tableDataList);
					tableData.setKey(interfaceKey);
					tableDataList.add(tableData);
					c++;
				}
				this.refreshList();
			}catch(Exception e){}
		}
		else if (data.substring(0, 1).equals("P")) {
			resetModifiedDisplayTags();
			//json data - present step
			data = data.substring(1, data.length());
			try {
				JSONObject jSONObject =
								new JSONObject(data);
				observe(jSONObject, false);
			}catch(Exception e){
				throw new RuntimeException(e.getMessage());
			}
		}
		else if (data.substring(0, 1).equals("H")) {
			resetModifiedDisplayTags();
			//json data - history step
			data = data.substring(1, data.length());
			try {
				JSONObject jSONObject =
								new JSONObject(data);
				observe(jSONObject, true);
			}catch(Exception e){
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	//-------------------------------------------------------------------------

	/**
     * Get an array of field names from a JSONObject.
     *
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Enumeration i = jo.keys();
        String[] names = new String[length];
        int j = 0;
        while (i.hasMoreElements()) {
            names[j] = (String)i.nextElement();
            j += 1;
        }
        return names;
    }

    //-------------------------------------------------------------------------
	
	private void observe(JSONObject allData, boolean historyStep) {
		//create a new temporary list
		Vector tableDataTmp = new Vector();

		try {
			String[] fieldNames = getNames(allData);
			if (fieldNames != null) {
				for (int c = 0; c < fieldNames.length; c++) {
					//extract key, value from JSONObject
					Object obj = allData.get(fieldNames[c]);
					String key = fieldNames[c];
					
					if (key.equals("_KIEMExecutionSteps")) {
						table.setTitle("Mobile Table - "+(String)obj);
						continue;
					}
					
					String value;
					boolean isPresent = false; //default
					boolean isSignal = false;
					if (obj instanceof JSONObject) {
					   //can be a signal
					   isPresent = JSONSignalValues.isPresent((JSONObject)obj);
					   //extract signal value if any
					   if (JSONSignalValues.isSignalValue((JSONObject)obj)) {
							isSignal = true;
							obj = JSONSignalValues
									.getSignalValue((JSONObject)obj);
					   }
					}
					
					//set value according to the object instance
					if (obj == null) {
						value = "";
					}
					else if (obj instanceof Double) {
						value = ((Double)obj)+"";
					}
					else if (obj instanceof Integer) {
						value = ((Integer)obj)+"";
					}
					else if (obj instanceof Boolean) {
						value = ((Boolean)obj).toString();
					}
					else if (obj instanceof JSONObject) {
						value = ((JSONObject)obj).toString();
					}
					else if (obj instanceof JSONArray) {
						value = ((JSONArray)obj).toString();
					}
					else {
						value = "\""+(String)obj+"\"";
					}
					
					//add to table or update table
					if (TableDataList.getInstance().contains(key)) {
						//update
						TableData tableData = TableDataList
												.getInstance().get(key);
						tableData.setSignal(isSignal);
						tableData.setValue(value);
						tableData.setPresent(isPresent);
						tableData.setModified(false);
						tableDataTmp.addElement(tableData);
					}
					else {
						//add
						TableData tableData = new TableData(
								TableDataList.getInstance(),
								isPresent, 
								isSignal,
								key,
								value);
						tableDataTmp.addElement(tableData);
						TableDataList.getInstance().add(tableData);
					}
				}

				if (historyStep) {
					//set all NOT updated entries to absent per default
					int tableSize = TableDataList.getInstance().size();
					for (int c = 0; c < tableSize; c++) {
					  TableData tableData = TableDataList.getInstance().get(c);
					  if (tableData.isPresent()&&(!tableData.isModified())) {
							if(!tableDataTmp.contains(tableData)) {
								tableData.setPresent(false);
							}
					  }
					  if (tableData.isModified()) {
							tableData.setModified(false);
					  }
					}
				}
				
				
			}
		}catch(Exception e){
			//this should not happen
			e.printStackTrace();
		}
				
		//update only if not currently edited
		if (this.currentForm == this.FORM_TABLE) {
			this.refreshList();
			this.display.vibrate(100);
		}
	}
	
	//-------------------------------------------------------------------------

	private void sendCommand(String command) {
		if (connection == null) return;
		try {
			if (writer == null)
				writer = new PrintStream(connection.openDataOutputStream());
			writer.println(command);
			writer.flush();
		}
		catch(Exception e) {
			this.disconnect();
			e.printStackTrace();
		}
		
	}
	
	//-------------------------------------------------------------------------
	
	public MobileDataTable() {
		//set instance
		MobileDataTable.instance = this;
		lastSelected = null;
		master = true;
		
		//default values
		this.signalImage = null;
		try {
			this.signalImage = Image.createImage("/signalIcon.png");
		}catch(Exception e){}
		this.variableImage = null;
		try {
			this.variableImage = Image.createImage("/variableIcon.png");
		}catch(Exception e){}
		this.tableDataList = new TableDataList(this);
		this.host = "socket://www.delphino.net";
		this.port = 2222;
		runMode = false;
		executing = false;
		this.display = Display.getDisplay(this);

		//table form
		table = new Form("Mobile Table");
		
		//define commands
		this.stepTableCommand = new Command("Step", Command.EXIT, 0x00);
		this.pauseTableCommand = new Command("Pause", Command.EXIT, 0x00);

		this.stepCommand = new Command("Step", Command.SCREEN, 0x02);
		this.runCommand = new Command("Run", Command.SCREEN, 0x03);
		this.pauseCommand = new Command("Pause", Command.SCREEN, 0x04);
		this.stopCommand = new Command("Stop", Command.SCREEN, 0x05);
		this.connectCommand = new Command("Connect", Command.SCREEN, 0x08);
		this.disconnectCommand = new Command("Disconnect", Command.SCREEN, 0x09);
		this.exit = new Command("Exit", Command.SCREEN, 0x10);
		
		this.okCommand = new Command("Ok", Command.OK, 0x05);
		this.cancelCommand = new Command("Cancel", Command.CANCEL, 0x05);
		
		this.debug = new StringItem("debug","");

		this.addCommand = new Command("Add", Command.SCREEN, 0x06);
		this.modifyCommand = new Command("Modify", Command.SCREEN, 0x01);
		this.deleteCommand = new Command("Delete", Command.SCREEN, 0x07);
		this.presentCommand = new Command("Present", Command.SCREEN, 0x00);
		this.absentCommand = new Command("Absent", Command.SCREEN, 0x00);
		
		//command button view
		table.setCommandListener(this);
		table.addCommand(this.addCommand);
		table.addCommand(this.exit);
		table.addCommand(this.connectCommand);
		
		//buttons
		try {
			stepImage = Image.createImage("/stepIcon.png");
			pauseImage = Image.createImage("/pauseIcon.png");
			runImage = Image.createImage("/runIcon.png");
			stopImage = Image.createImage("/stopIcon.png");
			stepImageDisabled = Image.createImage("/stepIconDisabled.png");
			pauseImageDisabled = Image.createImage("/pauseIconDisabled.png");
			runImageDisabled = Image.createImage("/runIconDisabled.png");
			stopImageDisabled = Image.createImage("/stopIconDisabled.png");
		}catch(Exception e){
			stepImage = null;
			pauseImage = null;
			runImage = null;
			stopImage = null;
			stepImageDisabled = null;
			pauseImageDisabled = null;
			runImageDisabled = null;
			stopImageDisabled = null;
		}
		
		stepButton = new ImageItem(null, stepImageDisabled, Item.LAYOUT_CENTER, "Step", Item.BUTTON);
		stepButton.addCommand(stepCommand);
		stepButton.setItemCommandListener(this);

		runButton = new ImageItem(null, runImageDisabled, Item.LAYOUT_CENTER, "Run", Item.BUTTON);
		runButton.addCommand(runCommand);
		runButton.setItemCommandListener(this);

		pauseButton = new ImageItem(null, pauseImageDisabled, Item.LAYOUT_CENTER, "Pause", Item.BUTTON);
		pauseButton.addCommand(pauseCommand);
		pauseButton.setItemCommandListener(this);

		stopButton = new ImageItem(null, stopImageDisabled, Item.LAYOUT_CENTER, "Stop", Item.BUTTON);
		stopButton.addCommand(stopCommand);
		stopButton.setItemCommandListener(this);

		//refresh the list
		refreshList();
		
		//current focused button
		this.display.setCurrentItem(this.stepButton);
	}
	
	//-------------------------------------------------------------------------
	
	public void alert(String text) {
		Alert alert = new Alert(text);
		alert.setTimeout(2000);
		alert.setType(AlertType.ERROR);
		currentForm = MobileDataTable.FORM_TABLE;
		this.display.setCurrent(alert,this.table);
		this.display.vibrate(2000);
		this.display.flashBacklight(2000);
	}
	
	//-------------------------------------------------------------------------
	
	public void connectDialog() {
		//connection form
		Form connectionForm = new Form("Mobile Table - Connect");
		
		TextField hostEdit = new TextField("Host", 
											this.host, 
											255, 
											TextField.ANY); 
		TextField portEdit = new TextField("Port", 
										   this.port+"", 
										   5, 
										   TextField.NUMERIC); 
		
		connectionForm.append(hostEdit);
		connectionForm.append(portEdit);
		connectionForm.addCommand(okCommand);
		connectionForm.addCommand(cancelCommand);
		connectionForm.setCommandListener(this);
		
		currentForm = MobileDataTable.FORM_CONNECT;
		this.display.setCurrent(connectionForm);
	}
	
	//-------------------------------------------------------------------------
	
	public void connect() {
		try {
			//System.out.println(this.host + ":" + this.port);
			String url = this.host + ":" + this.port;
			connection = (SocketConnection)
						 Connector.open(url, 
								 		Connector.READ_WRITE,
										true);
			//set button images
			this.stepButton.setImage(stepImage);
			this.runButton.setImage(runImage);
			this.pauseButton.setImage(pauseImage);
			this.stopButton.setImage(stopImageDisabled);
			//set commands
			table.addCommand(this.stepCommand);
			table.addCommand(this.runCommand);
			table.addCommand(this.pauseCommand);
			table.removeCommand(this.connectCommand);
			table.addCommand(this.disconnectCommand);
			//reader thread
			reader = new DataReceiver(connection.openDataInputStream());
			new Thread(reader).start();
		}
		catch(Exception e) {
			try{reader.terminate();}catch(Exception ee){}
			reader = null;
			this.disconnect();
			writer = null;
			try{connection.close();}catch(Exception ee){}
			connection = null;
			alert("Cannot connect. Check host and port.");
			this.table.addCommand(this.connectCommand);
		}
		//set current form back
		this.table.addCommand(this.connectCommand);
		currentForm = MobileDataTable.FORM_TABLE;
		this.display.setCurrent(this.table);
		new Backlight(10000);
	}

	//-------------------------------------------------------------------------
	
	public void disconnect() {
		try {
			if (this.writer != null)
				this.writer.close();
			if (connection != null)
				connection.close();
			this.master = true;
			this.refreshList();
			//set button images
			this.stepButton.setImage(stepImageDisabled);
			this.runButton.setImage(runImageDisabled);
			this.pauseButton.setImage(pauseImageDisabled);
			this.stopButton.setImage(stopImageDisabled);
			this.table.removeCommand(this.stopCommand);
			this.table.removeCommand(this.stepCommand);
			this.table.removeCommand(this.runCommand);
			this.table.removeCommand(this.pauseCommand);
			//set commands
			table.removeCommand(this.disconnectCommand);
			table.addCommand(this.connectCommand);
			//stop thread
			if (reader != null)
				this.reader.terminate();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		table.setTitle("Mobile Table");
		this.tableDataList.removeAll();
		this.refreshList();
		reader = null;
		connection = null;
		writer = null;
	}
	
	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		currentForm = MobileDataTable.FORM_TABLE;
		this.display.setCurrent(this.table);
	}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command, Displayable displayable) {
		if (command == this.exit) {
			this.notifyDestroyed();
		}
//		else if (command == this.tableCommand) {
//			currentForm = MobileDataTable.FORM_TABLE;
//			this.display.setCurrent(this.dataList);
//		}
//		else if (command == this.controlCommand) {
//			currentForm = MobileDataTable.FORM_COMMAND;
//			this.display.setCurrent(this.command);
//		}
		else if (command == this.connectCommand) {
			this.connectDialog();
		}
		else if (command == this.disconnectCommand) {
			this.disconnect();
		}
		else if (command == this.okCommand) {
			if (currentForm == FORM_CONNECT) {
				new SocketConnector();
				this.table.removeCommand(this.connectCommand);
				this.refreshList();
				//this.connect();
			}
			else if (currentForm == FORM_EDIT) {
				//save current modifications
				if (currentTableData != null) {
					currentTableData.setSignal(typeEdit.getSelectedIndex() == 1);
					if (!this.tableDataList.containsOther(this.keyEdit.getString(), currentTableData)) {
						try {
							currentTableData.setKey(this.keyEdit.getString());
						}catch(Exception e) {}
					}
					currentTableData.setValue(this.valueEdit.getString());
					this.currentTableData.setModified(true);
					this.currentTableData.setModifiedDisplay(true);
				}
				currentForm = MobileDataTable.FORM_TABLE;
				this.refreshList();
				sendAllData();					
			}
		}
		else if (command == this.cancelCommand) {
			if (currentForm == FORM_CONNECT) {
				currentForm = MobileDataTable.FORM_TABLE;
			}
			else if (currentForm == FORM_EDIT) {
				this.tableDataList.remove("");
				currentForm = MobileDataTable.FORM_TABLE;
			}
			this.refreshList();
		}
		else if (command == this.addCommand) {
			this.editTableEntry(null);
		}
		
		//do nothing if not connected
		if (this.connection == null) return;
		
		if (((command == this.stepCommand)||(command == this.stepTableCommand)) 
				&& (!this.runMode)) {
			//this.table.setTitle("STEP");
			this.sendCommand("CS");
			this.setExecuting(true);
			this.setRunMode(false);
			this.refreshList();
		}
		else if (((command == this.pauseCommand)||(command == this.pauseTableCommand))
				&& ((this.runMode) || (!this.executing))) {
			//this.table.setTitle("PAUSE");
			this.sendCommand("CP");
			this.setExecuting(true);
			this.setRunMode(false);
			this.lastSelected = this.stepButton;
			this.display.setCurrentItem(this.stepButton);
		}
		else if ((command == this.runCommand) && (!this.runMode)) {
			//this.table.setTitle("RUN");
			this.sendCommand("CR");
			this.setExecuting(true);
			this.setRunMode(true);
			this.lastSelected = this.pauseButton;
			this.display.setCurrentItem(this.pauseButton);
		}
		else if ((command == this.stopCommand) && (this.executing)) {
			//this.table.setTitle("STOP");
			this.sendCommand("CT");
			this.setExecuting(false);
			this.setRunMode(false);
			this.lastSelected = this.stepButton;
			this.display.setCurrentItem(this.stepButton);
		}
	}
	
	//-------------------------------------------------------------------------
	
	public void setExecuting(boolean executing) {
		if ((executing)) {
			this.stopButton.setImage(stopImage);
			this.table.addCommand(this.stopCommand);
			this.executing = true;
		}
		else if ((!executing)) {
			table.setTitle("Mobile Table");
			this.stopButton.setImage(stopImageDisabled);
			this.table.removeCommand(this.stopCommand);
			this.executing = false;
		}
	}
	
	//-------------------------------------------------------------------------

	public void setRunMode(boolean runMode) {
		if ((runMode)) {
			this.table.addCommand(pauseTableCommand);
			this.table.removeCommand(stepTableCommand);
			this.pauseButton.setImage(pauseImage);
			this.runButton.setImage(runImageDisabled);
			this.stepButton.setImage(stepImageDisabled);
			this.table.removeCommand(this.runCommand);
			this.table.removeCommand(this.stepCommand);
			this.table.addCommand(this.pauseCommand);
			this.runMode = true;
		}
		else if ((!runMode)) {
			this.table.addCommand(this.runCommand);
			this.table.addCommand(this.stepCommand);
			if (this.executing) {
				this.table.addCommand(stepTableCommand);
				this.table.removeCommand(pauseTableCommand);
				this.pauseButton.setImage(pauseImageDisabled);
				this.table.removeCommand(this.pauseCommand);
			}
			else {
				this.table.removeCommand(stepTableCommand);
				this.table.removeCommand(pauseTableCommand);
				this.pauseButton.setImage(pauseImage);
				this.table.addCommand(this.pauseCommand);
			}
			this.runButton.setImage(runImage);
			this.stepButton.setImage(stepImage);
			this.runMode = false;
		}
	}

	//-------------------------------------------------------------------------

	public void commandAction(Command command, Item item) {
		//lastSelected = item;
		//forward action of buttons to action of form-command
		if (command == this.presentCommand) {
			if (item instanceof TableDataItem) {
				TableDataItem tableDataItem = 
					(TableDataItem)item;
				TableData tableData = tableDataItem.getTableData();
				if (tableData.isSignal()) {
					tableData.setPresent(true);
					tableDataItem.removeCommand(this.presentCommand);
					tableDataItem.addCommand(this.absentCommand);
					tableData.setModified(true);
					tableData.setModifiedDisplay(true);
					this.refreshList();
					sendAllData();					
				}
			}
		}
		else if (command == this.absentCommand) {
			if (item instanceof TableDataItem) {
				TableDataItem tableDataItem = 
					(TableDataItem)item;
				TableData tableData = tableDataItem.getTableData();
				if (tableData.isSignal()) {
					tableData.setPresent(false);
					tableDataItem.removeCommand(this.absentCommand);
					tableDataItem.addCommand(this.presentCommand);
					tableData.setModified(true);
					tableData.setModifiedDisplay(true);
					this.refreshList();
					sendAllData();					
				}
			}
		}
		else if (command == this.deleteCommand) {
			if (item instanceof TableDataItem) {
				TableDataItem tableDataItem = 
					(TableDataItem)item;
				TableData tableData = tableDataItem.getTableData();
				this.setLastSelected(this.getLastSelected(), true);
				this.tableDataList.remove(tableData.getKey());
				this.refreshList();
			}
		}
		else if (command == this.modifyCommand) {
			if (item instanceof TableDataItem) {
				TableDataItem tableDataItem = 
					(TableDataItem)item;
				TableData tableData = tableDataItem.getTableData();
				this.setLastSelected(tableDataItem, false);
				this.editTableEntry(tableData);
			}
		}
		else {
			//not a specific item command => forward
			commandAction(command, this.table);
		}
	}

}




//Criteria crit = new Criteria();
//crit.setHorizontalAccuracy(Criteria.NO_REQUIREMENT); // 2km
//crit.setVerticalAccuracy(Criteria.NO_REQUIREMENT); // 2km
//crit.setPreferredResponseTime(Criteria.NO_REQUIREMENT);
//crit.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
//crit.setCostAllowed(true);
//crit.setSpeedAndCourseRequired(false);
//crit.setAltitudeRequired(false);
//crit.setAddressInfoRequired(false);		
//
//LocationProvider provider;
//try {
//	provider = LocationProvider.getInstance(crit);
//	if (provider != null) {
//		Location loc = provider.getLocation(60); // timeout von 60 Sek
//		coordiates = loc.getQualifiedCoordinates();
//		if (coordiates != null) {
//			this.form.setTitle(
//					coordiates.getLongitude() 
//					+ ":" +coordiates.getLatitude());
//		}
//	}
//} catch (Exception e) {
//	e.printStackTrace();
//}


/*
long startTime = System.currentTimeMillis();

String msg="";
int cnt = -10000;
try {
	DataInputStream connector = Connector.openDataInputStream(
			"http://www.delphino.de/ip.php");
    char z;
    do {
	  z = (char)connector.read();
	  if ((int)z != 65535) {
		  msg += z;
		  cnt = 0;
	  }
      cnt++;
      System.out.print((int)z);
      System.out.print(" ");
    } while(cnt < MAXREAD);
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
//this.canvas.message = msg.trim();
//this.canvas.repaint();
* 		
*/

//String cellID = System.getProperty("Cell-ID");
//if (cellID == null)
//	cellID = System.getProperty("com.nokia.mid.cellid");
//
//String lac = System.getProperty("com.nokia.mid.lac");
//String ns = System.getProperty("com.nokia.mid.networksignal");
//
//this.form.setTitle(ns);
