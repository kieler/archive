package modelgui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import exceptions.ClientException;
import exceptions.SimulationException;

/**
 * SCADE Slave Simulator Gateway. An object of this class can open a TCP/IP
 * connection to a SCADE model in the SCADE Editor. From there the SCADE
 * simulator is started and run in a remote controlled slave mode. In this mode
 * TCP/IP commands send from this object control the whole SCADE simulation.
 * You can advance simulation steps, read outputs, set inputs, activate or
 * refresh the SCADE simulator GUI and close the simulation session.
 *
 * This Java gateway was implemented by reverse engineering of the original
 * Windows C library of the SCADE gateway.
 *
 * Notice the private String constants in this class! SCADE reacts quite fragile
 * to different TCP messages and might crash. For example a different newline
 * symbol (only \n instead of \r\n) will crash SCADE. So be careful in adding or
 * changing any of these Strings.
 *
 *  @version for SCADE 6.0 Prototype 3A (only rudimentary functions work in SCADE)
 *
 * @author Hauke Fuhrmann <haf@informatik.uni-kiel.de>
 *
 */

public class ScadeSlaveGateway extends AbstractGateway {

	/** Every TCP command is prefixed with this String **/
	private static final String prefix = "SsmProxy::";
	/** SCADE command that starts the simulator **/
	private static final String start = "Start";
	/** SCADE command that evaluates some simulation steps **/
	private static final String step = "Step";
	/** Acknowledgement of SCADE if it has received a command correctly **/
	private static final String ok = "OK";
	/** SCADE command that requests the value of one explicit SCADE variable **/
	private static final String getoutputvalue = "GetOutputValue";
	/** SCADE command that requests the value of all top level SCADE outputs **/
	private static final String getoutputvector = "GetOutputVector";
	/** SCADE command that sets all top level SCADE inputs **/
	private static final String setinputvector = "SetInputVector";
	/** SCADE command that sets the value of one explicit SCADE variable **/
	private static final String setinputvalue = "SetInputValue";
	/** SCADE command that activates the simulator GUI and brings it into foreground **/
	private static final String activate = "Activate";
	/** SCADE command that refreshes the SCADE simulator gui **/
	private static final String refresh = "Refresh";
	/** SCADE command that evaluates some simulation steps and then pauses until manually resumed **/
	private static final String debugstep = "DbgStep";
	/** SCADE command that closes the simulation session and the TCP communication **/
	private static final String close = "Close";
	/** RegExp that matches an error message of SCADE that is send instead of an acknowledgemen **/
	private static final String error = "(\\d+\\.([^\\.])+\\.)|ERROR"; // number.text.|ERROR
	/** RegExp that matches a normal response of SCADE with the String representation of data **/
	private static final String response = ".+";
    /** Delimiter symbol that separates arguments in a TCP command to SCADE **/
	private static final String delim = " ";
	/** Finalizer symbol that SCADE either sends or expects in some messages **/
	private static final String finalizer = ".";
	/** Line terminating symbol that SCADE expects in some messages **/
	private static final String terminator = "\r\n";
	/** Some messages even require a tailing null character. This symbol is only a placeholder
	 *  in the string and will be replaced by the real 0 when sent **/
	private static final String nullSymbol = "|NULL|";

	private final String defaultConfiguration = "Simulation";

	private int shortto = 1000;  // short timeout
	private int longto = 0; // long timeout (infinity)
	private int readRetries = 10;

	Socket socket;

	BufferedReader in;

	PrintWriter out;

	ClientException scadeException;
	String outputPath;

	/**
	 * Standard Constructor. Creates new uninitialized Gateway object.
	 *
	 */
	public ScadeSlaveGateway() {
		socket = new Socket();
	}
	
	public ScadeSlaveGateway(String outputPath) {
		socket = new Socket();
		this.outputPath = outputPath;
	}

