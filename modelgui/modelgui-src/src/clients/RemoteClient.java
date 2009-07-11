package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.concurrent.TimeoutException;

import exceptions.ClientException;

import modelgui.JavaStringData;
import modelgui.Tools;

/**
 * @author Steffen Jacobs
 * 
 */
public class RemoteClient extends Observable implements Runnable {

	private PrintWriter out;
	private BufferedReader in;
	private static RemoteClient client = null;
	private Socket socket;
	private boolean connected = false;

	JavaStringData guiControlData;

	private boolean newData = false;
	private long lastreceivetime = System.currentTimeMillis();
	// private float delay = 0;

	/**
	 * Determines if the modeling tool should exit the simulation. Calling an
	 * explicit exit function might also tear down the whole simulation engine.
	 */
	boolean exit = false;

	/**
	 * @return
	 */
	public boolean isExit() {
		return exit;
	}

	private RemoteClient() throws ClientException {
		this.init("127.0.0.1", 1234);
	}

	/**
	 * @param host
	 * @param port
	 * @throws ClientException
	 */
	public RemoteClient(String host, int port) throws ClientException {
		this.init(host, port);
	}

	/**
	 * @param host
	 * @param port
	 * @throws ClientException
	 */
	public void init(String host, int port) throws ClientException {
		System.out.println("Initializing base client.");
		exit = false;
		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			connected = true;
			System.out.println("finished");
		}
		catch (UnknownHostException e) {
			exit = true;
			throw new ClientException(e,
					"Problem creating socket to the host. Hostname or IP is unknown: "
							+ host + "\n : ");
		}
		catch (IOException e) {
			exit = true;
			throw new ClientException(
					e,
					"Problem creating socket to the host!\n"
							+ e.getMessage()
							+ "\nPlease make sure the GUI server is up and running and reachable!");
		}
		catch (Exception e) {
			exit = true;
			throw new ClientException(e, "");
		}
	}

	/**
	 * @param str
	 * @throws ClientException
	 */
	public void send(String str) throws ClientException {
		try {
			out.print(str);
			out.println();
			// Tools.showDialog("sending: "+str);
		}
		catch (NullPointerException e) {
			Tools.showDialog("Sending of Data to the GUI failed!\n"
					+ e.getMessage());
			exit = true;
		}
	}

	/**
	 * @return
	 * @throws ClientException
	 */
	public String receive() throws ClientException {
		String message = "";
		try {
			while (!in.ready()) {
				Thread.sleep(10);
			}
			message = in.readLine().trim();
		}
		catch (InterruptedException e) {
			Tools.showDialog("Interrupted during waiting of data from GUI");
		}
		catch (Exception e) {
			Tools
					.showDialog("Reading Data from GUI failed!\n"
							+ e.getMessage());
			exit = true;
		}
		// return empty data in case of an error
		return message;
	}

	/**
	 * Method returns the received guiControlData. Blocks until either some new
	 * data is available or a connection timeout has happened. A timeout will
	 * occur, if the server does not send a ping signal regularly during a
	 * pause.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public JavaStringData getControlDataTimeout() throws ClientException {
		try {
			while (!newData) {
				Thread.sleep(1);
				if (System.currentTimeMillis() - lastreceivetime > 5000)
					throw new TimeoutException(
							"Connection to the GUI server timed out!");
			}
			newData = false;
			return this.guiControlData;

		}
		catch (Exception e) {
			Tools.showDialog("Could not fetch the Data to display: "
					+ e.getMessage());
			this.exit = true;
		}
		return guiControlData;
	}

	/**
	 * might return null if the run method has not received anything so far
	 * 
	 * @return
	 */
	public JavaStringData getControlData() {
		return this.guiControlData;
	}

	/**
	 * 
	 */
	public static void releaseClient() {
		if (client != null) {
			client = null;
		}
	}

	/**
	 * 
	 */
	public void close() {
		if (connected) {
			try {
				this.send("close");
			}
			catch (Exception e) {/* nothing */
			}
			;
			connected = false;
			try {
				this.socket.close();
			}
			catch (Exception e) {/* nothing */
			}
			this.exit = true;
		}
	}

	/**
	 * If the thread is started, the receive method is called and waits for
	 * data. If data arrives, it is parsed. Known control commands are processed
	 * and data is passed to the modelling tool.
	 */
	public void run() {
		// Tools.showDialog("starting Receiving Thread");
		while (true) {
			try {
				while (!isExit()) {
					String serverResponse = this.receive().trim();
					// System.out.println("server Response: "+serverResponse);
					this.lastreceivetime = System.currentTimeMillis();
					// ping only indicates that the server is still alive
					if (serverResponse.equalsIgnoreCase("ping")) {
						this.setChanged();
						this.notifyObservers("ping");
					}
					else if (serverResponse.startsWith("delay")) {
						String delayString = serverResponse.substring(5).trim();
						this.setChanged();
						this.notifyObservers(delayString);
					}
					else {
						this.guiControlData = new JavaStringData(serverResponse);
						// System.out.println("serverResponse: " +
						// serverResponse);
						// tell observers, that fresh data is available
						this.setChanged();
						this.notifyObservers("data");
						newData = true;
					}
					Thread.yield();
				}
			}
			catch (Exception e) {
				this.exit = true;
				System.out.println(e.getMessage());
			}
			Thread.yield();
		}
	}
}
