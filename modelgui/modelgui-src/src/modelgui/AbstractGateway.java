package modelgui;

import exceptions.ClientException;
import exceptions.SimulationException;

public abstract class AbstractGateway extends SimulationEventSource {

	private static AbstractGateway me;
	
	public AbstractGateway(){
		me = this;
	}
	
	public abstract void init(String host, int ports) throws ClientException;
	public abstract void close();
	public abstract void startSimulation() throws ClientException;
	public abstract void stopSimulation() throws SimulationException, ClientException;
	public abstract void step(int stepsize, boolean refresh) throws ClientException;
	public abstract String receiveMessage() throws ClientException;
	public abstract void sendMessage(String message) throws ClientException;
	public abstract boolean isConnected();
	
	public final static AbstractGateway getInstance(){
		if (me != null)
			return me;
		try {
			return AbstractGateway.class.newInstance();
		}
		catch (InstantiationException e) {		
		}
		catch (IllegalAccessException e) {
		}
		return null;
	}
}
