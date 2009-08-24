package com.cn.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

import com.cn.constants.ClientConstants;
import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.constants.ServerConstants;
import com.cn.protocol.Protocol;

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
		try { myURL = InetAddress.getLocalHost().getHostAddress();}
		catch(Exception e) { e.printStackTrace(); System.exit(1); }
	}

	/**
	 * Method to run the Client as the cmd line interface.
	 */
	public void runClient() {
		userInput = new UserInput();
		try {
			while(true) {
				String cmd = userInput.getUserInput();
				String[] input = cmd.split(" ");
				if(input[0].equals(ClientConstants.HELP)) {
					System.out.println(ClientConstants.HELP_FORMATTED);
				}
				else if(input[0].equals(ClientConstants.CONNECT)) {
					System.out.println(ClientConstants.CONNECT_ATTEMPT);
					if(input.length != 3) {
						System.out.println(ClientConstants.CONNECT_ATTEMPT_MALFORMED);
					}
					else {
						if (connectToServer(input[1], Integer.valueOf(input[2])) == 0) {
							System.out.println(ClientConstants.CONNECT_SUCCESS);
						}
					}
				}
				else if(input[0].equals(ClientConstants.DISCONNECT)) {
					disconnectFromServer();
					System.out.println(ClientConstants.DISCONNECT_SUCCESS);
				}
				else if(input[0].equals(ClientConstants.ATTACK)) {
					doATTACK(input);
				}
				else if(input[0].equals(ClientConstants.GET_MONSTER)) {
					doGETMONSTERS(input);
				}
				else if(input[0].equals(Constants.HEAL)) {
					doHEAL(input);
				}
				else if(input[0].equals(Constants.REST)) {
					doREST(input);
				}
				else if(input[0].equals(Constants.LOOT)) {
					doLOOT(input);
				}
				else if(input[0].equals(Constants.LOGIN)) {
					doLOGIN(input);
				}
			}
		} catch(Exception e) {
			disconnectFromServer();
			System.out.println(ClientConstants.DISCONNECT_SUCCESS);
		}
	}

	/**
	 * Method that connects to the server Given a serverName (ip/url)
	 * and serverPort.  Returns 0 on success.
	 */
	protected int connectToServer(String serverName, int serverPort) { 
		int retVal = 0;
		this.serverName = serverName;
		this.serverPort = serverPort;

		try {
			clientSocket = new Socket(serverName, serverPort);
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

	public void doATTACK(String[] args) {
		String response = sendToServerAndGetResponse(Protocol.attackRequest(Integer.valueOf(args[1]), Double.valueOf(args[2])));
		if(Protocol.getRequestCmdSimple(response).equals(Constants.DEATH)) {
			System.out.println(ClientConstants.LOOT_IS_POSSIBLE);
		}
	}

	public void doHEAL(String[] args) {
		String response = sendToServerAndGetResponse(Protocol.attackRequest(Integer.valueOf(args[1]), Double.valueOf(args[2])));
		if(Protocol.getRequestCmdSimple(response).equals(ProtocolConstants.SUCCESS)) {
			System.out.println(ClientConstants.HEAL_COMPLETE);
		}
	}

	public void doLOOT(String[] args) {
		System.out.println(ClientConstants.NOT_IMPLEMENTED);
	}

	public void doREST(String[] args) {
		System.out.println(ClientConstants.NOT_IMPLEMENTED);
	}

	public void doGETMONSTERS(String[] args) {
		sendToServerAndGetResponse(Protocol.getMonstersRequest());
	}

	private void doLOGIN(String[] input) {
		Socket authServerSocket = null;
		BufferedReader authSockBufReader = null;
		PrintWriter authSockPrintWriter = null;
		BufferedInputStream authBis;
		BufferedOutputStream authBos;
		try {
			authServerSocket = new Socket(ServerConstants.HOSTNAME, 8888);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		try {
			authBis = new BufferedInputStream(authServerSocket.getInputStream());
			authBos = new BufferedOutputStream(authServerSocket.getOutputStream());
			authSockPrintWriter = new PrintWriter(new OutputStreamWriter(authBos), true);
			authSockBufReader = new BufferedReader(new InputStreamReader(authBis));
		} catch (IOException e) {
			System.out.println(e);  //temp until we import log4j jar.
			System.exit(1);
		} 
		authSockPrintWriter.println(Protocol.createResponseSimple(input));
		try {
			String response = authSockBufReader.readLine();
			System.out.println(response);
			String character = userInput.getUserInput();
			authSockPrintWriter.println(Protocol.createSimpleRequest(character));
			
			String stats = authSockBufReader.readLine();
			connectToServer(ServerConstants.HOSTNAME, ServerConstants.PORT);
			sendToServerAndGetResponse(Protocol.createLoginWithCharName(stats));
			
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String sendToServerAndGetResponse(String message) {
		try {
			sockPrintWriter.println(message);
			String response = sockBufReader.readLine();
			System.out.println(response);
			return response;
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		}	
	}

}
