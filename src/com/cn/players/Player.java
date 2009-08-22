package com.cn.players;

import com.ptc.npc.monsters.Monster;

public class Player {
	
	private String name;
	private int health;
	private int energy;
	private boolean isAlive;
	
	public Player(String name) {
		health = 100;
		energy = 100;
		this.name = name;
		isAlive = true;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public String getName() {
		return name;
	}
	
	public void beAttacked(int dmg) {
		if(dmg >= health) {
			health = 0;
			isAlive = false;
		}
		else {
			health -= dmg;
		}
	}
	
	public void attack(Monster monster) {
		energy -= 2;
	}

}
