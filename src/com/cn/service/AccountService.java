package com.cn.service;

import java.sql.ResultSet;
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
	
//	public static void main(String[] args) {
//		AccountService as = new AccountService();
//		int numb = as.getID("");
//		System.out.println(numb);
//	}
	
}
