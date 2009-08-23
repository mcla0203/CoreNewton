package com.cn.server;

import java.util.List;

import com.cn.players.Player;

/**
 * Helper class for the server's players.
 * @author Michael
 *
 */
public class ServerPlayersHelper {

	/**
	 * Returns the player for the given name.
	 * @param name
	 * @return Player
	 */
	public static Player getPlayerByName(String name, List<Player> list) {
		for( Player p : list ) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
}
