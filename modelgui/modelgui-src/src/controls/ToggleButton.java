package controls;

import modelgui.ControlAnimationManager;
import modelgui.JavaStringData;
import modelgui.SimulationListener;

import org.apache.batik.dom.svg.SVGGraphicsElement;
import org.w3c.dom.events.Event;

import events.SimulationEvent;

public class ToggleButton extends Control implements SimulationListener {

	public ToggleButton(int port, SVGGraphicsElement element,
			JavaStringData dataToSend, Boolean status) {
		super(port, element, dataToSend, status);
	}

	public void handleEvent(Event evt) {
		// this.setStatus(!this.getStatus());
	}

	public void simulationStepped(SimulationEvent e) {
		
	}

	@Override
	public void setDisabledLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnabledLayout() {
		// TODO Auto-generated method stub
		
	}

	public void simulationPaused(SimulationEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void simulationStarted(SimulationEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void simulationStopped(SimulationEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
