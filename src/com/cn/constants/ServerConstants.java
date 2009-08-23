package com.cn.constants;

public class ServerConstants extends Constants {

	public static String LOGGER = "com.cn.server.logger";
	public static String SERVER_STARTUP = formatOutput("The CoreNewton server has been started");
	public static String SERVER_ACCEPTING = formatOutput("The CoreNewton server is waiting for client connections...");
	public static String Server_STARTUP_FAILED = formatOutput("The CoreNewton server failed to start.");
	
	public static String INVALID_MSG_RECVD = formatOutput("An invalid message was received!!");
	public static String INVALIS_MSG_RECVD_CODE = "999";
	public static String HOSTNAME = "localhost";
	public static int PORT = 7777;
	
}
