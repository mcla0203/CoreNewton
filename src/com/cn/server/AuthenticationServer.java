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
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.constants.ServerConstants;
import com.cn.protocol.Protocol;
import com.cn.service.AccountService;
import com.cn.service.CharacterService;
import com.cn.service.StatService;

public class AuthenticationServer {

	private int port;
	private ServerSocket serverSocket;
	Logger logger = Logger.getLogger(AuthenticationServer.class);

	public AuthenticationServer() {
		port = 8888;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			if(logger.isEnabledFor(Level.ERROR)) {
				logger.error("AuthenticationServer threw an exception: ", e);
			}
		}
		logger.trace("We started an Auth Server!");
		logger.debug(ServerConstants.AUTH_SERVER_STARTUP);
		logger.debug(ServerConstants.AUTH_SERVER_ACCEPTING);

	}

	/**
	 * Main method that is called when you launch the 
	 * run_auth_server.bat or run_auth_server.sh
	 * 
	 * @param args not needed for now.
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(Constants.LOGGER_PROPERTIES);
		AuthenticationServer server = new AuthenticationServer();
		server.loop();
	}

	private void loop() {
		logger.trace("Entering the auth server loop");
		Socket clientSocket = null;
		while(true) {
			try {
				clientSocket = null;
				clientSocket = serverSocket.accept();
				logger.trace("Client-(Auth)Server connection has been made successfully");
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("Error establishing client-server connection: ", e);
				}
				logger.debug(ServerConstants.Server_STARTUP_FAILED);
				System.exit(1);
			} 

			/*
			 * Start a new thread (ServerThread).
			 */
			Thread t = new Thread(new AuthenticationServerThread(clientSocket));
			if(logger.isTraceEnabled()) {
				logger.trace("Starting a new AuthenticationServerThread: " + t);
			}
			t.start();
		}
	}

	public class AuthenticationServerThread implements Runnable{

		// Client-Server Interaction
		protected Socket clientSocket;
		protected BufferedReader sockBufReader = null;
		protected PrintWriter sockPrintWriter = null;
		protected BufferedInputStream bis;
		protected BufferedOutputStream bos;
		protected String accountName = null;
		String username = null;
		boolean isAuthenticated = false;

		public AuthenticationServerThread(Socket socket) {
			clientSocket = socket;

			try {
				bis = new BufferedInputStream(clientSocket.getInputStream());
				bos = new BufferedOutputStream(clientSocket.getOutputStream());
				sockPrintWriter = new PrintWriter(new OutputStreamWriter(bos), true);
				sockBufReader = new BufferedReader(new InputStreamReader(bis));
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("Error initializing AuthenticationServerThread ", e);
				}
				System.exit(1);
			} 
		}

		@Override
		public void run() {
			logger.trace("Beginning run method - AuthenticationServerThread.");
			try {
				String request;
				while ((request = sockBufReader.readLine()) != null) {
					logger.debug(request);

					if (request.length() < 1) {
						logger.debug("Message length is too short: "+request.length());
						continue;
					}

					String cmd = Protocol.getRequestCmdSimple(request);
					String[] args = Protocol.getRequestArgsSimple(request);

					logger.debug("the cmd is: "+ cmd);

					if(cmd.equalsIgnoreCase(Constants.LOGIN)) {
						logger.debug("The cmd was 'login'");
						onLOGIN(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.SAVE)) {
						logger.debug("The cmd was 'save'");
						onSAVE(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.CREATE_ACC)) {
						logger.debug("The cmd was 'create account'");
						onCREATEACC(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.CREATE_CHAR)) {
						logger.debug("The cmd was 'create char'");
						onCREATECHAR(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.PLAY)) {
						logger.debug("The cmd was 'select char'");
						onSELECTCHAR(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.GET_STATS)) {
						logger.debug("The cmd was 'get stats'");
						onGETSTATS(args);
						continue;
						
					}
					if(logger.isDebugEnabled()) {
						logger.debug("An invalid message was received: " + request);
						logger.debug(ServerConstants.INVALID_MSG_RECVD + request);
					}
					sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
				}
			} catch (Exception e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("User has disconnected from Auth Server... " +clientSocket.getInetAddress(), e);
				}

			} finally {
				try {
					if (sockBufReader != null)
						sockBufReader.close();
					if (sockPrintWriter != null)
						sockPrintWriter.close();
					clientSocket.close();	
				} catch (Exception e) {
					if(logger.isEnabledFor(Level.ERROR)) {
						logger.error("Exception thrown closing sockBufReader and sockPrintWriter", e);
					}
				}
			}
		}

		/**
		 * Example of the save protocol that is incomming:
		 * <save><mcla0203><Duckie><1><100><20><20><10>
		 * 
		 * This function will save the character to the databse.
		 * 
		 * @param args
		 */
		private void onSAVE(String[] args) {
			logger.trace("onSAVE method");
			if(args.length != 8) {
				for(String s : args) {System.out.println("The args " + s);}
				System.out.println("The arg length is supposed to be 8");
				invalidMsg();
				return;
			}
			String name = args[2];
			int lvl = Integer.valueOf(args[3]);
			int health = Integer.valueOf(args[4]);
			int energy = Integer.valueOf(args[5]);
			int xp = Integer.valueOf(args[6]);
			int credits = Integer.valueOf(args[7]);

			StatService ss = new StatService();
			int status = ss.setStats(name, lvl, credits, xp, health, energy);

			if(status == 1) {
				sockPrintWriter.println(ProtocolConstants.SUCCESS);
			}
			else if(status == 2 || status == 999) {
				sockPrintWriter.println(ProtocolConstants.FAILURE);
			}
		}

		private void onLOGIN(String[] args) throws IOException {
			logger.trace("Inside AuthServerThread.onLOGIN");
			if(args.length != 3) {
				invalidMsg();
				return;
			}
			String local_usr = args[1];
			String local_pw = args[2];
			AccountService as = new AccountService();
			int status = as.login(local_usr, local_pw);
			if(status == 2) {
				sockPrintWriter.println(ProtocolConstants.USER_NOT_FOUND);
				System.out.println("The login user was not found.");
				return;
			}
			else if(status == 3) {
				sockPrintWriter.println(ProtocolConstants.PASSWORD_INCORRECT);
				System.out.println("Password incorrect.");
				return;
			}
			else if(status == 999) {
				sockPrintWriter.println(ProtocolConstants.FAILURE);
				System.out.println("General failure");
				return;
			}
			//if success then return characters or let them create a new one
			else if(status == 1) {
				username = local_usr;
				isAuthenticated = true;
				CharacterService cs = new CharacterService();
				ArrayList<String> chars = cs.getCharacters(username);
				if(chars.size() == 0) {
					logger.debug("No characters have been created on the account.");
					sockPrintWriter.println(ProtocolConstants.NO_CHARS_CREATED);
					return;
				}
				else {
					sockPrintWriter.println(chars);
//				}
//				String request = null;
//				if ((request = sockBufReader.readLine()) != null) {
//					String loginAs = Protocol.getRequestCmdSimple(request);
//					//create a new character, then process it
//					if(loginAs.equals(Constants.CREATE_CHAR)) {
//						onCREATECHAR(Protocol.getRequestArgsSimple(request));
//					}
//					//choose existing character, then process it
//					else if(loginAs.equals(Constants.CHAR_SELECTED)) {
//						onSELECTCHAR(Protocol.getRequestArgsSimple(request));
//					}
//					
////					else if(characters.containsKey(loginAs)) {
////						sockPrintWriter.println(Protocol.convertListToProtocol(characters.get(loginAs)));
////					}
//					else {
//						invalidMsg();
//					}
				}
			}		
		}

		private void onSELECTCHAR(String[] args) {
			if(args.length != 2) {
				invalidMsg();
				return;
			}
			if(!isAuthenticated) {
				sockPrintWriter.println(ProtocolConstants.NOT_LOGGED_IN);
				return;
			}
			String charName = args[1];
			CharacterService cs = new CharacterService();
			if(cs.hasCharacter(username, charName)) {
				StatService ss = new StatService();
				ArrayList<String> stats = ss.getStats(charName);
				if(stats == null) {
					sockPrintWriter.println(ProtocolConstants.FAILURE);
					return;
				}
				sockPrintWriter.println(Protocol.convertListToProtocol(stats));
			}
			else {
				sockPrintWriter.println(ProtocolConstants.DOES_NOT_OWN_THAT_CHAR);
			}
			
		}

		private void onCREATEACC(String[] args) {
			AccountService as = new AccountService();
			int ret = as.createAccount(args[1], args[2]);
			if(ret == 1) {
				sockPrintWriter.println(ProtocolConstants.SUCCESS);
			}
			else if(ret == 3) {
				sockPrintWriter.println(ProtocolConstants.ACCOUNT_ALREADY_IN_USE);
			}
			else if(ret == 4) {
				sockPrintWriter.println(ProtocolConstants.WEAK_PASSWORD);
			}
			else if(ret == 999 || ret == 2) {
				sockPrintWriter.println(ProtocolConstants.FAILURE);
			}
		}
		
		private void onGETSTATS(String[] args) {
			if(args.length != 1) {
				invalidMsg();
				return;
			}

			CharacterService cs = new CharacterService();
			ArrayList<String> chars = cs.getCharacters(username);
			
			StatService ss = new StatService();
			ArrayList<ArrayList<String>> statsList = new ArrayList<ArrayList<String>>();
			for(String name : chars) {
				statsList.add(ss.getStats(name));
			}
			
			String response = ProtocolConstants.SUCCESS;
			for( ArrayList<String> stats : statsList ) {
				response += Protocol.createSimpleResponse(Constants.CHARACTER);
				response += Protocol.createSimpleResponse(stats);
			}
			
			System.out.println("Response is: " + response);
			sockPrintWriter.println(response);
		}

		private void onCREATECHAR(String[] args) {
			CharacterService cs = new CharacterService();
			int ret = cs.createCharacter(username, args[1]);
			if(ret == 1) {
				sockPrintWriter.println(ProtocolConstants.SUCCESS);
			}
			else if(ret == 2) {
				sockPrintWriter.println(ProtocolConstants.ACCOUNT_DOES_NOT_EXIST);
			}
			else if(ret == 3) {
				sockPrintWriter.println(ProtocolConstants.TOO_MANY_CHARS);
			}
			else if(ret == 4) {
				sockPrintWriter.println(ProtocolConstants.CHAR_ALREADY_EXISTS);
			}
			else if(ret == 999 || ret == 5) {
				sockPrintWriter.println(ProtocolConstants.FAILURE);
			}
		}

		public void invalidMsg() {
			System.out.println(ServerConstants.INVALID_MSG_RECVD);
			sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
		}

	}

}
