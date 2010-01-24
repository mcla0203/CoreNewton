package com.cn.npc.monsters;

import org.apache.log4j.Logger;

import com.cn.players.Player;

public class FaerieLumpAmoeba extends Monster {
	Logger logger = Logger.getLogger(FaerieLumpAmoeba.class);
	
	public FaerieLumpAmoeba() {
		logger.trace("Creating an instance of a FaerieLumpAmoeba");
		level = 2;
	}
	
	/**
	 * This method is overridden because the amoeba blocks 
	 * some attacks against it.
	 */
//	public int beAttacked(int dmg) {
//		logger.trace("Inside FaerieLumpAmoeba.beAttacked()");
//		return  1;
//	}
	
	/**
	 * The amoeba's method of defense. Does some damage 
	 * to the attacker.
	 */
	public void ooze(Player p) {
		logger.trace("Inside FaerieLumpAmoeba.ooze()");
		//TODO: Implement me
	}

}
