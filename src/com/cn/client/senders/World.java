package com.cn.client.senders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.cn.constants.ClientConstants;
import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.protocol.Protocol;

public class World {

	//World server interaction
	protected Socket clientSocket = null;
	protected BufferedInputStream bis;
	protected BufferedOutputStream bos;
	protected BufferedReader sockBufReader;
	public PrintWriter sockPrintWriter;

	
	String serverName;
	int port;
	Logger logger = Logger.getLogger(this.getClass());
	
	public World(String serverName, int port) {
		this.serverName = serverName;
		this.port = port;
		connectToServer();
	}
	
	/**
	 * Method that connects to the server Given a serverName (ip/url)
	 * and serverPort.  Returns 0 on success.
	 */
	private int connectToServer() { 
		int retVal = 0;
		try {
			logger.debug("Attempting to connect to the server." + serverName +":"+port);
			clientSocket = new Socket(serverName, port);
			Thread t = new Thread(new ClientShutdown());
			Runtime.getRuntime().addShutdownHook(t);
		} catch (Exception e) {
			clientSocket = null;
			retVal = -1;
		}
		try {
			bis = new BufferedInputStream(clientSocket.getInputStream());
			bos = new BufferedOutputStream(clientSocket.getOutputStream());
			sockPrintWriter = new PrintWriter(new OutputStreamWriter(bos), true);
			sockBufReader = new BufferedReader(new InputStreamReader(bis));
		} catch (Exception e) {
			e.printStackTrace();
			clientSocket = null;
			retVal = -1;
		}
		return retVal;
	}

	/**
	 * Sends a message to the world server, and then returns
	 * the response.
	 * @param message
	 * @return
	 */
	public String sendToServerAndGetResponse(String message) {
		try {
			sockPrintWriter.println(message);
			String response = sockBufReader.readLine();
			return response;
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		}	
	}

	public void processInput(String[] input, boolean isLoggedIn, boolean isPlaying) {
		if(input[0].equals(Constants.GET_HEALTH)) {
			doGETHEALTH(input, isPlaying);
		}
		else if(input[0].equals(Constants.GET_ENERGY)) {
			doGETENERGY(input, isPlaying);
		}
		else if(input[0].equals(ClientConstants.GET_DEAD_MONSTERS)) {
			doGETDEADMONSTERS(input, isPlaying);
		}
		else if(input[0].equals(ClientConstants.GET_MONSTER)) {
			doGETMONSTERS(input, isPlaying);
		}
		else if(input[0].equals(ClientConstants.GET_PLAYERS)) {
			doGETPLAYERS(input, isPlaying);
		}
		else if(input[0].equals(Constants.REST)) {
			doREST(input, isPlaying);
		}
		else if(input[0].equals(Constants.LOOT)) {
			doLOOT(input, isLoggedIn, isPlaying);
		}
	}

	public void doLOOT(String[] args, boolean isLoggedIn, boolean isPlaying) {
		if(args.length != 2) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		if(!isLoggedIn || !isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		String response = sendToServerAndGetResponse(Protocol.createLootRequest(args[1]));
		if(response.equals(ProtocolConstants.SUCCESS)) {
			System.out.println("You looted!");
		}
		else if(response.equals(ProtocolConstants.MONSTER_DOES_NOT_EXIST)) {
			System.out.println("This monster does not exist.");
		}
		else if(response.equals(ProtocolConstants.CANNOT_LOOT_MONSTER)) {
			System.out.println("Monster has already been looted or is still alive.");
		}
		else {
			System.out.println("Failed to loot monster.");
		}
	}

	public void doGETENERGY(String[] input, boolean isPlaying) {
		if(!isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(input.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			System.out.println("Just use 'getEnergy'");
			return;
		}	
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_ENERGY);
		String cmd = Protocol.getRequestCmdSimple(response);
		logger.debug("The cmd is: " + cmd);
		String[] responseArgs = Protocol.getRequestArgsSimple(response);
		if(response == null) {
			System.out.println(ClientConstants.GENERAL_FAILURE);
			return;
		}
		if(Constants.SUCCESS.equals(cmd)) {
			System.out.println("Your energy is: " + responseArgs[1]);
		}
	}

	public void doGETHEALTH(String[] input, boolean isPlaying) {
		if(!isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(input.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			System.out.println("Just use 'getHealth'");
			return;
		}	
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_HEALTH);
		String cmd = Protocol.getRequestCmdSimple(response);
		logger.debug("The cmd is: " + cmd);
		String[] responseArgs = Protocol.getRequestArgsSimple(response);
		if(response == null) {
			System.out.println(ClientConstants.GENERAL_FAILURE);
			return;
		}
		if(Constants.SUCCESS.equals(cmd)) {
			System.out.println("Your health is: " + responseArgs[1]);
		}
	}


	public void doREST(String[] args, boolean isPlaying) {
		if(args.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		if(!isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		String response = sendToServerAndGetResponse(ProtocolConstants.REST);
		if(response == null) {
			System.out.println(ClientConstants.GENERAL_FAILURE);
			return;
		}
		if(ProtocolConstants.SUCCESS.equals(response)) {
			System.out.println(ClientConstants.REST_DISABLED);
		}
		else if(ProtocolConstants.ENERGY_FULL.equals(response)) {
			System.out.println(ClientConstants.ENERGY_FULL);
		}
	}

	public void doGETMONSTERS(String[] args, boolean isPlaying) {
		if(!isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(args.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			System.out.println("Just use 'getHealth'");
			return;
		}
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_MONSTERS);
		System.out.println(response);
	}

	public void doGETDEADMONSTERS(String[] args, boolean isPlaying) {
		if(!isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(args.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			System.out.println("Just use 'getHealth'");
			return;
		}
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_DEAD_MONSTERS);
		System.out.println(response);
	}

	private void doGETPLAYERS(String[] input, boolean isPlaying) {
		if(!isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(input.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			System.out.println("Just use 'getPlayers'");
			return;
		}
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_PLAYERS);
		System.out.println(response);
	}

	/**
	 * Destroy socket to disconnect from the server.
	 * Sets the value of socket to null after calling close()
	 */
	public void disconnectFromServer() {
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
	 * Shutdown handler for the world
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

}
