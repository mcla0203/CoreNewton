package com.cn.npc.monsters;

import com.cn.players.Player;

public class FaerieLumpAmoeba extends Monster {
	
	public FaerieLumpAmoeba() {
		level = 2;
	}
	
	/**
	 * This method is overridden because the amoeba blocks 
	 * some attacks against it.
	 */
	public void beAttacked(int dmg) {
		//TODO: Implement me
	}
	
	/**
	 * The amoeba's method of defense. Does some damage 
	 * to the attacker.
	 */
	public void ooze(Player p) {
		//TODO: Implement me
	}

}
