package com.cn.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;


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
				if (request.length() == 0)
					continue;

				System.out.println(request);

				if (request.length() < 1) {
					System.out.println("Message length is too short: "+request.length());
					continue;
				}
//
//				String cmd = Protocol.getRequestCmd(request);
//				String[] args = Protocol.getRequestArgs(request);
//
//				if (cmd.equalsIgnoreCase("LOGIN")) {
//					onLOGIN(args);
//					continue;
//				}
//
//				if (user == null) {
//					sockPrintWriter.println(Protocol.createSimpleResponse(Protocol.RNOTLOGGEDIN));
//					continue;
//				}
//
//				if (cmd.equalsIgnoreCase("LOGOUT")) {
//					onLOGOUT(args);
//					continue;
//				}
//
//				if (cmd.equalsIgnoreCase("POST")) {
//					onPOST(args);
//					continue;
//				}
//
//				if (cmd.equalsIgnoreCase("LIST")) {
//					onLIST(args);
//					continue;
//				}
//
//				if (cmd.equalsIgnoreCase("SCORE")) {
//					onSCORE(args);
//					continue;
//				}
//
//				if (cmd.equalsIgnoreCase("CLIP")) {
//					onCLIP(args);
//					continue;
//				}
//				
//				if (cmd.equalsIgnoreCase("DOWNLOAD")) {
//					onDOWNLOAD(args);
//					continue;
//				}
//
//				/*
//				 * Send failure code
//				 */
//				System.out.println("Invalid message!");
//				sockPrintWriter.println(Protocol.createSimpleResponse(Protocol.RINVALIDMSG));
			}
		} catch (Exception e) {
			/* Handling unexpected exceptions */
			//e.printStackTrace();
			System.out.println("Socket for user "+user+" is disconnected...");

		} finally {
			/* Release all the resources */
			try {
				if (sockBufReader != null)
					sockBufReader.close();
				if (sockPrintWriter != null)
					sockPrintWriter.close();

				// close the socket
				clientSocket.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}		
}
