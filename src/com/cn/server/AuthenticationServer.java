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
import java.util.Map;
import java.util.logging.Logger;

import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.constants.ServerConstants;
import com.cn.protocol.Protocol;

public class AuthenticationServer {
	
	private int port;
	private ServerSocket serverSocket;
	
	public AuthenticationServer() {
		port = 8888;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(ServerConstants.AUTH_SERVER_STARTUP);
		System.out.println(ServerConstants.AUTH_SERVER_ACCEPTING);
	}
	
	/**
	 * Main method that is called when you launch the 
	 * run_auth_server.bat or run_auth_server.sh
	 * 
	 * @param args not needed for now.
	 */
	public static void main(String[] args) {
		AuthenticationServer server = new AuthenticationServer();
		server.loop();
	}

	private void loop() {
		Socket clientSocket = null;
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
			Thread t = new Thread(new AuthenticationServerThread(clientSocket));
			t.start();
			String success = ServerConstants.formatOutput("Accepted connection "+serverSocket.getInetAddress() +":"+serverSocket.getLocalPort());
			System.out.println(success);
		}
	}
	
	public class AuthenticationServerThread implements Runnable{
		
		// Client-Server Interaction
		Logger logger = Logger.getLogger(ServerConstants.LOGGER);
		protected Socket clientSocket;
		protected BufferedReader sockBufReader = null;
		protected PrintWriter sockPrintWriter = null;
		protected BufferedInputStream bis;
		protected BufferedOutputStream bos;
		protected String accountName = null;
		AuthenticationServerHelper ash = null;
		
		public AuthenticationServerThread(Socket socket) {
			clientSocket = socket;

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

		@Override
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

					if(cmd.equalsIgnoreCase(Constants.LOGIN)) {
						onLOGIN(args);
						continue;
					}
					else if(cmd.equalsIgnoreCase(Constants.SAVE)) {
						onSAVE(args);
						continue;
					}
					
					System.out.println(ServerConstants.INVALID_MSG_RECVD + request);
					sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
				}
			} catch (Exception e) {
				System.out.println("User has disconnected from Auth Server... " +clientSocket.getInetAddress());

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

		@SuppressWarnings("unchecked")
		private void onSAVE(String[] args) {
			if(ash.isAuthenticated() && ash != null) {
				Map map = ash.getCharacterMap();
				if(map.containsKey(args[2])) {
					map.remove(args[2]);
					String[] stats = new String[5];
					for(int i=2; i<args.length; i++) {
						//should include name...
						stats[i-2] = args[i];
					}
					System.out.println(stats);
					map.put(args[2], stats);
					ash.overWriteChar();
					sockPrintWriter.println(ProtocolConstants.SUCCESS);
				}
			}
			else {
				sockPrintWriter.println(ProtocolConstants.FAILURE);
			}
		}

		private void onLOGIN(String[] args) throws IOException {
			if(args.length != 3) {
				invalidMsg();
			}
			else {
				ash = new AuthenticationServerHelper(args[1], args[2]);
				if(ash.isAuthenticated()) {
					Map<String, String[]> characters = ash.getCharacterMap();
					sockPrintWriter.println(characters.keySet());
					String request = null;
					if ((request = sockBufReader.readLine()) != null) {
						String loginAs = Protocol.getRequestCmdSimple(request);
						if(characters.containsKey(loginAs)) {
							sockPrintWriter.println(Protocol.convertListToProtocol(characters.get(loginAs)));
						}
						else {
							invalidMsg();
						}
					}
				}
			}
		}

		public void invalidMsg() {
			System.out.println(ServerConstants.INVALID_MSG_RECVD);
			sockPrintWriter.println(Protocol.createSimpleResponse(ServerConstants.INVALID_MSG_RECVD_CODE));
		}

	}

}
