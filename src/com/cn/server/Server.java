package com.cn.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private int port;
	private String hostName;
	private ServerSocket serverSocket;
	
	public Server() {
		port = 7870;
		hostName = "localhost";
	}
	
	/**
	 * Sets the hostname to the input string.
	 * @param hostName
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	/**
	 * Returns the hostName.
	 * @return hostName
	 */
	public String getHostName() {
		return hostName;
	}
	
	/**
	 * Sets the hostname to the input string.
	 * @param hostName
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Returns the hostName.
	 * @return hostName
	 */
	public int getPort() {
		return port;
	}

	public void setSocket(ServerSocket socket) {
		this.serverSocket = socket;
	}

	public ServerSocket getSocket() {
		return serverSocket;
	}
	
	/**
	 * Opens the socket that the server uses
	 * @throws IOException
	 */
	public Boolean openSocket() {
		try {
			setSocket(new ServerSocket(port));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Method to kick off the server.
	 */
	public Boolean start() {
		return (this.openSocket() ? true : false);
	}
	
	/**
	 *  1. Loop for accepting sockets
	 *  2. Create a thread for the accepted socket
	 */
	protected void loop() {
		
		Socket clientSocket = null;

		/*
		 * Wait for clients at serverSockets, 
		 * instantiate a new thread for each new connection with the clientSocket information
		 */
		while(true) {
			try {
				clientSocket = null;
				System.out.println("Waiting for accepting...");
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.out.println("Server socket is closed");
				System.exit(1);
			} 
			
			/*
			 * Start a new thread (ServerThread).
			 */
			Thread t = new Thread(new ServerThread(clientSocket));
			t.start();
			System.out.println("Accepted a connection on port: " + serverSocket.getLocalPort());
		}
	}
}
