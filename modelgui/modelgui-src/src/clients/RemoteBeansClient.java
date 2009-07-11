package clients;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import exceptions.ClientException;

import modelgui.JavaStringData;
import modelgui.KeyValuePanel;
import modelgui.Tools;

/**
 * SCADE Beans Client for the Modelgui Application. Any SCADE complex type can
 * be passed through a special setter and getter that take, resp. return the
 * Data in its String representation by SCADE.
 * 
 * @author haf
 */
public class RemoteBeansClient extends JPanel implements ActionListener,
		Observer, PropertyChangeListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6385948105942611977L;
	JLabel debugInfo = new JLabel("");
	JButton reconnectButton = new JButton("Reconnect");

	KeyValuePanel hostPanel = new KeyValuePanel("GUI IP", "10.6.0.24", 0);
	KeyValuePanel portPanel = new KeyValuePanel("GUI port", "1234", 0);

	private JavaStringData displayData = new JavaStringData("");
	private JavaStringData controlData = new JavaStringData("");

	private RemoteClient client;

	/**
	 * This constructor creates some dummy panel, because SCADE needs to
	 * instantiate some graphical representation. Some Debug Info can be
	 * displayed in the panel.
	 */
	public RemoteBeansClient() {
		super();
		GridLayout gl = new GridLayout(5, 1);
		this.setLayout(gl);
		JLabel text = new JLabel("Running Java GUI client..");
		this.add(text);
		this.add(hostPanel);
		this.add(portPanel);
		this.add(debugInfo);
		this.add(reconnectButton);
		reconnectButton.addActionListener(this);

		Dimension size = new Dimension(300, 150);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMinimumSize(size);

		this.debugInfo.setText("Trying to connect to the GUI server...");
		System.out.println("ModelGui SCADE Client started...");
		this.init();
	}

	private void init() {
		String host = hostPanel.getTextfield().getText();
		int port = 1234;
		try {
			port = Integer.parseInt(portPanel.getTextfield().getText());
		}
		catch (NumberFormatException e) {/* nothing */
		}

		try {
			client = new RemoteClient(host, port);
		}
		catch (ClientException e) {
			Tools.showDialog(e.getMessage());
		}

		if (client != null && !client.isExit()) {
			new Thread(client).start();
			this.debugInfo.setText("Successfully connected!");
			new Thread(this).start(); // start the "ping" sender

			client.addObserver(this);
			this.addPropertyChangeListener(this);
		}
		else {
			this.debugInfo.setText("Try to reconnect with different settings!");
		}
	}

	/**
	 * Important setter to pass all Simulation Data from SCADE to this Java
	 * Beans object. The corresponding Type conversion functions are implemented
	 * in the package com.estereltechnologies.scade.simulation.converters
	 * 
	 * @param data
	 */
	public void setGuiDisplayData(JavaStringData data) {
		JavaStringData oldData = this.displayData;
		this.displayData = data;
		// System.out.println("setGuiDisplayData: " + data);
		firePropertyChange("guiDisplayData", oldData, this.displayData);
	}

	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public JavaStringData getGuiDisplayData() {
		return this.displayData;
	}

	/**
	 * Important getter to send the Java Control Data from the Gui back to
	 * SCADE. The corresponding Type conversion functions are implemented in the
	 * package com.estereltechnologies.scade.simulation.converters
	 * 
	 * @return
	 */
	public JavaStringData getGuiControlData() {
		return this.controlData;
	}

	/**
	 * @param data
	 */
	public void setGuiControlData(JavaStringData data) {
		JavaStringData oldData = this.controlData;
		this.controlData = data;
		System.out.println("setGuiControlData: " + this.controlData);
		firePropertyChange("guiControlData", oldData, this.controlData);
	}

	public void update(Observable o, Object arg) {
		// client has received something from the GUI server. So send this data
		// to SCADE!
		JavaStringData data = client.getControlData();
		// data = new JavaStringData("{true,true,true}");
		this.setGuiControlData(data);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// SCADE has changed the data to be displayed. So pass this to the
		// TCP/IP client
		// to send it to the GUI server
		if (evt.getPropertyName().equals("guiDisplayData")) {
			try {
				client.send(this.getGuiDisplayData().toString());
			}
			catch (ClientException e) {
				Tools.showDialog(e.getMessage());
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame();
			Object bean;

			bean = Beans.instantiate(null, "modelgui.RemoteBeansClient");
			frame.getContentPane().add((Component) bean);
			frame.pack();
			frame.setVisible(true);

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * The beans client sends a "ping" String every 2 seconds to show the server
	 * that it is still correctly working. This is necessary, because this
	 * client cannot determine when the simulation is explicitly closed. Hence
	 * the bean cannot inform the server that the simulation has terminated.
	 */
	public void run() {
		while (!client.isExit()) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {/* Nothing */
			}
			try {
				client.send("ping");
			}
			catch (ClientException e) {
				Tools.showDialog(e.getMessage());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reconnectButton) {
			this.init();
		}
	}
}
