package com.cn.client;

import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.cn.client.senders.Sender;
import com.cn.constants.ClientConstants;
import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.protocol.Protocol;
import com.cn.service.CharacterService;

public class Client {	

	protected UserInput userInput = null;

	private Sender Sender = null;
	private String username = null;
	private String charName = null;
	private boolean isPlaying = false;
	protected String myURL = null;

	Logger logger = Logger.getLogger(ClientConstants.LOGGER);

	/**
	 * Default constructor.  Sets the URL, and gets the user input.
	 */
	public Client() {
		this.Sender = new Sender();
		try { myURL = InetAddress.getLocalHost().getHostAddress();}
		catch(Exception e) { e.printStackTrace(); System.exit(1); }
		logger.debug("My URL is : " + myURL);
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
				
				else if(input[0].equals(ClientConstants.ATTACK)) {
					doATTACK(input);
				}

				else if(input[0].equals(Constants.SAVE)) {
					doSAVE(input);
				}
				else if(input[0].equals(Constants.LOGOUT)) {
					doLOGOUT(input);
				}

				else if(input[0].equals(Constants.PLAY)) {
					doPLAY(input);
				}
				else {
					Sender.processInput(input, Sender.Auth.isLoggedIn, isPlaying, charName, username);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			Sender.Auth.disconnectFromAuthServer(); 
			Sender.World.disconnectFromServer();
			Sender.Chat.disconnectFromChatServer();
			System.out.println(ClientConstants.DISCONNECT_SUCCESS);
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

	private void doSAVE(String[] input) {
		if(!isPlaying) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(input.length == 1) {
			String response = Sender.World.sendToServerAndGetResponse(Protocol.createSaveRequest(charName));
//			String response = sendToServerAndGetResponse(Protocol.createSaveRequest(charName));
			String toForward = ProtocolConstants.SAVE + Protocol.createSimpleRequest(username) + response;
			response = Sender.Auth.sendToAuthServerAndGetResponse(toForward);
//			response = sendToAuthServerAndGetResponse(toForward);
			if(response.equals(ProtocolConstants.SUCCESS)) {
				System.out.println("Character saved.");
			}
		}
		else {
			System.out.println(ClientConstants.INVALID_INPUT);
		}
	}
	
	private void doPLAY(String[] input) {
		if(!Sender.Auth.isLoggedIn) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
		if(isPlaying) {
			System.out.println("You must not be playing to select a character.");
			return;
		}
		if(input.length != 2) {
			System.out.println(ClientConstants.INVALID_INPUT);
			return;
		}
		logger.debug("input is : " + input);
		logger.debug("the request is : " + Protocol.convertListToProtocol(input));
		String response = Sender.Auth.sendToAuthServerAndGetResponse(Protocol.convertListToProtocol(input));
		if(response.contains(ProtocolConstants.NOT_LOGGED_IN)) {
			Sender.Auth.isLoggedIn = false;
			System.out.println("You are not logged in.");
			return;
		}
		else if(response.contains(ProtocolConstants.FAILURE)) {
			System.out.println("Failed.");
			return;
		}
		else if(response.contains(ProtocolConstants.DOES_NOT_OWN_THAT_CHAR)) {
			System.out.println("You dont own that character...");
			return;
		}
//		Servers.World.connectToServer();
		logger.debug("auth server response: " + response);
		response = Sender.World.sendToServerAndGetResponse(ProtocolConstants.LOGIN_NAME + response);
		logger.debug("server response: " + response);
		if(response.equals(ProtocolConstants.SUCCESS)) {
			isPlaying = true;
			charName = input[1];
			//Servers.Chat.connectToChatServer();
			Sender.Chat.sendToChatServer(Protocol.chatLoginRequest(charName));
		}
		Sender.Chat.beginChatServerListener();
		isPlaying = true;
		//launchGame();
		System.out.println(ClientConstants.LOGIN_SUCCESS);
		//responseGUI.getSuccessDialog();
	}

	private void doLOGOUT(String[] input) {
		if(Sender.Auth.isLoggedIn && isPlaying) {
			doSAVE(input);  //only works cause input.length of logout == input.length of save
			Sender.World.sockPrintWriter.println(ProtocolConstants.LOGOUT);
			Sender.Chat.chatSockPrintWriter.println(ProtocolConstants.CHAT_LOGOUT + Protocol.createSimpleRequest(charName));
			destroyServerConnections();
			username = null;
			charName = null;
			isPlaying = false;
			Sender.Auth.isLoggedIn = false;
		}
		else if(Sender.Auth.isLoggedIn) {
			destroyServerConnections();
			username = null;
			charName = null;
			isPlaying = false;
			Sender.Auth.isLoggedIn = false;
			System.out.println("You've logged out.");
		}
		else if(!Sender.Auth.isLoggedIn) {
			System.out.println(ClientConstants.NOT_LOGGED_IN);
			return;
		}
	}

	
	public void doATTACK(String[] args) {
		if(args.length != 2) {
			System.out.println(ClientConstants.INVALID_INPUT);
		}
		logger.debug("charName: " + charName);
		String response = Sender.World.sendToServerAndGetResponse(Protocol.attackRequest(Integer.valueOf(args[1]), charName));
		String cmd = Protocol.getRequestCmdSimple(response);
		String[] responseArgs = Protocol.getRequestArgsSimple(response);
		if(cmd.equals(Constants.SUCCESS)) {
			if(responseArgs[2] == null) {
				System.out.println("You did " + responseArgs[1] + " damage to the monster.");
			}
			else {
				System.out.println("You did " + responseArgs[1] + " damage to the monster," +
						" and the monster attacked you back for " + responseArgs[2] + "damage.");
				if(responseArgs[3].equals("true")) {
					System.out.println("You died during the attack. You were resurrected with full health and energy, " +
							"but your experience was reset.");
					String[] saveArgs = new String[1];
					saveArgs[0] = "save";
					doSAVE(saveArgs);
				}
			}	
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
	
	private void destroyServerConnections() {
		Sender.World.disconnectFromServer();
		Sender.Chat.disconnectFromChatServer();
		Sender.Auth.disconnectFromAuthServer();
		Sender.World = null;
		Sender.Auth = null;
		Sender.Chat = null;
		Sender = new Sender();
	}

	/**
	 * Launches the "Select Character" UI for the given user.
	 * @param user
	 */
	public void launchCharacterSelectGUI(String user) {
		logger.debug("Inside launch char select gui method...");
		CharacterService cs = new CharacterService();
		ArrayList<String> characters = cs.getCharacters(user);
		while(characters.size() != 4) {
			characters.add("New Character");
		}
		logger.debug("Trying to launch gui...");
	}

}
