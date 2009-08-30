package com.cn.chars;

import org.apache.log4j.Logger;

import com.cn.npc.monsters.Monster;

public class Character {

	protected int health;
	protected int energy;
	protected boolean isAlive;
	protected int level;
	protected int xp;

	protected int dmgReceived;
	protected int MAX_HEALTH;
	protected int MAX_ENERGY;
	Logger logger = Logger.getLogger(Character.class);
	
	/**
	 * Construct an instance of a Character
	 */
	public Character() {
		logger.trace("Creating an instance of character()");
		MAX_HEALTH = 100;
		MAX_ENERGY = 20;
		health = MAX_HEALTH;
		energy = MAX_ENERGY;
		isAlive = true;
		level = 1;
		xp = 0;
	}
	
	/**
	 * Returns true if the player is alive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * Returns the level of the character
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Returns the xp of the character
	 * @return
	 */
	public int getXP() {
		return xp;
	}
	
	/**
	 * Sets the xp of the character
	 */
	public void setXP(int exp) {
		xp = exp;
	}
	
	/**
	 * Sets the level of the character
	 */
	public void setLevel(int lvl) {
		level = lvl;
	}
	
	/**
	 * Returns the health of the player
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Sets the health of the character. For now this method is used
	 * primarily for testing purposes, but later could have a greater 
	 * use. Returns true if the health was set successfully and false 
	 * if not.
	 * @param h
	 * @return
	 */
	public boolean setHealth(int h) {
		if(h <= MAX_HEALTH) {
			health = h;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the energy of the player
	 */
	public int getEnergy() {
		return energy;
	}
	
	/**
	 * Sets the energy of the character. For now this method is used
	 * primarily for testing purposes, but later could have a greater 
	 * use. Returns true if the energy was set successfully and false 
	 * if not.
	 * @param e
	 * @return
	 */
	public boolean setEnergy(int e) {
		if(e <= MAX_ENERGY) {
			energy = e;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the amount of damage that this character received.
	 * @return
	 */
	public int getDmgReceived() {
		return dmgReceived;
	}
	
	/**
	 * When a character is attacked, the health is decremented by the 
	 * amount of the damage done. If the character doesn't have enough 
	 * health, he dies and his health is set to 0.
	 */
	public void beAttacked(int dmg) {
		logger.trace("Inside character.beAttacked");
		if(dmg < health) {
			health -= dmg;
			dmgReceived = dmg;
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug("the character: " +this.toString() + " died");
			}
			health = 0;
			dmgReceived = dmg - health;
			isAlive = false;
		}
	}
	
	/**
	 * Attacks character and decrements the energy 
	 * of the attacker
	 */
	public void attack(Character character, int dmg) {
		logger.trace("Inside character.attack()");
		character.beAttacked(dmg);
		if(logger.isDebugEnabled()) {
			logger.debug("Character: " +character+ "was attacked for "+dmg+ "damage");
		}
		loseEnergy(2);
	}
	
	/**
	 * Returns true if the player has enough
	 * energy to complete the action requested.
	 * Also decrements the energy by the correct
	 * amount.
	 */
	public boolean loseEnergy(int amount) {
		if(amount <= energy) {
			energy -= amount;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns an int that represents the amount of xp received
	 * for the character that you just killed.
	 */
	public int receiveXp(Monster m) {
		logger.trace("Inside Character.receiveXP(Monster)");
		int ret = 1;
		if(level < m.getLevel()) {
			ret = 5;
		}
		if(level == m.getLevel()) {
			ret = 3;
		}
		return ret;
	}
}
