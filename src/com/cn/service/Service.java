package com.cn.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Service {

	//configurations
	// Step 2: Establish the connection to the database.
	// The 'localhost' might change if you're not on the same computer
	// that the database is on...
	private String url = "jdbc:oracle:thin:@//localhost:1521/root";
	protected Connection connection = null;
	
	protected Service() {
		connect();
	}
	
	private void connect() { 
		// Step 1: Load the JDBC driver. 
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("You don't have the OracleDriver... you need to:");
			System.out.println("1. subst your drive to Z:");
			System.out.println("2. add the odjbc jar in the CoreNewton/dep to your eclipse project build path");
		}
		try {
			// Step 2: Connect to the DB.
			connection = DriverManager.getConnection(url,"mcla0203","pw123");
		} catch (SQLException e) {
			System.out.println("Unable to connect to the database.  Check the url variable for IP:PORT/Service");
		}

	}
}
