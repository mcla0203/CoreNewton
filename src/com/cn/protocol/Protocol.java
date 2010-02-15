package com.cn.protocol;

import java.util.ArrayList;
import java.util.List;

import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.npc.monsters.Monster;
import com.cn.players.Player;

public class Protocol {

	public static String attackRequest(double id, String name) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + id + "><" + name + ">";
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
	public static String convertListToProtocol(ArrayList<String> list) {
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
	
	public static String createSimpleResponse(ArrayList<String> s) {
		String ret = "";
		for(String str : s) {
			ret += createSimpleResponse(str);
		}
		return ret;
	}
	
	public static String createSimpleRequest(String s) {
		return createSimpleResponse(s);
	}
	
	public static String createSimpleRequest(int i) {
		return createSimpleResponse(String.valueOf(i));
	}
	
	public static String createLoginWithCharName(String s) {
		return ProtocolConstants.LOGIN_NAME + s;
	}
	
	public static String createLoginResponse(ArrayList<String> chars) {
		String response = ProtocolConstants.LOGIN_RESPONSE;
		for(String name : chars) {
			response += "<" + name + ">";
		}
		return response;
	}
	
	public static String createSaveRequest(String name) {
		return ProtocolConstants.SAVE + Protocol.createSimpleRequest(name);
	}
	
	public static String chatLoginRequest(String name) {
		return ProtocolConstants.CHAT_LOGIN + "<" + name + ">";
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


	public static String sendPlayerMessageRequest(String[] input) {
		// TODO Auto-generated method stub
		String name = input[1];
		String message = "";
		for(String s : input) {
			message += s + " ";
		}
		message = message.substring(Constants.SEND_MESSAGE.length() + name.length() + 2).trim();
		return ProtocolConstants.SEND_MESSAGE + "<" + name + ">" + "<" + message + ">";
	}

	public static String sendAllMessageRequest(String[] input, String myCharname) {
		// TODO Auto-generated method stub
		String message = "";
		for(String s : input) {
			message += s + " ";
		}
		message = message.substring(Constants.CHAT_CHANNEL.length() + Constants.CHANNEL_ALL.length() + 2).trim();
		return ProtocolConstants.CHAT_CHANNEL + ProtocolConstants.CHANNEL_ALL +"<" + myCharname + ">" + "<" + message + ">";
	}
	
	public static String getCharactersResponse(ArrayList<String> charList) {
		String response = "";
		for(String name : charList) {
			response += "<" + name + ">";
		}
		return response;
	}
}