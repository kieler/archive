package exceptions;

public class SimulationNotLaunchedException extends SimulationException {

	public SimulationNotLaunchedException(Throwable e, String msg) {
		super(e, msg);

	}
	
	public SimulationNotLaunchedException(String msg) {
		super(msg);
	}

}
