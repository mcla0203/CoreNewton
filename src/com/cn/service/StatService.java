package com.cn.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class StatService extends Service {

	public StatService() {
		super();
	}
	
	Logger logger = Logger.getLogger(StatService.class);

	public void createDefaultStats(int cid) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeQuery("INSERT INTO Stats "+
					"(cid, lvl, credits, xp, health, energy) " +
					"VALUES " +
					"("+cid+", 1, 0, 0, 100, 20)" );
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the stats of the character.
	 * 
	 * Returns:<br>
	 * 1: success
	 * 2: the name of that character does not exist
	 * 999: general failure
	 * 
	 * @param name
	 */
	public int setStats(String name, int lvl, int credits, int xp, int health, int energy) {
		//make sure the name is real, get cid
		CharacterService cs = new CharacterService();
		int cid = cs.getCid(name);
		if(cid == -1) {
			return 2;
		}
		
		//update the stats
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeQuery("UPDATE Stats " +
					"SET lvl = " + lvl + ", " +
					    "credits = "+ credits + ", " +
					    "xp = " + xp + ", " +
					    "health = " + health + ", " +
					    "energy = " + energy +
					"WHERE cid = " + cid);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 999;
		}
		return 1;
	}

	public ArrayList<String> getStats(String charName) {
		ArrayList<String> charStats = new ArrayList<String>();
		charStats.add(charName);
		Statement stmt;
		try {
			CharacterService cs = new CharacterService();
			int cid = cs.getCid(charName);
			logger.debug("charName: "+ charName);
			logger.debug("cid: " + cid);
			if(cid == 999 || cid == -1) {
				return null;
			}
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT lvl, health, energy, xp, credits FROM Stats WHERE cid = "+ cid);
			if (rset.next()) {
				charStats.add(String.valueOf(rset.getInt(1)).trim());
				charStats.add(String.valueOf(rset.getInt(2)).trim());
				charStats.add(String.valueOf(rset.getInt(3)).trim());
				charStats.add(String.valueOf(rset.getInt(4)).trim());
				charStats.add(String.valueOf(rset.getInt(5)).trim());
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return charStats;
	}
	
	public static void main(String[] args) {
		StatService ss = new StatService();
		ss.setStats("Duckie", 50, 60000, 3422, 200, 30);
		ss.setStats("bobbybouche", 50, 60000, 3422, 200, 30);
		System.out.println(ss.getStats("Duckie"));
	}
	
}



