package com.cn.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	private int port;
	private String hostName;
	private ServerSocket socket;
	
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
		this.socket = socket;
	}

	public ServerSocket getSocket() {
		return socket;
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
}
