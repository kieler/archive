package modelgui;

import java.util.EventListener;

import events.SimulationEvent;

public abstract interface SimulationListener extends EventListener {

	public abstract void simulationStepped(SimulationEvent evt);
	public abstract void simulationStarted(SimulationEvent evt);
	public abstract void simulationStopped(SimulationEvent evt);

}
