package com.cn.protocol;

import com.cn.npc.monsters.Monster;
import com.cn.players.Player;

public class Protocol {

	public static String attack(int damage, Monster monster) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + monster.getName() + "><" + damage + ">";
	}
	
	public static String respondToAttack(int damage, Monster monster, Player player) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + monster.getName() + "><" + damage + ">";
	}
	
	public static String hmmmm(int damage, Monster monster) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + monster.getName() + "><" + damage + ">";
	}
}
