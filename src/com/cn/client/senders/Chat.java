package com.cn.client.senders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.cn.constants.Constants;
import com.cn.protocol.Protocol;

public class Chat {

	//Chat-Server Listening
	Socket chatListenerSocket;
	public PrintWriter chatSockPrintWriter = null;
	protected BufferedReader chatBufferedReader = null;
	protected BufferedInputStream chatBis;
	protected BufferedOutputStream chatBos;
	protected Socket chatServerSocket = null;
	
	String serverName;
	int port;
	Logger logger = Logger.getLogger(this.getClass());
	
	
	public Chat(String serverName, int port) {
		this.serverName = serverName;
		this.port = port;
		connectToChatServer();
	}
	
	/**
	 * Connects to the chat server.
	 */
	protected void connectToChatServer() {
		try {
			chatServerSocket = new Socket(serverName, port);
			Thread t = new Thread(new ChatClientShutdown());
			Runtime.getRuntime().addShutdownHook(t);
		} catch (Exception e) {
			chatServerSocket = null;
		}
		try {
			chatBis = new BufferedInputStream(chatServerSocket.getInputStream());
			chatBos = new BufferedOutputStream(chatServerSocket.getOutputStream());
			chatSockPrintWriter = new PrintWriter(new OutputStreamWriter(chatBos), true);
			chatBufferedReader = new BufferedReader(new InputStreamReader(chatBis));
		} catch (IOException e) {
			System.out.println(e);  //temp until we import log4j jar.
			System.exit(1);
		} 
	}
	
	/**
	 * Destroy socket to disconnect from the auth server.
	 * Sets the value of socket to null after calling close()
	 */
	public void disconnectFromChatServer() {
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
	 * Send a message to the chat server.  There is no need
	 * to listen for a response because the client listener
	 * thread will be listening for it.
	 * @param message
	 */
	public void sendToChatServer(String message) {
		try {
			chatSockPrintWriter.println(message);
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return;
	}
	
	public void processInput(String[] input, String charName) {
		if(input[0].equals(Constants.SEND_MESSAGE)) {
			doSENDMSG(input);
		}

		else if(input[0].equals(Constants.CHAT_CHANNEL)) {
			doSENDMSGC(input, charName);
		}
	}
	
	private void doSENDMSG(String[] input) {
		if(input.length < 3) {
			return;
		}
		String request = Protocol.sendPlayerMessageRequest(input);
		sendToChatServer(request);
	}
	
	private void doSENDMSGC(String[] input, String charName) {
		if(input.length < 3) {
			return;
		}
		String request = Protocol.sendAllMessageRequest(input, charName);
		System.out.println("Sending message "+ request);
		sendToChatServer(request);
	}
	
	/**
	 * Shutdown handler for the auth
	 */
	public class ChatClientShutdown extends Thread {
		public void run() {
			try {
				if (chatServerSocket != null) {
					System.out.println("Closing client...");
					chatServerSocket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void beginChatServerListener() {
		Thread t = new Thread(new ChatServerListenerThread());
		logger.trace("Starting the ChatServerListener");
		t.start();
	}

	public class ChatServerListenerThread extends Thread implements Runnable {

		public ChatServerListenerThread() {
			System.out.println("Creating a ChatServerListenerThread");
		}

		@Override
		public void run() {
			System.out.println("Running ChatServerListnerThread");
			while(true) {
				try {
					String someResponse = chatBufferedReader.readLine();
					System.out.println(someResponse);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Closed ChatServerListenerThread");
					break;
				}
			}
		}

	}
	
}