	/**
	 * Initialize the Gateway object by connecting to the corresponding host and
	 * port. This method assumes, SCADE is up and running and the corresponding
	 * project is loaded (simulator is not yet started). In project settings the
	 * listening port must have been set.
	 *
	 * @param host
	 * @param port
	 * @throws ClientException
	 *             if connection fails
	 */
	public void init(String host, int port) throws ClientException {
		System.out.println("Initializing SCADE slave simulation gateway");
		try {
			socket = new Socket(host, port);
			socket.setSoTimeout(shortto);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			System.out.println("finished");
		} catch (UnknownHostException e) {
			throw new ClientException(e,
					"Problem creating socket to the host. Hostname or IP is unknown: "
							+ host + "\n : ");
		} catch (IOException e) {
			throw new ClientException(
					e,
					"Problem creating socket to the host!\n"
							+ e.getMessage()
							+ "\nPlease make sure, SCADE is up and the corresponding project is loaded and the correct connection port is set up in the project settings for co-simulation!");
		} catch (Exception e) {
			throw new ClientException(e, "");
		}
	}

	/**
	 * Change the timeout that is waited for a response of SCADE after a
	 * request was sent. The default is optimized for a local area network
	 * and might need to be increased for environments with long tcp/ip
	 * delays.
	 * @param timeout
	 */
	public void setReadTimeout(int timeout){
		this.shortto = timeout;
	}

	/**
	 * Starts simulation with the given configuration (Name of the code
	 * generator configuration as set in the SCADE settings). The root node is
	 * used from the SCADE configuration settings.
	 *
	 * @param configuration
	 * @throws ClientException
	 */
	public void startSimulation(String configuration) throws ClientException {
		String rootnode = null;
		startSimulation(configuration, rootnode);
	}

	/**
	 * Starts simulation with the given configuration (Name of the code
	 * generator configuration as set in the SCADE settings) and the rootnode.
	 *
	 * @param configuration
	 * @param rootnode
	 * @throws ClientException
	 *             if no connection was established before
	 */
	public void startSimulation(String configuration, String rootnode)
			throws ClientException {
		String conf = "\"" + defaultConfiguration;
		if (configuration != null)
			conf = "\"" + configuration;
		conf += "\"";
		if (rootnode == null)
			requestAcknowledge(prefix + start + delim + conf + delim + terminator, false, longto);
		else
			requestAcknowledge(prefix + start + delim + conf + delim + rootnode + terminator , false, longto);
	}

	/**
	 * Will tell the simulator to advance some simulation steps.
	 *
	 * @param stepsize
	 *            Number of steps that should be calculated
	 * @param refresh
	 *            true if the SCADE Gui should be refreshed after the steps
	 * @throws ClientException
	 *             if no connection was established before
	 */
	public void step(int stepsize, boolean refresh) throws ClientException {
		checkConnection();
		String refreshString = refresh ? "1" : "0";
		requestAcknowledge(prefix + step + delim + stepsize + delim + refreshString + terminator, false, shortto);
	}

	/**
	 * Will tell the simulator to advance some simulation steps and wait for the
	 * user to press the SCADE continue button.
	 *
	 * @param stepsize
	 *            Number of steps that should be calculated
	 * @throws ClientException
	 *             if no connection was established before
	 */
	public void debugStep(int stepsize) throws ClientException {
		checkConnection();
		requestAcknowledge(prefix + debugstep + delim + stepsize + terminator, false, shortto);
	}

	/**
	 * Activates the SCADE Gui and bring it into focus.
	 *
	 * @throws ClientException
	 *             if no connection was established before
	 */
	public void activate() throws ClientException {
		checkConnection();
		requestAcknowledge(prefix + activate + terminator + nullSymbol, false, shortto);
	}

	/**
	 * Refresh the SCADE Gui. Useful together with the step method and there the
	 * refresh value set to false. This way the developer has full control over
	 * the refresh behaviour of the SCADE gui.
	 *
	 * @throws ClientException
	 *             if no connection was established before
	 */
	public void refresh() throws ClientException {
		checkConnection();
		requestAcknowledge(prefix + refresh + terminator + nullSymbol , false, shortto);
	}

