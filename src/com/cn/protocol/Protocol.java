package com.cn.protocol;

import com.cn.constants.ProtocolConstants;

public class Protocol {

	public static String attackRequest(int damage, double id) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + id + "><" + damage + ">";
	}

	public static String attackResponse(int damage, double id) {
		return attackRequest(damage, id);
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
	
	public static String getMonstersRequest() {
		return ProtocolConstants.GET_MONSTERS;
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
	public static String createResponseSimple(String[] list) {
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
	public static String createResponseSimple(Double[] list) {
		String result = "";
		for( Double s : list ) {
			result += "<" + String.valueOf(s) + ">";
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
}