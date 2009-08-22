package com.cn.chars;

public class Character {

	protected int health;
	protected int energy;
	protected boolean isAlive;
	
	/**
	 * Given a name, construct an instance of a Player
	 */
	public Character() {
		health = 100;
		energy = 20;
		isAlive = true;
	}
	
	/**
	 * Returns true if the player is alive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * Returns the health of the player
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Returns the energy of the player
	 */
	public int getEnergy() {
		return energy;
	}
	
	/**
	 * When a character is attacked, the health is decremented by the 
	 * amount of the damage done. If the character doesn't have enough 
	 * health, he dies and his health is set to 0.
	 */
	public void beAttacked(int dmg) {
		if(dmg < health) {
			health -= dmg;
		}
		else {
			health = 0;
			isAlive = false;
		}
	}
	
	/**
	 * Attacks character and decrements the energy 
	 * of the attacker
	 */
	public void attack(Character character, int dmg) {
		character.beAttacked(dmg);
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
	
}
