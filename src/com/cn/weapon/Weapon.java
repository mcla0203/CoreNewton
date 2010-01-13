package com.cn.weapon;

public class Weapon {

	private String name;
	private int max;
	private int min;
	private double criticalPercent;
	private int criticalHit;

	public Weapon(String name, int max, int min, double criticalPercent) {
		this.name = name;
		this.max = max;
		this.min = min;
		this.criticalPercent = criticalPercent;
		setCriticalHit();
	}

	private void setCriticalHit() {
		criticalHit = (max + min) * 3;
	}

	/**
	 * This function computes damage based on some
	 * qualities of the weapon. Basically returns a 
	 * random number between max and min.
	 */
	public int computeDamage() {
		double random = Math.random();
		//return critical hit
		if(random < criticalPercent) {
			return criticalHit;
		}
		//return regular hit
		else {
			return (int)(min + random*(max - min));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public double getCriticalPercent() {
		return criticalPercent;
	}

	public void setCriticalPercent(double criticalPercent) {
		this.criticalPercent = criticalPercent;
	}

	public int getCriticalHit() {
		return criticalHit;
	}

	public void setCriticalHit(int criticalHit) {
		this.criticalHit = criticalHit;
	}
	
}
