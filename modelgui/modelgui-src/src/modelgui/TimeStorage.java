package modelgui;

import java.util.Observable;

/**
 * Objects of this class saves a time and checks periodically if the elapsed
 * time since the saved time has exceeded some defined timeout. This class
 * implements the Observable class and therefore can be observed by some
 * Observers. If the timeout has expired, all Observers are informed and
 * therefore can do some appropriate actions.
 * 
 * @author haf
 */
public class TimeStorage extends Observable implements Runnable {

	private long time = System.currentTimeMillis();

	private long timeout;
	private long checkperiod;

	private boolean isActive = false;

	/**
	 * @param timeout
	 * @param checkperiod
	 */
	public TimeStorage(long timeout, long checkperiod) {
		this.timeout = timeout;
		this.checkperiod = checkperiod;
		new Thread(this).start();
	}

	/**
	 * Sets the saved time to the current time. Can be used to simply update the
	 * "last" time.
	 */
	public synchronized void setTime() {
		time = System.currentTimeMillis();
	}

	/**
	 * @param time
	 */
	public synchronized void setTime(long time) {
		this.time = time;
	}

	/**
	 * The time checking can be activated or deactivated with this method. If it
	 * is deactivated it will not check for the timeout.
	 * 
	 * @param b
	 */
	public void setActive(boolean b) {
		setTime();
		isActive = b;
		System.out.println("Timeout Checker: " + isActive);
	}

	public void run() {
		try {
			while (true) {
				if (!isActive)
					Thread.sleep(checkperiod);
				while (isActive) {
					// System.out.println("Checking for timeout");
					Thread.sleep(checkperiod);
					if (System.currentTimeMillis() - time > timeout) {
						// System.out.println("timeout occured");
						this.setChanged();
						this.notifyObservers("timeout");
					}
				}
				Thread.yield();
			}
		}
		catch (Exception e) {/* nothing */
		}
		;
	}

}
