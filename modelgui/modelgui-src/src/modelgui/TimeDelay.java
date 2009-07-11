package modelgui;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeoutException;

/**
 * @author haf
 * 
 */
public class TimeDelay implements Observer {

	private long time = System.currentTimeMillis();
	private long delay = 0;
	private long sleeptime = 0;

	private TimeStorage checkoutTimer;
	private boolean steptoggle = false;
	private boolean pause = true;
	private boolean interrupted = false;
	private boolean isActive = true;

	/**
	 * @param delay
	 * @param timeout
	 * @param checkperiod
	 */
	public TimeDelay(long delay, long timeout, long checkperiod) {
		this.delay = delay;
		this.checkoutTimer = new TimeStorage(timeout, checkperiod);
		this.checkoutTimer.addObserver(this);
		this.checkoutTimer.setActive(true);
	}

	/**
	 * @param delay
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * @throws TimeoutException
	 */
	public void stepDelay() throws TimeoutException {
		try {
			boolean starttoggle = this.steptoggle;
			sleeptime = (delay > 100) ? 20 : 1;
			while (this.isActive
					&& (this.pause || System.currentTimeMillis() - time < delay)) {
				// System.out.print(".");
				if (sleeptime > 0)
					Thread.sleep(sleeptime);
				// if the user wants to do one step, break the sleep loop
				if (starttoggle != this.steptoggle)
					break;
				// if timeout has occured, break the sleep loop. Exception is
				// thrown later
				if (this.getInterrupt())
					break;
			}
			time = System.currentTimeMillis();
			if (this.getInterrupt())
				throw (new TimeoutException("Timeout has occured."));
		}
		catch (InterruptedException e) {/* nothing */
		}
	}

	/**
	 * 
	 */
	public void setInterrupt() {
		synchronized (this) {
			this.interrupted = true;
		}
	}

	/**
	 * @return
	 */
	public boolean getInterrupt() {
		synchronized (this) {
			return this.interrupted;
		}
	}

	/**
	 * 
	 */
	public void close() {
		this.checkoutTimer.setActive(false);
	}

	/**
	 * @param b
	 */
	public void setActive(boolean b) {
		this.isActive = b;
		this.interrupted = !b;
		this.pause = b;
		checkoutTimer.setActive(b);
	}

	// any observable can notify this object by sending a new Delay as
	// an String object
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			this.checkoutTimer.setTime(); // ping the checkoutTimer
			String delayString = ((String) arg).trim();
			if (delayString.equalsIgnoreCase("pause"))
				this.pause = true;
			else if (delayString.equalsIgnoreCase("step")) {
				this.pause = true;
				this.steptoggle = !this.steptoggle;
			}
			else if (delayString.equalsIgnoreCase("timeout")) {
				// System.out.println(o+"timeout");
				this.setInterrupt();
			}
			else if (delayString.equalsIgnoreCase("data")
					|| delayString.equalsIgnoreCase("ping"))
				; /* ignore */
			else {
				this.pause = false;
				delay = Long.parseLong(((String) arg));
			}
		}
	}

}
