package com.cn.players;

import com.cn.npc.monsters.Monster;

public class Player {
	
	private String name;
	private int health;
	private int energy;
	private boolean isAlive;
	
	/*
	 * Given a name, construct an instance of a Player
	 */
	public Player(String name) {
		health = 100;
		energy = 100;
		this.name = name;
		isAlive = true;
	}
	
	/*
	 * Returns the health of the player
	 */
	public int getHealth() {
		return health;
	}
	
	/*
	 * Returns the energy of the player
	 */
	public int getEnergy() {
		return energy;
	}
	
	/*
	 * Returns the name of the player
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Returns true if the player is alive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/*
	 * When a player is attacked, the health is decremented by the 
	 * amount of the damage done. If the player doesn't have enough 
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
	
	/*
	 * Returns the name of the player
	 */
	public void attack(Monster monster) {
		energy -= 2;
	}

}
