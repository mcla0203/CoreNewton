package com.cn.constants;

public class Constants {
	public static String CONNECT = "connect";
	public static String DISCONNECT = "disconnect";
	public static String ATTACK = "attack";
	public static String ATTACK_MONSTER = "attackMonster";
	public static String HEAL = "heal";
	public static String REST = "rest";
	public static String GET_HEALTH = "getHealth";
	public static String GET_ENERGY = "getEnergy";
	
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
