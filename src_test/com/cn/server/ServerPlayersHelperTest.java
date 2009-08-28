package com.cn.server;

import java.util.ArrayList;
import java.util.List;

import com.cn.players.Player;

import junit.framework.TestCase;

public class ServerPlayersHelperTest extends TestCase {

	public void testGetPlayerByName_Found() {
		Player p1 = new Player("player1");
		Player p2 = new Player("player2");
		List<Player> l = new ArrayList<Player>();
		l.add(p1);
		l.add(p2);
		assertEquals(p1, ServerPlayersHelper.getPlayerByName("player1", l));
		assertEquals(p2, ServerPlayersHelper.getPlayerByName("player2", l));
		assertNotSame(p2, ServerPlayersHelper.getPlayerByName("player1", l));
		assertNotSame(p1, ServerPlayersHelper.getPlayerByName("player2", l));
	}
	
	public void testGetPlayerByName_NotFound() {
		Player p1 = new Player("player1");
		List<Player> l = new ArrayList<Player>();
		l.add(p1);
		assertNotSame(p1, ServerPlayersHelper.getPlayerByName("player2", l));
		assertNotSame(p1, ServerPlayersHelper.getPlayerByName("not_the_player's_name", l));
	}
	
	public void testGetPlayerByName_NullList() {
		Player p1 = new Player("player1");
		assertNotSame(p1, ServerPlayersHelper.getPlayerByName("player1", null));
		assertEquals(ServerPlayersHelper.getPlayerByName("player1", null), null);
	}
	
	public void testGetPlayerByName_NullPlayer() {
		assertEquals(ServerPlayersHelper.getPlayerByName(null, new ArrayList<Player>()), null);
	}
}
