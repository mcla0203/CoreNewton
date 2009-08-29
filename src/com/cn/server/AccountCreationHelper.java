package com.cn.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.cn.constants.ProtocolConstants;
import com.cn.protocol.Protocol;

/**
 * Helper class to create new accounts.  No logic for creating new users should be
 * inside the AuthenticationServerHelper.  It should go here.
 * 
 * @author Michael
 *
 */
public class AccountCreationHelper {
	
	Logger logger = Logger.getLogger(AccountCreationHelper.class);
	String saveFile = null;
	private boolean userAlreadyExists;
	
/**
 * Connstructor for the AccountCreationHelper.  It takes in a username
 * and a password, and will only create the username if the
 * username has not been in use by someone else.
 * 
 * @param username
 * @param password
 */
	public AccountCreationHelper(String username, String password) {
		logger.trace("Entering AccountCreationHelper constructor.");
		saveFile = "src/users/" + username;
		try {
			new Scanner(new FileInputStream(saveFile));
			userAlreadyExists = true;
			logger.debug("The user already exists");
		} catch (FileNotFoundException e) {
			userAlreadyExists = false;
			logger.debug("The username is available");
			createUser(username, password);
		}
	}
	
	public boolean userAlreadyExists() {
		return userAlreadyExists;
	}
	
	
	/**
	 * Method that creates the save file for the new user.
	 * 
	 * @param username
	 * @param password
	 */
	private void createUser(String username, String password) {
		logger.trace("Entering createUser method.");
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(saveFile));
			outputStream.println(ProtocolConstants.PASSWORD + Protocol.createSimpleRequest(password));
			outputStream.close();
		} catch (FileNotFoundException e) {
			logger.error("When this was designed, it was clear that users would never get here, something went wrong....", e);
		}
		logger.trace("Exiting createUser method.");
	}
	
}
