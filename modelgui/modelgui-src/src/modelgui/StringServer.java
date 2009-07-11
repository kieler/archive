package modelgui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;


import org.omg.CORBA_2_3.portable.OutputStream;

/**
 * Simple TCP/IP Server for testing. Reads from TCP/IP and displays the data on
 * the console. Vice versa reads from stdin and writes to TCP/IP. Main method
 * will start both threads, the reader and the writer.
 * 
 * @author haf
 */
public abstract class StringServer {

	
	private static int BUFFER_SIZE = 1024;
	Socket socket;
	BufferedWriter out;
	BufferedReader in;
	CharBuffer cbuf = CharBuffer.allocate(BUFFER_SIZE);
	ServerSocket server;
	boolean isSending = true;
	protected boolean isReceiving = true;

	/**
	 * @param isSending
	 * @param socket
	 */
	public StringServer(int port) {
		try {
			server = new ServerSocket(port);
			System.out.print("Waiting for client... ");
			this.socket = server.accept();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			System.out.println("Connected. ");
			new Thread(new Runnable(){

				public void run() {
					while (isSending) {
						send();
						try {
							Thread.sleep(10);
						}
						catch (InterruptedException e) {
						}
					}
				}
				
			}).start();
			
			new Thread(new Runnable(){

				public void run() {
					StringBuilder builder = new StringBuilder(1024);
					while (isReceiving) {
						try {
							boolean newMessage = false;
							while (in.ready()){
								char c = (char)in.read();
								builder.append(c);
								newMessage = true;									
							}							
							if (newMessage){	
								messageReceived(builder.toString());
								builder.delete(0, builder.length());
							} else {
								try {
									Thread.sleep(10);
								}
								catch (InterruptedException e) {
								}
							}									
						}
						catch (IOException e) {
							isReceiving = false;
						}							
					}
				}
				
			}).start();
		}
		catch (IOException e1) {
			isReceiving = false;
			isSending = false;
			return;
		}
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e) {
			isReceiving = false;
			isSending = false;
		}
	}
	
	public boolean bufferFull(){
		if (cbuf.remaining() == 0)
			return true;
		else
			return false;
	}
	
	public boolean bufferEmpty(){
		System.out.println(cbuf.remaining());
		if (cbuf.remaining() == BUFFER_SIZE)
			return true;
		else
			return false;
	}
	
	private void send(){
		try {
			if (out != null)
				out.flush();
		}
		catch (IOException e) {
			this.isSending =false;
		}
	}
		
	public void send(String str){
		try {
			if (out != null)
				out.write(str);
		}
		catch (IOException e) {
			this.isSending = false;
		}
	}
	
	
	
	public abstract void messageReceived(String msg);
	
	

	/**
	 * Test function to receive TCP/IP data and dump it to the console.
	 * 
	 * @author haf
	 * @param args
	 */
	public static void main(String[] args) {

		StringServer server = new StringServer(1234){
			int count = 0;
			@Override
			public void messageReceived(String msg) {
				count++;
				System.out.println("Server received: " + msg);
				if (count < 100)
					this.send("OK");
			}
			
			
			
		};

	}

}
