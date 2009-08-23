package com.cn.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.cn.constants.ClientConstants;

public class UserInput{

    
	String userInput; 
	BufferedReader reader; 
	
	public UserInput() {
		System.out.println(ClientConstants.USER_INPUT);
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public String getUserInput() {
		try{
			userInput = reader.readLine(); 
		}
		catch (IOException e){
			System.out.println(e);
		}
		return userInput;

	}

}
