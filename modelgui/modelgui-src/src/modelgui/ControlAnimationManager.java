package modelgui;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.apache.batik.swing.JSVGCanvas;

import events.SimulationEvent;
import exceptions.ClientException;

public abstract class ControlAnimationManager extends Observable implements
		Runnable, Observer, SimulationListener {

	private static ControlAnimationManager manager = null;
	JSVGCanvas canvas;
	String mapFilename;
	String status = "Server undefined.";
	private boolean connected = false;
	JavaStringData dataToSend = new JavaStringData("");
	JavaStringData receivedData = new JavaStringData("");
	PreferencesDialog preferences;
	

	public ControlAnimationManager(JSVGCanvas canvas, PreferencesDialog prefs) {
		super();
		this.preferences = prefs;
		this.canvas = canvas;
		manager = this;
	}

	public static ControlAnimationManager getInstance() {
		return manager;
	}

	public JavaStringData getDataToSend() {
		return dataToSend;
	}

	public JavaStringData getReceivedData() {
		return this.receivedData;
	}

	void setStatusText(String text) {
		status = text;
		this.setChanged();
		this.notifyObservers();
		// System.out.println(text);
	}

	public String getStatus() {
		return status;
	}

	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean flag){
		connected = flag;
	}

	public abstract void run();

	public abstract void close();

	public abstract void setDelay(int delay);

	public abstract void doPause();

	public abstract void doStep();

	public int getPort() throws ClientException {
		int port = 0;
		try {
			port = Integer.parseInt(preferences.getPreferenceValue(
					PreferencesDialog.PORT_PREF_NAME).toString());
		}
		catch (Exception e) {
			preferences.setVisible(true);
			throw new ClientException(e,
					"The port number must be a positive integer value!");
		}
		return port;
	}

	public String getHost() {
		return preferences.getPreferenceValue(PreferencesDialog.HOST_PREF_NAME)
				.toString();
	}

	

	public PreferencesDialog getPreferencesDialog() {
		return preferences;
	}

	public abstract void update(Observable o, Object arg);

}
