package com.cn.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CharacterService extends Service {

	public CharacterService() {
		super();
	}

	/**
	 * Service method.  Return the all the characters ever created.
	 */
	public boolean isUniqueCharName(String name) {
		boolean isUnique = false;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT count(*) FROM Character WHERE name = '"+name+"'");
			if (rset.next()) {
				if(rset.getInt(1) == 0) {
					isUnique = true;
				}
			}
			stmt.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return isUnique;
	}

	/**
	 * This will method will create a character for a given account.
	 * Acceptable input is the string username.
	 * Returns: <br>
	 * 1: if the character was created successfully<br>
	 * 2: if the user account does not exist<br>
	 * 3: if the user has the max number of characters already<br>
	 * 4: if the character name already exists in the db<br>
	 * 5: if the username or character name is null or empty<br>
	 * 999: if something else goes wrong<br>
	 * 
	 * @param account
	 */
	public int createCharacter(String username, String charName) {
		if(username == null || username.equals("") || charName == null || charName.equals("")) {
			return 5;
		}
		try {
			//convert the username to account id
			AccountService as = new AccountService();
			int userID = as.getID(username);

			//ensure account exists
			if(userID == -1) {
				System.out.println("Account does not exist.");
				return 2;
			}
			//make sure they don't have > 4 chars
			if(nCharacters(userID) >= 4) {
				System.out.println("This user has too many characters.");
				return 3;
			}
			//make sure that character name is unique
			if(!isUniqueCharName(charName)) {
				System.out.println("This charName already exists.");
				return 4;
			}
			//insert row into character
			int charID = maxCharId() + 1;
			Statement stmt;
			stmt = connection.createStatement();
			stmt.executeQuery("INSERT INTO Character "+
					"(id, aid, name) " +
					"VALUES " +
					"(" + charID + ", " + userID + ", '" + charName + "')" );
			stmt.close();

			//insert row into stats
			StatService ss = new StatService();
			ss.createDefaultStats(charID);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 999;
		}
		return 1;
	}

	/**
	 * Returns the number of characters that are persisted in the database.
	 * @return
	 */
	public int nCharacters() {
		int nCharacters = -1;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT count(*) FROM Character");
			if (rset.next()) {
				nCharacters = rset.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nCharacters;
	}
	
	/**
	 * Returns the number that represents the maximum genereated id.
	 */
	public int maxCharId() {
		int maxId = -1;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT Max(id) FROM Character");
			if (rset.next()) {
				maxId = rset.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxId;
	}

	/**
	 * Returns the number of characters that are persisted in the database for this user id.
	 * @return
	 */
	public int nCharacters(int userID) {
		int nCharacters = 0;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT count(*) FROM Character WHERE aid = "+userID);
			if (rset.next()) {
				nCharacters = rset.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nCharacters;
	}
	
	/**
	 * Returns the Character ID of the given charName.  If it doesn't exist, then it returns -1.
	 * @param name
	 * @return
	 */
	public int getCid(String name) {
		int cid = -1;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT id FROM Character WHERE name = '" + name + "'");
			if (rset.next()) {
				cid = rset.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 999;
		}
		return cid;
	}
	
	/**
	 * Returns a list of characters associated with the given user in the db. Returns an 
	 * empty list if the user does not have any characters.
	 * 
	 * @param username
	 * @return
	 */
	public ArrayList<String> getCharacters(String username) {
		Statement stmt;
		ArrayList<String> chars = new ArrayList<String>();
		try {
			AccountService as = new AccountService();
			int aid = as.getID(username);
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT name FROM Character WHERE aid = " + aid);
			while (rset.next()) {
				chars.add(rset.getString(1).trim());
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return chars;
	}
	
	/**
	 * This method returns true if the given charName belongs to the given username.
	 * @param username
	 * @param charName
	 * @return
	 */
	public boolean hasCharacter(String username, String charName) {
		boolean ret = false;
		try {
			AccountService as = new AccountService();
			int aid = as.getID(username);
			int cid = getCid(charName);
			Statement stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT count(*) FROM Character WHERE id = " + cid + 
																			   "AND aid = " + aid);
			if (rset.next()) {
				if(rset.getInt(1) == 1) {
					ret = true;
				}
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Attempts to delete the given charName from the db. Returns true if it was successful, otherwise
	 * returns false.
	 * @param charName
	 * @return
	 */
	public boolean deleteCharacter(String charName) {
		int cid = getCid(charName);
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeQuery("DELETE FROM Character WHERE id = " + cid);	
			stmt.executeQuery("DELETE FROM Stats WHERE cid = " + cid);
			stmt.close();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
		return true;
	}

	public static void main(String[] args) {
		CharacterService cs = new CharacterService();
		System.out.println(cs.nCharacters());
		System.out.println(cs.nCharacters(3));
//		cs.createCharacter("blarg", "blarg");
//		cs.createCharacter("mcla0203", "Bilbo");
//		cs.createCharacter("osgoo030", "Dirka");
//		cs.createCharacter("osgoo030", "Bilbo");
//		cs.createCharacter("osgoo030", "Bilbo Baggins");
//		cs.createCharacter("osgoo030", "Marty");
		System.out.println(cs.getCid("Duckie"));
		System.out.println(cs.getCid("BOMBAY"));
		ArrayList<String> chars = cs.getCharacters("mcla0203");
		for(String s: chars) {
			System.out.println(s);
		}
		System.out.println(cs.hasCharacter("mcla0203", "Duckie"));
		System.out.println(cs.hasCharacter("mcla0203", "Chica"));
	}


}
