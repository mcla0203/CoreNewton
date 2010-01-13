package com.cn.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.cn.constants.ClientConstants;
import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.constants.ServerConstants;
import com.cn.gui.LoginGUI;
import com.cn.gui.LoginResponseGUI;
import com.cn.protocol.Protocol;

public class Client {

	//World server interaction
	protected String serverName;
	protected int serverPort;
	protected Socket clientSocket = null;
	protected BufferedInputStream bis;
	protected BufferedOutputStream bos;
	protected BufferedReader sockBufReader;
	protected PrintWriter sockPrintWriter;
	
	//Auth server interaction
	Socket authServerSocket = null;
	BufferedReader authSockBufReader = null;
	PrintWriter authSockPrintWriter = null;
	BufferedInputStream authBis;
	BufferedOutputStream authBos;
	
	//Chat-Server Listening
	protected Socket chatListenerSocket;
	protected BufferedReader chatSockBufReader = null;
	protected PrintWriter chatSockPrintWriter = null;
	protected BufferedInputStream chatBis;
	protected BufferedOutputStream chatBos;
	protected ServerSocket chatServerSocket = null;		

	protected UserInput userInput = null;
	
	private String username = null;
	private String charName = null;
	private boolean isLoggedIn = false;

	protected String myURL = null;
	protected String cmd = "";
	
	Logger logger = Logger.getLogger(ClientConstants.LOGGER);

	/**
	 * Default constructor.  Sets the URL, and gets the user input.
	 */
	public Client() {
		try { myURL = InetAddress.getLocalHost().getHostAddress();}
		catch(Exception e) { e.printStackTrace(); System.exit(1); }
		logger.debug("My URL is : " + myURL);
	}

