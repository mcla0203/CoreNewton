package com.cn.players;

import com.cn.chars.Character;

public class Player extends Character {
	
	private String name;
	private int MAX_HEALTH = 100;
	private int MAX_ENERGY = 20;
	
	public Player(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of the player
	 */
	public String getName() {
		return name;
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
	 * for 1 minute after this. 
	 */
	public void sleep() {
		//TODO:Implement me
	}
}
