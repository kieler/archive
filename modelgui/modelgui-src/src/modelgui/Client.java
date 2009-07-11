package modelgui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Logger;

import exceptions.ClientException;


public class Client implements Runnable{

	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	boolean isSending = true;
	boolean isReceiving = true;
	boolean connected = false;
	private String receivedMessage = "";
	private String sendMessage = "";
	private boolean messageAvailable = false;
	private boolean waitingForMessage = false;
	private int timeout = 1000;
	private boolean timeoutOccured = false;
	public static final int MAX_TIMEOUTS = 1;
	private static final int IDLENESS = 10;
	private char[] buffer = new char[16000];
	private Logger logger;
	
	public Client(){
		socket = new Socket();
		isSending = false;
		isReceiving = false;
		logger = Logger.getLogger(SVGApplication.LOGGER_NAME);
		
	}
	
	public void init(String host, int port) throws UnknownHostException, ConnectException, IOException, SocketTimeoutException{
		socket = new Socket(host, port);
		InputStreamReader inReader = new InputStreamReader(socket.getInputStream());
		in = new BufferedReader(inReader);
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		connected = true;
		isSending = true;
		isReceiving = true;
		new Thread(new Runnable(){
        
			public void run() {
				while (isSending) {
					send();
					Thread.yield();
				}
				close();
			}
			
		}).start();
		
		new Thread(this).start();
	}
	
	public void startSending(){
		isSending = true;
	}
	
	public void stopSending(){
		isSending = false;
	}
	
	public void startReceiving(){
		isReceiving = true;
	}
	
	public void stopReceiving(){
		isReceiving = false;
	}
	
	private void close(){
		isSending = false;
		isReceiving = false;
		if (socket != null)
			try {
				socket.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (in != null)
			try {
				in.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (out != null)
			try {
				out.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void send(){
		if (out != null)
			try {
				out.flush();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	public synchronized void send(String str){
		try {
			if (out != null){
				out.write(str);
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String receive() throws SocketTimeoutException{
		waitingForMessage = true;
		while (!messageAvailable){
			if (timeoutOccured){
				waitingForMessage = false;
				throw new SocketTimeoutException();
			}
			Thread.yield();
		}
		messageAvailable = false;
		waitingForMessage = false;
		return receivedMessage;
	}
	
	/**
	 * Checks if the connection is up and running.
	 */
	public boolean isConnected(){
		if (socket == null || socket.isClosed() || in == null || out == null){
			System.out.println("Socket closed!");
			return false;
		}
		return true;
	}

	public void run() {
		StringBuilder builder = new StringBuilder(1024);
		long wait = 0;
		while (isReceiving) {
			//try {
				
				boolean newMessage = false;
				try {
					Tools.tic();
					while (!messageAvailable && in.ready()){
						int numberOfChars = in.read(buffer, 0, buffer.length);
						timeoutOccured = false;
						//System.out.print(".");
						builder.append(buffer, 0, numberOfChars);
						newMessage = true;
					}					
					if (newMessage == false && waitingForMessage){
						wait = wait + Tools.tac();
						//System.out.println(wait);
						if (wait > timeout){
							wait = 0;
							timeoutOccured = true;
						}
					} else 
						wait = 0;
					if (newMessage){	
						//System.out.println(builder.toString());
						receivedMessage = builder.toString();
						messageAvailable = true;
						//System.out.println(builder.length());
						builder.delete(0, builder.length());
					}
					Thread.yield();		
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					new ModelguiErrorDialog(e, "Input stream is not available!");
					this.close();
				}
			//}
			
		}
		//close();		
	}
	
	public void setSoTimeout(int timeout) throws SocketException{
		this.timeout = timeout;
		socket.setSoTimeout(timeout);
	}
	
	
	public static void main(String[] args){
		Client testClient = new Client();
		try {
			testClient.init("localhost", 1234);
			while(true){
				testClient.send("this is a test");
				/*try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				System.out.println("Client received: " + testClient.receive());
			}
		}
		catch (ConnectException e) {
			new ModelguiErrorDialog(e.getLocalizedMessage() + "! Ensure that the server is up and running and that your port and host address settings are correct!");
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			new ModelguiErrorDialog("Socket timeout: " + e.getMessage());
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			new ModelguiErrorDialog("Unknown host: " + e.getLocalizedMessage());
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			new ModelguiErrorDialog(e.getLocalizedMessage());
		}
	}
}
