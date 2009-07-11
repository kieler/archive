package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;

import modelgui.StringClient;

public class BenchmarkTestClient extends StringClient {

	public BenchmarkTestClient(String host, int port) {
		super();
		try {
			init(host, port);
		}
		catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args){
		try {
			Socket socket = new Socket("127.0.0.1", 60014);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while (true){
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				for (int i = 0; i <= 100; i++){
					writer.write(String.valueOf(i) + "\n");
					writer.flush();
					Thread.yield();
					sleep(40);
				}
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int i = 100; i >= 0; i--){
					writer.write(String.valueOf(i) + "\n");
					writer.flush();
					Thread.yield();
					sleep(40);
				}
			}
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void sleep(long millis){
		 try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
 