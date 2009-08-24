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
import java.util.logging.Logger;

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
	
	public Server() {
		port = ServerConstants.PORT;
		hostName = ServerConstants.HOSTNAME;
		monsterList = Collections.synchronizedList(new ArrayList<Monster>());
		playerList = Collections.synchronizedList(new ArrayList<Player>());
		for(int i=0; i<10; i++) {
			monsterList.add(new Monster());
		}
		System.out.println(ServerConstants.SERVER_STARTUP);
		System.out.println(ServerConstants.SERVER_ACCEPTING);
	}
	
	public Server(int port) {
		this();
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
	public void start() {
		if(openSocket()) {
			loop();
		}
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
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.out.println(ServerConstants.Server_STARTUP_FAILED);
				System.exit(1);
			} 
			
			/*
			 * Start a new thread (ServerThread).
			 */
			Thread t = new Thread(new ServerThread(clientSocket));
			t.start();
			String success = ServerConstants.formatOutput("Accepted connection "+serverSocket.getInetAddress() +":"+serverSocket.getLocalPort());
			System.out.println(success);
		}
	}
	
	/**
	 * Main method that is called when you launch the 
	 * run_server.bat or run_server.sh
	 * 
	 * @param args not needed for now.
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	
	public class ServerThread implements Runnable {
		
		// Client-Server Interaction
		Logger logger = Logger.getLogger(ServerConstants.LOGGER);
		protected Socket clientSocket;
		protected BufferedReader sockBufReader = null;
		protected PrintWriter sockPrintWriter = null;
		protected BufferedInputStream bis;
		protected BufferedOutputStream bos;
		protected String user = null;
		
		/*
		 * The constructor for the ServerThread object.
		 */
		public ServerThread(Socket s) {
			clientSocket = s;

			try {
				bis = new BufferedInputStream(clientSocket.getInputStream());
				bos = new BufferedOutputStream(clientSocket.getOutputStream());
				sockPrintWriter = new PrintWriter(new OutputStreamWriter(bos), true);
				sockBufReader = new BufferedReader(new InputStreamReader(bis));
			} catch (IOException e) {
				System.out.println(e);  //temp until we import log4j jar.
				System.exit(1);
			} 
		}
		
		/*
		 * Thread main routine
		 */
		public void run() {

			try {
				String request;
				while ((request = sockBufReader.readLine()) != null) {
					System.out.println(request);

					if (request.length() < 1) {
						System.out.println("Message length is too short: "+request.length());
						continue;
					}

					String cmd = Protocol.getRequestCmdSimple(request);
					String[] args = Protocol.getRequestArgsSimple(request);

					if (cmd.equalsIgnoreCase(Constants.ATTACK_MONSTER)) {
						onATTACK(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.HEAL)) {
						onHEAL(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.LOOT)) {
						onLOOT(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.GET_MONSTER)) {
						onGETMONSTERS(args);
						continue;
					}
					else if (cmd.equalsIgnoreCase(Constants.LOGIN_NAME)) {
						onLOGINNAME(args);
						continue;
					}
					
					System.out.println(ServerConstants.INVALID_MSG_RECVD);
					sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
				}
			} catch (Exception e) {
				System.out.println("Disconnected: " +clientSocket.getInetAddress());

			} finally {
				try {
					if (sockBufReader != null)
						sockBufReader.close();
					if (sockPrintWriter != null)
						sockPrintWriter.close();
					clientSocket.close();	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		public void onATTACK(String[] args) {
			Monster m = ServerMonstersHelper.getMonsterById(Double.valueOf(args[1]), monsterList);
			if(m == null) {
				invalidMsg();
			} else {
				m.beAttacked(Integer.valueOf(args[2]));
				if(m.isAlive()) {
					sockPrintWriter.println(Protocol.createSuccessResponse());
				} else {
					sockPrintWriter.println(Protocol.createCharacterDiedResponse(Double.valueOf(args[1])));  //attacking a dead monster is invalid...
				}
			}
		}
		
		public void onHEAL(String[] args) {
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
			Monster m = ServerMonstersHelper.getMonsterById(Double.valueOf(args[1]), monsterList);
			if(m == null) {
				invalidMsg();
			} else {
				if(!m.isAlive()) {
					invalidMsg();
				} else {
					invalidMsg();  //loot while alive is invalid
				}
			}
		}
		
		public void onGETMONSTERS(String[] args) {
			String response = Protocol.createResponseSimple(ServerMonstersHelper.getMonsterIds(monsterList));
			sockPrintWriter.println(response);
		}
		
		public void onLOGINNAME(String[] args) {
			playerList.add(new Player(args[1], Integer.valueOf(args[2]), Integer.valueOf(args[3]),
					       Integer.valueOf(args[4]), Integer.valueOf(args[5]), Integer.valueOf(args[6])));
			String response = ProtocolConstants.SUCCESS;
			sockPrintWriter.println(response);
		}
		
		public void invalidMsg() {
			System.out.println(ServerConstants.INVALID_MSG_RECVD);
			sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
		}
	}

}
