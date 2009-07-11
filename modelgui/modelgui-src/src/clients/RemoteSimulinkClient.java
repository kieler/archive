package clients;

import java.util.concurrent.TimeoutException;

import exceptions.ClientException;

import modelgui.JavaStringData;
import modelgui.TimeDelay;
import modelgui.TimeStorage;

/**
 * @author Steffen Jacobs
 * 
 */
public class RemoteSimulinkClient {

	static RemoteClient baseClient;
	static TimeDelay timer;
	static TimeStorage timeoutChecker;
	static Pinger pingSender;

	static ClientException exception;
	static boolean isException = false;

	/**
	 * @param host
	 * @param port
	 */
	public static void initialize(String host, int port) {
		System.out.println("ModelGUI client started... v013");
		isException = false;

		try {
			if (baseClient == null) {
				baseClient = new RemoteClient(host, port);
				new Thread(baseClient).start();
			}
			else
				baseClient.init(host, port);

			if (timer == null) {
				timer = new TimeDelay(100, 3000, 1000);
				baseClient.addObserver(timer);
			}
			timer.setActive(true);

			if (pingSender == null)
				pingSender = new Pinger(baseClient);
			pingSender.setActive(true);
		}
		catch (ClientException e) {
			exception = e;
			isException = true;
		}
	}

	/**
	 * 
	 */
	public static void terminate() {
		try {
			baseClient.close();
		}
		catch (Exception e) {/* nothing */
		}
		try {
			timer.setActive(false);
		}
		catch (Exception e) {/* nothing */
		}
		try {
			pingSender.setActive(false);
		}
		catch (Exception e) {/* nothing */
		}
		System.out.println("Simulink GUI client terminated.");
	}

	/**
	 * 
	 */
	public static void stepDelay() {
		try {
			timer.stepDelay();
		}
		catch (TimeoutException e) {
			baseClient.exit = true;
			exception = new ClientException(e,
					"Simulation was exited by the GUI server!");
			isException = true;
		}
	}

	/**
	 * @return
	 */
	public static boolean isExit() {
		return isException;
	}

	/**
	 * @return
	 */
	public static char[] getException() {
		String message;
		if (exception != null)
			message = exception.getMessage();
		else
			message = "";
		char[] charArray = message.toCharArray();
		return charArray;
	}

	/**
	 * @param str
	 */
	public static void send(String str) {
		try {
			baseClient.send(str);
		}
		catch (ClientException e) {
			exception = e;
			isException = true;
		}
	}

	/**
	 * @return
	 */
	public static double[] getControlData() {
		JavaStringData data = baseClient.getControlData();
		if (data == null) // wait for first initialized data
			for (int i = 0; i < 30; i++) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {/* nothing */
				}
				data = baseClient.getControlData();
				if (data != null)
					break;
			}
		if (data == null) {
			exception = new ClientException(new Exception(""),
					"Could not read init data from Gui server.");
			isException = true;
			double[] dummy = new double[1];
			dummy[0] = 0;
			return dummy;
		}
		else
			return data.getDoubles();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize("127.0.0.1", 1234);
		while (true) {
			System.out.println("step");
			double[] data = getControlData();
			System.out.println("data: " + data[0] + " " + data[1] + " "
					+ data[2]);
			stepDelay();
		}
	}

}

class Pinger implements Runnable {

	boolean isActive = true;
	RemoteClient out;

	/**
	 * @param out
	 */
	public Pinger(RemoteClient out) {
		this.out = out;
		new Thread(this).start();
	}

	/**
	 * The beans client sends a "ping" String every 2 seconds to show the server
	 * that it is still correctly working. This is necessary, because this
	 * client cannot determine when the simulation is explicitly closed. Hence
	 * the bean cannot inform the server that the simulation has terminated.
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {/* Nothing */
			}
			if (isActive)
				try {
					out.send("ping");
				}
				catch (ClientException e) {/* nothing */
				}
			Thread.yield();
		}
	}

	/**
	 * @param b
	 */
	public void setActive(boolean b) {
		this.isActive = b;

	}
}
