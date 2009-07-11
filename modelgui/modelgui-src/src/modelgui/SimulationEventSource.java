package modelgui;

import java.util.Vector;

import events.SimulationEvent;

public abstract class SimulationEventSource {

	private Vector<SimulationListener> listeners = new Vector<SimulationListener>();

	
	public void addSimulationListener(SimulationListener list) {
		listeners.add(list);
	}

	public void removeSimulationListener(SimulationListener list) {
		listeners.remove(list);
	}

	protected void fireSimulationStepped(SimulationEvent evt) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).simulationStepped(evt);
		}
	}

	protected void fireSimulationStarted(SimulationEvent evt) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).simulationStarted(evt);
		}
	}

	protected void fireSimulationStopped(SimulationEvent evt) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).simulationStopped(evt);
		}
	}
}
