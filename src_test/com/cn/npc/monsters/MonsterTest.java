package com.cn.npc.monsters;

import junit.framework.TestCase;

public class MonsterTest extends TestCase {
	
	public void testMonster() {
		Monster m = new Monster();
		assertNotNull(m.id);
		assertFalse(m.isLooted);
	}
	
	public void testGetId() {
		Monster m = new Monster();
		assertNotNull(m.getId());
	}
	
	public void testIsLooted() {
		Monster m = new Monster();
		assertFalse(m.isLooted());
	}
	
	public void testSetIsLooted() {
		Monster m = new Monster();
		assertFalse(m.isLooted());
		m.setIsLooted(true);
		assertTrue(m.isLooted());
	}

}