	/**
	 * Should return the output vector of the top level SCADE outputs. Seems to
	 * be not implemented by SCADE yet, as it always returns only a . character.
	 *
	 * @return .
	 * @throws ClientException
	 *             if no connection was established before
	 */
	public String getOutputVector() throws ClientException {
		checkConnection();
		return requestAcknowledgeResponse(prefix + getoutputvector + terminator , true, shortto);
	}

	/**
	 * Returns the String representation of a specified SCADE output variable.
	 * The variable is set by the corresponding parameter in the typical SCADE
	 * format: package::rootnode/variable All accessible variables (set by the
	 * optimization level of the SCADE code generator can be accessed. Set the
	 * path parameter to the same String as displayed in the SCADE simulator Gui
	 * in the watch list. Variables of other nodes can be accessed by using the
	 * corresponding occurrence number: package::rootnode/package::childnode
	 * occnumber/variable e.g.
	 * simulation::railway/simulation::kicking_horse_pass 1/feedback
	 *
	 * @param pathToScadeVariable
	 * @return
	 * @throws ClientException
	 */
	public String getOutputValue(String pathToScadeVariable)
			throws ClientException {
		checkConnection();
		return requestAcknowledgeResponse(prefix + getoutputvalue + delim + pathToScadeVariable + terminator, true, shortto);
	}

/*	public void setInputValue(String pathToScadeVariable, String inputVector)
			throws ClientException {
		checkConnection();
		requestAcknowledge(prefix + setinputvalue + delim + pathToScadeVariable + delim + terminator + inputVector , false, shortto);
	}
*/
	
	public void setInputVector(String inputVector) throws ClientException{
		checkConnection();
		requestAcknowledge(prefix + setinputvector + delim  + inputVector + terminator , false, shortto);
	}

	/**
	 * Closes the SCADE slave simulator and closes the TCP/IP connection.
	 *
	 * @throws ClientException
	 *             if no connection was established before
	 */
	public void close() {
		try {
			checkConnection();
			requestAcknowledge(prefix + close + terminator , false, shortto);
		}
		catch (ClientException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}		
	}

	/**
	 * Checks if the connection to the SCADE slave simulator via TCP/IP is up
	 * and running. Will either return normally or throw an Exception.
	 *
	 * @throws ClientException
	 *             if no connection was established before
	 */
	private void checkConnection() throws ClientException {
		if (socket == null || socket.isClosed() || in == null || out == null)
			throw new ClientException(
					"No connection to the SCADE slave simulator!");
		return;
	}
	
	/**
	 * Checks if the connection to the SCADE slave simulator via TCP/IP is up
	 * and running.
	 * */
	public boolean isConnected(){
		if (socket == null || socket.isClosed() || in == null || out == null)
			return false;
		return true;
	}

	/**
	 * Reads one answer of SCADE. Unfortunately the answers are not terminated
	 * by a newline character. So it is difficult to decide how many characters
	 * should be expected. Hence the content is parsed and compared to some
	 * internal regular expressions with expected response signatures
	 *
	 * @return The whole read response
	 * @throws ClientException
	 */
	private boolean readAck(boolean expectFinalizer) throws ClientException, SocketTimeoutException{
		String line = "";
		try {
			char c;
			int size = 0;
			do {
				c = (char) in.read();
				line += c;
				size++;

				if(expectFinalizer && line.matches(ok+finalizer))
					return true;
				else if(!expectFinalizer && line.matches(ok))
					return true;
				else if(line.matches(error))
					throw new ClientException("Message from SCADE: " + line);
				else if(size > 1000){
					freeBuffer();
					return false;
					//throw new ClientException("No valid data received from SCADE: "+line);
				}
			} while (true);
		} catch (SocketTimeoutException ste){
			throw ste;
		} catch (IOException e) {
			throw new ClientException(e, "");
		}
	}

