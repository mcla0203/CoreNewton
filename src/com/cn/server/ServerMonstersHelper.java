package com.cn.server;

import java.util.List;

import com.cn.npc.monsters.Monster;

/**
 * Class for the server to hold information about the current Monsters
 * in the instance.
 * 
 * @author Michael
 * 
 */
public class ServerMonstersHelper {
	
	/**
	 * Returns the monster for the given id.
	 * @param id
	 * @return Monster
	 */
	public static Monster getMonsterById(double id, List<Monster> monsterList) {
		for(Monster m : monsterList) {
			if(m.getId() == id) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Returns an array representation of all the monster ids.
	 * 
	 * @param monsterList
	 * @return
	 */
	public static Double[] getMonsterIds(List<Monster> monsterList) {
		Double[] result = new Double[monsterList.size()];
		for(int i = 0; i<monsterList.size() ; i++ ) {
			result[i] = monsterList.get(i).getId();
		}
		return result;
	}

}
