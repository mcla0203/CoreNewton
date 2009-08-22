package com.cn.server;

public class ServerConstants {

	public static String LOGGER = "com.cn.server.logger";
	public static String SERVER_STARTUP = formatOutput("The CoreNewton server has been started");
	public static String SERVER_ACCEPTING = formatOutput("The CoreNewton server is waiting for client connections...");
	public static String Server_STARTUP_FAILED = formatOutput("The CoreNewton server failed to start.");
	
	public static String HOSTNAME = "localhost";
	public static int PORT = 7777;
	
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
