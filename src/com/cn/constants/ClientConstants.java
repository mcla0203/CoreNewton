package com.cn.constants;

/**
 * Constants for the client side processing. 
 */
public class ClientConstants extends Constants {
	public static String LOGGER = "com.cn.client.ClientConstants";
	public static int PORT = 8888;
	public static String CONNECT_ATTEMPT = formatOutput("Attempting to connect to the server specified...");
	public static String CONNECT_ATTEMPT_MALFORMED = formatOutput("You are using the connect command wrong... use \"connect <ip> <port>\"");
	public static String CONNECT_SUCCESS = formatOutput("Successfully connected to server");
	public static String DISCONNECT_SUCCESS = formatOutput("Disconnected from server.");
	
	public static String USER_INPUT = "Type \"help\" for a list of commands..";
	
	public static String HELP = "help";
	public static String HELP_CMDS_AVAILABLE = "Available commands are:\n   connect <ip> <port>   - connect to the server"
																	+ "\n   disconnect            - disconnect from the server"
																	+ "\n   attack <monster id>   - attack the monster"
																	+ "\n   loot <monster id>     - loot the monster you killed"
																	+ "\n   heal                  - heal yourself"
																	+ "\n   rest                  - rest yourself"
																	+ "\n   getHealth             - get your current health"
																	+ "\n   getEnergy             - get your energy"
																	+ "\n   getMonsters           - get the monsters in this instance";
	public static String HELP_FORMATTED = formatOutput(HELP_CMDS_AVAILABLE);
	
	public static String LOOT_IS_POSSIBLE = formatOutput("You can loot these remains...");
	public static String HEAL_COMPLETE = formatOutput("You are completely healed.");
	public static String NOT_IMPLEMENTED = formatOutput("Feature not yet implemented...");
	
}