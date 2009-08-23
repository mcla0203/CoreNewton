package com.cn.npc.monsters;

import com.cn.chars.Character;

public class Monster extends Character {
	
	private double id;
	
	public Monster() {
		id = Math.floor(Math.random()*1000);
		System.out.println(id);
	}
	
	public double getId() {
		return id;
	}
	
}
