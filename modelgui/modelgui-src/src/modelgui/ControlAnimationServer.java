package modelgui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.EventListenerList;

import org.apache.batik.swing.JSVGCanvas;

import events.SimulationEvent;

/**
 * @author Steffen Jacobs
 * 
 */
public class ControlAnimationServer extends ControlAnimationManager {

	// JPanel serverPanel = new JPanel();

	// Animation animation;

	int a = 0;
	PrintWriter out;
	BufferedReader in;
	ServerSocket srvr;
	Socket skt;
	// int[] buffer = new int[100];

	StringBuffer strBuf;
	//EventListenerList eventListeners = new EventListenerList();
	ControlAnimationServer server = this;
	boolean resetted = false;
	boolean isRunning = false;

	Thread thread;

	/**
	 * @param text
	 * @param canvas
	 * @param port
	 */
	public ControlAnimationServer(JSVGCanvas canvas, PreferencesDialog pref) {
		super(canvas, pref);
		thread = new Thread(this);
	}

	/**
	 */
	private void init() {
		// document = canvas.getSVGDocument();
		// svgFilename = document.getURL();
	}

	@Override
	public void run() {
		// System.out.println("starting server");
		while (true) {
			try {
				init();
				while (isRunning == false) {
					try {
						Thread.sleep(200);
					}
					catch (InterruptedException e2) {

					}
				}
				while (isRunning) {
					this.setChanged();
					this.notifyObservers("server restarted");
					try {
						srvr = new ServerSocket(getPort());
						// System.out.println(mapFilename);

						setStatusText("Waiting for client at port " + getPort()
								+ "...");
						skt = srvr.accept();

						skt.setSoTimeout(3000);
						setStatusText("Client connected from IP "
								+ skt.getInetAddress());

						// timer is deactivated. Using socket timeout
						// timer.setActive(true);

						out = new PrintWriter(skt.getOutputStream(), true);
						in = new BufferedReader(new InputStreamReader(skt
								.getInputStream()));
						this.setConnected(true);
						this.setChanged();
						this.notifyObservers("client connected");
						// initially send the data once, so that the client can
						// see how
						// the data types are
						this.send(this.getDataToSend().toString());
						/* ControlAnimationServerSender observer = */

						// create a new sender. Will send if dataToSend has
						// changed and
						// additionally a "ping" every second
						ControlAnimationServerSender sender = new ControlAnimationServerSender(
								out, dataToSend);
						this.addObserver(sender);
						new Thread(sender).start();

						while (isConnected()) {

							// block for some specific time to slow down the
							// whole
							// simulation
							// delay(this.delay);

							// RECEIVING =======================================
							// readline will block until data is available
							String line = in.readLine();
							// client has sent something! yeah, inform the
							// timeout
							// timer!
							// timer.setTime();

							// System.out.println(line);
							// haf
							// client will send "close" if it shuts down. Then
							// stop
							// reading lines
							if (line != null) {
								if (line.trim().equalsIgnoreCase("close")) {
									this.close();
									break;
								}
								else if (line.trim().equalsIgnoreCase("ping")) {
									/* nothing */
								}
								else if (line.trim().toLowerCase().startsWith(
										"delay ")) {
									this.setChanged();
									this.notifyObservers(line.trim()
											.toLowerCase());
								}
								else { // meaningful data has arrived
									// System.out.println("\n"+line);
									receivedData = new JavaStringData(line);
									this.setChanged();
									this.notifyObservers(receivedData);
								}
							}
						}

						this.close();

					}
					catch (SocketTimeoutException e) {
						setStatusText("Connection timeout!");
						// isRunning = false; // let thread finish
						setConnected(false);
						this.setChanged();
						this.notifyObservers("timeout");
					}
					catch (BindException e) {
/*
 * int p = -1; String msg = ""; while (true){ //System.out.println("msg: " +
 * msg); //repeat as long as the user input value is not valid //msg =
 * JOptionPane.showInputDialog(null, "Port number " + port + " is already in use
 * by another application. Please enter a new port number:", "Error",
 * JOptionPane.ERROR_MESSAGE);
 * 
 * if (msg == null){ //System.out.println("abgebrochen"); isRunning = false;
 * break; } try { p = Integer.parseInt(msg); //if parsing was successful, test
 * if p is in valid range if (p <= 65000 && p > 0){ setPort(p); //port value is
 * ok and set, so we can break this loop break; } } catch(NumberFormatException
 * e1){ } }
 */
						// if port is already used, inform Observers to update
						// (actually, the
						// ModelguiToolBar)
						new ModelguiErrorDialog(e,
								"The port is already in use! Choose another port!");
						this.preferences.setVisible(true);
						setConnected(false);
						isRunning = false;
						this.setChanged();
						this.notifyObservers("connection closed");
						// this.setChanged();
						// this.notifyObservers(new Integer(p));

					}
					catch (SocketException e) {
						setStatusText("Connection closed by server!");
						// isRunning = false; // let thread finish
						setConnected(false);
						this.setChanged();
						this.notifyObservers("connection closed");
					}
					catch (IOException e) {
						// e.printStackTrace();
						this.setChanged();
						this.notifyObservers("connection closed");
					}
					finally {
						this.close();
						if (isConnected()) {
							setStatusText("Connection closed by client!");
							setConnected(false);
							this.setChanged();
							this.notifyObservers("connection closed");
						}
						Thread.yield();
					}
				}// while(isRunning)
			}
			catch (Exception e) {
				new ModelguiErrorDialog(e, "Unknown error!");
			}

		} // while(true)
	}