	private String readResponse() throws ClientException, IllegalArgumentException{
		String line = "";
		try {
			char c;
			int size = 0;
			int bracketCount1 = 0; // (
			int bracketCount2 = 0; // [
			int bracketCount3 = 0; // {
			boolean bracketsBalanced = false;
			boolean scalar = true;
			do {
				// System.out.println("start read");
				c = (char) in.read();
				line += c;
				//System.out.print(c);
				size++;
				//System.out.print((int)c + " ");
				System.out.flush();

				// check if bracket amounts are correct
				if (c == '(')
					bracketCount1++;
				else if (c == ')')
					bracketCount1--;
				else if (c == '[')
					bracketCount2++;
				else if (c == ']')
					bracketCount2--;
				else if (c == '{')
					bracketCount3++;
				else if (c == '}')
					bracketCount3--;
				if (bracketCount1 == 0 && bracketCount2 == 0
						&& bracketCount3 == 0){
					bracketsBalanced = true;
					//System.out.println("\nbracketsBalanced");
				}
				else{
					bracketsBalanced = false;
					scalar = false;
				}

				if(c == '\n')
					return line;
				else if(scalar){
//System.out.println(c+ " Is good scalar char: "+Character.isJavaIdentifierPart(c));
					if( !Character.isDefined(c) ||
						!Character.isJavaIdentifierPart(c))
						throw new IllegalArgumentException("SCADE started to send a scalar which is not valid: "+line);
					if(!in.ready())
						return line;
				}
				else if(line.matches(response) && bracketsBalanced && !scalar){
//					System.out.println("returning");
					return line;
				}
				else if(line.matches(error))
					throw new ClientException("Message from SCADE: " + line);
			} while (true);
		} catch (IOException e) {
			throw new ClientException(e, "");
		}
	}

	/**
	 * Empties the receive buffer and throws the data away. This method can be used
	 * if an error disturbed the communication and the order of messages is somewhat
	 * scrambled. E.g. sending requests to SCADE too fast might return multiple successive
	 * acknowledgements before multiple successive responses. To start over the request,
	 * the read buffer should be freed completely.
	 */
	public void freeBuffer(){
		try{
			while(in.ready())
				in.read();
		}catch(IOException e){
			/*nothing*/
		}
	}

	/**
	 * This methods sends a command request to SCADE and reads the acknowledgement from
	 * SCADE. After sending the request, the method only reads for a specified timeout.
	 * If the timeout has expired, the request is resent. This is repeated several times.
	 * Unfortunately we have observed, that SCADE sometimes does not react to a request.
	 * @param request
	 *                String message that is send to SCADE.
	 * @param expectFinalizer
	 *                must be true, iff the Acknowledgement of SCADE is followed by the
	 *                finalizer string (e.g. "OK." instead of "OK")
	 * @param timeout
	 *                Number of milliseconds that the method waits for a reaction of SCADE
	 * @throws ClientException
	 *                thrown if something unexpected happened in the underlying send or
	 *                readAck methods or if the timeout has expired too often
	 */
	private void requestAcknowledge(String request, boolean expectFinalizer, int timeout) throws ClientException{
//System.out.println("Request: "+request.replaceAll("\\s", " "));
		try {
			socket.setSoTimeout(timeout);
		}catch(SocketException e){
			throw new ClientException(e.getMessage());
		}
		for( int i=0 ; i<=readRetries ; i++ ){
			send(request);
			try {
				readAck(expectFinalizer);
//System.out.println("Acknowledgement OK");
				break; // if the read finished without timeout exception, everything is fine
			} catch (SocketTimeoutException e) {
				if(i==readRetries)
					throw new ClientException(e,"SCADE did not answer to the request within "+readRetries+" retries: \""+request+"\"");
				else
					freeBuffer(); // the request is resent, so reset the receive buffer first
//				System.out.println("Timeout for request: "+request);
			}
		}
	}

