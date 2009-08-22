package com.ptc.npc.monsters;

public class Monster {

	private String name;
	private int health;
	private int energy;
	private boolean isAlive;
	
	public Monster() {
		health = 100;
		energy = 100;
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
	
	public void attack(Monster monster, int damage) {
		monster.beAttacked(damage);
		//don't let the monster attack you...you need to defend yourself :-p
	}
}
