package com.cn.protocol;

import java.util.ArrayList;

import com.cn.npc.monsters.Monster;
import com.cn.players.Player;

public class Protocol {

	public static String attackRequest(int damage, Monster monster) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + monster.getId() + "><" + damage + ">";
	}
	
	public static String attackResponse(int damage, Monster monster, Player player) {
		return attackRequest(damage, monster) + "<" + player.getName() + ">";
	}
	
	public static String healRequest(int heal, Player player) {
		return ProtocolConstants.HEAL + "<" + player.getName() + "><" + heal + ">";
	}
	
	public static String healResponse(String s) {
		return ProtocolConstants.HEAL + ProtocolConstants.SUCCESS;
	}
	
	/**
	 * This method is used to parse the most simple strings.
	 * It returns an array of strings created by splitting 
	 * the original string at the "<>".
	 */
	public static String[] parseSimple(String s) {
		String[] array;
		s = s.trim();
		s = s.substring(1, s.length()-1);
		array = s.split("><");
		return array;
	}	
}
