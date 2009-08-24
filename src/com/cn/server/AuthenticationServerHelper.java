package com.cn.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.cn.constants.Constants;
import com.cn.protocol.Protocol;

public class AuthenticationServerHelper {
	
	private Scanner inputStream = null;
	private boolean userFound = false;
	private boolean authenticated = false;
	private Map<String, String[]> characterMap = null;
	
	public AuthenticationServerHelper(String username, String password) {
		try {
			characterMap = new HashMap<String, String[]>();
			inputStream = new Scanner(new FileInputStream("src/users/"+username));
			userFound = true;
			while(inputStream.hasNextLine()) {
				String line = inputStream.nextLine();
				String[] args = Protocol.getRequestArgsSimple(line);
				String cmd = Protocol.getRequestCmdSimple(line);
				if(cmd.equals(Constants.PASSWORD)) {
					if(args[1].equals(password)) {
						authenticated = true;
					}
				}
				else {  //they are stats
					for(int i=1; i<5; i++) {
						characterMap.put(cmd, Protocol.getRequestArgsSimple(line));
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
	


}
