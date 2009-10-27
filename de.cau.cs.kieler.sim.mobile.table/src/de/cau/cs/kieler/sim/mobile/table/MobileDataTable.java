/*
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
 */
package de.cau.cs.kieler.sim.mobile.table;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

/**
 * The class MobileDataTable is the main class an implements the MIDlet
 * with all its basic functionality. This includes the TCP connection,
 * the processing of received data and the whole user interface.
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 * 
 */
public class MobileDataTable extends MIDlet implements CommandListener,
													   ItemCommandListener {
	/** The image for signals. */
	public Image signalImage;
	
	/** The image for variables. */
	public Image variableImage;
	
	/** The master flag indicating that this application controls the 
	 * execution. */
	private boolean master;

	/** The host for the TCP connection. */
	private String host;
	
	/** The port for the TCP connection. */
	private int port;
	
	/** The display. */
	private Display display;
	
	/** The current form. This can take the values {@link #FORM_TABLE},
	 * {@link #FORM_EDIT} and {@link #FORM_CONNECT}. */
	private int currentForm;
	
	/** The constant FORM_TABLE. */
	private final static int FORM_TABLE = 1;
	
	/** The constant FORM_EDIT. */
	private final static int FORM_EDIT = 2;
	
	/** The constant FORM_CONNECT. */
	private final static int FORM_CONNECT = 3;
	
	/** The ChoiceGroup for the type (signal or variable). */
	ChoiceGroup typeEdit;
	
	/** The key edit TextField. */
	private TextField keyEdit; 
	
	/** The value edit TextField. */
	private TextField valueEdit;
	
	/** The current active and focused TableData. */
	private TableData currentTableData;
	
	/** The exit command. */
	private Command exit;
	
	/** The connect command. */
	private Command connectCommand;
	
	/** The disconnect command. */
	private Command disconnectCommand;
	
	/** The OK command. */
	private Command okCommand;
	
	/** The cancel command. */
	private Command cancelCommand;

	/** The add command. */
	private Command addCommand;
	
	/** The delete command. */
	private Command deleteCommand;
	
	/** The modify command. */
	private Command modifyCommand;
	
	/** The present command. */
	private Command presentCommand;
	
	/** The absent command. */
	private Command absentCommand;
	
	/** The step command (within a table entry). */
	private Command stepTableCommand;
	
	/** The pause command (within a table entry). */
	private Command pauseTableCommand;
	
	/** The step command. */
	private Command stepCommand;
	
	/** The pause command. */
	private Command pauseCommand;
	
	/** The run command. */
	private Command runCommand;
	
	/** The stop command. */
	private Command stopCommand;
	
	/** The step button. */
	private ImageItem stepButton;
	
	/** The pause button. */
	private ImageItem pauseButton;
	
	/** The run button. */
	private ImageItem runButton;
	
	/** The stop button. */
	private ImageItem stopButton;
	
	/** The connect image. */
	private Image connectImage;
	
	/** The disconnect image. */
	private Image disconnectImage;
	
	/** The step image. */
	private Image stepImage;
	
	/** The pause image. */
	private Image pauseImage;
	
	/** The run image. */
	private Image runImage;
	
	/** The stop image. */
	private Image stopImage;
	
	/** The step image disabled. */
	private Image stepImageDisabled;
	
	/** The pause image disabled. */
	private Image pauseImageDisabled;
	
	/** The run image disabled. */
	private Image runImageDisabled;
	
	/** The stop image disabled. */
	private Image stopImageDisabled;
	
	/** The table. */
	private Form table;
	
	/** The run mode flag is true iff execution is in run mode. */
	private boolean runMode;
	
	/** The executing flag is true iff execution is ongoing. */
	private boolean executing;
	
	/** The connection, null if no connection. */
	private SocketConnection connection;
	
	/** The TCP writer, set iff connected. */
	private PrintStream writer;
	
	/** The TCP reader, set iff connected. */
	private DataReceiver reader;
	
	/** The TableDataList holds all TableData entries which can be edited. */
	private TableDataList tableDataList;
	
	/** The last selected Item. This is necessary to set this on focus again
	 * after e.g., a refresh or rebuild of the TableDataList. */
	public Item lastSelected;

	/** The single instance of this class. */
	private static MobileDataTable instance;
	
	//-------------------------------------------------------------------------

	/**
	 * Gets the single instance of MobileDataTable.
	 * 
	 * @return single instance of MobileDataTable
	 */
	public static MobileDataTable getInstance() {
		return instance;
	}
	
	//-------------------------------------------------------------------------

	/**
	 * Display {@link #lastSelected} Item.
	 */
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
	
	//-------------------------------------------------------------------------
	
	/**
	 * Sets or resets the {@link #lastSelected} Item. If the reset flag is set 
	 * to true, then the {@link #lastSelected} variable is cleared.
	 * 
	 * @param item the item
	 * @param reset the reset
	 */
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

	//-------------------------------------------------------------------------
	
	/**
	 * Gets the {@link #lastSelected} Item.
	 * 
	 * @return the last selected
	 */
	public Item getLastSelected() {
		return this.lastSelected;
	}

	//-------------------------------------------------------------------------

	/**
	 * Displays a form for editing a TableData entry. The user here can switch
	 * the type between a variable and a signal. Also the key and the value
	 * of the TableData can be modified. The form can be closed by using the 
	 * OK or the CANCEL command.
	 * 
	 * @param tableData the table data
	 */
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
		//make this the active form
		currentForm = MobileDataTable.FORM_EDIT;
		this.display.setCurrent(editForm);
	}

	//-------------------------------------------------------------------------
	
	/**
	 * Send all data entries that are contained in the current TableDataList 
	 * and that are flagged as modified.
	 */
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

	/**
	 * Checks if this application is flagged to be a master (by the remote
	 * Execution Manager DataComponent, that must be enabled in this case).
	 * 
	 * @return true, if it is a master
	 */
	public boolean isMaster() {
		return this.master;
	}
	
	//-------------------------------------------------------------------------

	/**
	 * Sets the title of the application.
	 * 
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.table.setTitle(title);
	}
	
	//-------------------------------------------------------------------------

	/**
	 * Refresh list of TableDataItems. If currently an item is selected this
	 * method backups the selected key and tries to find the item in a
	 * fresh rebuild list to reselect it afterwards.
	 */
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
			//fresh table, delete all entries
			this.table.deleteAll();
			//additional maste buttons
			if (master) {
				table.append(stepButton);
				table.append(runButton);
				table.append(pauseButton);
				table.append(stopButton);
				table.append(new Spacer(200, 10));
			}
			//go thru TableData entries
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
				//add commands to each item
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
			//display last selected item
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

	/**
	 * Receive data. This method is triggered by the DataReceiver thread 
	 * asynchronously. It checks whether the data is a command (starting with
	 * "E" for enabling the master functionality, "D" for disabling the master
	 * functionality, or "I" for the initialization data) or a JSON data string
	 * (starting with "P" for present steps or "H" for history steps).
	 * 
	 * @param data the data to process
	 */
	public void receiveData(String data) {
		if (data.startsWith("E")) {
			//enable this application as a master
			this.table.addCommand(this.stepCommand);
			this.table.addCommand(this.runCommand);
			this.table.addCommand(this.pauseCommand);
			this.master = true;
			this.setLastSelected(this.lastSelected, true);
			this.refreshList();
		}
		else if (data.startsWith("D")) {
			//disable this application as a master
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
	 * @param jo the JSONObject to get the field names from
	 * 
	 * @return an array of field names, or null if there are no names.
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
	
	/**
     * This method implements the observer part of this application. It 
     * traverses thru the list of TableData items and merges all new
     * JSON data (allData) into it either updating existing or creating new 
     * entries.<BR>
     * It will also extract the step information and display it in the
     * application's title.
     * 
     * @param allData the complete (delta) JSON data send by the remote 
     * DataComponent
     * @param historyStep the history step flag
     */
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
		if (this.currentForm == MobileDataTable.FORM_TABLE) {
			this.refreshList();
			this.display.vibrate(100);
		}
	}
	
	//-------------------------------------------------------------------------

	/**
	 * This method can be used to send a command via the opened TCP connection.
	 * It send this command as a single line and flushes the stream.
	 * 
	 * @param command the command to send
	 */
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
	
	/**
	 * Instantiates a new MobileDataTable setting the default host, port and
	 * images, commands etc. and building the main form.
	 */
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
			connectImage = Image.createImage("/connectIcon.png");
			disconnectImage = Image.createImage("/disconnectIcon.png");
		}catch(Exception e){
			connectImage = null;
			disconnectImage = null;
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
	
	/**
	 * This method brings the String text to the user's attention for a 
	 * duration of 2 seconds. It also triggeres a vibrating command if this is
	 * supported by the mobile phone.
	 * 
	 * @param text the text
	 */
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
	
	/**
	 * This is the connect dialog form in which the predefined (default) host
	 * and port for the TCP connection can be modified. Whenever the user hits
	 * the OK command the connection is asynchronously mad by a different 
	 * thread. When the user uses the cancel command no connection attempt is 
	 * being made.
	 */
	public void connectDialog() {
		//connection form
		Form connectionForm = new Form("Mobile Table - Connect");
		//edit fields for the host and the port
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
		//set the new current form
		currentForm = MobileDataTable.FORM_CONNECT;
		this.display.setCurrent(connectionForm);
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * This method is called asynchronously from within the SocketConnector
	 * thread. It tries to connect to the given/set host and port. It also
	 * displays a BusyAlert during the connection attempt. 
	 */
	public void connect() {
		try {
			new Backlight(5000);
			this.display.setCurrent( new BusyAlert("Mobile Table - Connect", 
													 "Connecting ...",
													 this.connectImage, 
													 Alert.FOREVER));
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
			currentForm = MobileDataTable.FORM_TABLE;
			new Backlight(5000);
			this.display.setCurrent( new Alert("Mobile Table - Connect", 
					"Connected", this.connectImage, AlertType.INFO), this.table);
			return;
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
			currentForm = MobileDataTable.FORM_TABLE;
			new Backlight(5000);
			this.display.setCurrent( new Alert("Mobile Table - Connect", 
					"Connection failed", this.disconnectImage, AlertType.ERROR), this.table);
			return;
		}
	}

	//-------------------------------------------------------------------------
	
	/**
	 * Disconnect from the (possibly) connected host. This also sets the 
	 * application back to the default extended master mode and disables the
	 * execution buttons and commands. This also resets the TableDataList.
	 */
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
		new Backlight(5000);
		this.display.setCurrent( new Alert("Mobile Table - Connect", 
				"Disconnected", this.disconnectImage, AlertType.INFO), this.table);
	}
	
	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		//the default form for application start
		currentForm = MobileDataTable.FORM_TABLE;
		this.display.setCurrent(this.table);
	}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional)
						throws MIDletStateChangeException {
		//noop
	}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		//noop
	}

	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command, Displayable displayable) {
		//this method handles the action commands
		if (command == this.exit) {
			this.notifyDestroyed();
		}
		else if (command == this.connectCommand) {
			this.connectDialog();
		}
		else if (command == this.disconnectCommand) {
			this.disconnect();
		}
		else if (command == this.okCommand) {
			if (currentForm == FORM_CONNECT) {
				this.table.removeCommand(this.connectCommand);
				new SocketConnector();
				return;
			}
			else if (currentForm == FORM_EDIT) {
				//save current modifications
				if (currentTableData != null) {
				  currentTableData.setSignal(typeEdit.getSelectedIndex() == 1);
				  if (!this.tableDataList.containsOther(
					  this.keyEdit.getString(), currentTableData)) {
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
	
	/**
	 * Sets the executing flag to execution (true) or non-execution (false).
	 * 
	 * @param executing the new executing boolean flag
	 */
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

	/**
	 * Sets the run mode to running execution (true) or paused execution
	 * (false).
	 * 
	 * @param runMode the new boolean run mode flag
	 */
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

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.ItemCommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Item)
	 */
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
