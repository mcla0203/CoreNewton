package com.cn.players;

import com.cn.chars.Character;
import com.cn.npc.monsters.Monster;

public class Player extends Character {
	
	protected String name;
	protected int credits;
	protected int xp;
	
	public Player(String name) {
		this.name = name;
		this.credits = 0;	
		this.xp = 0;
	}
	
	public Player(String name, int lvl, int health, int energy, int xp, int credits) {
		this.name = name;
		this.credits = credits;
		this.xp = xp;
		this.level = lvl;
		this.health = health;
		this.energy = energy;
	}
	
	/**
	 * Returns the stats that are saved in the user file.
	 */
	public String[] getStats() {
		String[] stats = new String[6];
		stats[0] = name;
		stats[1] = String.valueOf(credits);
		stats[2] = String.valueOf(xp);
		stats[3] = String.valueOf(level);
		stats[4] = String.valueOf(health);
		stats[5] = String.valueOf(energy);
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
	 * Returns the xp of the player
	 * @return
	 */
	public int getXP() {
		return xp;
	}
	
	/**
	 * Sets the players credits to the number that is passed in.
	 * @param credit
	 */
	public void setCredits(int credit) {
		credits = credit;
	}
	
	/**
	 * This method heals the player for the amount
	 * specified. If the player's health plus the amount 
	 * specified is greater than max health, the player is 
	 * healed for only the amount needed to get to max
	 * health. This action also costs 5 energy to do.
	 */
	public boolean heal(int amount) {
		if(loseEnergy(5)) {
			if(health + amount > MAX_HEALTH) {
				health = MAX_HEALTH;
			}
			else {
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
		if(monster.isAlive()) {
			return -1;
		}
		if(monster.isLooted()) {
			return 0;
		}
		else {
			monster.setIsLooted(true);
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
		level += 1;
		MAX_HEALTH += 10;
		MAX_ENERGY += 2;
		health = MAX_HEALTH;
		energy = MAX_ENERGY;
	}
}
