package com.cn.npc.monsters;

import java.util.HashMap;
import java.util.Map;

import com.cn.chars.Character;
import com.cn.players.Player;

public class Monster extends Character {
	
	protected double id;
	protected boolean isLooted;
	protected Map<Player, Integer> playersEligibleForXP;
	protected Map<Player, Integer> attackedBy;
	
	public Monster() {
		isLooted = false;
		id = Math.floor(Math.random()*1000);
		playersEligibleForXP = new HashMap<Player, Integer>();
		attackedBy = new HashMap<Player, Integer>();
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
	 * @param p
	 * @param dmg
	 */
	public void beAttacked(Player p, int dmg) {
		if(!isAlive) {
			return;
		}
		int damageDone = 0;
		if(dmg < health) {
			health -= dmg;
			damageDone = dmg;
		}
		else {
			health = 0;
			damageDone = dmg - health;
			isAlive = false;
		}
		if(playersEligibleForXP.containsKey(p)) {
			int totalDmg = playersEligibleForXP.get(p) + damageDone;
			playersEligibleForXP.remove(p);
			playersEligibleForXP.put(p, totalDmg);
		}
		else if(attackedBy.containsKey(p)) {
			int totalDmg = attackedBy.get(p) + damageDone;
			if(isEligibleForXP(p, totalDmg)) {
				attackedBy.remove(p);
				playersEligibleForXP.put(p, totalDmg);
			}
			else {
				attackedBy.remove(p);
				attackedBy.put(p, totalDmg);
			}
		}
		else {
			if(isEligibleForXP(p, dmg)) {
				playersEligibleForXP.put(p, dmg);
			}
			else {
				attackedBy.put(p, dmg);
			}
		}
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
}
