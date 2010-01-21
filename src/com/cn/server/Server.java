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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.constants.ServerConstants;
import com.cn.npc.monsters.Monster;
import com.cn.players.Player;
import com.cn.protocol.Protocol;

public class Server {
	private int port;
	private String hostName;
	private ServerSocket serverSocket;
	public static List<Monster> monsterList;
	public static List<Player> playerList;
	public static List<ServerThread> threadList;
	Logger logger = Logger.getLogger(Server.class);
	
	public Server() {
		logger.trace("Creating an instance of Server");
		port = ServerConstants.PORT;
		hostName = ServerConstants.HOSTNAME;
		monsterList = Collections.synchronizedList(new ArrayList<Monster>());
		playerList = Collections.synchronizedList(new ArrayList<Player>());
		threadList = Collections.synchronizedList(new ArrayList<ServerThread>());
		for(int i=0; i<10; i++) {
			monsterList.add(new Monster());
		}
		logger.debug(ServerConstants.SERVER_STARTUP);
		logger.debug(ServerConstants.SERVER_ACCEPTING);
	}
	
	public Server(int port) {
		this();
		logger.trace("Creating specialized Server with port # " + port);
		this.port = port;
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
		logger.trace("Opening socket.");
		try {
			setSocket(new ServerSocket(port));
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
		logger.trace("Inside Server.loop method");
		Socket clientSocket = null;

		/*
		 * Wait for clients at serverSockets, 
		 * instantiate a new thread for each new connection with the clientSocket information
		 */
		while(true) {
			try {
				clientSocket = null;
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error(ServerConstants.Server_STARTUP_FAILED);
					logger.error(e);
				}
				System.exit(1);
			} 
			
			/*
			 * Start a new thread (ServerThread).
			 */
			ServerThread st = new ServerThread(clientSocket);
			threadList.add(st);
			Thread t = new Thread(st);
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
		Server server = new Server();
		server.start();
	}
	
	public class ServerThread implements Runnable {
		
		//Client-Server Interaction
		protected Socket clientSocket;
		protected BufferedReader sockBufReader = null;
		protected PrintWriter sockPrintWriter = null;
		protected BufferedInputStream bis;
		protected BufferedOutputStream bos;
		protected String user = null;
		protected String name = null;
		
		//Server-ChatServer interaction
		Socket chatServerSocket = null;
		BufferedReader chatSockBufReader = null;
		PrintWriter chatSockPrintWriter = null;
		BufferedInputStream chatBis;
		BufferedOutputStream chatBos;

		/*
		 * The constructor for the ServerThread object.
		 */
		public ServerThread(Socket s) {
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
				System.exit(1);
			} 
		}
		
		
//		/**
//		 * Connects to the chat server.
//		 */
//		protected void connectToChatServer(String name, String url) {
//			try {
//				chatServerSocket = new Socket(ServerConstants.CHAT_HOSTNAME, ServerConstants.CHAT_PORT);
//			} catch (Exception e) {
//				chatServerSocket = null;
//			}
//			try {
//				chatBis = new BufferedInputStream(chatServerSocket.getInputStream());
//				chatBos = new BufferedOutputStream(chatServerSocket.getOutputStream());
//				chatSockPrintWriter = new PrintWriter(new OutputStreamWriter(chatBos), true);
//				chatSockBufReader = new BufferedReader(new InputStreamReader(chatBis));
//				url = (url.contains("/") ? url.replace("/", "") : url);
//				chatSockPrintWriter.println(Protocol.chatLoginRequest(name, url));
//			} catch (IOException e) {
//				logger.error(e);  //temp until we import log4j jar.
//				System.exit(1);
//			} 
//		}
		
		/**
		 * Disconnects from the chat server.
		 */
		protected void disconnectFromChatServer() {
			try {
				if (chatServerSocket != null) {
					chatSockPrintWriter.println(Protocol.chatLogoutRequest(name));
					chatServerSocket.close();
					chatServerSocket = null;
				}
			} catch (Exception e) {
				logger.error("Error disconnecting from the chat server.", e);
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

					String cmd = Protocol.getRequestCmdSimple(request);
					String[] args = Protocol.getRequestArgsSimple(request);
					if(logger.isDebugEnabled()) {
						logger.debug("This is the command: " + cmd);
						logger.debug("These are the args: " + args.toString());
					}

					if (cmd.equalsIgnoreCase(Constants.ATTACK_MONSTER)) {
						logger.debug("cmd is Attack Monster");
						onATTACK(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.HEAL)) {
						logger.debug("cmd is Heal");
						onHEAL(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.LOOT)) {
						logger.debug("cmd is loot");
						onLOOT(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.GET_MONSTER)) {
						logger.debug("cmd is Get Monsters");
						onGETMONSTERS(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.GET_DEAD_MONSTERS)) {
						logger.debug("cmd is Get Monsters");
						onGETDEADMONSTERS(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.GET_PLAYERS)) {
						logger.debug("cmd is Get Players");
						onGETPLAYERS(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.LOGIN_NAME)) {
						logger.debug("cmd is LOGINNAME");
						onLOGINNAME(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.LOGOUT)) {
						logger.debug("cmd is LOGOUT");
						onLOGOUT(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.SAVE)) {
						logger.debug("cmd is save");
						onSAVE(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.REST)) {
						logger.debug("cmd is rest");
						onREST(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.GET_HEALTH)) {
						logger.debug("cmd is get health");
						onGETHEALTH(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.GET_ENERGY)) {
						logger.debug("cmd is get health");
						onGETENERGY(args);
						continue;
					}
					if(logger.isEnabledFor(Level.ERROR)) {
						logger.error(ServerConstants.INVALID_MSG_RECVD);
					}
					sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
				}
			} catch (Exception e) {
				if(logger.isEnabledFor(Level.ERROR)) {
					logger.error("Disconnected: " +clientSocket.getInetAddress());
				}
			} finally {
				disconnectFromChatServer();
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
		



		private void onGETENERGY(String[] args) {
			// TODO Auto-generated method stub
			Player p = ServerPlayersHelper.getPlayerByName(name, playerList);
			if( p == null) {
				logger.error("This player is null");
				sockPrintWriter.println(ProtocolConstants.FAILURE);
			}
			else {
				int energy = p.getEnergy();
				logger.debug(ProtocolConstants.SUCCESS + Protocol.createSimpleRequest(energy));
				sockPrintWriter.println(ProtocolConstants.SUCCESS + Protocol.createSimpleRequest(energy));
			}
		}


		private void onGETHEALTH(String[] args) {
			// TODO Auto-generated method stub
			Player p = ServerPlayersHelper.getPlayerByName(name, playerList);
			if( p == null) {
				logger.error("This player is null");
				sockPrintWriter.println(ProtocolConstants.FAILURE);
			}
			else {
				int health = p.getHealth();
				logger.debug(ProtocolConstants.SUCCESS + Protocol.createSimpleRequest(health));
				sockPrintWriter.println(ProtocolConstants.SUCCESS + Protocol.createSimpleRequest(health));
			}
		}


		public void onATTACK(String[] args) {
			logger.trace("Inside Server.onAttack.");
			Monster m = ServerMonstersHelper.getMonsterById(Double.valueOf(args[1]), monsterList);
			Player p = ServerPlayersHelper.getPlayerByName(args[2], playerList);
			if(m == null || p == null) {
				logger.error("This monster does not exist.");
				sockPrintWriter.println(ProtocolConstants.MONSTER_DOES_NOT_EXIST);
			} else {
				if(logger.isDebugEnabled()) {
					logger.debug("SERVER: Player is attacking monster");
				}
				if(!p.loseEnergy(2)) {
					logger.debug("Player does not have enough energy to attack.");
					sockPrintWriter.println(ProtocolConstants.NOT_ENOUGH_ENERGY);
					return;
				}
				if(m.isAlive()) {
					int hitDmg = m.beAttacked(p);
					String dmgDone = Protocol.createSimpleResponse(String.valueOf(hitDmg));
					if(!m.isAlive()) {
						logger.debug("Player attacked and killed monster.");
						sockPrintWriter.println(ProtocolConstants.MONSTER_WAS_KILLED + dmgDone);
						Map<Player, Integer> xpMap = m.divvyUpXP();
						logger.debug(xpMap.size() + " players are receiving xp for the death of this monster.");
						for(Player player: xpMap.keySet()) {
							player.updateXP(xpMap.get(player));
							sendXPNotification(xpMap.get(player));
						}
						
					}
					else {
						logger.debug("Player successfully attacked but did not kill monster.");
						sockPrintWriter.println(Protocol.createSuccessResponse() + dmgDone);
					}
				} 
				else {
					logger.debug("Player tried to attack a dead monster.");
					sockPrintWriter.println(Protocol.createCharacterDiedResponse(Double.valueOf(args[1])));  //attacking a dead monster is invalid...
				}
			}
		}

		private void sendXPNotification(int amt) {
			logger.trace("Entering sendXPNotification");
			if(chatSockPrintWriter != null) {
				logger.debug("Sending XP Notification.");
				logger.debug("Attempting to send xp with request: " + Protocol.createXPNotification(amt, name));
				chatSockPrintWriter.println(Protocol.createXPNotification(amt, name));
			}
			logger.trace("Exiting sendXPNotification");
		}

		public void onHEAL(String[] args) {
			logger.trace("Inside Server.onHEAL.");
			Player p = ServerPlayersHelper.getPlayerByName(args[2], playerList);
			if(p == null) {
				invalidMsg();
			} else {
				p.heal(Integer.valueOf(args[1]));
				if(!p.isFullyHealed()) {
					sockPrintWriter.println(Protocol.createSuccessResponse());
				} else {
					invalidMsg();  //heal when having max health is invalid...
				}
			}
		}
		
		public void onLOOT(String[] args) {
			logger.trace("Inside Server.onLOOT.");
			Monster m = ServerMonstersHelper.getMonsterById(Double.valueOf(args[1]), monsterList);
			Player p = ServerPlayersHelper.getPlayerByName(name, playerList);
			if(m == null) {
				logger.error("This monster does not exist.");
				sockPrintWriter.println(ProtocolConstants.MONSTER_DOES_NOT_EXIST);
			} else {
				if(m.isAlive() || m.isLooted()) {			
					logger.debug("Monster is still alive or has already been looted. Player cannot loot.");
					sockPrintWriter.println(ProtocolConstants.CANNOT_LOOT_MONSTER);
				} else {
					p.loot(m);
					sockPrintWriter.println(ProtocolConstants.SUCCESS);
				}
			}
		}
		
		public void onGETMONSTERS(String[] args) {
			logger.trace("Inside Server.onGETMONSTERS.");
			String response = Protocol.convertMonsterListToProtocol(ServerMonstersHelper.getAliveMonsterIds(monsterList));
			sockPrintWriter.println(response);
		}
		
		public void onGETDEADMONSTERS(String[] args) {
			logger.trace("Inside Server.onGETDEADMONSTERS.");
			String response = Protocol.convertMonsterListToProtocol(ServerMonstersHelper.getDeadMonsterIds(monsterList));
			sockPrintWriter.println(response);
		}
		
		private void onGETPLAYERS(String[] args) {
			logger.trace("Inside Server.onGETPLAYUERS");
			String response = Protocol.convertPlayerListToProtocol(playerList);
			sockPrintWriter.println(response);
		}
		
		public void onLOGINNAME(String[] args) {
			logger.trace("Inside Server.onLOGINNAME.");
			name = args[1];
			String url = clientSocket.getRemoteSocketAddress().toString();
			logger.debug("The URL is : " + url);
//			connectToChatServer(name, url);
			playerList.add(new Player(args[1], Integer.valueOf(args[2]), Integer.valueOf(args[3]),
					       Integer.valueOf(args[4]), Integer.valueOf(args[5]), Integer.valueOf(args[6])));
			String response = ProtocolConstants.SUCCESS;
			sockPrintWriter.println(response);
		}
		
		public void onLOGOUT(String[] args) {
			logger.trace("Inside Server.onLOGOUT.");
			logger.debug("The playerList size is " + playerList.size());
			for(int i=0; i<playerList.size(); i++) {
				if(playerList.get(i).getName().equals(name)) {
					logger.debug("Removing the player " + name + " from the playerList");
					playerList.remove(i);
					break;
				}
			}
			disconnectFromChatServer();
			logger.trace("Inside Server.onLOGOUT.");
		}
		
		public void onSAVE(String[] args) {
			logger.trace("Inside Server.onSAVE.");
			Player p = ServerPlayersHelper.getPlayerByName(args[1], playerList);
			sockPrintWriter.println(Protocol.convertListToProtocol(p.getStats()));
		}
		
		public void onREST(String[] args) {
			logger.trace("Inside onREST");
			Player p = ServerPlayersHelper.getPlayerByName(name, playerList);
			boolean result = p.rest();
			if(result) {
				logger.debug("The players energy was recharged to full");
				sockPrintWriter.println(ProtocolConstants.ENERGY_FULL);
			}
			else {
				logger.debug("success resting");
				sockPrintWriter.println(ProtocolConstants.SUCCESS);
			}
			logger.trace("Exiting onREST");
		}
		
		public void invalidMsg() {
			System.out.println(ServerConstants.INVALID_MSG_RECVD);
			sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
		}
	}

}
