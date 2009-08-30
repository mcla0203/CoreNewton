package com.cn.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.cn.constants.ProtocolConstants;
import com.cn.protocol.Protocol;

public class AccountCreationHelper {
	
	Logger logger = Logger.getLogger(AccountCreationHelper.class);
	String username;
	String password;
	boolean accountInUse;

	/**
	 * Constructor for the AccountCreationHelper.
	 * 
	 * @param username
	 * @param password
	 */
	public AccountCreationHelper(String username, String password) {
		this.username = username;
		this.password = password;
		this.accountInUse = true;
		
		try {
			new Scanner(new FileInputStream(getSaveFile()));
		} catch (FileNotFoundException e) {
			accountInUse = false;
			createSaveFile();
		}
	}
	
	/**
	 * Method that creates the save file from the initalized username and password.
	 * This method should only be called if the username is not already in use.
	 */
	private void createSaveFile() {
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(getSaveFile()));
			outputStream.println(ProtocolConstants.PASSWORD + Protocol.createSimpleRequest(password));
			outputStream.close();
		} catch(FileNotFoundException e){
			logger.error("There was a problem creating the new user", e);
		}
		
	}

	/**
	 * Returns true if the account is already in use.  False if it is not.
	 * @return
	 */
	public boolean isAccountInUse() {
		return accountInUse;
	}
	
	/**
	 * Returns the string name of the file path.
	 * @return
	 */
	private String getSaveFile() {
		return "src/users/"+username;
	}
	
}
