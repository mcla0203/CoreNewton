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
	 * 
	 * @param account
	 */
	public void createCharacter(String username, String charName) {
		if(username == null || username.equals("") || charName == null || charName.equals("")) {
			return;
		}
		try {
			//convert the username to account id
			AccountService as = new AccountService();
			int userID = as.getID(username);

			//ensure account exists
			if(userID == -1) {
				//TODO: ERROR
				System.out.println("Account does not exist.");
				return;
			}
			//make sure they don't have > 4 chars
			if(nCharacters(userID) >= 4) {
				//TODO: ERROR
				System.out.println("This user has too many characters.");
				return;
			}
			//make sure that character name is unique
			if(!isUniqueCharName(charName)) {
				//TODO: ERROR
				System.out.println("This charName already exists.");
				return;
			}
			//insert row into character
			int charID = nCharacters() + 1;
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
		}
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

//	public static void main(String[] args) {
//		CharacterService cs = new CharacterService();
//		System.out.println(cs.nCharacters());
//		System.out.println(cs.nCharacters(3));
//		cs.createCharacter("blarg", "blarg");
//		cs.createCharacter("mcla0203", "Bilbo");
//		cs.createCharacter("osgoo030", "Dirka");
//		cs.createCharacter("osgoo030", "Bilbo");
//		cs.createCharacter("osgoo030", "Bilbo Baggins");
//		cs.createCharacter("osgoo030", "Marty");
//	}


}
