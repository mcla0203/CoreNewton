package com.cn.protocol;

import com.ptc.npc.monsters.Monster;

public class Protocol {

	public static String attack(int damage, Monster monster) {
		return "<attackMonster><" + monster.getName() + "><" + damage + ">";
	}
	
	public static String respondToAttack(int damage, Monster monster) {
		return "<attackMonster><" + monster.getName() + "><" + damage + ">";
	}
	
	public static String hmmmm(int damage, Monster monster) {
		return ProtocolConstants.ATTACK_MONSTER + "<" + monster.getName() + "><" + damage + ">";
	}
}
