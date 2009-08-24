package com.cn.npc.monsters;

import com.cn.chars.Character;

public class Monster extends Character {
	
	protected double id;
	protected boolean isLooted;
	
	public Monster() {
		isLooted = false;
		id = Math.floor(Math.random()*1000);
		System.out.println(id);
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
	
}