	/*
	 * public JPanel getServerPanel(){ return serverPanel; }
	 */

	/**
	 * @param data
	 */
	private void send(String data) {
		if (out != null) {
			this.out.println(data);
		}
	}

	/**
	 * 
	 */
	@Override
	public void close() {
		try {
			if (skt != null)
				skt.close();
			if (srvr != null)
				srvr.close();
		}
		catch (Exception e) {
			// new ModelguiErrorDialog(e);
		}
	}

	@SuppressWarnings("unused")
	private void delay(int delaymillis) {
		try {
			int rest = delaymillis;
			// send a ping every 2000 idle millisecs
			while (rest > 2000) {
				Thread.sleep(2000);
				out.println("ping");
				rest -= 2000;
			}
			Thread.sleep(rest);
		}
		catch (InterruptedException e) {
			new ModelguiErrorDialog(e);
		}
	}

	/*
	 * public void setDelay(int millis){ this.delay = millis; }
	 */

	/**
	 * after changing port, server should restart
	 */
	private void reset() {
		try {
			if (srvr != null) {
				srvr.close();
				isRunning = false;
				setConnected(false);
				// System.out.println("server resetted");
			}
		}
		catch (IOException e) {

		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof SVGApplication && arg instanceof String) {
			String msg = (String) arg;
			if (msg.equals("rendering done")) {
				isRunning = true;
				// after loading a new SVG file the server thread should already
				// be alive
				if (!thread.isAlive()) {
					thread.start();
				}
			}
			if (msg.equals("loading started")) {
				this.reset();
			}
		}
	}

	@Override
	public void doPause() {
		this.send("delay pause");
	}

	@Override
	public void doStep() {
		this.send("delay step");
	}

	@Override
	public void setDelay(int delay) {
		this.send("delay " + delay);
	}


	public void simulationStarted(SimulationEvent evt) {
		// TODO Auto-generated method stub
		
	}


	public void simulationStepped(SimulationEvent evt) {
		// TODO Auto-generated method stub
		
	}


	public void simulationStopped(SimulationEvent evt) {
				
	}

}

class ControlAnimationServerSender implements Observer, Runnable {

	PrintWriter out;
	JavaStringData data;
	boolean running = true;

	/**
	 * @param o
	 * @param jsd
	 */
	public ControlAnimationServerSender(PrintWriter o, JavaStringData jsd) {
		out = o;
		data = jsd;
		data.addObserver(this);
	}

	/**
	 * Update method is called by the registered Observables, which is in this
	 * case the dataToSend object. dataToSend: This method recognizes if the
	 * dataToSend has changed and will handle the sending of this data.
	 */
	public void update(Observable o, Object arg) {
		// System.out.println("update: "+this.data.toString());
		if (o == data) {
			// System.out.println("dataToString: " + data);
			out.println(data);
		}
		if (o instanceof SVGApplication && arg instanceof String) {
			String msg = (String) arg;
			System.out.println("send: " + msg);
			out.println(msg);
		}
		else if (o instanceof ControlAnimationServer) {
			if (arg instanceof String
					&& ((String) arg).equals("server restarted"))
				this.running = false;
		}
	}

	/**
	 * sends a "ping" string every second. So the receiver can determine if the
	 * sender is still active or not.
	 */
	public void run() {
		while (this.running) { // exit this thread if the server is restarted
			try {
				// out.println("ping");
				// send data instead of string. helps to initialize client
				// regularly
				if (data != null)
					out.println(data);
				else
					out.println("ping");
				Thread.yield();
			}
			catch (Exception e) {/* nothing */
			}
		}
	}
}
