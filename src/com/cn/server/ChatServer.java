package com.cn.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.cn.constants.Constants;
import com.cn.constants.ServerConstants;
import com.cn.protocol.Protocol;

public class ChatServer {
	private int port;
	private ServerSocket serverSocket;
	Map<String, PrintWriter> chatMap;
	Logger logger = Logger.getLogger(ChatServer.class);

	public ChatServer() {
		logger.trace("Creating an instance of ChatServer");
		port = ServerConstants.CHAT_PORT;
		chatMap = Collections.synchronizedMap(new HashMap<String, PrintWriter>());
		logger.debug(ServerConstants.CHAT_SERVER_STARTUP);
		logger.debug(ServerConstants.CHAT_SERVER_ACCEPTING);
	}

	/**
	 * Opens the socket that the server uses
	 * @throws IOException
	 */
	public Boolean openSocket() {
		logger.trace("Opening socket.");
		try {
			this.serverSocket = new ServerSocket(port);
			return true;
		} catch (IOException e) {
			if(logger.isEnabledFor(Level.ERROR)) {
				logger.error("Error opening socket ", e);
			}
			return false;
		}
	}

	/**
	 * Method to kick off the server.
	 */
	public void start() {
		logger.trace("Starting up the server.");
		if(openSocket()) {
			loop();
		}
	}

	/**
	 *  1. Loop for accepting sockets
	 *  2. Create a thread for the accepted socket
	 */
	protected void loop() {
		logger.trace("Inside ChatServer.loop method");
		Socket clientSocket = null;

		/*
		 * Wait for clients at serverSockets, 
		 * instantiate a new thread for each new connection with the clientSocket information
		 */
		while(true) {
			try {
				clientSocket = null;
				clientSocket = serverSocket.accept();
				logger.debug("Has accepted connection from"+clientSocket.getRemoteSocketAddress());
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error(ServerConstants.Server_STARTUP_FAILED);
					logger.error(e);
				}
				System.exit(1);
			}

			Thread t = new Thread(new ChatServerThread(clientSocket));
			logger.trace("Starting a new thread");
			t.start();
		}
	}

	/**
	 * Main method that is called when you launch the 
	 * run_server.bat or run_server.sh
	 * 
	 * @param args not needed for now.
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(Constants.LOGGER_PROPERTIES);
		ChatServer chatServer2 = new ChatServer();
		chatServer2.start();
	}
	public class ChatServerThread implements Runnable {
		//ChatServer-Client Interaction
		protected Socket clientSocket;
		protected BufferedReader sockBufReader = null;
		protected PrintWriter sockPrintWriter = null;
		protected BufferedInputStream bis;
		protected BufferedOutputStream bos;
		protected String name;

		/*
		 * The constructor for the ChatServerThread object.
		 */
		public ChatServerThread(Socket s) {
			logger.trace("ServerThread initialization");
			clientSocket = s;

			try {
				bis = new BufferedInputStream(clientSocket.getInputStream());
				bos = new BufferedOutputStream(clientSocket.getOutputStream());
				sockPrintWriter = new PrintWriter(new OutputStreamWriter(bos), true);
				sockBufReader = new BufferedReader(new InputStreamReader(bis));
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("Exception initializing ServerThread", e);
				}
				try {
					if (sockBufReader != null)
						sockBufReader.close();
					if (sockPrintWriter != null)
						sockPrintWriter.close();
					serverSocket.close();	
				} catch (Exception ee) {
					if(logger.isEnabledFor(Level.ERROR)) {
						logger.error("Exception thrown closing sockBufReader and sockPrintWriter", ee);
					}
				}
			} 
		}

		/*
		 * Thread main routine
		 */
		public void run() {
			logger.trace("Inside Server.run method");
			try {
				String request;
				while ((request = sockBufReader.readLine()) != null) {
					if(logger.isDebugEnabled()) {
						logger.debug("Request: " + request);
					}
					if (request.length() < 1) {
						if(logger.isEnabledFor(Level.ERROR)) {
							logger.error("Message length is too short: "+request.length());
						}
						continue;
					}
					logger.debug("The request is : " + request);
					String cmd = Protocol.getRequestCmdSimple(request);
					String[] args = Protocol.getRequestArgsSimple(request);
					if(cmd.equals(Constants.CHAT_LOGIN)) {
						logger.debug("The cmd was " + Constants.CHAT_LOGIN);
						onCHATLOGIN(args);
						continue;
					}
					else if(cmd.equals(Constants.CHAT_LOGOUT)) {
						logger.debug("The cmd was " + Constants.CHAT_LOGOUT);
						onCHATLOGOUT(args);
						continue;
					}
					else if(cmd.equals(Constants.XP)) {
						logger.debug("The cmd was " + Constants.XP);
						logger.debug("This is not implemented yet");
						//onXP(args);
						continue;
					}
					else if (cmd.equals(Constants.SEND_MESSAGE)) {
						logger.debug("The cmd was " + Constants.SEND_MESSAGE);
						onSENDMSG(args);
						continue;
					}
				}
			} catch (Exception e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("User has disconnected from Chat Server... " +serverSocket.getInetAddress());
				}
			}
			 finally {
					logger.trace("Closing socket.");
					try {
						if (sockBufReader != null)
							sockBufReader.close();
						if (sockPrintWriter != null)
							sockPrintWriter.close();
						clientSocket.close();	
					} catch (Exception e) {
						if(logger.isEnabledFor(Level.ERROR)) {
							logger.error("Error closing socket.", e);
						}
					}
				}
		}


		private void onCHATLOGOUT(String[] args) {
			if(args.length != 2) {
				return;
			}
			String name = args[1];
			if(chatMap.containsKey(name)) {
				logger.debug("Removing the player " + name + "from the chatMap");
				chatMap.remove(name);
			}
			else {
				logger.error("The logout user is not logged in to the chat server");
			}

		}

		private void onCHATLOGIN(String[] args) {
			if(args.length != 2) {
				return;
			}
			String name = args[1];
			if(chatMap.containsKey(name)) {
				logger.debug("This character is already logged in to the chat server...");
				return;
			}
			else {
				this.name = name;
				chatMap.put(this.name, sockPrintWriter);
			}
		}

		private void onSENDMSG(String[] args) {
			if(args.length != 3) {
				return;
			}
			String name = args[1];
			String message = args[2];
			if(chatMap.containsKey(name)) {
				logger.debug("Sending message to " + name);
				logger.debug("Print writer " + chatMap.get(name));
				logger.debug(clientSocket.getLocalAddress());
				chatMap.get(name).println("["+name+"] " + message);
				return;
			}
			else {
				logger.debug("This user is not logged in to the chat server...");
			}
		}

	}

}
