package com.cn.chars;

import org.apache.log4j.Logger;

import com.cn.npc.monsters.Monster;
import com.cn.players.Player;
import com.cn.weapon.DefaultWeapon;
import com.cn.weapon.Weapon;

public class Character {

	protected int health;
	protected int energy;
	protected boolean isAlive;
	protected int level;
	protected int xp;
	protected String type;
	
	protected int dmgReceived;
	protected int MAX_HEALTH;
	protected int MAX_ENERGY;
	Logger logger = Logger.getLogger(Character.class);
	
	protected Weapon weapon = null;
	
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
		weapon = DefaultWeapon.getDefaultWeapon(level);		
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
		weapon = DefaultWeapon.getDefaultWeapon(level);
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
	 * Sets the character's health and energy to their MAX_HEALTH and MAX_ENERGY.
	 */
	public void rechargeHealthAndEnergy() {
		health = MAX_HEALTH;
		energy = MAX_ENERGY;
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
	 * Returns false if the character survived the attack, returns true
	 * if it died.
	 */
	public boolean beAttacked(int dmg) {
		logger.trace("Inside character.beAttacked");
		if(dmg < health) {
			health -= dmg;
			dmgReceived = dmg;
			return false;
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug("the character: " +this.toString() + " died");
			}
			dmgReceived = health;
			health = 0;
			die();
			return true;
		}
	}
	
	/**
	 * Method for handling what happens when a character dies. If the character is a player, 
	 * it should automatically resurrect. 
	 */
	public void die() {
		isAlive = false;
		if(type.equals("player")) {
			resurrect();
		}
	}
	
	/**
	 * This method is called right after a player dies. It allows them to resurrect and continue
	 * playing, only costing them a minor penalty. Their xp is reset to the minimum amount for
	 * the current level that they are. The health and energy are recharged to full as well.
	 */
	public void resurrect() {
		int level = getLevel();
		int xp = getMinXPBasedOnLevel(level);
		logger.debug("Player is being resurrected. He is level: " + level + ". The xp is being reset to: " + xp);
		setXP(xp);
		rechargeHealthAndEnergy();
		isAlive = true;
	}
	
	/**
	 * Returns an int that represents the minimum amount of xp that a player needs in order to 
	 * be a certain level. The level is passed in as a parameter (lvl).
	 * @param lvl
	 */
	public int getMinXPBasedOnLevel(int lvl) {
		int minXP = 0;
		if(lvl == 1) {
			return 0;
		}
		for(int i = 1; i<lvl; i++) {
			minXP += 600 + 180*(i-1);
		}		
		return minXP;
	}
	
	/**
	 * Attacks character and decrements the energy 
	 * of the attacker
	 * This method is only to be used in tests.
	 */
	@Deprecated
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
		logger.trace("Entering character.loseEnergy method");
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
	 * for the monster that was just killed. 
	 * 
	 * 100xp is received for killing monsters equal to your level, no matter what your level is.
	 * If you are level 10 or below, killing a monster that is: 
	 * 		5 or more levels below you earns you 0xp.
	 * 		5 or more levels above you earns you 150xp.
	 * 		1 to 4 levels below you earns you 100-[(x-y)*.1]
	 * 		1 to 4 levels above you earns you 100+[(y-x)*.1]
	 * If you are level 11 or higher, the amount of xp you earn is based on the ratio of the monster's
	 * level to your level:
	 * 		xpEarned = y/x * 100.
	 * There are two edge cases to this. If xpEarned is:
	 * 		less than 50, you earn 0xp.
	 * 		greater than or equal to 200, you earn 300xp;
	 * @param m
	 */
	public int receiveXp(Monster m) {
		logger.trace("Inside Character.receiveXP(Monster)");
		int playerLevel = getLevel();
		int monsterLevel = m.getLevel();
		double xpToGive = ((double)monsterLevel/(double)playerLevel)*100;
		if(playerLevel <= 10) {
			logger.trace("Player is level 10 or lower.");
			if(monsterLevel <= playerLevel-5) {
				xpToGive = 0;
			}
			else if(monsterLevel >= playerLevel+5) {
				xpToGive = 150;
			}
			else if(monsterLevel < playerLevel) {
				xpToGive = 100 - ((playerLevel - monsterLevel)*.1);
			}
			else if(monsterLevel > playerLevel) {
				xpToGive = 100 + ((monsterLevel - playerLevel)*.1);
			}
		}
		else {
			logger.trace("Player is level 11 or higher.");
			if(xpToGive < 50) {
				xpToGive = 0;
			}
			else if(xpToGive >= 200) {
				xpToGive = 300;
			}
		}	
		return (int)xpToGive;
	}
	
	public void setWeapon(Weapon w) {
		weapon = w;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
}
