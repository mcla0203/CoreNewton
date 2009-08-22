package com.cn.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {
	
	// Client-Server Interaction
	protected String serverName;
	protected int serverPort;
	protected Socket clientSocket = null;
	protected BufferedInputStream bis;
	protected BufferedOutputStream bos;
	protected BufferedReader sockBufReader;
	protected PrintWriter sockPrintWriter;

	protected UserInput userInput = null;
	
	protected String myURL = null;
	protected String cmd = "";
	
	Logger logger = Logger.getLogger(ClientConstants.LOGGER);
	
	/**
	 * Default constructor.  Sets the URL, and gets the user input.
	 */
	public Client() {
		//init the URL
		try { myURL = InetAddress.getLocalHost().getHostAddress();}
		catch(Exception e) { e.printStackTrace(); System.exit(1); }
		userInput = new UserInput();
	}
	
	/**
	 * Method to run the Client as the cmd line interface.
	 */
	public void runClient() {
		while(true) {
			String cmd = userInput.getUserInput();
			String[] input = cmd.split(" ");
			if(input[0].equals(ClientConstants.CONNECT)) {
				System.out.println(ClientConstants.CONNECT_ATTEMPT);
				if(input.length != 3) {
					System.out.println(ClientConstants.CONNECT_ATTEMPT_MALFORMED);
				}
				else {
					connectToServer(input[1], Integer.valueOf(input[2]));
				}
			}
		}
	}
	
	
	/**
	 * Method that connects to the server Given a serverName (ip/url)
	 * and serverPort.
	 */
	protected int connectToServer(String serverName, int serverPort) { 
		System.out.println("Connecting to the server...." + serverName+ ":"+serverPort);
		int retVal = 0;
		this.serverName = serverName;
		this.serverPort = serverPort;

		try {
			clientSocket = new Socket(serverName, serverPort);
			Thread t = new Thread(new ClientShutdown());
			Runtime.getRuntime().addShutdownHook(t);
		} catch (Exception e) {
			System.out.println("Socket connection to "+serverName+":"+serverPort+" failed!");
			clientSocket = null;
			retVal = -1;
		}

		try {
			bis = new BufferedInputStream(clientSocket.getInputStream());
			bos = new BufferedOutputStream(clientSocket.getOutputStream());
			sockPrintWriter = new PrintWriter(new OutputStreamWriter(bos), true);
			sockBufReader = new BufferedReader(new InputStreamReader(bis));
		} catch (Exception e) {
			clientSocket = null;
			retVal = -1;
		}

		return retVal;
	}
	
	/**
	 * Destroy socket to disconnect from the server.
	 * Sets the value of client to null after calling close()
	 */
	protected void disconnectFromServer() {
		try {
			if (clientSocket != null) {
				clientSocket.close();
				clientSocket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shutdown handler for the client
	 */
	public class ClientShutdown extends Thread {
		public void run() {
			try {
				if (clientSocket != null) {
					System.out.println("Closing client...");
					clientSocket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Main method that is called when you launch the 
	 * run_client.bat or run_server.sh
	 * 
	 * @param args not needed for now.
	 */
	public static void main(String[] args) {
		Client client = new Client();
		client.runClient();
	}
}
