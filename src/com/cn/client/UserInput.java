package com.cn.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInput{

    
	String userInput; 
	BufferedReader reader; 
	
	public UserInput() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public String getUserInput() {
		System.out.println(ClientConstants.USER_INPUT);
		try{
			userInput = reader.readLine(); 
		}
		catch (IOException e){
			System.out.println(e);
		}
		return userInput;

	}

}
