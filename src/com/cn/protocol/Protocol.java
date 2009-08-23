package com.cn.protocol;

import com.cn.constants.ProtocolConstants;
import com.cn.players.Player;

public class Protocol {

	public static String attackRequest(int damage, double id) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + id + "><" + damage + ">";
	}
	
	public static String attackResponse(int damage, double id) {
		return attackRequest(damage, id);
	}
	
	public static String healRequest(int heal, Player player) {
		return ProtocolConstants.HEAL + "<" + player.getName() + "><" + heal + ">";
	}
	
	public static String healResponse(String s) {
		return ProtocolConstants.HEAL + ProtocolConstants.SUCCESS;
	}
	
	public static String createSuccessResponse() {
		return ProtocolConstants.SUCCESS;
	}
	
	/**
	 * This method is used to parse the most simple strings.
	 * It returns an array of strings created by splitting 
	 * the original string at the "<>".
	 */
	public static String[] getRequestArgsSimple(String s) {
		String[] array;
		s = s.trim();
		s = s.substring(1, s.length()-1);
		array = s.split("><");
		return array;
	}	
	
	/**
	 * Return the first argument of getRequestArgsSimple.
	 */
	public static String getRequestCmdSimple(String s) {
		return getRequestArgsSimple(s)[0];
	}
	
	public static String createSimpleResponse(String s) {
		return "<" + s + ">";
	}
}
