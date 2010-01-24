package com.cn.npc.monsters;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cn.chars.Character;
import com.cn.players.Player;

public class Monster extends Character {
	Logger logger = Logger.getLogger(Monster.class);

	protected double id;
	protected boolean isLooted;
	protected Map<Player, Integer> playersEligibleForXP;
	protected Map<Player, Integer> attackedBy;

	public Monster() {
		isLooted = false;
		id = Math.floor(Math.random()*1000);
		playersEligibleForXP = new HashMap<Player, Integer>();
		attackedBy = new HashMap<Player, Integer>();
		type = "monster";
	}

	public Monster(double id) {
		this();
		this.id = id;
		this.type = "monster";
	}

	public double getId() {
		return id;
	}

	/**
	 * Returns true if the monster has been looted.
	 * @return
	 */
	public boolean isLooted()  {
		return isLooted;
	}

	public void setIsLooted(boolean b) {
		isLooted = b;
	}

	public Map<Player, Integer> getPlayersEligibleForXP() {
		return playersEligibleForXP;
	}

	public Map<Player, Integer> getAttackedBy() {
		return attackedBy;
	}


	/**
	 * Method for handling the attacks of players against monsters. Decrements
	 * monster's health and keeps track of players that are eligible for gaining
	 * xp upon the monster's death. Amount of xp gained depends on percentage of 
	 * damage done by a particular player to this monster.
	 * The server handles all the cases in which an attack would fail (ie. incorrect args,
	 * already dead monster, etc..) so if the code gets here, we are assuming that the 
	 * attack is successful.
	 * @param p, player performing the attack
	 * @param dmg, damage done to monster
	 */
	public int beAttacked(Player p) {
		int dmg = p.getWeapon().computeDamage();
		if(logger.isDebugEnabled()) {
			logger.debug("Attacking player level is: " + p.getLevel());
			logger.debug("The dmg done is: " + dmg);
			logger.debug("The min dmg is: " + p.getWeapon().getMin());
			logger.debug("The max dmg is: " + p.getWeapon().getMax());
		}
		if(logger.isTraceEnabled()) {
			logger.trace("Inside Monster.beAttacked(): " + this.toString());
		}
		int damageDone = 0;
		logger.debug("Player is attacking monster. Player will lose 2 energy.");
		if(dmg < health) {
			damageDone = dmg;
			health -= dmg;
			logger.debug("damage done: " + damageDone);
		}
		else {
			damageDone = health;
			health = 0;
			isAlive = false;
			logger.debug("Monster killed. damage done: " + damageDone);
		}
		dmgReceived = damageDone;
		if(playersEligibleForXP.containsKey(p)) {
			logger.debug("Player " + p + "is already eligible for XP but did more damage.");
			int totalDmg = playersEligibleForXP.get(p) + damageDone;
			playersEligibleForXP.remove(p);
			playersEligibleForXP.put(p, totalDmg);
		}
		else if(attackedBy.containsKey(p)) {
			int totalDmg = attackedBy.get(p) + damageDone;
			if(isEligibleForXP(p, totalDmg)) {
				attackedBy.remove(p);
				playersEligibleForXP.put(p, totalDmg);
				logger.debug("Player "+p+" is now eligible for XP");
			}
			else {
				attackedBy.remove(p);
				attackedBy.put(p, totalDmg);
				logger.debug("Player " + p + "did damage to this monster again but is not " +
				"yet eligible for XP");
			}
		}
		else {
			if(isEligibleForXP(p, dmg)) {
				logger.debug("Player " + p + "did enough damage the first time to be" +
				"eligible for XP");
				playersEligibleForXP.put(p, dmg);
			}
			else {
				logger.debug("Player " + p + "did damage for the first time but it is not" +
				"enough to be eligible for XP");
				attackedBy.put(p, dmg);
			}
		}
		return dmg;
	}

	/**
	 * Returns true if the player has done a total of 20% or more damage
	 * to this monster
	 * @param p
	 * @param totalDmg
	 * @return
	 */
	private boolean isEligibleForXP(Player p, int totalDmg) {
		double dmg = (double)totalDmg;
		double monsterHealth = (double)MAX_HEALTH;
		if(dmg/monsterHealth >= .2) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method used to distribute XP to all players eligible. Returns a map where the key is a Player
	 * and the value is the amount of xp that the player is to receive.
	 */
	public Map<Player, Integer> divvyUpXP() {
		Map<Player, Integer> xpMap = new HashMap<Player, Integer>();
		int value = 0;
		for(Player p: playersEligibleForXP.keySet()) {
			value = p.receiveXp(this);
			xpMap.put(p, value);
		}
		return xpMap;
	}
}
