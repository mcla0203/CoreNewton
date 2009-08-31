package com.cn.npc.monsters;

import com.cn.players.Player;

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
	
	public void testbeAttacked_EligibleFirstAttack() {
		Monster m = new Monster();
		Player p = new Player("player");
		m.beAttacked(p, 30);
		assertTrue(m.getPlayersEligibleForXP().containsKey(p));
	}
	
	public void testbeAttacked_NotEligibleFirstAttackButEligibleSecond() {
		Monster m = new Monster();
		Player p = new Player("player");
		m.beAttacked(p, 10);
		assertFalse(m.getPlayersEligibleForXP().containsKey(p));

		m.beAttacked(p, 10);
		assertTrue(m.getPlayersEligibleForXP().containsKey(p));
	}
	
	public void testbeAttacked_NeverEligible() {
		Monster m = new Monster();
		Player p = new Player("player");
		m.beAttacked(p, 10);
		Player p2 = new Player("player2");
		m.beAttacked(p2, 100);
		
		assertFalse(m.getPlayersEligibleForXP().containsKey(p));
		assertTrue(m.getPlayersEligibleForXP().containsKey(p2));
	}
	
	public void testBeAttacked_BothEligible() {
		Monster m = new Monster();
		Player p = new Player("player");
		m.beAttacked(p, 30);
		Player p2 = new Player("player2");
		m.beAttacked(p2, 30);
		assertTrue(m.getPlayersEligibleForXP().containsKey(p));
		assertTrue(m.getPlayersEligibleForXP().containsKey(p2));
	}
	
	public void testBeAttacked_EligibleMoreDmg() {
		Monster m = new Monster();
		Player p = new Player("player");
		m.beAttacked(p, 30);
		m.beAttacked(p, 30);
		assertTrue(m.getPlayersEligibleForXP().containsKey(p));
		assertFalse(m.getAttackedBy().containsKey(p));
	}
	
	public void testBeAttacked_NeverInEligible() {
		Monster m = new Monster();
		Player p = new Player("player");
		m.beAttacked(p, 5);
		assertFalse(m.getPlayersEligibleForXP().containsKey(p));
		assertTrue(m.getAttackedBy().containsKey(p));
		m.beAttacked(p,5);
		assertFalse(m.getPlayersEligibleForXP().containsKey(p));
		assertTrue(m.getAttackedBy().containsKey(p));
	}

}