	/**
	 * Same as requestAcknowledge with an integrated readResponse which is also included
	 * into the timeout repeat sequence. Hence an invalid response leads to a resend
	 * of the request.
	 * @param request
	 * @param expectFinalizer
	 * @param timeout
	 * @return
	 * @throws ClientException
	 */
	private String requestAcknowledgeResponse(String request, boolean expectFinalizer, int timeout) throws ClientException{
//System.out.println("Request: "+request.replaceAll("\\s", " "));
		try {
			socket.setSoTimeout(timeout);
		}catch(SocketException e){
			throw new ClientException("Could not set timeout: "+e.getMessage());
		}
		for( int i=0 ; i<=readRetries ; i++ ){
			send(request);
			try {
				readAck(expectFinalizer);
//				System.out.println("Acknowledgement OK");
				return readResponse();
				// if the read finished without timeout exception, everything is fine
			} catch (SocketTimeoutException e) {
				if(i==readRetries)
					throw new ClientException(e,"SCADE did not answer to the request within "+readRetries+" retries: \""+request+"\"");
				/*else nothing, just repeat the loop */
//				System.out.println("Timeout for request: "+request);
			} catch (IllegalArgumentException e){
				// scade has returned some bad value
				if(i==readRetries)
					throw new ClientException(e,"SCADE did not answer to the request within "+readRetries+" retries: \""+request+"\"");
				/*else nothing, just repeat the loop */
//				System.out.println("Illegal Argument: "+request);
			}
		}
		return ""; // this should never happen
	}

	/**
	 * Sends a command string to SCADE. Might have some debugging side effects,
	 * like printing the sent string to the console or something. Notice that
	 * SCADE is quite fragile corresponding the line terminators.
	 *
	 * @param msg
	 *            Message that should be send to SCADE
	 * @throws ClientException
	 *             if no connection was established before
	 */
	private void send(String msg) throws ClientException {
		// check if the String should be terminated with 0
		int index = msg.indexOf(nullSymbol);
		if(index > -1){
			// dont write the nullSymbol place holder
			out.write(msg, 0, index);
			// write a real Null character
			out.write(0);
		}
		else
			out.print(msg);
		out.flush();
	}

	public static void main(String[] args) {
		ScadeSlaveGateway scade = new ScadeSlaveGateway();
		try {
			// test03();
			scade.init("10.6.3.5", 50014);
			//new Thread(scade).start();
			scade.startSimulation("Simulation", null);
			//while(true){
			// scade.step(1, true);
			//}
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// scade.step(3, true);
			scade.refresh();
			scade.activate();
			// scade.debugStep(2);

			// System.out.println("OutputVector: " + scade.getOutputVector());
			/*String pointString = "[";
			for (int i = 0; i < 47; i++) {
				pointString += "simulation::p_turn,";
			}
			pointString += "simulation::p_turn]";*/
			
			for (int i = 0; i < 100; i++){
				scade.step(1, true);
				try {
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			scade.setInputVector("(false,false,true)");
			System.out.println("OutputValue: "
					+ scade.getOutputValue("simulation::Simulation/displayData"));
			for (int i = 0; i < 100; i++){
				scade.step(1, true);
				try {
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// scade.setInputValue(pointString,"simulation::railway/points.");
			//System.out.println("OutputValue: "
			//		+ scade.getOutputValue("simulation::Simulation/displayData"));
			// System.out.println("OutputValue: " +
			// scade.getOutputValue("simulation::railway/feedback"));
			// System.out.println("OutputValue: " +
			// scade.getOutputValue("simulation::railway/feedback"));
			 scade.close();
			 System.out.println("finished.");

		} catch (ClientException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}

	/**
	 * Just a test thread that displays all incoming data.
	 */
	/*public void run() {
		try {
			checkConnection();
			char c;
			do {
				// System.out.println("start read");
				c = (char) in.read();
				System.out.print(c);
				System.out.flush();
				// System.out.println("Ready: "+in.ready());
			} while (true);
		} catch (ClientException e) {
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	@Override
	public String receiveMessage() throws ClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(String message) throws ClientException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startSimulation() throws ClientException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopSimulation() throws SimulationException, ClientException {
		// TODO Auto-generated method stub
		
	}

}
