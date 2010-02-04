package com.cn.client.senders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.cn.client.UserInput;
import com.cn.constants.ClientConstants;
import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.constants.ServerConstants;
import com.cn.gui.LoginGUI;
import com.cn.gui.LoginResponseGUI;
import com.cn.gui.SelectCharacter;
import com.cn.protocol.Protocol;

public class Auth {

	//Auth server interaction
	Socket authServerSocket = null;
	BufferedReader authSockBufReader = null;
	PrintWriter authSockPrintWriter = null;
	BufferedInputStream authBis;
	BufferedOutputStream authBos;
	
	public boolean isLoggedIn = false;
	
	
	String serverName;
	int port;
	Logger logger = Logger.getLogger(this.getClass());
	
	public Auth(String serverName, int port) {
		this.serverName = serverName;
		this.port = port;
		connectToAuthServer();
	}
	
	/**
	 * Connects to the authentication server.
	 */
	protected void connectToAuthServer() {
		try {
			authServerSocket = new Socket(ServerConstants.HOSTNAME, ServerConstants.AUTH_PORT);
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
	
	public void processInput(String[] input, String username, boolean isPlaying) {
		if(input[0].equals(Constants.LOGIN)) {
			doLOGIN(input, username, isPlaying);
		}
		else if(input[0].equals(Constants.CREATE_ACC)) {
			doCREATEACC(input);
		}
		else if(input[0].equals(Constants.CREATE_CHAR)) {
			doCREATECHAR(input);
		}
		else if(input[0].equals(Constants.GET_STATS)) {
			doGETSTATS(input);
		}
		else if(input[0].equals(Constants.DELETE_CHAR)) {
			doDELETECHAR(input, username, isPlaying);
		}
	}

	@SuppressWarnings({ "all", "null" })
	public void doLOGIN(String[] input, String username, boolean isPlaying) {
		if(isPlaying) {
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
				doLOGIN(input, username, isPlaying);
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
			username = usr;
			isLoggedIn = true;
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
		isLoggedIn = true;
	}
	
	private void doGETSTATS(String[] input) {
		if(input.length != 1) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		if(!isLoggedIn) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		String response = sendToAuthServerAndGetResponse(ProtocolConstants.GET_STATS);
		System.out.println(response);
		new SelectCharacter(response);
	}

	private void doCREATEACC(String[] input) {
		logger.trace("Entering the doCREATEACC method.");
		if(input.length != 4) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		if(!input[2].equals(input[3])) {
			System.out.println(ClientConstants.PASSWORDS_DONT_MATCH);
			return;
		}
		String response = sendToAuthServerAndGetResponse(Protocol.convertListToProtocol(input));
		if(response.equals(ProtocolConstants.ACCOUNT_ALREADY_IN_USE)) {
			System.out.println(ClientConstants.ACCOUNT_ALREADY_EXISTS);
			return;
		}
		else if(response.equals(ProtocolConstants.SUCCESS)) {
			System.out.println(ClientConstants.ACCOUNT_CREATED_SUCCESSFULLY);
			return;
		}
		else if(response.equals(ProtocolConstants.WEAK_PASSWORD)) {
			System.out.println("Your password is so weak my kitty could guess it... pick a new one.");
			return;
		}
		else if(response.equals(ProtocolConstants.FAILURE)) {
			System.out.println("Something went wrong.");
			return;
		}

		logger.trace("Exiting the doCREATEACC method.");
	}

	private void doCREATECHAR(String[] input) {
		if(!isLoggedIn) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(input.length != 2) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		logger.debug("input is : " + input);
		logger.debug("the request is : " + Protocol.convertListToProtocol(input));
		String response = sendToAuthServerAndGetResponse(Protocol.convertListToProtocol(input));
		String cmd = Protocol.getRequestCmdSimple(response);
		if(cmd.equals(ClientConstants.SUCCESS)) {
			System.out.println("Your character was created!");
		}
		else if(cmd.equals(ClientConstants.CHAR_ALREADY_EXISTS)) {
			System.out.println("Somebody else has that name already... please pick a different one.");
		}
		else if(cmd.equals(ClientConstants.TOO_MANY_CHARS)) {
			System.out.println("You have too many chars already... just use the ones you have...");
		}
	}
	
	private void doDELETECHAR(String[] input, String username, boolean isPlaying) {
		if(!isLoggedIn) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(isPlaying) {
			System.out.println(ClientConstants.ALREADY_PLAYING);
			return;
		}
		if(input.length != 2) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		logger.debug("input is : " + input);
		logger.debug("the request is : " + Protocol.convertListToProtocol(input));
		System.out.println(ClientConstants.DELETE_CHAR_CONFIRMATION);
		UserInput userInput = new UserInput();
		String userConf = userInput.getUserInput();
		if(userConf.equals("yes")) {
			String response = sendToAuthServerAndGetResponse(Protocol.convertListToProtocol(input) + Protocol.createSimpleRequest(username));
			String cmd = Protocol.getRequestCmdSimple(response);
			if(cmd.equals(ClientConstants.SUCCESS)) {
				System.out.println("Your character was successfully deleted.");
			}
			else if(cmd.equals(ClientConstants.DOES_NOT_OWN_THAT_CHAR)) {
				System.out.println("You do not own that character.");
			}
			else { 
				System.out.println(ClientConstants.GENERAL_FAILURE);
			}
		}
		else if(userConf.equals("no")) {
			System.out.println("Your character is not being deleted.");
			return;
		}
		else {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
	}
	
	/**
	 * Destroy socket to disconnect from the auth server.
	 * Sets the value of socket to null after calling close()
	 */
	public void disconnectFromAuthServer() {
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
	 * Sends a message to the Auth server and returns the 
	 * response.
	 * @param message
	 * @return
	 */
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
	
	/**
	 * Shutdown handler for the auth
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
	
}
