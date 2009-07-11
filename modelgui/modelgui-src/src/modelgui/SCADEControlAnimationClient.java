package modelgui;

import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.JTextField;

import org.apache.batik.swing.JSVGCanvas;

import events.SimulationEvent;
import exceptions.ClientException;
import exceptions.SimulationException;
import exceptions.SimulationNotLaunchedException;

public class SCADEControlAnimationClient extends ControlAnimationManager implements SimulationListener {

	AbstractGateway gateway;


	Thread playThread;

	boolean paused = false;

	int delay = 0;

	long requestedDelay = delay;

	int stepsPerRequest = 1;

	private String outputPath = "simulation::Simulation/displayData";
	private String inputPath  = "simulation::Simulation/controlData";
	
	boolean dataHasChanged = false;
	
	private boolean initialData = true;
	
	Logger log;

	private boolean simulationStarted = false;

	public SCADEControlAnimationClient(JSVGCanvas canvas, PreferencesDialog pref) {
		super(canvas, pref);
		log = Logger.getLogger(SVGApplication.LOGGER_NAME);
		
		outputPath = ((JTextField)pref.getPreference(PreferencesDialog.OUTPUT_PREF_NAME)).getText();
		inputPath  = ((JTextField)pref.getPreference(PreferencesDialog.INPUT_PREF_NAME)).getText();		

		gateway = new ScadeSlaveGateway3(outputPath,inputPath);
		
		dataToSend.addObserver(this); // listen if button was pressed etc.
		gateway.addSimulationListener(this);
	}

	private void init() {
		try {
			log.info("Initializing connection to " + getHost() + ":" + getPort() + "... ");
			gateway.init(getHost(), getPort());
			log.info("succeeded!");
			
			setConnected(true);
			this.simulationStarted = false;
		}
		catch (ClientException e) {
			new ModelguiErrorDialog(
					e,
					"Error connecting to the model server! Make sure"
							+ " the server is up and running and hostname and port are correct"
							+ " and reachable. ");
			this.setStatusText("Not connected!");
		}
		
		if (isConnected()){
			try {
				log.info("succeeded!");
				log.info("Connection to server " + getHost() + ":" + getPort() + " established!");
				this.setStatusText("Connected to " + getHost() + "/" + getPort());
				this.setChanged();
				this.notifyObservers("client connected");
			}
			catch (ClientException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}

// TODO: after changing port or hostname in PreferencesDialog client should
	// reconnect
	public void reconnect() {
		this.close();
		this.init();
	}
	
	@Override
	public void close() {
		try {
			this.paused = true;
			SVGApplication.getProgressBar().setVisible(false);
			//while (playThread != null && playThread.isAlive()){
			//	Thread.sleep(100);
			//}			
			gateway.stopSimulation();
			
		}
		catch (SimulationException e){
			log.warning("Error stopping simulation: " + e.getMessage());
		}
		catch (ClientException e) {
			log.warning("Error closing the connection to model server: " + e.getMessage());
		}
		setConnected(false);
		gateway.close();
		this.setStatusText("No connection to server.");
	}

	@Override
	public void doPause() {
		this.paused = true;

	}

	@Override
	public void doStep() {
		try {
			if (!isConnected())
				this.reconnect();
			gateway.step(1, false);

			receivedData = new JavaStringData(gateway.receiveMessage());

			this.setChanged();
			this.notifyObservers(receivedData);
		}
		catch (ClientException e) {
			new ModelguiErrorDialog(e, "Error performing a simulation step.");
		}
	}

	@Override
	public void run() {
		paused = false;
		long lastTime = System.currentTimeMillis();
		long actualDelay = 0;
		try {
			while (!paused) {
				//System.out.println("do Step");
					//try {
						gateway.step(stepsPerRequest, false);
					//} catch(SimulationNotLaunchedException e2){
					//	gateway.startSimulation("Simulation");
					//}
				Tools.tic();
				String outputVal = gateway.receiveMessage();
				long time = Tools.tac();
				
				receivedData = new JavaStringData(outputVal);
				
				//log.info("Data-String-Länge pro Step: " + receivedData.toString().length());
				//log.info("Sende/Empfangszeit pro Step: " + time/1000.0f + " ms");

				this.setChanged();
				this.notifyObservers(receivedData);
				
				actualDelay = System.currentTimeMillis() - lastTime;					
				//log.println("Actual Delay: " + actualDelay);
				// if reaction time of SCADE is longer than requested delay,
				// increase the
				// number of steps that SCADE should do in one request
				if (actualDelay > requestedDelay) {
					stepsPerRequest++;
					requestedDelay = requestedDelay + delay;
				}
				else {
					stepsPerRequest = (stepsPerRequest == 1) ? 1
							: (stepsPerRequest - 1);
					requestedDelay = Math.max(requestedDelay - delay, delay);
				}

				// if computation time is shorter than requested delay, wait
				while (actualDelay < requestedDelay && !paused) {
					actualDelay = System.currentTimeMillis() - lastTime;
					// System.out.println("delay: " + requestedDelay
					// + " actDelay: " + actualDelay + " steps: "
					// + stepsPerRequest);
					if (requestedDelay - actualDelay > 20){
						try {
							Thread.sleep(requestedDelay - actualDelay - 10);
						}
						catch (InterruptedException e) {
						}
					}
				}

				lastTime = System.currentTimeMillis();
			}
		}
		catch (SimulationNotLaunchedException e){
			
			//this.init();
			try {
				
				gateway.startSimulation();
				setDelay(this.delay);
				
			}
			catch (ClientException e1) {
				this.close();
				new ModelguiErrorDialog("Cannot start simulator!");
			}
			
		}
		catch (SimulationException e){
			this.close();
		}
		catch (ClientException e) {
			new ModelguiErrorDialog(e, e.getMessage());
		}
	}

	@Override
	public void setDelay(int delay) {
		if (!isConnected())
			reconnect();
		
		this.delay = delay;
		requestedDelay = delay;
		stepsPerRequest = 1;

		// start a thread that plays multiple steps
		//if (playThread == null || !playThread.isAlive()) {
			playThread = new Thread(this);
			playThread.start();
		//}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof SVGApplication && arg instanceof String) {
			String msg = (String) arg;
			if (msg.equals("rendering done")) {
				//this.init();
				
			}
			if (msg.equals("loading started")) {
				this.close();
			}
		}

		// listen if button was pressed etc.
		if (o == dataToSend && arg instanceof String) {
			String ls = (String)arg;
			if (ls.equals("data")){
				dataHasChanged = true;
			}
		}
		/*if (initialData == true){
			try {
				gateway.setInputVector(dataToSend.toString());
				initialData = false;
			}
			catch (ClientException e) {
				new ModelguiErrorDialog(e, "Error while sending data to SCADE");
			}
		}*/
	}

	public void simulationStepped(SimulationEvent evt) {
		try {
			if (gateway.isConnected() && dataHasChanged && dataToSend != null){
				gateway.sendMessage(dataToSend.toString());
				dataHasChanged = false;
			}
		}
		catch (ClientException e) {
			new ModelguiErrorDialog(e, "Error while sending data to SCADE");
		}
	}

	public void simulationPaused(SimulationEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void simulationStarted(SimulationEvent evt) {
		//System.out.println("Simulation started");
		simulationStarted = true;
		
	}

	public void simulationStopped(SimulationEvent evt) {
		//connected = false;
		//System.out.println("Simulation stopped");
		simulationStarted = false;
		
	}
}
