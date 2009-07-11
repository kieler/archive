package testing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import modelgui.StringServer;

import junit.framework.TestCase;

public class BenchmarkTestServer extends StringServer{
	
	Socket socket;
	ServerSocket server;
	
	public BenchmarkTestServer(int port){
		super(port);
	}
	
	@Override
	public void messageReceived(String msg) {
		System.out.println("Server received: " + msg);
		this.send("OK.");
	}
	
	public static void main(String[] args){
		BenchmarkTestServer server = new BenchmarkTestServer(1234);
		server.send("huhu");
		
	}
}
