package controls;

import java.util.Observer;

import modelgui.ControlAnimationManager;
import modelgui.JavaStringData;
import modelgui.ScadeSlaveGateway3;
import modelgui.SimulationListener;

import org.apache.batik.dom.svg.SVGGraphicsElement;
import org.apache.batik.dom.svg.SVGOMElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import events.SimulationEvent;

/**
 * @author sja
 * 
 */
public class Button extends Control implements SimulationListener {

	private boolean buttonPressed = false;
	
	/**
	 * @param port
	 * @param element
	 * @param dataToSend
	 * @param status
	 */
	public Button(int port, SVGOMElement element, JavaStringData dataToSend,
			Boolean status) {
		super(port, element, dataToSend, status);
		ScadeSlaveGateway3.getInstance().addSimulationListener(this);
		element.addEventListener("mousedown", new EventListener() {
			public void handleEvent(Event evt) {
				if (getStatus().equals(Boolean.valueOf(false))) {
					setChanged();
					setStatus(Boolean.valueOf(true));
					buttonPressed = true;
					getElement().setAttribute("opacity", "1");
				}
			}
		}, false);

		element.addEventListener("mouseup", new EventListener() {
			public void handleEvent(Event evt) {
				if (getStatus().equals(Boolean.valueOf(true))) {
					buttonPressed = false;
				}				
			}
		}, false);

		element.addEventListener("mouseout", new EventListener() {
			public void handleEvent(Event evt) {
				if (getStatus().equals(Boolean.valueOf(true))) {
					buttonPressed = false;
				}
			}
		}, false);

		element.setAttribute("opacity", "0.7");

	}
	
	/**
	 * 
	 */
	/*public void blink() {
		if (this.getElement() instanceof SVGGraphicsElement) {
			if (this.getStatus().equals(Boolean.valueOf(true))) {
				SVGGraphicsElement element = (SVGGraphicsElement) this
						.getElement();
				CSSStyleDeclaration style = element.getStyle();
				int styleLength = style.getLength();
				if (styleLength == 0) {
					//System.out.println("Element has no style...");
					NamedNodeMap attributes = element.getAttributes();
					if (attributes.getLength() == 0) {
						//System.out.println("Element has no attribute...");
					}
					else {

					}
				}
				// System.out.println(styleLength);
			}
		}
	}*/

	public void simulationStepped(SimulationEvent e) {
		if (!buttonPressed) {
			setChanged();
			setStatus(false);
			getElement().setAttribute("opacity", "0.7");
		}
		
	}

	@Override
	public void setDisabledLayout() {
		getElement().setAttribute("opacity", "0.3");	
	}

	@Override
	public void setEnabledLayout() {
		getElement().setAttribute("opacity", "0.7");	
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
