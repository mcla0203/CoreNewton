package com.cn.chars;

import com.cn.players.Player;

import junit.framework.TestCase;

public class CharacterTest extends TestCase {
	
	public void testCharacter() {
		Character c = new Character();
		assertEquals(c.health, 100);
		assertEquals(c.energy, 20);
		assertTrue(c.isAlive);
	}

	public void testLoseEnergy() {
		Character c = new Character();
		assertTrue(c.loseEnergy(10));
		assertEquals(c.getEnergy(), 10);
		assertFalse(c.loseEnergy(20));
		assertEquals(c.getEnergy(), 10);
	}
	
	public void testAttack() {
		Character c = new Character();
		Player p = new Player("Bob");		
		p.attack(c, 10);
		
		assertEquals(p.getEnergy(), 18);
		assertEquals(c.getHealth(), 90);
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
	
}