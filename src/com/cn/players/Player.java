package com.cn.players;

import org.apache.log4j.Logger;

import com.cn.chars.Character;
import com.cn.npc.monsters.Monster;
import com.cn.weapon.DefaultWeapon;

public class Player extends Character {
	Logger logger = Logger.getLogger(Player.class);
	
	protected String name;
	protected int credits;
	protected boolean isOnline;
	
	public Player(String name) {
		if(logger.isTraceEnabled()) {
			logger.trace("Creating player: " + name);
		}
		this.name = name;
		this.credits = 0;
	}
	
	public Player(String name, int lvl, int health, int energy, int xp, int credits) {
		logger.trace("Creating an instance of Player with many attributes.");
		this.name = name;
		this.credits = credits;
		this.xp = xp;
		this.level = lvl;
		this.health = health;
		this.energy = energy;
		this.isOnline = true;
		weapon = DefaultWeapon.getDefaultWeapon(level);
	}
	
	/**
	 * Returns the stats that are saved in the user file.
	 */
	public String[] getStats() {
		String[] stats = new String[6];
		stats[0] = name;
		stats[1] = String.valueOf(level);
		stats[2] = String.valueOf(health);
		stats[3] = String.valueOf(energy);
		stats[4] = String.valueOf(xp);
		stats[5] = String.valueOf(credits);
		return stats;
	}
	
	/**
	 * Returns the name of the player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the number of credits that the player currently
	 * has.
	 * @return
	 */
	public int getCredits() {
		return credits;
	}
	
	/**
	 * Sets the players credits to the number that is passed in.
	 * @param credit
	 */
	public void setCredits(int credit) {
		credits = credit;
	}
	
	public boolean isOnline() {
		return isOnline;
	}
	
	/**
	 * This method heals the player for the amount
	 * specified. If the player's health plus the amount 
	 * specified is greater than max health, the player is 
	 * healed for only the amount needed to get to max
	 * health. This action also costs 5 energy to do.
	 */
	public boolean heal(int amount) {
		logger.trace("Inside heal()");
		if(loseEnergy(5)) {
			if(health + amount > MAX_HEALTH) {
				health = MAX_HEALTH;
			}
			else {
				if(logger.isDebugEnabled()) {
					logger.debug("Player " + this.getName() + "healing for: " + amount);
				}
				health += amount;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * This method recharges energy. Returns true if the 
	 * player's energy was recharged to full, else returns 
	 * false.
	 */
	public boolean rest() {
		logger.trace("Inside rest()");
		if(energy + 5 < MAX_ENERGY) {
			energy += 5;
			return false;
		}
		else {
			energy = MAX_ENERGY;
			return true;
		}
	}
	
	/**
	 * Recharges energy and health. Player cannot do anything
	 * for 1 minute after executing this command. 
	 */
	public void sleep() {
		logger.trace("Inside sleep()");
		//TODO:Implement me
	}
	
	/**
	 * Returns true if the player is fully healed.
	 * @return
	 */
	public boolean isFullyHealed() {
		return (health == MAX_HEALTH ? true : false);
	}
	
	/**
	 * Returns true if the player's energy is full.
	 * @return
	 */
	public boolean isFullyEnergized() {
		return (energy == MAX_ENERGY ? true : false);
	}
	
	/**
	 * Returns an int that represents the number of 'credits'
	 * (coins) that a player receives for killing a monster.
	 * The number of credits returned will depend on the level 
	 * of the monster killed.
	 * @param monster
	 * @return
	 */
	public int loot(Monster monster) {
		logger.trace("Inside Player.loot()");
		if(monster.isAlive() || monster.isLooted()) {
			if(logger.isDebugEnabled()) {
				logger.debug("Monster " + monster + "is still alive or has already been looted. Player cannot loot.");
			}
			return 0;
		}
		else {
			monster.setIsLooted(true);
			if(logger.isDebugEnabled()) {
				logger.debug("Player is going to loot monster " + monster);
			}
			if(monster.getLevel() < 6) {
				credits += 5;
				return 5;
			}
			if(monster.getLevel() < 11) {
				credits += 10;
				return 10;
			}
			else {
				credits += 20;
				return 20;
			}
		}
	}
	
	public void levelUp() {
		logger.trace("Inside levelUp()"); 
		if(logger.isDebugEnabled()) {
			logger.debug("Player " + this.getName() + "of level " + this.getLevel() +
				"is going to level up.");
		}
		level += 1;
		MAX_HEALTH += 10;
		MAX_ENERGY += 2;
		health = MAX_HEALTH;
		energy = MAX_ENERGY;
		if(logger.isDebugEnabled()) {
			logger.debug("Player " + this.getName() + "is now level " + this.getLevel());
		}
	}
	
	public int getMaxHealth() {
		return MAX_HEALTH;
	}
	
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}

	public void updateXP(Integer amount) {
		logger.trace("Entering Player.updateXP");
		if(shouldPlayerLevelUp(amount)) {
			if(logger.isDebugEnabled()) {
				logger.debug("Player should level up. Current XP: " + getXP() + " amount: " + amount);
			}
			levelUp();
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Player's current XP is: " + getXP() + ". Adding this much XP: " + amount);
		}
		this.setXP(this.getXP() + amount);
		logger.debug("Players new XP: " + getXP());
		logger.trace("Exiting Player.updateXP");
	}

	/**
	 * Method to determine whether or not a player is eligible to level up. The current algorithm
	 * to determine the experience needed to level up to the next level is: (current level - 1 * 180) + 600.
	 * @param amount
	 * @return true if the player should level up, else return false.
	 */
	public boolean shouldPlayerLevelUp(Integer amount) {
		int minXPNeededForNextLevel = (getLevel()*180)+420;
		int xpNeededToLevelUp = minXPNeededForNextLevel - getXP();
		if(amount >= xpNeededToLevelUp) {
			return true;
		}
		return false;
	}
}
