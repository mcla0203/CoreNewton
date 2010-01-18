package com.cn.chars;

import junit.framework.TestCase;

public class CharacterTest extends TestCase {
	
	public void testCharacter() {
		Character c = new Character();
		assertEquals(c.health, 100);
		assertEquals(c.energy, 20);
		assertTrue(c.isAlive);
		assertEquals(c.level, 1);
		assertEquals(c.MAX_ENERGY, 20);
		assertEquals(c.MAX_HEALTH, 100);
	}

	public void testLoseEnergy() {
		Character c = new Character();
		assertTrue(c.loseEnergy(10));
		assertEquals(c.getEnergy(), 10);
		assertFalse(c.loseEnergy(20));
		assertEquals(c.getEnergy(), 10);
	}
	
	public void testBeAttacked() {
		Character c = new Character();
		c.beAttacked(40);
		assertEquals(c.getHealth(), 60);
		
		c.beAttacked(60);
		assertEquals(c.getHealth(), 0);
		assertFalse(c.isAlive());
		
		c.beAttacked(10);
		assertEquals(c.getHealth(), 0);
	}
	
	public void testGetEnergy() {
		Character c = new Character();
		assertEquals(c.getEnergy(), 20);
	}
	
	public void testGetHealth() {
		Character c = new Character();
		assertEquals(c.getHealth(), 100);
	}
	
	public void testIsAlive() {
		Character c = new Character();
		assertTrue(c.isAlive());
	}
	
	public void testGetLevel() {
		Character c = new Character();
		assertEquals(c.getLevel(), 1);
	}
	
	public void testSetLevel() {
		Character c = new Character();
		c.setLevel(5);
		assertEquals(c.getLevel(), 5);
	}
	
	public void testSetHealth() {
		Character c = new Character();
		assertEquals(c.getHealth(), 100);
		assertTrue(c.setHealth(5));
		assertEquals(c.getHealth(), 5);
		assertFalse(c.setHealth(200));
		assertEquals(c.getHealth(), 5);
	}
	
	public void testSetEnergy() {
		Character c = new Character();
		assertEquals(c.getEnergy(), 20);
		assertTrue(c.setEnergy(10));
		assertEquals(c.getEnergy(), 10);
		assertFalse(c.setEnergy(100));
		assertEquals(c.getEnergy(), 10);
	}
}
