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
	public static String INVALID_INPUT = "Invalid command. " + USER_INPUT;
	
	public static String HELP = "help";
	public static String HELP_CMDS_AVAILABLE = "Available commands are:"
    + "\n   login                                    - login to the server"
	+ "\n   createAcc <user> <password> <password>   - create a new account"
	+ "\n   attack <id>                              - attack the monster"
	+ "\n   loot <id>                                - loot the monster you killed"
	+ "\n   heal                                     - heal yourself"
	+ "\n   rest                                     - rest yourself"
	+ "\n   getHealth                                - get your current health"
	+ "\n   getPlayers                               - find out who's online"
	+ "\n   getMonsters                              - get the monsters in this instance"
	+ "\n   getDeadMonsters                          - get the dead monsters in this instance"
	+ "\n   getEnergy                                - get your energy";
	public static String HELP_FORMATTED = formatOutput(HELP_CMDS_AVAILABLE);
	
	public static String LOOT_IS_POSSIBLE = formatOutput("You can loot these remains...");
	public static String HEAL_COMPLETE = formatOutput("You are completely healed.");
	public static String NOT_IMPLEMENTED = formatOutput("Feature not yet implemented...");
	public static String SAVE_COMPLETE = formatOutput("Success saving the game...");
	public static String SAVE_FAILED = formatOutput("Failed to save game.");
	
	public static String NOT_LOGGED_IN = formatOutput("You need to be logged in to do that...");
	public static String ALREADY_LOGGED_IN = formatOutput("You're already logged in...");
	
	public static String LOGIN_CHARACTERS = formatOutput("Here is a list of characters to choose from...");
	
	public static String PASSWORDS_DONT_MATCH = formatOutput("Your passwords do not match, type them again.");
	public static String ACCOUNT_CREATED_SUCCESSFULLY = formatOutput("Your new account has been created.");
	public static String ACCOUNT_ALREADY_EXISTS = formatOutput("Someone else uses that account, please choose another...");
	public static String NO_CHARS_CREATED = formatOutput("You have not made any characters yet... create one now!");
	
	public static String USER_NOT_FOUND = formatOutput("The username you typed does not exist.");
	
	public static String LOGIN_SUCCESS = formatOutput("You have successfully logged in.");
	
	public static String GENERAL_FAILURE = formatOutput("Something when horribly wrong.");
	
	public static String REST_DISABLED = formatOutput("You stop resting.");
	public static String REST_ENABLED = formatOutput("You start to rest.");
	public static String ENERGY_FULL = formatOutput("Your energy was recharged to full.");
}
