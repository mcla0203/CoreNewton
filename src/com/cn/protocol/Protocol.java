package com.cn.protocol;

import java.util.List;

import com.cn.constants.ProtocolConstants;
import com.cn.npc.monsters.Monster;
import com.cn.players.Player;

public class Protocol {

	public static String attackRequest(int damage, double id, String name) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + damage + "><" + id + "><" + name + ">";
	}

	public static String attackResponse(int damage, double id, String name) {
		return attackRequest(damage, id, name);
	}

	public static String healRequest(int heal, String name) {
		return ProtocolConstants.HEAL + "<" + heal + "><" + name + ">";
	}

	public static String healResponse(String s) {
		return ProtocolConstants.HEAL + ProtocolConstants.SUCCESS;
	}

	public static String createSuccessResponse() {
		return ProtocolConstants.SUCCESS;
	}

	public static String createCharacterDiedResponse(double id) {
		return ProtocolConstants.DEATH + "<" + id + ">";
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
	 * This method is used to iterate a set of strings and create a 
	 * simple response.
	 * @param array
	 * @return String result
	 */
	public static String convertListToProtocol(String[] list) {
		String result = "";
		for( String s : list ) {
			result += "<" + s + ">";
		}
		return result;
	}
	
	/**
	 * This method is used to iterate a set of strings and create a 
	 * simple response.
	 * @param array
	 * @return String result
	 */
	public static String convertListToProtocol(Double[] list) {
		String result = "";
		for( Double s : list ) {
			result += "<" + String.valueOf(s) + ">";
		}
		return result;
	}
	
	/**
	 * This method is used to iterate a set of strings and create a 
	 * simple response.
	 * @param array
	 * @return String result
	 */
	public static String convertPlayerListToProtocol(List<Player> list) {
		String result = "";
		for( Player p : list ) {
			result += "<" + p.getName() + ">";
		}
		return result;
	}
	
	/**
	 * This method is used to iterate a set of strings and create a 
	 * simple response.
	 * @param array
	 * @return String result
	 */
	public static String convertMonsterListToProtocol(List<Monster> list) {
		String result = "";
		for( Monster m : list ) {
			result += "<" + m.getId() + ">";
		}
		return result;
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
	
	public static String createSimpleRequest(String s) {
		return createSimpleResponse(s);
	}
	
	public static String createLoginWithCharName(String s) {
		return ProtocolConstants.LOGIN_NAME + s;
	}
	
	public static String createSaveRequest(String name) {
		return ProtocolConstants.SAVE + Protocol.createSimpleRequest(name);
	}
	
	public static String chatLoginRequest(String name, String url) {
		return ProtocolConstants.CHAT_LOGIN + "<" + name + ">" + "<" + url + ">";
	}
	
	public static String chatLogoutRequest(String name) {
		return ProtocolConstants.CHAT_LOGOUT + "<" + name + ">";
	}
	public static String createXPNotification(int amt, String name) {
		return ProtocolConstants.XP + "<" + amt +">" + "<" + name + ">";
	}
	public static String createLootRequest(String id) {
		return ProtocolConstants.LOOT + "<" + id + ">";
	}
	
}