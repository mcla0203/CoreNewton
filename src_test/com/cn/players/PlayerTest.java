package com.cn.players;

import com.cn.npc.monsters.Monster;

import junit.framework.TestCase;

public class PlayerTest extends TestCase {

	public void testPlayer() {
		Player p = new Player("Billy Bob");
		assertEquals(p.name, "Billy Bob");
	}
	
	public void testGetName() {
		Player p = new Player("Bonnie");
		assertEquals(p.getName(), "Bonnie");
	}
	
	public void testGetCredits() {
		Player p = new Player("Clyde");
		assertEquals(p.getCredits(), 0);
	}
	
	public void testSetCredits() {
		Player p = new Player("Cartman");
		assertEquals(p.getCredits(), 0);
		p.setCredits(100000);
		assertEquals(p.getCredits(), 100000);
	}
	
	public void testHeal() {
		Player p = new Player("Jo");
		p.setHealth(8);
		assertTrue(p.heal(10));
		assertEquals(p.getHealth(), 18);
		//This is assuming MAX_HEALTH is 100
		assertTrue(p.heal(100));
		assertEquals(p.getHealth(), 100);
		assertTrue(p.heal(1));
		assertTrue(p.heal(1));
		//Player should be out of energy now
		assertFalse(p.heal(1));
	}
	
	public void testRest() {
		Player p = new Player("Sue");
		p.setEnergy(8);
		assertFalse(p.rest());
		assertFalse(p.rest());
		assertTrue(p.rest());		
	}
	
	public void testSleep() {
		//TODO: Implement me
	}
	
	public void testIsFullyHealed() {
		Player p = new Player("Wally");
		assertTrue(p.isFullyHealed());
		p.setHealth(50);
		assertFalse(p.isFullyHealed());
	}
	
	public void testIsFullyEnergized() {
		Player p = new Player("Larry");
		assertTrue(p.isFullyEnergized());
		p.loseEnergy(10);
		assertFalse(p.isFullyEnergized());
	}
	
	public void testLoot() {
		Player p = new Player("Stan");
		assertEquals(0, p.credits);
		Monster m = new Monster();
		assertEquals(p.loot(m), 0);
		assertEquals(p.getCredits(), 0);
		m.beAttacked(m.getHealth());
		assertEquals(p.loot(m), 5);
		assertEquals(p.getCredits(), 5);
		assertEquals(p.loot(m), 0);
		assertEquals(p.getCredits(), 5);
	}
	
	public void testPlayerStatsConstructor() {
		Player p = new Player("name", 1, 2, 3, 4, 5);
		assertEquals(p.getName(),"name");
		assertEquals(p.getLevel(),1);
		assertEquals(p.getHealth(),2);
		assertEquals(p.getEnergy(),3);
		assertEquals(p.getXP(),4);
		assertEquals(p.getCredits(),5);
	}
	
	public void testGetStats() {
		Player p = new Player("name", 1, 2, 3, 4, 5);
		String[] stats = p.getStats();
		assertEquals(stats[0],"name");
		assertEquals(stats[1],"1");
		assertEquals(stats[2],"2");
		assertEquals(stats[3],"3");
		assertEquals(stats[4],"4");
		assertEquals(stats[5],"5");
	}
	
	public void testLevelUp() {
		Player p = new Player("name", 1, 2, 3, 4, 5);
		assertEquals(p.getName(),"name");
		assertEquals(p.getLevel(),1);
		assertEquals(p.getHealth(),2);
		assertEquals(p.getEnergy(),3);
		assertEquals(p.getMaxEnergy(), 20);
		assertEquals(p.getMaxHealth(), 100);
		p.levelUp();
		assertEquals(p.getLevel(),2);
		assertEquals(p.getHealth(),110);
		assertEquals(p.getEnergy(),22);
		assertEquals(p.getMaxEnergy(), 22);
		assertEquals(p.getMaxHealth(), 110);	
	}
	
	public void testGetMAXHEALTH() {
		Player p = new Player("name");
		assertEquals(p.getMaxHealth(),100);
	}
	
	public void testGetMAXENERGY() {
		Player p = new Player("name");
		assertEquals(p.getMaxEnergy(),20);
	}
	
	public void testUpdateXP() {
		Player p = new Player("name");
		assertEquals(p.getXP(), 0);
		p.updateXP(10);
		assertEquals(p.getXP(), 10);
		p.updateXP(100);
		assertEquals(p.getXP(), 110);
	}
	
	public void testShouldPlayerLevelUp() {
		Player p = new Player("dirka_stan");
		assertEquals(p.getLevel(), 1);
		p.setXP(1990);
		assertFalse(p.shouldPlayerLevelUp(4));
		assertTrue(p.shouldPlayerLevelUp(10));
	}
}
