package com.cn.server;

import java.util.ArrayList;
import java.util.List;

import com.cn.npc.monsters.Monster;

import junit.framework.TestCase;

public class ServerMonstersHelperTest extends TestCase {

	public void testGetMonsterByID_Found() {
		double id1 = 123.0;
		double id2 = 1234.0;
		Monster m = new Monster(id1);
		Monster m2 = new Monster(id2);
		List<Monster> list = new ArrayList<Monster>();
		list.add(m);
		list.add(m2);
		assertEquals(m, ServerMonstersHelper.getMonsterById(id1, list));
		assertEquals(m2, ServerMonstersHelper.getMonsterById(id2, list));
	}
	
	public void testGetMonsterByID_NotFound() {
		double id1 = 123.0;
		double id2 = 1234.0;
		Monster m = new Monster(id1);
		Monster m2 = new Monster(id2);
		List<Monster> list = new ArrayList<Monster>();
		list.add(m);
		list.add(m2);
		assertNotSame(m, ServerMonstersHelper.getMonsterById(41421, list));
		assertNotSame(m2, ServerMonstersHelper.getMonsterById(482, list));
		assertEquals(null, ServerMonstersHelper.getMonsterById(482, list));
	}
	
	public void testMonsterIDs() {
		double id1 = 123.0;
		double id2 = 1234.0;
		Monster m = new Monster(id1);
		Monster m2 = new Monster(id2);
		List<Monster> list = new ArrayList<Monster>();
		list.add(m);
		list.add(m2);
		
		Double[] expected = new Double[2];
		expected[0] = m.getId();
		expected[1] = m2.getId();
		
		assertEquals(expected.length, ServerMonstersHelper.getMonsterIds(list).length);
		for(int i=0; i<expected.length; i++) {
			assertEquals(expected[i], ServerMonstersHelper.getMonsterIds(list)[i]);
		}
	}
	
	public void testGetMonsterByID_NullList() {
		double id1 = 123.0;
		double id2 = 1234.0;
		Monster m = new Monster(id1);
		Monster m2 = new Monster(id2);
		List<Monster> list = new ArrayList<Monster>();
		list.add(m);
		list.add(m2);
		assertNotSame(m, ServerMonstersHelper.getMonsterById(id1, null));
		assertNotSame(m2, ServerMonstersHelper.getMonsterById(id2, null));
		assertEquals(null, ServerMonstersHelper.getMonsterById(482, null));
		assertEquals(null, ServerMonstersHelper.getMonsterById(482, null));
	}
	
	
	
}