	/**
	 * Method to run the Client as the cmd line interface.
	 */
	public void runClient() {
		connectToAuthServer();
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
				else if(input[0].equals(ClientConstants.GET_DEAD_MONSTERS)) {
					doGETDEADMONSTERS(input);
				}
				else if(input[0].equals(ClientConstants.GET_MONSTER)) {
					doGETMONSTERS(input);
				}
				else if(input[0].equals(ClientConstants.GET_PLAYERS)) {
					doGETPLAYERS(input);
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
				else if(input[0].equals(Constants.SAVE)) {
					doSAVE(input);
				}
				else if(input[0].equals(Constants.CREATE_ACC)) {
					doCREATEACC(input);
				}
				else if(input[0].equals(Constants.LOGOUT)) {
					doLOGOUT(input);
				}
				else if (input[0].equals(Constants.DISCONNECT)) {
					doDISCONNECT(input);
				}
			}
		} catch(Exception e) {
			disconnectFromServer();
			disconnectFromAuthServer();
			disconnectFromChatServer();
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
	 * Connects to the authentication server.
	 */
	protected void connectToAuthServer() {
		try {
			authServerSocket = new Socket(ServerConstants.HOSTNAME, 8888);
			Thread t = new Thread(new AuthClientShutdown());
			Runtime.getRuntime().addShutdownHook(t);
		} catch (Exception e) {
			authServerSocket = null;
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
	}

	/**
	 * Destroy socket to disconnect from the server.
	 * Sets the value of socket to null after calling close()
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
	 * Destroy socket to disconnect from the auth server.
	 * Sets the value of socket to null after calling close()
	 */
	protected void disconnectFromAuthServer() {
		try {
			if (authServerSocket != null) {
				authServerSocket.close();
				authServerSocket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Destroy socket to disconnect from the auth server.
	 * Sets the value of socket to null after calling close()
	 */
	protected void disconnectFromChatServer() {
		try {
			if (chatServerSocket != null) {
				chatServerSocket.close();
				chatServerSocket = null;
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
	 * Shutdown handler for the client
	 */
	public class AuthClientShutdown extends Thread {
		public void run() {
			try {
				if (authServerSocket != null) {
					System.out.println("Closing client...");
					authServerSocket.close();
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
		PropertyConfigurator.configure(Constants.LOGGER_PROPERTIES);
		Client client = new Client();
		client.runClient();
	}

	public void doATTACK(String[] args) {
		if(args.length != 2) {
			System.out.println(ClientConstants.INVALID_INPUT);
		}
		logger.debug("charName: " + charName);
		String response = sendToServerAndGetResponse(Protocol.attackRequest(Integer.valueOf(args[1]), charName));
		String cmd = Protocol.getRequestCmdSimple(response);
		String[] responseArgs = Protocol.getRequestArgsSimple(response);
		if(cmd.equals(Constants.SUCCESS)) {
			System.out.println("You did " + responseArgs[1] + " damage to the monster.");
		}
		else if(cmd.equals(Constants.NOT_ENOUGH_ENERGY)) {
			System.out.println("You do not have enough energy to attack.");
		}
		else if(cmd.equals(Constants.MONSTER_WAS_KILLED)) {
			System.out.println("You did " + responseArgs[1] + " damage to the monster and the monster was killed!");
			System.out.println(ClientConstants.LOOT_IS_POSSIBLE);
		}
		else if(cmd.equals(Constants.MONSTER_DOES_NOT_EXIST)) {
			System.out.println("You nub, this monster does not exist!!");
		}
		else if(cmd.equals(Constants.DEATH)) {
			System.out.println("This monster is already dead.");
		}
		else {
			//should not get here ever
			System.out.println(ClientConstants.GENERAL_FAILURE);
		}
	}

	public void doHEAL(String[] args) {
//		String response = sendToServerAndGetResponse(Protocol.attackRequest(Integer.valueOf(args[1]), Double.valueOf(args[2]), username));
//		if(Protocol.getRequestCmdSimple(response).equals(ProtocolConstants.SUCCESS)) {
//			System.out.println(ClientConstants.HEAL_COMPLETE);
//		}
	}

	public void doLOOT(String[] args) {
		if(args.length != 2) {
			System.out.println(ClientConstants.INVALID_INPUT);
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

	public void doREST(String[] args) {
		if(args.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		if(!isLoggedIn) {
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

	public void doGETMONSTERS(String[] args) {
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_MONSTERS);
		System.out.println(response);
	}
	
	public void doGETDEADMONSTERS(String[] args) {
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_DEAD_MONSTERS);
		System.out.println(response);
	}
	
	private void doGETPLAYERS(String[] input) {
		String response = sendToServerAndGetResponse(ProtocolConstants.GET_PLAYERS);
		System.out.println(response);
	}

	@SuppressWarnings({ "all", "null" })
	public void doLOGIN(String[] input) {
		if(isLoggedIn) {
			System.out.println(ClientConstants.ALREADY_LOGGED_IN);
			return;
		}
		if(input.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			System.out.println("Just use 'login' now, you get a GUI");
			return;
		}
		LoginGUI g = new LoginGUI();
		int action = g.getLoginDialog();
		String usr = null;
		String pw = null;
		if(action == JOptionPane.OK_OPTION) {
			usr = g.getUserNameField().getText();
			char[] pwTemp = g.getPasswordField().getPassword();
			if(usr == null || "".equals(usr)) {
				return;
			}
			for(int i=0; i<pwTemp.length; i++) {
				pw += Character.toString(pwTemp[i]);
			}
			if(pw != null) {
				pw = pw.substring(4);
			}
			else if ( pw == null || "".equals(pw)) {
				System.out.println("You must specify a password!!");
				doLOGIN(input);
				return;
			}
			logger.debug("user clicked ok as : " + usr +"/"+ pw);
		}
		else{
			return;
		}
		String[] requestArgs = new String[3];
		requestArgs[0] = input[0];
		requestArgs[1] = usr;
		requestArgs[2] = pw; 
		String response = sendToAuthServerAndGetResponse(Protocol.convertListToProtocol(requestArgs));
		LoginResponseGUI responseGUI = new LoginResponseGUI();
		if(response.equals(ProtocolConstants.NO_CHARS_CREATED)) {
			responseGUI.getNoCharsDialog();
			System.out.println(ClientConstants.NO_CHARS_CREATED);
			return;
		}
		if(response.equals(ProtocolConstants.USER_NOT_FOUND)) {
			responseGUI.getUserNotFoundDialog();
			System.out.println(ClientConstants.USER_NOT_FOUND);
			return;
		}
		System.out.println(ClientConstants.LOGIN_CHARACTERS);
		System.out.println(response);
		username = usr;
		
		String character = userInput.getUserInput();
		if(!response.contains(character)) {
			System.out.println("You made a typo... try again SLOWLY...");
			character = userInput.getUserInput();
		}
		charName = character;
		String stats = sendToAuthServerAndGetResponse(Protocol.createSimpleRequest(character));
		
		connectToServer(ServerConstants.HOSTNAME, ServerConstants.PORT);
		response = sendToServerAndGetResponse(Protocol.createLoginWithCharName(stats));
		if(response.equals(ProtocolConstants.SUCCESS)) {
			beginChatServerListener();
			isLoggedIn = true;
			System.out.println(ClientConstants.LOGIN_SUCCESS);
			responseGUI.getSuccessDialog();
		}
	}
	
	private void doSAVE(String[] input) {
		if(!isLoggedIn) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(input.length == 1) {
			String response = sendToServerAndGetResponse(Protocol.createSaveRequest(charName));
			
			String toForward = ProtocolConstants.SAVE + Protocol.createSimpleRequest(username) + response;
			response = sendToAuthServerAndGetResponse(toForward);
		}
		else {
			System.out.println(ClientConstants.INVALID_INPUT);
		}
	}
	
	private void doCREATEACC(String[] input) {
		logger.trace("Entering the doCREATEACC method.");
		if(input.length != 4) {
			System.out.println(ClientConstants.INVALID_INPUT);
		}
		if(!input[2].equals(input[3])) {
			System.out.println(ClientConstants.PASSWORDS_DONT_MATCH);
		}
		String response = sendToAuthServerAndGetResponse(Protocol.convertListToProtocol(input));
		if(response.equals(ProtocolConstants.ACCOUNT_ALREADY_IN_USE)) {
			System.out.println(ClientConstants.ACCOUNT_ALREADY_EXISTS);
		}
		if(response.equals(ProtocolConstants.SUCCESS)) {
			System.out.println(ClientConstants.ACCOUNT_CREATED_SUCCESSFULLY);
		}
		logger.trace("Exiting the doCREATEACC method.");
	}
	
	private void doLOGOUT(String[] input) {
		doSAVE(input);  //only works cause input.length of logout == input.length of save
		sockPrintWriter.println(ProtocolConstants.LOGOUT);
		disconnectFromServer();
		disconnectFromChatServer();
		username = null;
		charName = null;
		isLoggedIn = false;
	}
	
	private void doDISCONNECT(String[] input) {
		sockPrintWriter.println(ProtocolConstants.LOGOUT);
		disconnectFromAuthServer();
		disconnectFromChatServer();
		disconnectFromServer();
		charName = null;
	}

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
	
	public String sendToAuthServerAndGetResponse(String message) {
		try {
			authSockPrintWriter.println(message);
			String response = authSockBufReader.readLine();
			return response;
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		}	
	}
	
	public void beginChatServerListener() {
		Thread t = new Thread(new ChatServerListenerThread());
		logger.trace("Starting the ChatServerListener");
		t.start();
	}
	
	public class ChatServerListenerThread implements Runnable {

		/*
		 * The constructor for the ChatServerThread object.
		 */
		public ChatServerListenerThread() {
			logger.trace("ServerThread initialization");
			openSocket();
			try {
				chatListenerSocket = null;
				chatListenerSocket = chatServerSocket.accept();
				logger.debug("Has accepted connection from"+clientSocket.getRemoteSocketAddress());
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error(ServerConstants.Server_STARTUP_FAILED);
					logger.error(e);
				}
				System.exit(1);
			}

			try {
				chatBis = new BufferedInputStream(chatListenerSocket.getInputStream());
				chatBos = new BufferedOutputStream(chatListenerSocket.getOutputStream());
				chatSockPrintWriter = new PrintWriter(new OutputStreamWriter(chatBos), true);
				chatSockBufReader = new BufferedReader(new InputStreamReader(chatBis));
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("Exception initializing ChatServerListener", e);
				}
				System.exit(1);
			} 
		}
		
		/**
		 * Opens the socket that the server uses
		 * @throws IOException
		 */
		public Boolean openSocket() {
			logger.trace("Opening ChatListener Socket.");
			try {
				chatServerSocket = new ServerSocket(Constants.CHAT_LISTENER_PORT);
				return true;
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("Error opening socket ", e);
				}
				return false;
			}
		}
		
		/*
		 * Thread main routine
		 */
		public void run() {
			try {
				String request;
				while ((request = chatSockBufReader.readLine()) != null) {
					if (request.length() < 1) {
						if(logger.isEnabledFor(Level.ERROR)) {
							logger.error("Message length is too short: "+request.length());
						}
						continue;
					}
					String cmd = Protocol.getRequestCmdSimple(request);
					String[] args = Protocol.getRequestArgsSimple(request);
					if(cmd.equals(Constants.ANNOUNCEMENT)) {
						System.out.println(args[1]);
						continue;
					}
					else if(cmd.equals(Constants.XP)) {
						System.out.println("You received " + args[1] + " XP!!");
					}
				}
			} catch (Exception e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("User has disconnected from Auth Server... " +chatListenerSocket.getInetAddress());
				}

			} finally {
				try {
					if (chatSockBufReader != null)
						chatSockBufReader.close();
					if (chatSockPrintWriter != null)
						chatSockPrintWriter.close();
					chatListenerSocket.close();	
				} catch (Exception e) {
					if(logger.isEnabledFor(Level.ERROR)) {
						logger.error("Exception thrown closing sockBufReader and sockPrintWriter", e);
					}
				}
			}
		}
	}

}
