package com.cn.client;

public class ClientConstants {
	public static String LOGGER = "com.cn.client.ClientConstants";
	public static int PORT = 8888;
	public static String CONNECT = "connect";
	public static String CONNECT_ATTEMPT = formatOutput("Attempting to connect to the server specified...");
	public static String CONNECT_ATTEMPT_MALFORMED = formatOutput("You are using the connect command wrong... use \"connect <ip> <port>\"");
	public static String USER_INPUT = "What command do you want to issue?  type \"help\" for a list of commands..";
	
	/**
	 * Formats the message to a more desirable output within the server.
	 * 
	 * @param msg
	 * @return formatted msg
	 */
	public static String formatOutput(String msg) {
		return "\n==========================================\n"
			                       +msg+"\n"
			   + "==========================================\n";
	}
	
}
