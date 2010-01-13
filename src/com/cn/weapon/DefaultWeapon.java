package com.cn.weapon;

public class DefaultWeapon {

	public static Weapon getDefaultWeapon(int level) {
		//Construct a default weapon
		String name = "Starter Melee Weapon";
		int min = 5 + level;
		int max = 20 + level;
		double criticalPercentage = .005;
		
		return new Weapon(name, max, min, criticalPercentage);
	}
	
}
