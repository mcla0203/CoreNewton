package com.cn.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountService extends Service {

	public AccountService() {
		super();
	}

	public int getID(String username) {
		int id = -1;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT id FROM Account WHERE username = '" + username + "'");
			if (rset.next()) {
				id = rset.getInt(1);  
			}
			stmt.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Service method. Creates an account given a username and password.
	 * Returns:<br>
	 * 1: if the account was created successfully<br>
	 * 2: if the username or password is null or empty<br>
	 * 3: if the username already exists <br>
	 * 4: if the password does not meet the criteria<br>
	 * 999: if something else went wrong<br>
	 * 
	 * @param username
	 * @param password
	 */
	public int createAccount(String username, String password) {
		if(username == null || username.equals("") || password == null || password.equals("")) {
			//TODO: Error
			System.out.println("Password or username cannot be null or empty.");
			return 2;
		}
		try {
			//ensure username does not already exist
			if(doesUserExist(username)) {
				//TODO: Error
				System.out.println("Username already exists.");
				return 3;
			}
			//ensure pw meets criteria
			//TODO: Decide pw criteria
			//return 4;

			//create account
			int accountID = nAccounts() + 1;
			Statement stmt;
			stmt = connection.createStatement();
			stmt.executeQuery("INSERT INTO Account "+
					"(id, username, password) " +
					"VALUES " +
					"(" + accountID + ", '" + username + "', '" + password + "')" );
			stmt.close();		
		}
		catch (Exception e) {
			e.printStackTrace();
			return 999;
		}
		return 1;
	}

	private int nAccounts() {
		int nAccounts = -1;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT count(*) FROM Account");
			if (rset.next()) {
				nAccounts = rset.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nAccounts;
	}

	/**
	 * This function will attempt to login to the database.
	 * It will return code with the given error.
	 * 
	 * Returns:<br>
	 * 1: success<br>
	 * 2: username incorrect<br>
	 * 3: password incorrect<br>
	 * 999: failure<br>
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public int login(String username, String password) {
		if(username == null || password == null || username.equals("") || password.equals("")) {
			return 999;
		}
		if(!doesUserExist(username)) {
			return 2;
		}
		String dbpw = "";
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT password FROM Account WHERE username = '" + username + "'");
			if (rset.next()) {
				dbpw = rset.getString(1).trim();
			}
			stmt.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(!dbpw.equals(password)) {
			return 3;
		}
		return 1;
	}

	/**
	 * Service method.  Return true if the username does not already exist, else return false.
	 */
	public boolean doesUserExist(String username) {
		System.out.println("Does user exist? : " + username);
		boolean userExists = false;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT count(*) FROM Account WHERE username = '" + username + "'");
			if (rset.next()) {
				int count = rset.getInt(1);
				if(count > 0) {
					userExists = true;
				}
			}
			stmt.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return userExists;
	}

	public static void main(String[] args) {
		AccountService as = new AccountService();
		int numb = as.getID("");
		System.out.println(numb);
		as.createAccount("mcla0203", "");
		as.createAccount("mcla0203", "dirka");
		as.createAccount("dirka", "mcla0203");
		System.out.println(as.doesUserExist("timmay"));
	}
}
