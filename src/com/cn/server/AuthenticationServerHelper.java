package com.cn.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cn.constants.Constants;
import com.cn.constants.ProtocolConstants;
import com.cn.protocol.Protocol;

/**
 * This class is non-static by design.  Since the authentication server
 * is multi threaded, there should be separate instances of the authentication
 * server helper to differentiate between the different users who want to login.
 * 
 * Each instance created is intended to be thread-specific.
 * 
 * @author Michael
 *
 */
public class AuthenticationServerHelper {
	Logger logger = Logger.getLogger(AuthenticationServerHelper.class);
	private Scanner inputStream = null;
	private boolean userFound = false;
	private boolean authenticated = false;
	private String username = null;
	private String password = null;
	private Map<String, String[]> characterMap = null;
	private String saveFile = null;
	
	/**
	 * Uses the inputStream to scan for the saved file.  If found, read it
	 * and initialize the object for the current user in session.
	 * 
	 * @param username
	 * @param password
	 */
	public AuthenticationServerHelper(String username, String password) {
		logger.trace("Auth Server Helper instance created. Username: " + username + " Password: " + password);
		init(username, password);
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public boolean isUserFound() {
		return userFound;
	}
	
	public Map<String, String[]> getCharacterMap() {
		return characterMap;
	}
	
	private String getSaveFile(String username) {
		logger.trace("Inside getSaveFile");
		String file = "src/users/"+username;
		if(saveFile == null) {
			saveFile = file;
		}
		return file;
	}
	
	/**
	 * This method overwrites the current save file in src/usrers/<userName>.
	 * Then, it reinitalizes the ash object.
	 */
	public void overWriteChar() {
		logger.trace("Inside overWriteChar");
		if(!userFound || !authenticated) {
			if(logger.isDebugEnabled()) {
				logger.debug("userFound: " + userFound + "authenticated: " + authenticated);
				logger.debug("returning from overWriteChar b/c one of those is false");
			}		
			return;
		}
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(saveFile));
			outputStream.println(ProtocolConstants.PASSWORD + Protocol.createSimpleRequest(password));
			for(String character : characterMap.keySet()) {
				outputStream.println(Protocol.convertListToProtocol(characterMap.get(character)));
			}
			outputStream.close();
			outputStream.flush();
			logger.trace("Closed output stream...going to re-init the character data structure.");
			//re-init the character data structure
			reInit();
		}
		catch(FileNotFoundException e) {
			if(logger.isEnabledFor(Level.ERROR)) {
				logger.error("Exception thrown in overWriteChar: ", e);
			}
		}
		
	}
	
	private void init(String username, String password) {
		logger.trace("Inside AuthServerHelper.init");
		try {
			characterMap = new HashMap<String, String[]>();
			inputStream = new Scanner(new FileInputStream(getSaveFile(username)));
			userFound = true;
			while(inputStream.hasNextLine()) {
				String line = inputStream.nextLine();
				if(logger.isDebugEnabled()) {
					logger.debug("inputStream hasNextLine: " + line);
				}
				String[] args = Protocol.getRequestArgsSimple(line);
				String cmd = Protocol.getRequestCmdSimple(line);
				if(cmd.equals(Constants.PASSWORD)) {
					logger.debug("cmd equals PASSWORD");
					if(args[1].equals(password)) {
						if(logger.isDebugEnabled()) {
							logger.debug("The password matches: " + password);
						}
						authenticated = true;
						this.password = password;
						this.username = username;
					}
				}
				else if(authenticated){  //they are stats
					logger.debug("Command wasn't PASSWORD, so these are stats.");
					for(int i=1; i<5; i++) {
						if(logger.isDebugEnabled()) {
							logger.debug("Adding character '" + cmd + "to the characterMap " +
									"with stats: " + Protocol.getRequestArgsSimple(line));
						}
						characterMap.put(cmd, Protocol.getRequestArgsSimple(line));
					}
				}
			}
		} catch (FileNotFoundException e) {
			if(logger.isEnabledFor(Level.ERROR)) {
				logger.error("Exception in AuthServerHelper.init", e);
			}
		}
	}
	
	/**
	 * This method is only intended to be used after init(String, String) has
	 * been called by the instructor.
	 */
	private void reInit() {
		logger.trace("Calling reinit");
		init(username, password);
	}


}
